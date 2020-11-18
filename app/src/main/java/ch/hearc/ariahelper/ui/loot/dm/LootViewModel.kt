package ch.hearc.ariahelper.ui.loot.dm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hearc.ariahelper.models.Item

/**
 * This ViewModel is used to keep track of current list items
 */
class LootViewModel : ViewModel() {
    public val _itemList = MutableLiveData<MutableList<Item>>()
    public val itemList: LiveData<MutableList<Item>> = _itemList

    public val _selectedItemList = MutableLiveData<MutableList<Item>>()
    public val selectedItemList: LiveData<MutableList<Item>> = _selectedItemList
}