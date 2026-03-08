package com.koma.oneword.model

sealed interface RefreshResult {
    data object Success : RefreshResult

    data class Offline(val message: String) : RefreshResult

    data class Failure(val message: String) : RefreshResult
}
