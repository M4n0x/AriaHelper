package ch.hearc.ariahelper.ui.loot.modal

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.DialogInterface
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver
import kotlinx.android.synthetic.main.fragment_wifip2p_connection_modal.*


/**
 * A fragment representing a list of Items.
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
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner);
        val view = inflater.inflate(R.layout.fragment_wifip2p_connection_modal, container, false)

        // Set the adapter
        adapter = WifiConnectionRecyclerViewAdapter(WifiP2PReceiver.wifiViewModel.peers.value)
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = this@WifiP2PConnectionDialog.adapter
            }
        }
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
            if(WifiP2PReceiver.wifiViewModel.searching.value!!){
                WifiP2PReceiver.stopDiscovery()
            } else {
                WifiP2PReceiver.discoverPeers()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        WifiP2PReceiver.wifiViewModel.searching.observe(viewLifecycleOwner, {
            if(it){
                progressBarSearch.visibility = View.VISIBLE
                btnRefresh.text = resources.getString(R.string.stop_refresh_string)
            } else {
                progressBarSearch.visibility = View.GONE
                btnRefresh.text = resources.getString(R.string.refresh_string)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * WIDTH_RATIO).toInt()
        val height = (resources.displayMetrics.heightPixels * HEIGHT_RATIO).toInt()
        dialog!!.window?.setLayout(width, height)
    }

    override fun onDismiss(dialog: DialogInterface) {
        WifiP2PReceiver.stopDiscovery()
        super.onDismiss(dialog)
    }
}