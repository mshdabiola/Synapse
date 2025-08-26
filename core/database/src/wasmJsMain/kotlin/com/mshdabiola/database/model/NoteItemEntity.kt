package com.mshdabiola.database.model

import kotlinx.serialization.Serializable

@Serializable
data class NoteItemEntity(
    val id: Long?,
    val noteId: Long,
    val content: String,
    val isCheck: Boolean,
)
