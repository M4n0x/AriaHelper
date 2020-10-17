package ch.hearc.ariahelper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * An attribute is a simple name->value class to stock attributes such as strenght, charisma, etc...
 */
@Parcelize
data class Attribute(val name : String, val value : Int=0) : Parcelable