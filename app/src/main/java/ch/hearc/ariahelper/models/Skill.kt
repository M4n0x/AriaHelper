package ch.hearc.ariahelper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Little, partial data class to stock skills and their value
 * Value c [0;100]
 */
@Parcelize
data class Skill(var name : String, var description : String, var value : Int = 50) : Parcelable
