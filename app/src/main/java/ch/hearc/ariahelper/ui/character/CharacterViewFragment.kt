package ch.hearc.ariahelper.ui.character

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R
import kotlinx.android.synthetic.main.fragment_character_view.*
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.ui.character.adapters.AttributeRecViewAdapter
import ch.hearc.ariahelper.ui.character.adapters.SkillRecViewAdapter


/**
 * A simple [Fragment] subclass.
 * Use the [CharacterViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CharacterViewFragment : Fragment() {
    private lateinit var attributeAdapter : AttributeRecViewAdapter
    private lateinit var skillAdapter : SkillRecViewAdapter
    private val characterViewModel : CharacterViewModel by navGraphViewModels(R.id.mobile_navigation) {
        //defaultViewModelProviderFactory or the ViewModelProvider.Factory you are using.
        defaultViewModelProviderFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var currentCharacter = characterViewModel.character.value!!

        //set adapter to attribute RV
        attributeAdapter = AttributeRecViewAdapter(currentCharacter.attributeList)
        attributesRecyclerView!!.adapter = attributeAdapter

        //set adapter to skill RV
        skillAdapter = SkillRecViewAdapter(currentCharacter.skillList)
        skillsRecyclerView!!.adapter = skillAdapter

        characterViewModel.character.observe(viewLifecycleOwner, Observer {
            currentCharacter = characterViewModel.character.value!!
            attributeAdapter.changeList(currentCharacter.attributeList)
            skillAdapter.changeList(currentCharacter.skillList)
        })

        //TODO change later - put progress as WIP
        diceProgressBar.setProgress(10, true)

        //put characters in spinner
        //TODO link names of all characters here
        val characterNames = arrayListOf(currentCharacter.name, "Jeanne D'arc", "Grand-Jean", "Atlan")

        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            characterNames)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(characterNames[position] != currentCharacter.name){
                    Log.i("change", "onItemSelected: change character")
                    characterViewModel._character.postValue(Character(characterNames[position]))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCharacterSettings.setOnClickListener {
            val directions =
                CharacterViewFragmentDirections.actionNavCharacterToCharacterSettingsFragment()
            view.findNavController().navigate(directions)
        }
    }
}