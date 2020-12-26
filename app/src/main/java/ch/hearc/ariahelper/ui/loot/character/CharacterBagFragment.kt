package ch.hearc.ariahelper.ui.loot.character

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.MainActivity
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.sensors.wifip2p.ItemSentCallback
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver
import ch.hearc.ariahelper.ui.character.CharacterViewModel
import ch.hearc.ariahelper.ui.loot.dm.LootViewModel
import ch.hearc.ariahelper.ui.loot.modal.WifiModalBuilder
import ch.hearc.ariahelper.ui.loot.modal.WifiP2PConnectionDialog
import kotlinx.android.synthetic.main.fragment_character_bag.*
import kotlinx.android.synthetic.main.fragment_share_dm_loot.view.*

/**
 * A simple [Fragment] subclass.
 */
class CharacterBagFragment : Fragment() {
    private val characterViewModel : CharacterViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }
    private val lootViewModel : LootViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }
    private var modal : WifiP2PConnectionDialog ? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // set itemlist to setup ItemFragment as it'll be used in the next navigation fragment
        lootViewModel._itemList.value = characterViewModel.character.value!!.itemList
        //automatically follow the list if the character changes
        characterViewModel.character.observe(viewLifecycleOwner, {
            lootViewModel._itemList.value = characterViewModel.character.value!!.itemList
        })

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_character_bag, container, false)

        // on btn add click we change view
        view.btnAdd.setOnClickListener {
            view.findNavController().navigate(R.id.action_loot_to_fragmentAddItem)
        }

        view.btnNFC.setOnClickListener{
            //prepare items to send
            val sentList = lootViewModel.selectedItemList.value!!

            //charge items into the receiver, prepare callback in case of success/failure
            WifiP2PReceiver.chargeItems(sentList, object : ItemSentCallback {
                override fun onSuccess() {
                    vibrateAndConfirmSent()
                    val newItemList = lootViewModel.itemList.value!!
                    newItemList.removeAll(sentList)
                    lootViewModel._itemList.value = newItemList
                }

                override fun onFailure() {
                    Toast.makeText(
                        activity,
                        "Error : Items were not sent !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            //start connection modal, which call the receiver to send the items
            modal = WifiModalBuilder.buildAndShow(
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
            btnNFC.isEnabled = !lootViewModel.selectedItemList.value.isNullOrEmpty()
        })
        //no item is selected from start : This button is natively disabled
        btnNFC.isEnabled = false
    }

    private fun vibrateAndConfirmSent(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Envois d'item")
                .setMessage("Les objets sont envoyés !")
                .setPositiveButton("Ok", null)
        (activity as MainActivity).vibratePhone()
        builder.show()
    }
}