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
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R
import kotlinx.android.synthetic.main.fragment_character_view.*
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.CharacterPersistenceManager
import ch.hearc.ariahelper.ui.character.adapters.AttributeRecViewAdapter
import ch.hearc.ariahelper.ui.character.adapters.SkillRecViewAdapter


class CharacterViewFragment : Fragment() {
    private val NEW_CHARACTER_NAME = "Nouveau personnage"
    private lateinit var attributeAdapter : AttributeRecViewAdapter
    private lateinit var skillAdapter : SkillRecViewAdapter
    private var currentPosition : Int ? = null
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

        //TODO change later - put progress as WIP with accelerometer
        diceProgressBar.setProgress(10, true)

        //put characters in spinner
        val characterNames = CharacterPersistenceManager.getAllCharacterNames()
        currentPosition = characterNames.indexOf(currentCharacter.name)
        characterNames.add(NEW_CHARACTER_NAME)

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
                when(position){
                    currentPosition -> { /*nothing to be done :-)*/ }
                    characterNames.size-1 -> {
                        val character = Character(NEW_CHARACTER_NAME)
                        CharacterPersistenceManager.registerCharacter(character)
                        val directions =
                            CharacterViewFragmentDirections.actionNavCharacterToCharacterSettingsFragment()
                        characterViewModel._character.postValue(character)
                        this@CharacterViewFragment.findNavController().navigate(directions)
                    }
                    else -> {
                        currentPosition = position
                        characterViewModel._character.postValue(CharacterPersistenceManager.getCharacterByPosition(currentPosition!!))
                    }
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