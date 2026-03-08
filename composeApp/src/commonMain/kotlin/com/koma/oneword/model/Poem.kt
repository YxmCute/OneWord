package com.koma.oneword.model

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
