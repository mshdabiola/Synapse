package com.mshdabiola.database.model

import kotlinx.serialization.Serializable

@Serializable
data class NoteLabelCrossRef(
    val noteId: Long,
    val labelId: Long,
)
