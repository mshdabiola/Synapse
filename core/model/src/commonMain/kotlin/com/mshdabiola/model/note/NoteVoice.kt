package com.mshdabiola.model.note

data class NoteVoice(
    val id: Long,
    val noteId: Long = 0,
    val path: String = "",
    val length: Long = 0,
)
