package com.mshdabiola.database.model

import kotlinx.serialization.Serializable

@Serializable
data class LabelEntity(
    val id: Long?,
    val name: String,
)
