package com.mshdabiola.model.note

data class Path(
    val points: List<Point> = emptyList(),
    val penProperties: PenProperties = PenProperties(),
    var isSelected: Boolean = false,
)
