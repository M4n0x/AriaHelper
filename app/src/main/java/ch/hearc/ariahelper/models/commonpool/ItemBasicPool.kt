package ch.hearc.ariahelper.models.commonpool

import ch.hearc.ariahelper.models.Attribute
import ch.hearc.ariahelper.models.Item
import java.util.ArrayList

/**
 * Basic pool of items so that the app is not empty
 * For future use, init basic items for a player
 */

object ItemBasicPool {
    public fun basicItems() : MutableList<Item> {
        return mutableListOf<Item>(
            Item("Chaise de jardin", "Description", 1, ""), // example item
        )
    }
}