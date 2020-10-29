package ch.hearc.ariahelper.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hearc.ariahelper.models.Character

class CharacterViewModel : ViewModel() {
    public val _character = MutableLiveData<Character>().apply {
        value = Character("VM Character")
    }

    val character: LiveData<Character> = _character
}