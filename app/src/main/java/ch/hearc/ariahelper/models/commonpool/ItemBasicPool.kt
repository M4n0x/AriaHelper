package ch.hearc.ariahelper.models.commonpool

import ch.hearc.ariahelper.models.Attribute
import ch.hearc.ariahelper.models.Item
import java.util.ArrayList

/**
 * Basic pool of items so that the app is not empty
 */

object ItemBasicPool {
    public fun basicItems() : MutableList<Item> {
        return mutableListOf<Item>(
            Item("Chaise de jardin", "Description", 1, "ic_menu_camera"),
            Item("Chaise de jardin", "Description", 1, "ic_menu_camera"),
            Item("Chaise de jardin", "Description", 1, "ic_menu_camera"),
            Item("Chaise de jardin", "Description", 1, "ic_menu_camera"),
            Item("Chaise de jardin", "Description", 1, "ic_menu_camera"),
            Item("Chaise de jardin", "Description", 1, "ic_menu_camera"),
            Item("Casque de Qualité Allemande", "Description", 2, "ic_menu_gallery")
        )
    }
}