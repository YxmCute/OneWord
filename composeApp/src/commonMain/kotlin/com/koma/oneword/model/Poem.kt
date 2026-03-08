package com.koma.oneword.model

/**
 * Domain model used by the UI to render a poem and its optional translation.
 */

data class Poem(
    val id: String,
    val content: String,
    val title: String,
    val author: String,
    val dynasty: String,
    val popularity: Long?,
    val fullText: List<String>,
    val translation: List<String>,
)
