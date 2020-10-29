package ch.hearc.ariahelper.models.commonpool

import ch.hearc.ariahelper.models.Attribute
import ch.hearc.ariahelper.models.Item
import ch.hearc.ariahelper.models.Skill
import java.util.ArrayList

/**
 * Basic pool of attributes so that the app is not empty
 */
object AttributeBasicPool {
    public fun basicAttributes() : MutableList<Attribute> {
        return mutableListOf<Attribute>(
            Attribute("force"),
            Attribute("charisme"),
            Attribute("intelligence"),
            Attribute("dextérité"),
            Attribute("sagesse"),
            Attribute("agilité"),
        )
    }
}