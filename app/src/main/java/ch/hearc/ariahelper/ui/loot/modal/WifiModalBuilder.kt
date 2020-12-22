package ch.hearc.ariahelper.ui.loot.modal

import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.FragmentManager
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver

object WifiModalBuilder {
    fun buildAndShow(context : Context, fragmentManager : FragmentManager, tag : String){
        //verify that the wifi P2P is supported
        if(!WifiP2PReceiver.wifiViewModel.p2pSupported.value!!){
            Toast.makeText(context, "Your device is not compatible with wifiP2P", Toast.LENGTH_SHORT).show()
            return
        }

        //verify that the wifi P2P is enable
        if(!WifiP2PReceiver.wifiViewModel.p2pEnabled.value!!){
            Toast.makeText(context, "Please enable Wifi p2p before going forward", Toast.LENGTH_SHORT).show()
            return
        }

        //verify that the location is enabled
        if(!isLocationEnabled(context)){
            Toast.makeText(context, "Please enable Location before going forward", Toast.LENGTH_SHORT).show()
            return
        }

        WifiP2PConnectionDialog().show(fragmentManager, tag)
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }
}