package ch.hearc.ariahelper.sensors.wifip2p

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat


object WifiP2PReceiver : BroadcastReceiver() {
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    private lateinit var activity: Activity
    val wifiViewModel = WifiP2PViewModel()

    fun init(_channel: WifiP2pManager.Channel,
              _manager: WifiP2pManager,
             _activity: Activity){
        channel = _channel
        manager = _manager
        activity = _activity

        checkPermission()
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Check to see if Wi-Fi is enabled and notify appropriate activity
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                wifiViewModel._p2pEnabled.value = (state ==  WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                Toast.makeText(context, "Discovered peers :-)", Toast.LENGTH_SHORT).show()
                wifiViewModel._searching.value = false
                checkPermission()
                manager.requestPeers(channel) { peers: WifiP2pDeviceList? ->
                    Log.d("TAG", "onReceive: ${peers?.deviceList}")
                    wifiViewModel._peers.value = peers
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Respond to new connection or disconnections
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
                when(reasonCode){
                    WifiP2pManager.P2P_UNSUPPORTED -> Log.d("info","p2p unsupported")
                    WifiP2pManager.ERROR -> Log.d("info","p2p error")
                    WifiP2pManager.BUSY -> Log.d("info","p2p b")
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

    fun connect(device : WifiP2pDevice){
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        checkPermission()
        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                wifiViewModel._connectedPeer.value = device
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(activity, "Failing to connect to ${device.deviceName}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun checkPermission(){
        //check permission for later use
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(activity, permissions,0)
        }
    }
}