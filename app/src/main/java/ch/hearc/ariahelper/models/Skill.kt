package ch.hearc.ariahelper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Little, partial data class to stock skills and their value
 * Value c [0;100]
 */
@Parcelize
@Serializable
data class Skill(var name : String, var description : String, var value : Int = 50) : Parcelable
