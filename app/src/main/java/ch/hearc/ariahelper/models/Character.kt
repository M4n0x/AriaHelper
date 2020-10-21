package ch.hearc.ariahelper.models

import android.graphics.drawable.Drawable
import ch.hearc.ariahelper.models.commonpool.AttributeBasicPool
import ch.hearc.ariahelper.models.commonpool.ItemBasicPool
import ch.hearc.ariahelper.models.commonpool.SkillBasicPool

/**
 * Stock the character with its context (item and skills)
 */
data class Character(val name: String, val level: Int,
                     val picture: Drawable ? = null,
                     val money : MoneyValue,
                     val itemList : MutableList<Item>,
                     val attributeList : MutableList<Attribute>,
                     val skillList : MutableList<Skill>) {

    //basic constructor with just a name
    constructor(name: String) : this(name, 0, null, MoneyValue(0,0,0), ItemBasicPool.ITEMS, AttributeBasicPool.ATTRIBUTES, SkillBasicPool.SKILLS)
}