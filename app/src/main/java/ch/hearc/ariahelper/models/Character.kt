package ch.hearc.ariahelper.models

import android.os.Parcelable
import ch.hearc.ariahelper.models.commonpool.AttributeBasicPool
import ch.hearc.ariahelper.models.commonpool.ItemBasicPool
import ch.hearc.ariahelper.models.commonpool.SkillBasicPool
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Stock the character with its context (item and skills)
 */
@Parcelize
@Serializable
data class Character(
    var id: Int ? = null,
    var name: String,
    var level: Int,
    var picture: String? = null,
    var money : MoneyValue,
    val itemList : MutableList<Item>,
    val attributeList : MutableList<Attribute>,
    val skillList : MutableList<Skill>) : Parcelable {

    //basic constructor with just a name
    constructor(name: String) : this(null, name, 0, null, MoneyValue(0,0,0), ItemBasicPool.basicItems(), AttributeBasicPool.basicAttributes(), SkillBasicPool.basicSkills())
}