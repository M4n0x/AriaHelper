package ch.hearc.ariahelper.ui.loot.dm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ch.hearc.ariahelper.R

class ItemDetailsFragment : Fragment() {

    companion object {
        fun newInstance(): ItemDetailsFragment {
            return ItemDetailsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }
}