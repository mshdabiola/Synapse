package com.mshdabiola.model.note

data class NoteItem(
    val id: Long = -1,
    val noteId: Long = 0,
    val content: String = "",
    val isCheck: Boolean = false,
)
