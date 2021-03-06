package ch.hearc.ariahelper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Simple data class, stock the Item information
 */

@Parcelize
@Serializable
data class Item(
    var name : String,
    val description : String,
    var quality : Int,
    var picture : String) : Parcelable