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
import ch.hearc.ariahelper.sensors.wifip2p.connection.SocketAction
import ch.hearc.ariahelper.sensors.wifip2p.connection.WifiP2pClient
import ch.hearc.ariahelper.sensors.wifip2p.connection.WifiP2pServer
import java.io.BufferedReader
import java.net.Socket


object WifiP2PReceiver : BroadcastReceiver() {
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    private lateinit var activity: Activity
    private lateinit var connectionListener : WifiP2pManager.ConnectionInfoListener
    val wifiViewModel = WifiP2PViewModel()
    private const val PORT_CONNECTIONS : Int = 9999
    //private var serverSocket : ServerSocket? = null
    private var items: List<Item>? = null
    private var onItemSent : ItemSentCallback? = null

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
                checkPermission()
                manager.requestPeers(channel) { peers: WifiP2pDeviceList? ->
                    wifiViewModel._peers.value = peers
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Respond to new connection or disconnections
                manager!!.let {
                    val networkInfo: NetworkInfo =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)!!
                    if (networkInfo?.isConnected == true) {
                        // We are connected with the other device, request connection
                        // info to find group owner IP
                        manager.requestConnectionInfo(channel, connectionListener)
                    } else {
                        //not connected anymore, the distant device quit the connection
                        wifiViewModel._peerConnecting.value = null
                        //restart the discovery service if necessary
                        if (wifiViewModel.p2pActivated.value == true) {
                            discoverPeers()
                        }
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
                wifiViewModel._peerConnecting.value = device
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
        return WifiP2pManager.ConnectionInfoListener {
            // After the group negotiation, we can determine the group owner (server).
            if (it.isGroupOwner) {
                // We are the group owner : Start a server and wait for connection
                startServer()
            } else {
                // We are not : We were just called and must connect to the server
                startClient(it.groupOwnerAddress.hostAddress)
            }
        }
    }

    private fun startServer(){
        WifiP2pServer(PORT_CONNECTIONS, sendOrReceive()).start()
    }

    private fun startClient(host: String){
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {}
        WifiP2pClient(host, PORT_CONNECTIONS, sendOrReceive()).start()
    }

    fun chargeItems(items: List<Item>, itemSentCallback: ItemSentCallback){
        this.items = items
        this.onItemSent = itemSentCallback
    }

    fun disconnect(){
        manager.removeGroup(channel, null)
    }

    private fun sendOrReceive() : SocketAction {
        return if (wifiViewModel.peerConnecting.value != null) sentItemsAction()  else receiveItemsAction()
    }

    private fun sentItemsAction() : SocketAction {
        return SocketAction {
            Log.d("TAG", "sendItems: sending items")
            val outputStream = it!!.getOutputStream()
            outputStream.write("Salut".toByteArray())
            Log.d("TAG", "sendItems: items sent, closing")
        }
    }

    private fun receiveItemsAction() : SocketAction {
        return SocketAction {
            val inputStream = it!!.getInputStream()
            val reader = BufferedReader(inputStream.reader())
            val reception = StringBuilder()
            Log.d("TAG", "start: reading")
            reader.use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    reception.append(line)
                    line = reader.readLine()
                }
            }
            onReceiveItems(reception.toString())
        }
    }

    fun onReceiveItems(message: String){
        activity.runOnUiThread{
            Toast.makeText(
                activity,
                "Received : $message",
                Toast.LENGTH_SHORT
            ).show()
        }
        onItemSent!!.onSuccess()
    }
}