package ch.hearc.ariahelper.ui.character

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
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
import ch.hearc.ariahelper.sensors.AcceleroManager
import ch.hearc.ariahelper.ui.character.adapters.AttributeRecViewAdapter
import ch.hearc.ariahelper.ui.character.adapters.SkillRecViewAdapter
import ch.hearc.ariahelper.ui.fragments.MoneyValueFragment


class CharacterViewFragment : Fragment() {
    // Attributes
    private val NEW_CHARACTER_NAME = "Nouveau personnage"
    private val ADD_CHARACTER_NAME = "Ajouter"
    private lateinit var attributeAdapter : AttributeRecViewAdapter
    private lateinit var skillAdapter : SkillRecViewAdapter
    private lateinit var characterNamesID : ArrayList<CharacterIdSpinnerContainer>
    private lateinit var acceleroManager: AcceleroManager
    //Viewmodels : Get them via the navGraph (viewmodels are binded to the activity nav graph, and alive during its whole life)
    private val characterViewModel : CharacterViewModel by navGraphViewModels(R.id.mobile_navigation) {
        //defaultViewModelProviderFactory or the ViewModelProvider.Factory you are using.
        defaultViewModelProviderFactory
    }
    private val characterComponentViewModel : CharacterComponentViewModel by navGraphViewModels(R.id.mobile_navigation) {
        //defaultViewModelProviderFactory or the ViewModelProvider.Factory you are using.
        defaultViewModelProviderFactory
    }

    /* ----- FRAGMENT LIFECYCLE RELATED FUNCTIONS ----- */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var currentCharacter = characterViewModel.character.value!!

        //init character observer
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
        characterComponentViewModel.DCUSTOM.observe(viewLifecycleOwner,{textViewDCustomResult.text = "D$it"})
        //attach observer to progress bar
        characterComponentViewModel.Progress.observe(viewLifecycleOwner,{diceProgressBar.setProgress(it, true)})

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_view, container, false)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        selectSpinnerPlayer() //put the good selected player BEFORE the view is created (risk of invalid selection)
        acceleroManager = AcceleroManager(characterComponentViewModel, requireContext())
        Thread(acceleroManager).start()
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set button action (go to settings)
        buttonCharacterSettings.setOnClickListener {
            val directions =
                CharacterViewFragmentDirections.actionNavCharacterToCharacterSettingsFragment()
            view.findNavController().navigate(directions)
        }
        //initialize view components
        initAdapters()
        initSpinner()
        selectSpinnerPlayer()
        initListeners()

        //init simple values
        diceProgressBar.setProgress(characterComponentViewModel.Progress.value!!, true)
        textViewDCustom.setText(characterComponentViewModel.DCUSTOMREQ.value.toString());
    }

    override fun onPause() {
        acceleroManager.stopSensor()
        super.onPause()
    }

    /**
     * Init the adapters for the 2 recycler view : Attribute and skills
     */
    private fun initAdapters(){
        //get character live data from view model
        var currentCharacter = characterViewModel.character.value!!

        //set adapter to attribute RV
        attributeAdapter = AttributeRecViewAdapter(currentCharacter.attributeList)
        attributesRecyclerView!!.adapter = attributeAdapter

        //set adapter to skill RV
        skillAdapter = SkillRecViewAdapter(currentCharacter.skillList)
        skillsRecyclerView!!.adapter = skillAdapter
    }

    private fun initSpinner(){
        //put characters in spinner (characters are wrapped in a (name, id) wrapper class : @class CharacterIdSpinnerContainer)
        characterNamesID = characterViewModel.getAllCharacters()
        //"add" character line as last : invalid ID se we know it's not a real character
        characterNamesID.add(CharacterIdSpinnerContainer(ADD_CHARACTER_NAME, -1))

        //set adaptor to spinner
        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            characterNamesID)

        //set spinner behavior on item select
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //switch over "idSelected", the id of the choosen character
                val currentCharacterID = characterViewModel.character.value!!.id
                when(val idSelected = characterNamesID[position].id){
                    -1 -> { //invalid id -> "add" line
                        characterViewModel.createAndSetCharacter(NEW_CHARACTER_NAME)
                        Toast.makeText(context, "Character created !", Toast.LENGTH_SHORT).show()
                        //JETPACK navigation action (navigate to "settings" to customize the new character)
                        val directions =
                            CharacterViewFragmentDirections.actionNavCharacterToCharacterSettingsFragment()
                        this@CharacterViewFragment.findNavController().navigate(directions)
                    }
                    currentCharacterID -> { /*nothing to be done :-)*/ }
                    else -> {
                        characterViewModel.changeCharacter(idSelected)
                    }
                }
            }
            //Nothing to be done
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * Init the different listeners of the view
     */
    private fun initListeners(){
        //"level" field binded to player level
        levelTextEdit.doAfterTextChanged {
            if(it!=null && !it.isEmpty()){
                characterViewModel.character.value!!.level = it.toString().toInt()
            }
        }

        //Link custom dice text edit to DCustom
        textViewDCustom.doAfterTextChanged {
            if (it != null && !it.isEmpty()) {
                characterComponentViewModel._DCUSTOMREQ.value = it.toString().toInt()
            }
        }
    }

    /**
     * Put the correct selected character on the spinner
     */
    private fun selectSpinnerPlayer(){
        val id = characterViewModel.character.value!!.id
        for(i in 0..characterNamesID.size){
            if (characterNamesID[i].id == id){
                spinner.setSelection(i)
                break
            }
        }
    }
}