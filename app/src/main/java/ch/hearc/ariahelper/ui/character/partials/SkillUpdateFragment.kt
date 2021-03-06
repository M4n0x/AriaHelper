package ch.hearc.ariahelper.ui.character.partials

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Attribute
import ch.hearc.ariahelper.models.Skill
import ch.hearc.ariahelper.ui.character.CharacterViewModel
import kotlinx.android.synthetic.main.fragment_skill_update.*

/**
 * Skill creation/modification fragment
 */
class SkillUpdateFragment : Fragment() {
    private val args: SkillUpdateFragmentArgs by navArgs()
    private val characterViewModel : CharacterViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }
    private lateinit var skill : Skill

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val position = args.position
        if(position >= 0){
            //get skill from selecter character
            skill = characterViewModel.character.value!!.skillList[position]
        } else {
            //create new skill
            skill = Skill("nouveau talent", "Description", 50)
            characterViewModel._character.value!!.skillList.add(skill)
        }
        return inflater.inflate(R.layout.fragment_skill_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //display skill in fragment
        skillNameEdit.setText(skill.name)
        skillDescriptionEdit.setText(skill.description)
        skillValueNumberEdit.setText(skill.value.toString())

        buttonConfirm.setOnClickListener {
            //update skill with given values
            skill.name = skillNameEdit.text.toString()
            skill.description = skillDescriptionEdit.text.toString()
            skill.value = skillValueNumberEdit.text.toString().toInt()
            findNavController().navigateUp() //go back to stack
        }

        buttonDelete.setOnClickListener {
            characterViewModel._character.value!!.skillList.remove(skill)
            findNavController().navigateUp() //go back to stack
        }
    }
}