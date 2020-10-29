package ch.hearc.ariahelper.ui.character

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Attribute
import kotlinx.android.synthetic.main.attribute_recycler_view_column.view.*

class AttributeRecViewAdapter(private val attributes: MutableList<Attribute>) :
    RecyclerView.Adapter<AttributeRecViewAdapter.AttributeViewHolder>() {

    /// Internal hodler class
    class AttributeViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var attribute: Attribute? = null
        private var position: Int? = null

        init{
            view.setOnClickListener {
                val directions = CharacterViewFragmentDirections.actionNavCharacterToAttributeUpdateFragment(position!!)
                view.findNavController().navigate(directions)
            }
        }

        fun bindAttribute(attribute : Attribute, position: Int) {
            this.attribute = attribute
            this.position = position
            view.nameTextView.text = attribute.name
            view.valueEdit.text = attribute.value.toString()
        }

        fun bindCustomCase(name : String, value : String){
            this.attribute = null
            this.position = -1
            view.nameTextView.text = name
            view.valueEdit.text = value
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttributeViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(
            R.layout.attribute_recycler_view_column,
            parent,
            false
        )
        return AttributeViewHolder(inflatedView)
    }

    override fun onBindViewHolder(
        holder: AttributeViewHolder,
        position: Int
    ) {
        when(position){
            in 0 until attributes.size -> holder.bindAttribute(attributes[position], position)
            attributes.size -> holder.bindCustomCase("click", "ajouter")
        }
    }

    override fun getItemCount(): Int {
        return attributes.size + 1
    }

}