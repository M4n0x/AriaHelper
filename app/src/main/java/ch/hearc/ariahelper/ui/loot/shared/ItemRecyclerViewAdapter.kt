package ch.hearc.ariahelper.ui.loot.shared

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Item
import ch.hearc.ariahelper.models.enums.Quality
import ch.hearc.ariahelper.models.persistence.PicturePersistenceManager

/**
 * [RecyclerView.Adapter] that can display a [Item].
 * This class is used to display items in a list
 * It allows to multi select items
 */
class ItemRecyclerViewAdapter(
    private val lvm: LootViewModel,
    private val context: Context,
    private var showSelect: Boolean = false
) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {
    private val values : MutableList<Item> = lvm.itemList.value!!
    private val selected : MutableList<Item> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_item,
            parent,
            false
        )

        selected.clear() // clear selected items as a new view is created

        return ViewHolder(view)
    }

    /**
     * To be called on list update
     * Clear the selected items
     */
    fun onItemListUpdated(){
        selected.clear()
        lvm._selectedItemList.value = selected
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item : Item = values[position]
        holder.idView.text = item.name
        holder.contentView.text = Quality.getStringFromId(item.quality)?.let { context.getString(it) }
        holder.selectView.isChecked = false
        if (item.picture.isEmpty())
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bag_icon))
        else
            holder.imageView.setImageBitmap(PicturePersistenceManager.getBitmapFromFilename(item.picture))
        holder.selectView.visibility = if (showSelect) View.VISIBLE else View.GONE

        //On long click view we enabled multi select
        holder.itemView.setOnLongClickListener {
            showSelect = !showSelect
            this@ItemRecyclerViewAdapter.notifyDataSetChanged()
            return@setOnLongClickListener true
        }

        // on simple click two situation :
        // 1. Multi select is not enable : we show item details
        // 2. Multi select is enabled : we select item
        // This data aren't persisted between view creation on purpose
        // cause this seem more convenient for the user
        holder.itemView.setOnClickListener { view ->
            if (!showSelect) {
                val args = Bundle()
                args.putInt("position", position)
                view.findNavController().navigate(R.id.action_loot_to_fragmentLootDetail, args)
            } else {
                holder.selectView.isChecked = !holder.selectView.isChecked
                if (holder.selectView.isChecked) selected.add(item) else selected.remove(item)
                lvm._selectedItemList.value = selected
            }
        }

        //the entire item handles the click, the checkbox passes it through
        holder.selectView.isClickable = false
    }

    override fun getItemCount(): Int = values.size

    /**
     * ViewHolder, this class is used to hold item's information
     * and show it to the view
     */
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