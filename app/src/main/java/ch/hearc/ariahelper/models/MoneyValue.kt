package ch.hearc.ariahelper.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Class used to create/stock/compare money values composed of gold, silver and bronze values
 */
@Parcelize
data class MoneyValue(val gold : Int, val silver : Int, val bronze : Int) : Parcelable {
}