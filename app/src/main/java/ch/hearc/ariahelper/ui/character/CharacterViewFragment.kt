package ch.hearc.ariahelper.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R
import kotlinx.android.synthetic.main.fragment_character_view.*
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.CharacterIdSpinnerContainer
import ch.hearc.ariahelper.models.CharacterPersistenceManager
import ch.hearc.ariahelper.ui.character.adapters.AttributeRecViewAdapter
import ch.hearc.ariahelper.ui.character.adapters.SkillRecViewAdapter
import ch.hearc.ariahelper.ui.fragments.MoneyValueFragment


class CharacterViewFragment : Fragment() {
    private val NEW_CHARACTER_NAME = "Nouveau personnage"
    private val ADD_CHARACTER_NAME = "Ajouter"
    private lateinit var attributeAdapter : AttributeRecViewAdapter
    private lateinit var skillAdapter : SkillRecViewAdapter
    private lateinit var characterNamesID : ArrayList<CharacterIdSpinnerContainer>
    private val characterViewModel : CharacterViewModel by navGraphViewModels(R.id.mobile_navigation) {
        //defaultViewModelProviderFactory or the ViewModelProvider.Factory you are using.
        defaultViewModelProviderFactory
    }
    private val characterComponentViewModel : CharacterComponentViewModel by navGraphViewModels(R.id.mobile_navigation) {
        //defaultViewModelProviderFactory or the ViewModelProvider.Factory you are using.
        defaultViewModelProviderFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var currentCharacter = characterViewModel.character.value!!

        //init observer
        characterViewModel.character.observe(viewLifecycleOwner, Observer {
            currentCharacter = characterViewModel.character.value!!
            attributeAdapter.changeList(currentCharacter.attributeList)
            skillAdapter.changeList(currentCharacter.skillList)
            val fragMoney = childFragmentManager.findFragmentById(R.id.fragmentCharacterMoney) as MoneyValueFragment
            fragMoney.linkToPlayer(currentCharacter)
            levelTextEdit.setText(currentCharacter.level.toString())
        })

        //attach observers to dice values
        characterComponentViewModel.D6.observe(viewLifecycleOwner,{textViewD6Result.text = "D$it"})
        characterComponentViewModel.D10.observe(viewLifecycleOwner,{textViewD10Result.text = "D$it"})
        characterComponentViewModel.D100.observe(viewLifecycleOwner,{textViewD100Result.text = "D$it"})
        //attach observer to progress bar
        characterComponentViewModel.Progress.observe(viewLifecycleOwner,{})

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_view, container, false)
    }

    private fun initLists(){
        var currentCharacter = characterViewModel.character.value!!

        //set adapter to attribute RV
        attributeAdapter = AttributeRecViewAdapter(currentCharacter.attributeList)
        attributesRecyclerView!!.adapter = attributeAdapter

        //set adapter to skill RV
        skillAdapter = SkillRecViewAdapter(currentCharacter.skillList)
        skillsRecyclerView!!.adapter = skillAdapter
    }

    private fun initSpinner(){
        //put characters in spinner
        characterNamesID = CharacterPersistenceManager.getAllCharacterNamesId()
        characterNamesID.add(CharacterIdSpinnerContainer(ADD_CHARACTER_NAME, -1))

        //set adaptor
        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            characterNamesID)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(val idSelected = characterNamesID[position].id){
                    -1 -> {
                        val character = Character(NEW_CHARACTER_NAME)
                        CharacterPersistenceManager.registerCharacter(character)
                        val directions =
                            CharacterViewFragmentDirections.actionNavCharacterToCharacterSettingsFragment()
                        characterViewModel._character.postValue(character)
                        this@CharacterViewFragment.findNavController().navigate(directions)
                    }

                    characterViewModel.character.value!!.id -> { /*nothing to be done :-)*/ }

                    else -> {
                        characterViewModel._character.postValue(CharacterPersistenceManager.getCharacterByID(idSelected))
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initListeners(){
        levelTextEdit.doAfterTextChanged {
            if(it!=null && !it.isEmpty()){
                characterViewModel.character.value!!.level = it.toString().toInt()
            }
        }
    }

    private fun selectSpinnerPlayer(){
        val id = characterViewModel.character.value!!.id
        for(i in 0..characterNamesID.size){
            if (characterNamesID[i].id == id){
                spinner.setSelection(i)
                break
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        selectSpinnerPlayer()
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCharacterSettings.setOnClickListener {
            val directions =
                CharacterViewFragmentDirections.actionNavCharacterToCharacterSettingsFragment()
            view.findNavController().navigate(directions)
        }

        initLists()
        initSpinner()
        selectSpinnerPlayer()
        initListeners()

        //TODO change later - put progress as WIP with accelerometer
        diceProgressBar.setProgress(10, true)
    }
}