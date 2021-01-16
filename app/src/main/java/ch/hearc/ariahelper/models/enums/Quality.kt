package ch.hearc.ariahelper.models.enums

import android.content.Context
import ch.hearc.ariahelper.R
import java.util.*
import java.util.stream.Collector

/**
 * This enum is used to set quality on items
 */
enum class Quality(val id: Int, val resId: Int) {
    POOR(0, R.string.item_quality_0),
    COMMON(1, R.string.item_quality_1),
    RARE(2, R.string.item_quality_2),
    EPIC(3, R.string.item_quality_3),
    LEGENDARY(4, R.string.item_quality_4),
    QUEST(5, R.string.item_quality_5);

    companion object {
        /**
         * Retrieve resid by id
         */
        fun getStringFromId(value: Int): Int? = values().find { it.id == value }?.resId

        /**
         * Create a string list of Quality values from context
         */
        fun getListWithLabel(context: Context): Array<String> = Arrays.stream(values()).map { context.getString(it.resId) }.toArray<String> { length ->
            arrayOfNulls(length)
        }
    }
}