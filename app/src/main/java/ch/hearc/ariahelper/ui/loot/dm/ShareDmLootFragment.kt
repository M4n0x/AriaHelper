package ch.hearc.ariahelper.ui.loot.dm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import ch.hearc.ariahelper.R
import kotlinx.android.synthetic.main.fragment_share_dm_loot.*
import kotlinx.android.synthetic.main.fragment_share_dm_loot.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [ShareDmLootFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShareDmLootFragment : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_share_dm_loot, container, false)

        view.btnAdd.setOnClickListener {
            view.findNavController().navigate(R.id.action_nav_lootdm_to_fragmentAddItem)
        }

        return view
    }

}