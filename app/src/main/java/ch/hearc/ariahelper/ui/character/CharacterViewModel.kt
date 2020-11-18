package ch.hearc.ariahelper.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.CharacterIdSpinnerContainer
import ch.hearc.ariahelper.models.persistence.CharacterPersistenceManager

/**
 * Character view model : Hold the current character for the views, talks with the persistence manager
 */
class CharacterViewModel : ViewModel() {
    public val _character = MutableLiveData<Character>().apply {
        value = CharacterPersistenceManager.getLastCharacter()
    }

    public val character: LiveData<Character> = _character
    /**
     * Change the selected character with the ID of the next Character
     */
    public fun changeCharacter(id : Int){
        _character.value = CharacterPersistenceManager.getCharacterByID(id)
    }

    /**
     * Create a new Character with the given name
     */
    public fun createAndSetCharacter(name : String){
        val character = Character(name)
        CharacterPersistenceManager.registerCharacter(character)
        _character.value = character
    }

    /**
     * Get all the stocked character names
     */
    public fun getAllCharacters() : ArrayList<CharacterIdSpinnerContainer> {
        return CharacterPersistenceManager.getAllCharacterNamesId()
    }
}