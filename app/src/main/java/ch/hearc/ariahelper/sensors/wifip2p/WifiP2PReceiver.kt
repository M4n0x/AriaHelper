package ch.hearc.ariahelper.sensors.wifip2p

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import ch.hearc.ariahelper.models.Item
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket


object WifiP2PReceiver : BroadcastReceiver() {
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    private lateinit var activity: Activity
    private lateinit var connectionListener : WifiP2pManager.ConnectionInfoListener
    private var items : List<Item>? = null
    val wifiViewModel = WifiP2PViewModel()
    private val PORT_CONNECTIONS : Int = 6666
    private var serverSocket : ServerSocket? = null

    fun init(
        _channel: WifiP2pManager.Channel,
        _manager: WifiP2pManager,
        _activity: Activity
    ){
        //external values
        channel = _channel
        manager = _manager
        activity = _activity

        connectionListener = createConnectionListener()

        checkPermission()
        checkWifiP2pCapability()
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Check to see if Wi-Fi is enabled and notify appropriate activity
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                //wifi p2p state changed : Change the viewmodel accordingly
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                wifiViewModel._p2pEnabled.value = (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                wifiViewModel._searching.value = false
                checkPermission()
                manager.requestPeers(channel) { peers: WifiP2pDeviceList? ->
                    wifiViewModel._peers.value = peers
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Respond to new connection or disconnections
                Log.d("TAG", "CONNECTION CHANGED")
                manager!!.let {
                    val networkInfo: NetworkInfo =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)!!
                    if (networkInfo?.isConnected == true) {
                        Log.d("TAG", "CONNECTED")
                        // We are connected with the other device, request connection
                        // info to find group owner IP

                        manager.requestConnectionInfo(channel, connectionListener)
                    } else {
                        Log.d("TAG", "DISCONNECTED")
                        serverSocket?.also {
                            it.close()
                            serverSocket == null
                        }

                        //not connected anymore, the distant device quit the connection
                        wifiViewModel._connectedPeer.value = null
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
            }
        }
    }

    fun discoverPeers(){
        checkPermission()
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                wifiViewModel._searching.value = true
            }

            override fun onFailure(reasonCode: Int) {
                Toast.makeText(activity, "Error while starting peer discovery", Toast.LENGTH_SHORT)
                    .show()
                when (reasonCode) {
                    WifiP2pManager.P2P_UNSUPPORTED -> Log.d("info", "p2p unsupported")
                    WifiP2pManager.ERROR -> Log.d("info", "p2p error")
                    WifiP2pManager.BUSY -> Log.d("info", "p2p b")
                }
            }
        })
    }

    fun stopDiscovery(){
        if(wifiViewModel.searching.value!!){
            manager.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    wifiViewModel._searching.value = false
                }

                override fun onFailure(reason: Int) {
                    wifiViewModel._searching.value = false
                }
            })
        }
    }

    fun connect(device: WifiP2pDevice){
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        checkPermission()
        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                //nothing to be done (Trying to connect)
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(
                    activity,
                    "Failed to connect to ${device.deviceName}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }


    private fun checkWifiP2pCapability(){
        val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        wifiViewModel._p2pSupported.value = (activity.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)
                && (wifiManager?.isP2pSupported == true))
    }

    private fun checkPermission(){
        //check permission for later use
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(activity, permissions, 0)
        }
    }

    private fun createConnectionListener() : WifiP2pManager.ConnectionInfoListener {
        return WifiP2pManager.ConnectionInfoListener { info ->
            // After the group negotiation, we can determine the group owner (server).
            if (info.groupFormed && info.isGroupOwner) {
                Log.d("TAG", "SERVER")
                // Do whatever tasks are specific to the group owner.
                // Server-side : we send items
                Toast.makeText(activity, "Server side !", Toast.LENGTH_SHORT).show()
                startServer()
            } else if (info.groupFormed) {
                Log.d("TAG", "CLIENT")
                // The other device acts as the peer (client). In this case,
                // Client-side : we receive items
                Toast.makeText(activity, "Client side !", Toast.LENGTH_SHORT).show()
                startClient(info.groupOwnerAddress.hostAddress)
            }
        }
    }

    private fun startServer(){
        Toast.makeText(
            activity,
            "Server starting...",
            Toast.LENGTH_LONG
        ).show()
        GlobalScope.launch {
            try {
                val ephemeralServerSocket = ServerSocket(PORT_CONNECTIONS)
                Log.d("a", "Server: Socket opened")
                val client: Socket = ephemeralServerSocket.accept()
                Log.d("b", "Server: connection done")
                val outputStream = client.getOutputStream()
                outputStream.write("Salut".toByteArray())
                outputStream.close()
                Log.d("b", "Server: sent message")

            } catch (e: IOException) {
                //mince
                Log.d("d", "EXCEPT $e")
            }
        }
    }

    private fun startClient(host : String){
        Toast.makeText(
            activity,
            "Client starting...",
            Toast.LENGTH_LONG
        ).show()
        GlobalScope.launch {
            val socket = Socket()
            socket.bind(null)
            socket.connect((InetSocketAddress(host, PORT_CONNECTIONS)), 2500)

            val inputStream = socket.getInputStream()
            val reader = BufferedReader(inputStream.reader())
            val reception = StringBuilder()
            reader.use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    reception.append(line)
                    line = reader.readLine()
                }
            }
            Log.d("c", "RECEIVED: $reception")
            this@WifiP2PReceiver.disconnect()
            socket.close()
        }
    }

    private fun disconnect(){
        manager.removeGroup(channel, object : WifiP2pManager.ActionListener{
            override fun onSuccess() {
            }

            override fun onFailure(reason: Int) {
            }

        })
    }
}