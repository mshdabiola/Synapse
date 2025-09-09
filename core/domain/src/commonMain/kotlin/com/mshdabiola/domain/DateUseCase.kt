/*
 * Designed and developed by 2024 mshdabiola (lawal abiola)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mshdabiola.domain

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class DateUseCase {
    val dateFormat = LocalDate.Format {
        this.monthName(MonthNames.ENGLISH_FULL)
        char(' ')
        day(padding = Padding.ZERO)
        char(',')
        year(Padding.SPACE)
    }
    val timeFormat = LocalTime.Format {
        amPmHour()
        chars(" : ")
        minute()
        chars(" : ")

        amPmMarker("AM", "PM")
    }

    @OptIn(ExperimentalTime::class)
    operator fun invoke(date: Long): String {
        val date = Instant.fromEpochMilliseconds(date)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        return when {
            now.date == date.date -> "Today ${date.time.format(timeFormat)} "
            date.date == now.date.minus(1, DateTimeUnit.DAY) ->
                "Yesterday ${date.time.format(timeFormat)}"

            else -> "${date.date.format(dateFormat)} ${date.time.format(timeFormat)}"
        }
    }
}
