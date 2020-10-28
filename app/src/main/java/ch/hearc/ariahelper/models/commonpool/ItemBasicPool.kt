package ch.hearc.ariahelper.models.commonpool

import ch.hearc.ariahelper.models.Attribute
import ch.hearc.ariahelper.models.Item
import java.util.ArrayList

/**
 * Basic pool of items so that the app is not empty
 */
object ItemBasicPool {
    val ITEMS: MutableList<Item> = ArrayList<Item>()
}