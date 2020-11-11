package ch.hearc.ariahelper.ui.loot.dm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.CharacterPersistenceManager

class LootViewModel : ViewModel() {
    public val _adapter = MutableLiveData<ItemRecyclerViewAdapter>().apply {
        value = null
    }

    public val adapter: LiveData<ItemRecyclerViewAdapter> = _adapter
}