package ch.hearc.ariahelper.ui.loot.dm

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Item

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

        val view = inflater.inflate(R.layout.fragment_item_details, container, false)
        val title : TextView = view.findViewById(R.id.itemTitle)
        val viewHolder: ViewHolder = ViewHolder(view)

        val item : Item? = arguments?.getParcelable("data")
        if (item != null) {
            title.text = item.name
            with(viewHolder) {
                titleView.text = item.name
                descriptionView.text = item.description
                imageView.setImageDrawable(getImage(requireContext(), item.picture))
            }
        }

        return view
    }

    private fun getImage(c: Context, ImageName: String?): Drawable? {
        return c.resources.getDrawable(
            c.resources.getIdentifier(
                ImageName,
                "drawable",
                c.packageName
            )
        )
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