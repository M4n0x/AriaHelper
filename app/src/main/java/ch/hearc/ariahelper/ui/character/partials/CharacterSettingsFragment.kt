package ch.hearc.ariahelper.ui.character.partials

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.persistence.CharacterPersistenceManager
import ch.hearc.ariahelper.ui.character.CharacterViewModel
import kotlinx.android.synthetic.main.fragment_character_settings.*
import kotlinx.android.synthetic.main.fragment_character_settings.buttonConfirm

/**
 * Simple Character Settings fragment
 */
class CharacterSettingsFragment : Fragment() {
    private val characterViewModel : CharacterViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val character = characterViewModel.character.value

        //display character settings
        editTextCharacterName.setText(character?.name)
        textViewIDSecond.text = character?.id.toString()

        buttonConfirm.setOnClickListener{
            character?.name = editTextCharacterName.text.toString()
            findNavController().navigateUp() //go back to stack
        }

        buttonDelete.setOnClickListener{
            //delete character and change selected character
            CharacterPersistenceManager.deleteCharacterById(character?.id!!)
            characterViewModel._character.postValue(CharacterPersistenceManager.getLastCharacter())
            findNavController().navigateUp() //go back to stack
        }
    }

}