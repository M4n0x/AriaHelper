package ch.hearc.ariahelper.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Item
import ch.hearc.ariahelper.models.QUALITY
import ch.hearc.ariahelper.models.commonpool.ItemBasicPool
import ch.hearc.ariahelper.ui.character.CharacterViewModel
import ch.hearc.ariahelper.ui.loot.dm.LootViewModel
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddItemFragment : Fragment() {
    private val lootViewModel : LootViewModel by navGraphViewModels(R.id.mobile_navigation) {
        //defaultViewModelProviderFactory or the ViewModelProvider.Factory you are using.
        defaultViewModelProviderFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)

        view.inputQuality.adapter = ArrayAdapter<QUALITY>(this.requireContext(), android.R.layout.simple_spinner_item, QUALITY.values())

        view.btnSubmit.setOnClickListener{
            //ItemBasicPool.ITEMS.add(Item(view.inputName.text.toString(), view.inputDescription.text.toString(), view.inputQuality.selectedItemId.toInt(), "ic_menu_slideshow" ))
            lootViewModel.itemList.value!!.add(Item(view.inputName.text.toString(), view.inputDescription.text.toString(), view.inputQuality.selectedItemId.toInt(), "ic_menu_slideshow" ))
            view.findNavController().navigate(R.id.action_fragmentAddItem_to_nav_lootdm)
        }

        return view
    }

}