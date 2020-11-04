package ch.hearc.ariahelper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * An attribute is a simple name->value class to stock attributes such as strenght, charisma, etc...
 */
@Parcelize
@Serializable
data class Attribute(var name : String, var value : Int=0) : Parcelable