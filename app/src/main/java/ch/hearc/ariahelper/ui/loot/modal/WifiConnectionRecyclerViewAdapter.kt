package ch.hearc.ariahelper.ui.loot.modal

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.hearc.ariahelper.R

class WifiConnectionRecyclerViewAdapter(
    private var values: WifiP2pDeviceList ?
) : RecyclerView.Adapter<WifiConnectionRecyclerViewAdapter.ViewHolder>() {

    fun updateDeviceList(deviceList : WifiP2pDeviceList ?){
        //values = deviceList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.wifip2p_peer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values!!.deviceList!!.elementAt(position)!!
        holder.idView.text = item.deviceAddress
        holder.contentView.text = item.deviceName
    }

    override fun getItemCount(): Int {
        Log.d("IN ADAPTER", "getItemCount: ${values?.deviceList?.size ?: 0}")
        return values?.deviceList?.size ?: 0
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.item_number)
        val contentView: TextView = view.findViewById(R.id.content)

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}