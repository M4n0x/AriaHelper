package ch.hearc.ariahelper.ui.loot.shared

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R

/**
 * A simple [Fragment] subclass.
 * this fragment is representing a list of Item.
 */
class ItemFragment : Fragment() {
    private val lootViewModel : LootViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        val itemAdapter = ItemRecyclerViewAdapter(lootViewModel, requireContext())

        with(view as RecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }

        lootViewModel.itemList.observe(viewLifecycleOwner, {
            itemAdapter.onItemListUpdated()
        })

        return view
    }

}