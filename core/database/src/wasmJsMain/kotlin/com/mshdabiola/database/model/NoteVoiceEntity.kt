package com.mshdabiola.database.model

import kotlinx.serialization.Serializable

@Serializable
data class NoteVoiceEntity(
    val id: Long,
    val noteId: Long,
    val path: String,
)
