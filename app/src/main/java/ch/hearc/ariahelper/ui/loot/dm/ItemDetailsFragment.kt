package ch.hearc.ariahelper.ui.loot.dm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Item
import ch.hearc.ariahelper.models.persistence.PicturePersistenceManager
import kotlinx.android.synthetic.main.fragment_item_details.view.*

/**
 * This class is used to display item details
 */
class ItemDetailsFragment : Fragment() {
    private val lootViewModel : LootViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_item_details, container, false)
        val title : TextView = view.findViewById(R.id.itemTitle)
        val viewHolder = ViewHolder(view)

        // data is an argument's navigation action, we get the item from there
        val position : Int? = arguments?.getInt("position")
        if (position != null) {
            val item : Item = lootViewModel.itemList.value!![position]
            //if item not empty we populate data in the viewHolder
            with(viewHolder) {
                titleView.text = item.name
                descriptionView.text = item.description
                if (item.picture != null && item.picture != "")
                    imageView.setImageBitmap(PicturePersistenceManager.getBitmapFromFilename(item.picture))
            }
        }

        view.btnDelete.setOnClickListener {
            if (position != null) {
                lootViewModel.itemList.value!!.removeAt(position)
            }
            findNavController().navigateUp()
        }

        view.btnEdit.setOnClickListener {
            if (position != null) {
                val args = Bundle()
                args.putInt("position", position)
                view.findNavController().navigate(R.id.action_fragmenttLootDetail_to_fragmentAddItem, args)
            }
        }

        return view
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.itemTitle)
        val descriptionView: TextView = view.findViewById(R.id.itemDescription)
        val imageView: ImageView = view.findViewById(R.id.itemImg)

        override fun toString(): String {
            return super.toString() + " '" + titleView.text + "'"
        }
    }
}