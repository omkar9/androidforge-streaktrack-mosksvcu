package com.androidforge.streaktrack.core.util

import android.content.Context
import androidx.annotation.StringRes

/**
 * A sealed class to represent text that can be displayed in the UI.
 * This allows for flexible handling of string resources and dynamic strings.
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}