package com.coolboost.performance.core

/**
 * Generic operation result wrapper used throughout the domain/data layers
 * to avoid throwing exceptions across architecture boundaries.
 */
sealed class AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : AppResult<Nothing>()
    data object Loading : AppResult<Nothing>()

    inline fun onSuccess(action: (T) -> Unit): AppResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (String) -> Unit): AppResult<T> {
        if (this is Error) action(message)
        return this
    }
}

inline fun <T> runCatchingResult(block: () -> T): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (t: Throwable) {
        AppResult.Error(t.message ?: "Unknown error", t)
    }
}
