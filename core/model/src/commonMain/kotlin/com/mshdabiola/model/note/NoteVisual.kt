package com.mshdabiola.model.note

sealed class NoteVisual(val key: Long)

data class NoteImage(
    val id: Long = -1,
    val noteId: Long = 0,
    val path: String = "",
) : NoteVisual(id)

data class NoteDrawing(
    val id: Long = -1,
    val noteId: Long,
    val paths: List<Path> = emptyList(),
) : NoteVisual(id)
