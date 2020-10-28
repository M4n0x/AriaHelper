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
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.ariahelper.MainActivity
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Item


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class ItemRecyclerViewAdapter(
    private val values: List<Item>,
    private val context: Context,
    private val source: Fragment
) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_item,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item : Item = values[position]
        holder.idView.text = item.name
        holder.contentView.text = item.quality.toString()
        holder.imageView.setImageDrawable(this.getImage(context, item.picture))

        holder.itemView.setOnClickListener { view ->
            val frag = ItemDetailsFragment()
            val args = Bundle()
            args.putParcelable("data", item)
            frag.arguments = args

            view.findNavController().navigate(R.id.action_nav_lootdm_to_fragmenttLootDetail, args)
        }
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

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.itemName)
        val contentView: TextView = view.findViewById(R.id.ItemQuality)
        val imageView: ImageView = view.findViewById(R.id.itemImage)

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}