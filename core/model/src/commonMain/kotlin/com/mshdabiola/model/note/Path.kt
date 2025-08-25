package com.mshdabiola.model.note

import kotlinx.serialization.Serializable

@Serializable
data class Path(
    val points: List<Point> = emptyList(),
    val penProperties: PenProperties = PenProperties(),
    var isSelected: Boolean = false,
)
