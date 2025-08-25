package com.mshdabiola.model.note

import kotlinx.datetime.LocalDateTime

data class Notification(
    val noteId: Long = -1,
    val currentDateTime: LocalDateTime,
    val currentInterval: RepeatSchedule,
    val currentPlace: Place?,
)
