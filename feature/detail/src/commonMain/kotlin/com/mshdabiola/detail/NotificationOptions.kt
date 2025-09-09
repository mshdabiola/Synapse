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
package com.mshdabiola.detail

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.AppConstant
import com.mshdabiola.model.NoteBg
import com.mshdabiola.model.testtag.NotificationOptionsTestTags
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun NotificationOptions(
    onAlarm: (Long, Long?) -> Unit = { _, _ -> },
    showDialog: () -> Unit = {},
    show: Boolean,
    currentColor: Int,
    currentImage: Int,
    onDismissRequest: () -> Unit,
) {
    val background = if (currentImage != -1) {
        Color(NoteBg.noteBgs [currentImage].fgColor)
    } else {
        if (currentColor != -1) {
            Color(AppConstant.noteColors[currentColor])
        } else {
            MaterialTheme.colorScheme.surface
        }
    }
    val dateTime = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }

    val morning = remember {
        LocalDateTime(dateTime.date, LocalTime(8, 0, 0))
    }

    val evening = remember {
        LocalDateTime(dateTime.date, LocalTime(22, 0, 0))
    }
    val morningTom = remember {
        LocalDateTime(dateTime.date.plus(1, DateTimeUnit.DAY), LocalTime(8, 0, 0))
    }
    val eveningTom = remember {
        LocalDateTime(dateTime.date.plus(1, DateTimeUnit.DAY), LocalTime(22, 0, 0))
    }

    val nextWk = remember {
        LocalDateTime(dateTime.date.plus(1, DateTimeUnit.WEEK), LocalTime(8, 0, 0))
    }

    val pastToday = remember {
        dateTime > morning && dateTime > evening
    }

    val dayOfWeek = remember {
        nextWk.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    // 7.22pm,19.22
    // if now 19.22> morning 7
    // later today 10pm22/tomorrow morning 7am
    // Tomorrow morning 10am/Tomorrow evening 7pm 19
    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            containerColor = background,
        ) {
            NotificationItem(
                modifier = Modifier.testTag(NotificationOptionsTestTags.LATER_TODAY_TOMORROW_MORNING),
                title = if (pastToday) "Tomorrow morning" else "Later today",
                time = if (pastToday) morning.toTimeString() else evening.toTimeString(),
                onClick = {
                    onDismissRequest()

                    val time = if (pastToday) {
                        morningTom.toInstant(TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                    } else {
                        evening.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    }

                    onAlarm(time, null)
                },
            )
            NotificationItem(
                modifier = Modifier.testTag(NotificationOptionsTestTags.TOMORROW_MORNING_TOMORROW_EVENING),
                title = if (pastToday) "Tomorrow evening" else "Tomorrow morning",
                time = if (pastToday) evening.toTimeString() else morning.toTimeString(),
                onClick = {
                    onDismissRequest()

                    val time = if (pastToday) {
                        eveningTom.toInstant(TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                    } else {
                        morningTom.toInstant(TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                    }

                    onAlarm(time, null)
                },

            )
            NotificationItem(
                modifier = Modifier.testTag(NotificationOptionsTestTags.NEXT_WEEK_MORNING),
                title = "$dayOfWeek morning",
                time = "${dayOfWeek.subSequence(0..2)} ${nextWk.toTimeString()}",
                onClick = {
                    onDismissRequest()
                    onAlarm(
                        nextWk.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                        null,
                    )
                },
            )
            NotificationItem(
                modifier = Modifier.testTag(NotificationOptionsTestTags.PICK_DATE_TIME),
                title = "Pick a date & time",
                time = "",
                onClick = {
                    showDialog()
                    onDismissRequest()
                },
            )
        }
    }
}

@Composable
fun NotificationItem(
    icon: ImageVector = SynIcons.AccessTime,
    title: String,
    time: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    DropdownMenuItem(
        modifier = modifier,
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "time")
        },
        text = { Text(text = title) },
        onClick = onClick,
        trailingIcon = { Text(text = time) },
    )
}

fun LocalDateTime.toTimeString(): String {
    val amPmHour = if (hour == 0 || hour == 12) 12 else hour % 12
    val amPmMarker = if (hour < 12) "AM" else "PM"
    val hourString = amPmHour.toString().padStart(2, '0')
    val minuteString = minute.toString().padStart(2, '0')
    return "$hourString : $minuteString $amPmMarker"
}
