package ch.hearc.ariahelper.ui.loot.dm

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.MainActivity
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.persistence.LootPersistenceManager
import ch.hearc.ariahelper.sensors.wifip2p.ItemSentCallback
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver
import ch.hearc.ariahelper.ui.loot.modal.WifiModalBuilder
import ch.hearc.ariahelper.ui.loot.shared.LootViewModel
import kotlinx.android.synthetic.main.fragment_character_bag.*
import kotlinx.android.synthetic.main.fragment_character_bag.view.*
import kotlinx.android.synthetic.main.fragment_share_dm_loot.*
import kotlinx.android.synthetic.main.fragment_share_dm_loot.view.*

/**
 * CharacterBagFragment : Top-level  class
 *
 * Display the current saved treasure and offer to add to it or give it
 *
 * Uses the item fragment with the saved DM item list
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
        view.layoutDmAdd.setOnClickListener {
            view.findNavController().navigate(R.id.action_loot_to_fragmentAddItem)
        }

        view.layoutDmTransfer.setOnClickListener {
            //prepare items to send
            val sentList = lootViewModel.selectedItemList.value!!

            //charge items into the receiver, prepare callback in case of success/failure
            WifiP2PReceiver.chargeItems(sentList, object : ItemSentCallback {
                override fun onSuccess() {
                    vibrateAndDisplay("Envois: succès","Le trésor est envoyé !")
                    val newItemList = lootViewModel.itemList.value!!
                    newItemList.removeAll(sentList)
                    lootViewModel._itemList.value = newItemList
                }

                override fun onFailure() {
                    vibrateAndDisplay("Envois: échec","Le trésor n'a pas pu être envoyé !")
                }
            })

            //start connection modal, which call the receiver to send the items
            WifiModalBuilder.buildAndShow(
                requireContext(),
                parentFragmentManager,
                "Wifi P2P connection modal"
            )
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //enable share button only when item(s) have been selected
        lootViewModel.selectedItemList.observe(viewLifecycleOwner, {
            btnDmTransfer.isEnabled = !lootViewModel.selectedItemList.value.isNullOrEmpty()
        })
        //no item is selected from start : This button is natively disabled
        btnDmTransfer.isEnabled = false
    }

    private fun vibrateAndDisplay(title : String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok", null)
            .show()
        (activity as MainActivity?)?.vibratePhone()
    }
}