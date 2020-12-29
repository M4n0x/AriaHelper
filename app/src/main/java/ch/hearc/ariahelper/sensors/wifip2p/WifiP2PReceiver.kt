package ch.hearc.ariahelper.sensors.wifip2p

import android.Manifest
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
import ch.hearc.ariahelper.MainActivity
import ch.hearc.ariahelper.models.Item
import ch.hearc.ariahelper.models.SerializableItem
import ch.hearc.ariahelper.sensors.wifip2p.connection.SocketAction
import ch.hearc.ariahelper.sensors.wifip2p.connection.WifiP2pClient
import ch.hearc.ariahelper.sensors.wifip2p.connection.WifiP2pServer
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.util.stream.Collectors


object WifiP2PReceiver : BroadcastReceiver() {
    //wifiViewModel is public and accessible
    val wifiViewModel = WifiP2PViewModel()

    //WifiP2P var and parameters
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    private lateinit var activity: MainActivity
    private lateinit var connectionListener: WifiP2pManager.ConnectionInfoListener
    private const val PORT_CONNECTIONS: Int = 5678

    //item-sending variables
    private var items: List<Item>? = null
    private var itemSentCallback: ItemSentCallback? = null

    fun init(
        channel: WifiP2pManager.Channel,
        manager: WifiP2pManager,
        activity: MainActivity
    ) {
        //external values
        this.channel = channel
        this.manager = manager
        this.activity = activity

        //init the connection listener that will start either the client or the server
        connectionListener = createConnectionListener()

        //check and ask permissions of needed
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
                    if (networkInfo.isConnected) {
                        // We are connected with the other device, request connection info to find group owner IP
                        manager.requestConnectionInfo(channel, connectionListener)
                    } else {
                        //not connected anymore, the distant device quit the connection
                        wifiViewModel._peerConnecting.value = null
                        //restart the discovery service if necessary
                        if (wifiViewModel.p2pActivated.value == true) { //==true to manage null and false
                            discoverPeers()
                        }
                    }
                }
            }
        }
    }

    fun discoverPeers() {
        checkPermission()
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                wifiViewModel._searching.value = true
            }

            override fun onFailure(reasonCode: Int) {
                makeToast("Erreur durant la découverte des voisins")
                when (reasonCode) {
                    WifiP2pManager.P2P_UNSUPPORTED -> Log.d("info", "p2p unsupported")
                    WifiP2pManager.ERROR -> Log.d("info", "p2p error")
                    WifiP2pManager.BUSY -> Log.d("info", "p2p b")
                }
            }
        })
    }

    fun stopDiscovery() {
        if (wifiViewModel.searching.value!!) {
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

    fun connect(device: WifiP2pDevice) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        checkPermission()
        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                wifiViewModel._peerConnecting.value = device
            }

            override fun onFailure(reason: Int) {
                makeToast("Echec de la connection à ${device.deviceName}")
            }
        })
    }


    private fun checkWifiP2pCapability() {
        val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        wifiViewModel._p2pSupported.value =
            (activity.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)
                    && (wifiManager?.isP2pSupported == true))
    }

    private fun checkPermission() {
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

    private fun createConnectionListener(): WifiP2pManager.ConnectionInfoListener {
        return WifiP2pManager.ConnectionInfoListener {
            makeToast("Démarrage de connection")
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

    private fun startServer() {
        WifiP2pServer(PORT_CONNECTIONS, sendOrReceive(), !wifiViewModel.isConnecting()).start()
    }

    private fun startClient(host: String) {
        WifiP2pClient(host, PORT_CONNECTIONS, sendOrReceive(), !wifiViewModel.isConnecting()).start()
    }

    fun chargeItems(items: List<Item>, itemSentCallback: ItemSentCallback) {
        this.items = items
        this.itemSentCallback = itemSentCallback
    }

    fun disconnect() {
        manager.removeGroup(channel, null)
    }

    fun onConnectionResult(success: Boolean) {
        //if callback exists, call it
        if (itemSentCallback != null) {
            //run that on UI thread in case an UI action is called on success/failure
            activity.runOnUiThread {
                if (success) itemSentCallback!!.onSuccess() else itemSentCallback!!.onFailure()
                //callback is called only once and then cleaned
                itemSentCallback = null
            }
        }
    }

    private fun sendOrReceive(): SocketAction {
        return if (wifiViewModel.isConnecting()) sentItemsAction() else receiveItemsAction()
    }

    private fun sentItemsAction(): SocketAction {
        return SocketAction { socket ->
            makeToast("Envois d'items en cours..", false)
            val serializedItems = items!!.parallelStream()
                    .map { SerializableItem(it) }
                    .collect(Collectors.toList())
            ObjectOutputStream(socket.getOutputStream()).writeObject(serializedItems)
        }
    }

    private fun receiveItemsAction(): SocketAction {
        return SocketAction { socket ->
            makeToast("Reception d'items en cours...", false)
            //we can ONLY receive a list of serializableitems in this configuration
            val serializableItemList = ObjectInputStream(socket.getInputStream()).readObject() as List<SerializableItem>
            //let the deserialization be out of the sockets managment
            Thread{
                //parallel deseialization (good if multiple bitmaps)
                val items = serializableItemList!!.parallelStream()
                    .map { it.getItem() }
                    .collect(Collectors.toList())
                activity.onReceiveItems(items)
            }.start()
        }
    }

    private fun makeToast(message: String, short: Boolean = true) {
        activity.runOnUiThread {
            Toast.makeText(activity, message, if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG)
                .show()
        }
    }
}