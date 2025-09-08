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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.mshdabiola.model.note.Notification
import com.mshdabiola.model.note.Place
import com.mshdabiola.model.note.RepeatSchedule
import com.mshdabiola.model.note.ScheduledTime
import com.mshdabiola.model.testtag.NotificationDialogTestTags // Added import
import com.mshdabiola.ui.state.NotificationDate
import kotlinx.coroutines.launch
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
// Make sure you have these string resources defined
import synapse.core.ui.generated.resources.add_reminder
import synapse.core.ui.generated.resources.cancel
import synapse.core.ui.generated.resources.delete
import synapse.core.ui.generated.resources.edit_reminder
import synapse.core.ui.generated.resources.modules_designsystem_days_of_weeks
import synapse.core.ui.generated.resources.modules_designsystem_notification_days
import synapse.core.ui.generated.resources.modules_designsystem_notification_interval
import synapse.core.ui.generated.resources.modules_designsystem_notification_places
import synapse.core.ui.generated.resources.modules_designsystem_notification_times
import synapse.core.ui.generated.resources.place
import synapse.core.ui.generated.resources.save
import synapse.core.ui.generated.resources.time
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun NotificationDialog(
    initState: Notification? = null,
    isEdit: Boolean = false,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onSetAlarm: (Notification) -> Unit = { },
    onDeleteAlarm: () -> Unit = {},

) {
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()
    var notificationUiState by remember(initState) {
        val value = initState ?: Notification(
            currentPlace = null,
            currentInterval = RepeatSchedule.DoNotRepeat,
            currentDateTime = Clock
                .System
                .now().plus(1, DateTimeUnit.HOUR)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
        )
        mutableStateOf(value)
    }

    var isError by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        AlertDialog(
            modifier = Modifier.testTag(NotificationDialogTestTags.DIALOG_ROOT),
            onDismissRequest = onDismissRequest,
            title = { Text(text = if (isEdit) stringResource(Res.string.edit_reminder) else stringResource(Res.string.add_reminder)) },
            text = {
                Column {
                    PrimaryTabRow(pagerState.currentPage) {
                        Tab(
                            modifier = Modifier.testTag(NotificationDialogTestTags.TIME_TAB),
                            selected = pagerState.currentPage == 0,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                            },
                        ) {
                            Text(text = stringResource(Res.string.time))
                        }
                        Tab(
                            modifier = Modifier.testTag(NotificationDialogTestTags.PLACE_TAB),
                            selected = pagerState.currentPage == 1,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(1) }
                            },
                        ) {
                            Text(text = stringResource(Res.string.place))
                        }
                    }
                    HorizontalPager(
                        modifier = Modifier,
                        state = pagerState,
                        userScrollEnabled = false,
                    ) { index ->
                        when (index) {
                            0 -> {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    TimeTextDropbox(
                                        modifier = Modifier.fillMaxWidth(),
                                        currentTime = notificationUiState.currentDateTime.time,
                                        onValueChange = {
                                            notificationUiState = notificationUiState.copy(
                                                currentDateTime = LocalDateTime(
                                                    date = notificationUiState.currentDateTime.date,
                                                    time = it,
                                                ),
                                            )
                                        },
                                        onErrorMessage = {
                                            isError = it
                                        },
                                    )
                                    DateTextDropbox(
                                        modifier = Modifier.fillMaxWidth(),
                                        currentDate = notificationUiState.currentDateTime.date,
                                        todayDate = today.date,
                                        onValueChange = {
                                            notificationUiState = notificationUiState.copy(
                                                currentDateTime = LocalDateTime(
                                                    date = it,
                                                    time = notificationUiState.currentDateTime.time,
                                                ),
                                            )
                                        },
                                    )
                                    IntervalTextDropbox(
                                        modifier = Modifier.fillMaxWidth(),
                                        currentInterval = notificationUiState.currentInterval,
                                        onValueChange = {
                                            notificationUiState = notificationUiState.copy(
                                                currentInterval = it,
                                            )
                                        },
                                    )
                                }
                            }

                            1 -> {
                                Place(
                                    onValueChange = {
                                        notificationUiState = notificationUiState.copy(
                                            currentPlace = it,
                                        )
                                    },
                                    currentPlace = notificationUiState.currentPlace,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                SynButton(
                    modifier = Modifier
                        .testTag(NotificationDialogTestTags.SAVE_BUTTON),
                    onClick = {
                        onSetAlarm(notificationUiState)
                        onDismissRequest()
                    },
                    enabled = !isError,
                    label = stringResource(Res.string.save),
                )
            },
            dismissButton = {
                Row {
                    if (isEdit) {
                        SynTextButton(
                            modifier = Modifier.testTag(NotificationDialogTestTags.DELETE_BUTTON),
                            onClick = {
                                onDismissRequest()
                                onDeleteAlarm()
                            },
                            label = stringResource(Res.string.delete),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    SynTextButton(
                        modifier = Modifier.testTag(NotificationDialogTestTags.CANCEL_BUTTON),
                        onClick = { onDismissRequest() },
                        label = stringResource(Res.string.cancel),
                    )
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NotificationDialogNewPreview() {
    val notificationUiState = Notification(
        currentDateTime = LocalDateTime(2026, 6, 16, 22, 1),
        currentInterval = RepeatSchedule.Daily(
            intervalEnd = IntervalEnd.Forever,
        ),
        currentPlace = Place.Home,

    )
    NotificationDialog(initState = notificationUiState, showDialog = true)
}
