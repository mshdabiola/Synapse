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
package com.mshdabiola.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.component.SynButton
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.model.note.IntervalEnd
import com.mshdabiola.model.note.Place
import com.mshdabiola.model.note.RepeatSchedule
import com.mshdabiola.model.note.ScheduledTime
import com.mshdabiola.model.testtag.TextDropBoxTestTags // Added import
import com.mshdabiola.ui.state.NotificationDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.core.ui.generated.resources.Res
import synapse.core.ui.generated.resources.core_ui_days_of_weeks
import synapse.core.ui.generated.resources.core_ui_notification_days
import synapse.core.ui.generated.resources.core_ui_notification_interval
import synapse.core.ui.generated.resources.core_ui_notification_places
import synapse.core.ui.generated.resources.core_ui_notification_times
import synapse.core.ui.generated.resources.date_dialog_cancel_button
import synapse.core.ui.generated.resources.date_dialog_set_date_button
import synapse.core.ui.generated.resources.text_drop_box_set_time_button
import synapse.core.ui.generated.resources.text_drop_box_time_has_past
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun Place(
    modifier: Modifier = Modifier,
    onValueChange: (Place) -> Unit = {},
    currentPlace: Place? = null,
    state: TextFieldState,
) {
    val places = remember {
        listOf(
            Place.Home,
            Place.Work,
            Place.School,
            Place.Edit((currentPlace as? Place.Edit)?.place ?: ""), // Default TextFieldState
        )
    }
    val placeStringArray = stringArrayResource(Res.array.core_ui_notification_places)
    Column(modifier = modifier.testTag(TextDropBoxTestTags.PLACE_ROOT_COLUMN)) {
        places.forEachIndexed { index, place ->
            val placeTagSuffix = when (place) {
                is Place.Home -> "home"
                is Place.Work -> "work"
                is Place.School -> "school"
                is Place.Edit -> "edit"
                else -> "unknown_$index" // Fallback, though current types are exhaustive
            }
            if (place is Place.Edit) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("${TextDropBoxTestTags.PLACE_OPTION_ROW_PREFIX}$placeTagSuffix"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        modifier = Modifier.testTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}$placeTagSuffix"),
                        selected = currentPlace is Place.Edit,
                        onClick = {
                            onValueChange(place) // Still allow click to select
                        },
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    onValueChange(place)
                                }
                            }
                            .testTag(TextDropBoxTestTags.PLACE_EDIT_TEXT_FIELD),
                        state = state, // Use the TextFieldState from the place object
                        lineLimits = TextFieldLineLimits.SingleLine,
                        placeholder = { Text(text = placeStringArray.getOrNull(index) ?: "") },
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onValueChange(place) } // Make the whole row clickable
                        .testTag("${TextDropBoxTestTags.PLACE_OPTION_ROW_PREFIX}$placeTagSuffix"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        modifier = Modifier.testTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}$placeTagSuffix"),
                        selected = place == currentPlace,
                        onClick = {
                            onValueChange(place)
                        },
                    )
                    Text(modifier = Modifier.weight(1f), text = placeStringArray.getOrNull(index) ?: "")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TimeTextDropbox(
    modifier: Modifier = Modifier,
    currentTime: LocalDateTime,
    nowTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    onValueChange: (LocalTime) -> Unit = {},
    onErrorMessage: (Boolean) -> Unit = {},
) {
    val times = remember {
        listOf(
            ScheduledTime.Time(LocalTime(7, 0, 0)),
            ScheduledTime.Time(LocalTime(13, 0, 0)),
            ScheduledTime.Time(LocalTime(19, 0, 0)),
            ScheduledTime.Time(LocalTime(20, 0, 0)),
            ScheduledTime.PickTime,
        )
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    var showError by remember {
        mutableStateOf(false)
    }
    var showTimeDialog by remember {
        mutableStateOf(false)
    }
    val state = rememberTextFieldState()
    val formatter = LocalTime.Format {
        amPmHour(Padding.ZERO) // hh (01-12) with zero padding
        char(':')
        minute(Padding.ZERO) // mm (00-59) with zero padding
        char(' ')
        amPmMarker("AM", "PM") // AM/PM marker in uppercase)
    }

    LaunchedEffect(key1 = currentTime) {
        state.clearText()
        state.edit {
            append(currentTime.time.format(formatter))
        }
        showError = currentTime <= nowTime
        onErrorMessage(showError)
    }

    val timeStringArray = stringArrayResource(Res.array.core_ui_notification_times)

    ExposedDropdownMenuBox(
        modifier = modifier.testTag(TextDropBoxTestTags.TIME_DROPBOX_ROOT),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true)
                .testTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD),
            readOnly = true,
            state = state,
            supportingText = { if (showError) Text(text = stringResource(Res.string.text_drop_box_time_has_past)) },
            isError = showError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            lineLimits = TextFieldLineLimits.SingleLine,

        )
        ExposedDropdownMenu(
            modifier = Modifier.testTag(TextDropBoxTestTags.TIME_DROPBOX_MENU),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            times.forEachIndexed { index, notificationTime ->
                val itemTagSuffix = when (notificationTime) {
                    is ScheduledTime.Time -> "${notificationTime.localTime.hour}_${notificationTime.localTime.minute}"
                    is ScheduledTime.PickTime -> "pick_time"
                }
                when (notificationTime) {
                    is ScheduledTime.Time -> {
                        DropdownMenuItem(
                            modifier = Modifier.testTag(
                                "${TextDropBoxTestTags.TIME_DROPBOX_MENU_ITEM_PREFIX}$itemTagSuffix",
                            ),
                            text = { Text(text = timeStringArray.getOrNull(index) ?: "") },
                            onClick = {
                                onValueChange(notificationTime.localTime)
                                expanded = false
                            },
                            enabled = LocalDateTime(nowTime.date, notificationTime.localTime) > nowTime,
                            trailingIcon = {
                                Text(
                                    notificationTime.localTime.format(formatter),
                                )
                            },
                        )
                    }

                    is ScheduledTime.PickTime -> {
                        DropdownMenuItem(
                            modifier = Modifier.testTag(
                                "${TextDropBoxTestTags.TIME_DROPBOX_MENU_ITEM_PREFIX}$itemTagSuffix",
                            ),
                            text = { Text(text = timeStringArray[index]) },
                            onClick = {
                                showTimeDialog = true
                                expanded = false
                            },
                        )
                    }
                }
            }
        }
    }

    if (showTimeDialog) {
        val timeState = rememberTimePickerState()
        DatePickerDialog(
            modifier = Modifier.testTag(TextDropBoxTestTags.TIME_PICKER_DIALOG_ROOT),
            onDismissRequest = { showTimeDialog = false },
            confirmButton = {
                SynButton(
                    modifier = Modifier.testTag(TextDropBoxTestTags.TIME_PICKER_DIALOG_CONFIRM_BUTTON),
                    onClick = {
                        showTimeDialog = false
                        onValueChange(LocalTime(timeState.hour, timeState.minute))
                    },
                    label = stringResource(Res.string.text_drop_box_set_time_button),
                )
            },
            dismissButton = {
                SynTextButton(
                    modifier = Modifier.testTag(TextDropBoxTestTags.TIME_PICKER_DIALOG_DISMISS_BUTTON),
                    onClick = {
                        showTimeDialog = false
                    },
                    label = stringResource(Res.string.date_dialog_cancel_button),
                )
            },
        ) {
            TimePicker(state = timeState, modifier = Modifier.testTag(TextDropBoxTestTags.TIME_PICKER_IN_DIALOG))
        }
    }
}

