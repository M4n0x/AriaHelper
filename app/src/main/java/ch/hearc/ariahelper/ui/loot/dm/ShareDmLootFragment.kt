package ch.hearc.ariahelper.ui.loot.dm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.persistence.LootPersistenceManager
import kotlinx.android.synthetic.main.fragment_share_dm_loot.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [ShareDmLootFragment.newInstance] factory method to
 * create an instance of this fragment.
 * This class is used for the DM loot view.
 */
class ShareDmLootFragment : Fragment() {
    private val lootViewModel : LootViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // set itemlist to setup ItemFragment as it'll be used in the next navigation fragment
        lootViewModel._itemList.value = LootPersistenceManager.getLoot()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_share_dm_loot, container, false)

        // on btn add click we change view
        view.btnAdd.setOnClickListener {
            view.findNavController().navigate(R.id.action_nav_lootdm_to_fragmentAddItem)
        }

        return view
    }

}