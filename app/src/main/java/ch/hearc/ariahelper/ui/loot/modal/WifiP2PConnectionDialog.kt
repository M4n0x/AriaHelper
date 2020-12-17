package ch.hearc.ariahelper.ui.loot.modal

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.ui.loot.modal.dummy.DummyContent

/**
 * A fragment representing a list of Items.
 */
class WifiP2PConnectionDialog : DialogFragment() {
    private val WIDTH_RATIO = 0.85
    private val HEIGHT_RATIO = 0.6

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner);
        val view = inflater.inflate(R.layout.fragment_wifip2p_connection_modal, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = WifiConnectionRecyclerViewAdapter(DummyContent.ITEMS)
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * WIDTH_RATIO).toInt()
        val height = (resources.displayMetrics.heightPixels * HEIGHT_RATIO).toInt()
        dialog!!.window?.setLayout(width, height)
    }
}