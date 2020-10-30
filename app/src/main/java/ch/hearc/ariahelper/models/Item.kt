package ch.hearc.ariahelper.models

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Simple data class, stock the full description/ID card of an item
 */
@Parcelize
@Serializable
data class Item(
    var name : String,
    val description : String,
    var value : MoneyValue,
    val picture : String) : Parcelable