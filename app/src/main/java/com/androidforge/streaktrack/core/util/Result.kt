package com.androidforge.streaktrack.core.util

import com.androidforge.streaktrack.R

sealed class Result<T>(val data: T? = null, val message: UiText? = null) {
    class Success<T>(data: T) : Result<T>(data)
    class Error<T>(message: UiText, data: T? = null) : Result<T>(data, message)
    class Loading<T>(data: T? = null) : Result<T>(data)
    class Offline<T>(data: T? = null) : Result<T>(data, UiText.StringResource(R.string.offline_message))
}