@Preview
@Composable
fun TimeTextDropboxPreview() {
    val currentTime = LocalDateTime(2026, 11, 4, 10, 30)

    TimeTextDropbox(currentTime = currentTime)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DateTextDropbox(
    modifier: Modifier = Modifier,
    currentDate: LocalDate,
    todayDate: LocalDate,
    onValueChange: (LocalDate) -> Unit = {},
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var showDateDialog by remember {
        mutableStateOf(false)
    }
    val nowDate = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    val dates = remember(nowDate) {
        listOf(
            NotificationDate.Date(nowDate),
            NotificationDate.Date(nowDate.plus(1, DateTimeUnit.DAY)),
            NotificationDate.Date(nowDate.plus(1, DateTimeUnit.WEEK)),
            NotificationDate.PickDate,
        )
    }

    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentDate) {
        state.clearText()
        state.edit {
            append(
                "${
                    currentDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
                } ${currentDate.dayOfMonth}",
            )
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            if (currentDate.year != now.year) {
                append(", ${currentDate.year}")
            }
        }
    }

    val dateStringArray = stringArrayResource(Res.array.core_ui_notification_days)
    val daysOfWeeks = stringArrayResource(Res.array.core_ui_days_of_weeks)

    ExposedDropdownMenuBox(
        modifier = modifier.testTag(TextDropBoxTestTags.DATE_DROPBOX_ROOT),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true)
                .testTag(TextDropBoxTestTags.DATE_DROPBOX_TEXT_FIELD),
            readOnly = true,
            state = state,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
            lineLimits = TextFieldLineLimits.SingleLine,

        )
        ExposedDropdownMenu(
            modifier = Modifier.testTag(TextDropBoxTestTags.DATE_DROPBOX_MENU),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            dates.forEachIndexed { index, notificationTime ->
                val itemTagSuffix = dateStringArray.getOrNull(index)?.lowercase()?.replace(" ", "_") ?: "pick_date"
                DropdownMenuItem(
                    modifier = Modifier.testTag("${TextDropBoxTestTags.DATE_DROPBOX_MENU_ITEM_PREFIX}$itemTagSuffix"),
                    text = {
                        Text(
                            text =
                            dateStringArray.getOrNull(index) + " " +
                                if (index == 2)daysOfWeeks.getOrNull(todayDate.dayOfWeek.ordinal) else "",
                        )
                    },
                    onClick = {
                        if (notificationTime is NotificationDate.Date) {
                            onValueChange(notificationTime.localDate)
                        } else {
                            showDateDialog = true
                        }
                        expanded = false
                    },
                )
            }
        }
    }

    if (showDateDialog) {
        val dateState = rememberDatePickerState()

        DatePickerDialog(
            modifier = Modifier.testTag(TextDropBoxTestTags.DATE_PICKER_DIALOG_ROOT),
            onDismissRequest = {
                showDateDialog = false
            },
            confirmButton = {
                SynButton(
                    modifier = Modifier.testTag(TextDropBoxTestTags.DATE_PICKER_DIALOG_CONFIRM_BUTTON),
                    onClick = {
                        showDateDialog = false

                        val date = dateState.selectedDateMillis?.let { millis ->
                            Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date
                        } ?: nowDate
                        onValueChange(
                            date,
                        )
                    },
                    label = stringResource(Res.string.date_dialog_set_date_button),
                )
            },
            dismissButton = {
                SynTextButton(
                    modifier = Modifier.testTag(TextDropBoxTestTags.DATE_PICKER_DIALOG_DISMISS_BUTTON),
                    onClick = {
                        showDateDialog = false
                    },
                    label = stringResource(Res.string.date_dialog_cancel_button),
                )
            },
        ) {
            DatePicker(
                state = dateState,
                modifier = Modifier.testTag(TextDropBoxTestTags.DATE_PICKER_IN_DIALOG),
                //   dateValidator = { it > (System.currentTimeMillis() - (48 * 60 * 60 * 1000)) }
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun DateTextDropboxPreview() {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    DateTextDropbox(
        currentDate = currentTime,
        todayDate = currentTime,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun IntervalTextDropbox(
    modifier: Modifier = Modifier,
    currentInterval: RepeatSchedule,
    onValueChange: (RepeatSchedule) -> Unit = {},
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val nowDate = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    var showIntervalDialog by remember {
        mutableStateOf(false)
    }

    val notificationIntervals = remember(nowDate) {
        listOf(
            RepeatSchedule.DoNotRepeat,
            RepeatSchedule.Daily(
                intervalEnd = IntervalEnd.Forever,
            ),
            RepeatSchedule.Weekly(
                intervalEnd = IntervalEnd.Forever,
            ),
            RepeatSchedule.Monthly(
                sameDay = true,
                intervalEnd = IntervalEnd.Forever,
            ),
            RepeatSchedule.Yearly(
                intervalEnd = IntervalEnd.Forever,
            ),
            RepeatSchedule.Custom,
        )
    }
    val intervalStringArray = stringArrayResource(
        Res.array.core_ui_notification_interval,
    )

    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentInterval) {
        state.clearText()
        state.edit {
            append(intervalStringArray.getOrNull(currentInterval.index) ?: "")
        }
    }

    ExposedDropdownMenuBox(
        modifier = modifier.testTag(TextDropBoxTestTags.INTERVAL_DROPBOX_ROOT),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true)
                .testTag(TextDropBoxTestTags.INTERVAL_DROPBOX_TEXT_FIELD),
            readOnly = true,
            state = state,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            lineLimits = TextFieldLineLimits.SingleLine,

        )
        ExposedDropdownMenu(
            modifier = Modifier.testTag(TextDropBoxTestTags.INTERVAL_DROPBOX_MENU),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            notificationIntervals.forEachIndexed { index, notificationTime ->
                val itemTagSuffix = intervalStringArray.getOrNull(index)?.lowercase()?.replace(" ", "_") ?: "custom"
                DropdownMenuItem(
                    modifier = Modifier.testTag(
                        "${TextDropBoxTestTags.INTERVAL_DROPBOX_MENU_ITEM_PREFIX}$itemTagSuffix",
                    ),
                    text = { Text(text = intervalStringArray[index]) },
                    onClick = {
                        if (notificationTime is RepeatSchedule.Custom) {
                            showIntervalDialog = true
                        } else {
                            onValueChange(notificationTime)
                        }
                        expanded = false
                    },
                )
            }
        }
    }

    if (showIntervalDialog) {
        // NotificationDialogInterval is tagged with its own set of test tags (NotificationDialogIntervalTestTags.DIALOG_ROOT)
        NotificationDialogInterval(
            initInterval = currentInterval,
            intervals = notificationIntervals.toMutableList().apply {
                removeAt(5) // Remove Custom itself from the list passed to the dialog
            },
            onValueChange = {
                onValueChange(it)
                showIntervalDialog = false
            },
            onDismiss = { showIntervalDialog = false },
            todayDate = nowDate,
        )
    }
}

@Preview
@Composable
fun IntervalTextDropboxPreview() {
    val currentInterval = RepeatSchedule.Daily(
        intervalEnd = IntervalEnd.Forever,
    )

    IntervalTextDropbox(
        currentInterval = currentInterval,
    )
}
