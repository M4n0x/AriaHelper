package ch.hearc.ariahelper.ui.loot.modal

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.location.LocationManager
import android.widget.Toast
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.FragmentManager
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver

object WifiModalBuilder {
    private val dialogListener = createListener()

    fun buildAndShow(context: Context, fragmentManager: FragmentManager, tag: String): WifiP2PConnectionDialog ? {
        //verify that the wifi P2P is supported
        if(!WifiP2PReceiver.wifiViewModel.p2pSupported.value!!){
            Toast.makeText(
                context,
                "Your device is not compatible with wifiP2P",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }

        //verify that the wifi P2P is enable
        if(!WifiP2PReceiver.wifiViewModel.p2pEnabled.value!!){
            Toast.makeText(
                context,
                "Please enable Wifi p2p before going forward",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }

        //verify that the wifi P2P is activated (for discovery)
        if(!WifiP2PReceiver.wifiViewModel.p2pActivated.value!!){
            //Give a last chance to the user to activate it
            createDialogActivation(context)
            return null
        }

        //verify that the location is enabled
        if(!isLocationEnabled(context)){
            Toast.makeText(
                context,
                "Please enable Location before going forward",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }

        val modal = WifiP2PConnectionDialog()
        modal.show(fragmentManager, tag)
        return modal
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun createDialogActivation(context: Context){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Enable Wifi for item exchange ?")
                .setPositiveButton("Yes", dialogListener)
                .setNegativeButton("No", dialogListener)
                .show()
    }

    private fun createListener() : DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        WifiP2PReceiver.wifiViewModel._p2pActivated.value = true
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        //nothing to be done
                    }
                }
            }
    }
}