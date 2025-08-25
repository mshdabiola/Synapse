package com.mshdabiola.model.note


data class PenProperties(
    val colorIndex: Int = 0,
    val lineWidth: Int = 8,
    val lineCapIndex: Int = 0,
    val lineJoinIndex: Int = 0,
    val colorAlphaIndex: Float = 1f,
    val isPen: Boolean = true,
)
