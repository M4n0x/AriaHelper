package ch.hearc.ariahelper.sensors.wifip2p

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WifiP2PViewModel : ViewModel() {
    val _peers = MutableLiveData<WifiP2pDeviceList>().apply {
        value = null
    }
    val peers: LiveData<WifiP2pDeviceList> = _peers

    val _peerConnecting = MutableLiveData<WifiP2pDevice>().apply {
        value = null
    }
    val peerConnecting: LiveData<WifiP2pDevice> = _peerConnecting

    val _searching = MutableLiveData<Boolean>().apply {
        value = false
    }
    val searching: LiveData<Boolean> = _searching

    val _p2pEnabled = MutableLiveData<Boolean>().apply {
        value = true
    }
    val p2pEnabled: LiveData<Boolean> = _p2pEnabled

    val _p2pSupported = MutableLiveData<Boolean>().apply {
        value = true
    }
    val p2pSupported: LiveData<Boolean> = _p2pSupported

    val _p2pActivated = MutableLiveData<Boolean>().apply {
        value = true
    }
    val p2pActivated: LiveData<Boolean> = _p2pActivated

    fun canP2PActivate() : Boolean {
        return (p2pSupported.value == true) && (p2pEnabled.value == true)
    }

    fun isConnecting(): Boolean{
        return peerConnecting.value != null
    }
}