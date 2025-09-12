package com.mshdabiola.draw

import com.mshdabiola.model.note.Path


data class DrawUiState(
    val drawingId: Long? = null,
    val noteId: Long? = null,
    val drawings: List<Path> = emptyList(),
)
