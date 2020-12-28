package ch.hearc.ariahelper.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hearc.ariahelper.models.persistence.CharacterPersistenceManager

/**
 * View model for the communication between the Character fragment and the Accelerometer service
 */
class CharacterComponentViewModel : ViewModel() {

    // ---dices---
    // ---Function to assign all dices as once ---
    fun assignDices(d6:Int, d10:Int, d100:Int, dCustom:Int){
        _D6.postValue(d6)
        _D10.postValue(d10)
        _D100.postValue(d100)
        if(dCustom != 0){
            _DCUSTOM.postValue(dCustom)
        }
    }

    // ---properties
    //d6
    val _D6 = MutableLiveData<Int>().apply {
        value = 6
    }
    val D6: LiveData<Int> = _D6

    //d10
    val _D10 = MutableLiveData<Int>().apply {
        value = 10
    }
    val D10: LiveData<Int> = _D10

    //d100
    val _D100 = MutableLiveData<Int>().apply {
        value = 100
    }
    val D100: LiveData<Int> = _D100

    //dcustom : Value requested, value displayes
    val _DCUSTOMREQ = MutableLiveData<Int>().apply {
        value = 0
    }
    val DCUSTOMREQ: LiveData<Int> = _DCUSTOMREQ

    val _DCUSTOM = MutableLiveData<Int>().apply {
        value = 0
    }
    val DCUSTOM: LiveData<Int> = _DCUSTOM

    // --Progress bar---
    //progressBar value
    val _Progress = MutableLiveData<Int>().apply {
        value = 0
    }
    val Progress: LiveData<Int> = _Progress
}