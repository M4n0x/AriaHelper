package ch.hearc.ariahelper.ui.loot.dm

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
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

    private var columnCount = 1

    private val lootViewModel : LootViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Set the Recyclerview
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = ItemRecyclerViewAdapter(lootViewModel)
            }
        }

        return view
    }

}