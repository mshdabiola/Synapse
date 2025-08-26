package com.mshdabiola.database.model

import kotlinx.serialization.Serializable

@Serializable

data class NoteDrawingEntity(
    val id: Long?,
    val noteId: Long,
    val paths: String?,
)
