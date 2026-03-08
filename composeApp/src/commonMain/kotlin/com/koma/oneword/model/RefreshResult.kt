package com.koma.oneword.model

/**
 * Repository refresh outcomes used by the view model to update UI feedback.
 */

sealed interface RefreshResult {
    data object Success : RefreshResult

    data class Offline(val message: String) : RefreshResult

    data class Failure(val message: String) : RefreshResult
}
