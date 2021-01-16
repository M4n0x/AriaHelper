package ch.hearc.ariahelper.ui.loot.modal

import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver
import kotlinx.android.synthetic.main.fragment_wifip2p_connection_modal.*


/**
 * Dialog to manage connection to other players
 */
class WifiP2PConnectionDialog : DialogFragment() {
    private val WIDTH_RATIO = 0.85
    private val HEIGHT_RATIO = 0.6
    private lateinit var adapter : WifiConnectionRecyclerViewAdapter
    private lateinit var wifiManager : WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wifiManager = requireContext().applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        val view = inflater.inflate(R.layout.fragment_wifip2p_connection_modal, container, false)

        // Init the devices adapter
        adapter = WifiConnectionRecyclerViewAdapter(WifiP2PReceiver.wifiViewModel.peers.value, this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //update modal list of peers
        WifiP2PReceiver.wifiViewModel.peers.observe(viewLifecycleOwner, {
            adapter.updateDeviceList(it)
        })

        //periodically update peerlist
        btnRefresh.setOnClickListener {
            refreshDiscovery()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        WifiP2PReceiver.wifiViewModel.searching.observe(viewLifecycleOwner, {
            progressBarSearch.visibility = if(it) View.VISIBLE else View.GONE
        })

        with(listDevices) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@WifiP2PConnectionDialog.adapter
        }

        //do a refresh of the discovery to have an updated list of devices
        // (not disconnected ones from 10min ago..)
        refreshDiscovery()
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * WIDTH_RATIO).toInt()
        val height = (resources.displayMetrics.heightPixels * HEIGHT_RATIO).toInt()
        dialog!!.window?.setLayout(width, height)
    }

    private fun refreshDiscovery(){
        WifiP2PReceiver.stopDiscovery()
        WifiP2PReceiver.discoverPeers()
    }
}