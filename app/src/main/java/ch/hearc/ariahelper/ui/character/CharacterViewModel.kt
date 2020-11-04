package ch.hearc.ariahelper.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.CharacterPersistenceManager

class CharacterViewModel : ViewModel() {
    public val _character = MutableLiveData<Character>().apply {
        value = CharacterPersistenceManager.getLastCharacter()
    }

    val character: LiveData<Character> = _character
}