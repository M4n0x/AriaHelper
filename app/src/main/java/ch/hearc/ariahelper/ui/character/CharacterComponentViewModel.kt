package ch.hearc.ariahelper.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.CharacterPersistenceManager

class CharacterComponentViewModel : ViewModel() {
    //dices
    public val _D6 = MutableLiveData<Int>().apply {
        value = 6
    }
    public val D6: LiveData<Int> = _D6

    public val _D10 = MutableLiveData<Int>().apply {
        value = 10
    }
    public val D10: LiveData<Int> = _D10

    public val _D100 = MutableLiveData<Int>().apply {
        value = 100
    }
    public val D100: LiveData<Int> = _D100

    //progressBar value
    public val _Progress = MutableLiveData<Int>().apply {
        value = 100
    }
    public val Progress: LiveData<Int> = _Progress
}