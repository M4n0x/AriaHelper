package ch.hearc.ariahelper.sensors.wifip2p

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.persistence.CharacterPersistenceManager

class WifiP2PViewModel : ViewModel() {
    val _peers = MutableLiveData<WifiP2pDeviceList>().apply {
        value = null
    }
    val peers: LiveData<WifiP2pDeviceList> = _peers

    val _connectedPeer = MutableLiveData<WifiP2pDevice>().apply {
        value = null
    }
    val connectedPeer: LiveData<WifiP2pDevice> = _connectedPeer
}