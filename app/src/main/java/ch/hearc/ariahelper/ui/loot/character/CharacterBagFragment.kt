package ch.hearc.ariahelper.ui.loot.character

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver
import ch.hearc.ariahelper.ui.character.CharacterViewModel
import ch.hearc.ariahelper.ui.loot.dm.LootViewModel
import ch.hearc.ariahelper.ui.loot.modal.WifiModalBuilder
import ch.hearc.ariahelper.ui.loot.modal.WifiP2PConnectionDialog
import kotlinx.android.synthetic.main.fragment_character_bag.*
import kotlinx.android.synthetic.main.fragment_share_dm_loot.view.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 * Use the [CharacterBagFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CharacterBagFragment : Fragment() {
    private val characterViewModel : CharacterViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }
    private val lootViewModel : LootViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // set itemlist to setup ItemFragment as it'll be used in the next navigation fragment
        lootViewModel._itemList.value = characterViewModel.character.value!!.itemList

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_character_bag, container, false)

        // on btn add click we change view
        view.btnAdd.setOnClickListener {
            view.findNavController().navigate(R.id.action_loot_to_fragmentAddItem)
        }

        view.btnNFC.setOnClickListener{
            try {
                WifiModalBuilder.buildAndShow(
                    requireContext(),
                    parentFragmentManager,
                    "Wifi P2P connection modal"
                )
                //TODO : remove items
                //clear selected items list
                //remove items from the character
            }catch (e : Exception){

            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //enable share button only when items have been selected
        lootViewModel.selectedItemList.observe(viewLifecycleOwner, {
            btnNFC.isEnabled = !lootViewModel.selectedItemList.value.isNullOrEmpty()
        })
        btnNFC.isEnabled = false
    }
}