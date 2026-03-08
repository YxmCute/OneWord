package com.koma.oneword.model

/**
 * Cached home payload combining the displayed poem and its fetch timestamp.
 */

data class HomeData(
    val poem: Poem,
    val fetchedAtEpochMs: Long,
)
