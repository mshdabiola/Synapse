package com.mshdabiola.model.note

import kotlinx.datetime.LocalDate

sealed class RepeatSchedule(val index: Int = 0) {
    data class Daily(
        val interval: String = "1",
        val intervalEnd: IntervalEnd,
    ) : RepeatSchedule(1)
    data class Weekly(
        val interval: String = "1",
        val days: Set<Int> = emptySet(),
        val intervalEnd: IntervalEnd,
    ) : RepeatSchedule(2)
    data class Monthly(
        val interval: String = "1",
        val sameDay: Boolean,
        val intervalEnd: IntervalEnd,
    ) : RepeatSchedule(3)
    data class Yearly(
        val interval: String = "1",
        val intervalEnd: IntervalEnd,
    ) : RepeatSchedule(4)
    data object DoNotRepeat : RepeatSchedule(0)
    data object Custom : RepeatSchedule(5)
}

sealed class IntervalEnd(val index: Int) {
    data object Forever : IntervalEnd(0)
    data class EndDate(val date: LocalDate) : IntervalEnd(1)
    data class NumberOfTimes(val times: Int) : IntervalEnd(2)
}
