package ch.hearc.ariahelper.ui.character

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Attribute
import kotlinx.android.synthetic.main.attribute_recycler_view.view.*

class AttributeRecViewAdapter(private val attributes: MutableList<Attribute>) :
    RecyclerView.Adapter<AttributeRecViewAdapter.AttributeViewHolder>() {

    class AttributeViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var attribute: Attribute? = null

        fun bindAttribute(attribute : Attribute) {
            this.attribute = attribute
            view.nameTextView.text = attribute.name
            view.valueTextView.text = attribute.value.toString()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttributeRecViewAdapter.AttributeViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(
            R.layout.attribute_recycler_view,
            parent,
            false
        )
        return AttributeViewHolder(inflatedView)
    }

    override fun onBindViewHolder(
        holder: AttributeRecViewAdapter.AttributeViewHolder,
        position: Int
    ) {
        holder.bindAttribute(attributes[position])
    }

    override fun getItemCount(): Int {
        return attributes.size
    }

}