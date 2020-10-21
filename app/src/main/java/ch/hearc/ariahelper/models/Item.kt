package ch.hearc.ariahelper.models

import android.graphics.drawable.Drawable

/**
 * Simple data class, stock the full description/ID card of an item
 */
data class Item(val name : String,
                val description : String,
                val quality : Int,
                val picture : String){
}