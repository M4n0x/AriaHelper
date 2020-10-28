package ch.hearc.ariahelper.models

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Simple data class, stock the full description/ID card of an item
 */
@Parcelize
data class Item(val name : String,
                val description : String,
                val quality : Int,
                val picture : String) : Parcelable