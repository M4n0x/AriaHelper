package ch.hearc.ariahelper.ui.loot.dm

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Item


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class ItemRecyclerViewAdapter(
    private val values: List<Item>,
    private val context: Context,
    private var showSelect: Boolean,
) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    private val selected : MutableList<Item> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_item,
            parent,
            false
        )

        selected.clear()

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item : Item = values[position]
        holder.idView.text = item.name
        holder.contentView.text = item.quality.toString()
        holder.imageView.setImageDrawable(this.getImage(context, item.picture))
        holder.selectView.visibility = if (showSelect) View.VISIBLE else View.GONE

        holder.itemView.setOnLongClickListener {
            showSelect = !showSelect
            this@ItemRecyclerViewAdapter.notifyDataSetChanged()
            return@setOnLongClickListener true
        }

        holder.itemView.setOnClickListener { view ->
            if (!showSelect) {
                val args = Bundle()
                args.putParcelable("data", item)
                view.findNavController().navigate(R.id.action_nav_lootdm_to_fragmenttLootDetail, args)
            } else {
                holder.selectView.isChecked = !holder.selectView.isChecked
                if (holder.selectView.isChecked) selected.add(item) else selected.remove(item)
                Log.d("test", selected.toString())
            }
        }

    }

    private fun getSelected() {

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
        val selectView: CheckBox = view.findViewById(R.id.itemSelected)

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}