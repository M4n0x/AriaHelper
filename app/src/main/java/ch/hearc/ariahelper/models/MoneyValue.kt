package ch.hearc.ariahelper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Class used to create/stock/compare money values composed of gold, silver and bronze values
 */
@Parcelize
@Serializable
data class MoneyValue(val gold : Int, val silver : Int, val bronze : Int) : Parcelable {
}