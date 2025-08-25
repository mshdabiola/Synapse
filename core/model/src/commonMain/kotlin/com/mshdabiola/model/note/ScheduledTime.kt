package com.mshdabiola.model.note

import kotlinx.datetime.LocalTime

sealed class ScheduledTime {
    data class Time(val localTime: LocalTime) : ScheduledTime()
    data object PickTime : ScheduledTime()
}
