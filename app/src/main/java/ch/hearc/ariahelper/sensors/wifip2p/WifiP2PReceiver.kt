package ch.hearc.ariahelper.sensors.wifip2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.widget.Toast


class WifiP2PReceiver(
    private val channel: WifiP2pManager.Channel,
    private val manager: WifiP2pManager,
    private val context: Context
) : BroadcastReceiver() {

    init {
        instance = this
    }

    companion object {
        //unique viewmodel
        public val wifiViewModel : WifiP2PViewModel = WifiP2PViewModel()

        //unique instance of wifi manager (singleton)
        private lateinit var instance : WifiP2PReceiver
        public fun getInstance() : WifiP2PReceiver {
            return instance
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                when (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        Toast.makeText(context, "Wifi peer to peer enabled", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Toast.makeText(context, "Wifi peer to peer disabled", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(channel) { peers: WifiP2pDeviceList? ->
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

    public fun discoverPeers(){
        manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(context, "Discovery successfull", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(reasonCode: Int) {
                Toast.makeText(context, "Discovery failure", Toast.LENGTH_SHORT).show()
            }
        })
    }

    public fun connect(device : WifiP2pDevice){
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                wifiViewModel._connectedPeer.value = device
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(context, "Failing to connect to {device.deviceName}", Toast.LENGTH_LONG).show()
            }
        })


    }



}