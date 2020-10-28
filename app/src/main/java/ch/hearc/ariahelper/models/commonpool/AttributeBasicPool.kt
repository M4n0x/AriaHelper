package ch.hearc.ariahelper.models.commonpool

import ch.hearc.ariahelper.models.Attribute
import ch.hearc.ariahelper.models.Item
import java.util.ArrayList

/**
 * Basic pool of attributes so that the app is not empty
 */
object AttributeBasicPool {
    val ATTRIBUTES: MutableList<Attribute> =
        listOf<Attribute>(
            Attribute("force"),
            Attribute("charisme"),
            Attribute("intelligence"),
            Attribute("dextérité"),
            Attribute("sagesse"),
            Attribute("agilité"),
        ) as MutableList<Attribute>
}