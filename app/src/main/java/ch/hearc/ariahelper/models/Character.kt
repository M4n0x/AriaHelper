package ch.hearc.ariahelper.models

import android.graphics.drawable.Drawable
import ch.hearc.ariahelper.models.commonpool.AttributeBasicPool
import android.os.Parcelable
import ch.hearc.ariahelper.models.commonpool.ItemBasicPool
import ch.hearc.ariahelper.models.commonpool.SkillBasicPool
import kotlinx.android.parcel.Parcelize

/**
 * Stock the character with its context (item and skills)
 */
@Parcelize
data class Character(val name: String,
                     val level: Int,
                     val picture: String? = null,
                     val money : MoneyValue,
                     val itemList : MutableList<Item>,
                     val attributeList : MutableList<Attribute>,
                     val skillList : MutableList<Skill>) : Parcelable {

    //basic constructor with just a name
    constructor(name: String) : this(name, 0, null, MoneyValue(0,0,0), ItemBasicPool.ITEMS, AttributeBasicPool.ATTRIBUTES, SkillBasicPool.SKILLS)
}