package com.mshdabiola.database.model

import kotlinx.serialization.Serializable

@Serializable
data class NoteEntity(
    val id: Long?,
    val title: String,
    val detail: String,
    val editDate: Long,
    val isCheck: Boolean,
    val color: Int,
    val background: Int,
    val isPin: Boolean,
    val noteType: Int,
)
