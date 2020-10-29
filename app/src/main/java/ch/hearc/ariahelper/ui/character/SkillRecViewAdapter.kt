package ch.hearc.ariahelper.ui.character

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Skill
import kotlinx.android.synthetic.main.skill_recycler_view_row.view.*

class SkillRecViewAdapter (private val skills: MutableList<Skill>) :
    RecyclerView.Adapter<SkillRecViewAdapter.SkillViewHolder>() {

    /// Internal hodler class
    class SkillViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var skill: Skill? = null

        fun bindAttribute(skill : Skill) {
            this.skill = skill
            view.skillNameTextView.text = skill.name
            view.skillDescriptionTextView.text = skill.description
            view.skillValueNumberView.text = skill.value.toString()
        }

        fun bindCustomCase(name : String, description : String, value : String){
            this.skill = null
            view.skillNameTextView.text = name
            view.skillDescriptionTextView.text = description
            view.skillValueNumberView.text = value
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SkillViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(
            R.layout.skill_recycler_view_row,
            parent,
            false
        )
        return SkillViewHolder(inflatedView)
    }

    override fun onBindViewHolder(
        holder: SkillViewHolder,
        position: Int
    ) {
        when(position){
            in 0 until skills.size -> holder.bindAttribute(skills[position])
            skills.size -> holder.bindCustomCase("ajouter", "click pour ajouter un nouvel attribut", "+")
        }
    }

    override fun getItemCount(): Int {
        return skills.size + 1
    }

}