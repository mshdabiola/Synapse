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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag // Added import
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.component.SynButton
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.note.IntervalEnd
import com.mshdabiola.model.note.RepeatSchedule
import com.mshdabiola.model.testtag.NotificationDialogIntervalTestTags // Added import
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.core.ui.generated.resources.Res
import synapse.core.ui.generated.resources.cancel
import synapse.core.ui.generated.resources.core_ui_days_of_weeks
import synapse.core.ui.generated.resources.core_ui_notification_interval
import synapse.core.ui.generated.resources.core_ui_notification_interval_end2
import synapse.core.ui.generated.resources.date_dialog_set_date_button
import synapse.core.ui.generated.resources.interval_text_field_prefix_every
import synapse.core.ui.generated.resources.interval_text_field_suffix_day
import synapse.core.ui.generated.resources.interval_text_field_suffix_days
import synapse.core.ui.generated.resources.interval_text_field_suffix_month
import synapse.core.ui.generated.resources.interval_text_field_suffix_months
import synapse.core.ui.generated.resources.interval_text_field_suffix_week
import synapse.core.ui.generated.resources.interval_text_field_suffix_weeks
import synapse.core.ui.generated.resources.interval_text_field_suffix_year
import synapse.core.ui.generated.resources.interval_text_field_suffix_years
import synapse.core.ui.generated.resources.notification_dialog_interval_close_button
import synapse.core.ui.generated.resources.notification_dialog_interval_date_icon_cd
import synapse.core.ui.generated.resources.notification_dialog_interval_events_suffix
import synapse.core.ui.generated.resources.notification_dialog_interval_monthly_day_of_week
import synapse.core.ui.generated.resources.notification_dialog_interval_monthly_same_day
import synapse.core.ui.generated.resources.notification_dialog_interval_set_repeat_button
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDialogInterval(
    modifier: Modifier = Modifier,
    initInterval: RepeatSchedule,
    todayDate: LocalDate,
    intervals: List<RepeatSchedule>,
    onValueChange: (RepeatSchedule) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var currentInterval by remember(initInterval) {
        mutableStateOf(initInterval)
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

    BasicAlertDialog(
        modifier = modifier.testTag(NotificationDialogIntervalTestTags.DIALOG_ROOT),
        onDismissRequest = { },
    ) {
        Surface(
            shape = ShapeDefaults.Small,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ExposedDropdownMenuBox(
                    modifier = Modifier.testTag(NotificationDialogIntervalTestTags.INTERVAL_TYPE_DROPDOWN_ROOT),
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true)
                            .testTag(NotificationDialogIntervalTestTags.INTERVAL_TYPE_TEXT_FIELD),
                        readOnly = true,
                        state = state,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        lineLimits = TextFieldLineLimits.SingleLine,

                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        },
                        modifier = Modifier.testTag(NotificationDialogIntervalTestTags.INTERVAL_TYPE_MENU),
                    ) {
                        intervals.forEachIndexed { index, interval ->
                            DropdownMenuItem(
                                text = { Text(text = intervalStringArray[index]) },
                                onClick = {
                                    currentInterval = interval
                                    expanded = false
                                },
                                modifier = Modifier.testTag(
                                    "${NotificationDialogIntervalTestTags
                                        .INTERVAL_TYPE_MENU_ITEM_PREFIX}_${intervalStringArray[index]
                                        .lowercase().replace(" ", "_")}",
                                ),
                            )
                        }
                    }
                }
                when (currentInterval) {
                    is RepeatSchedule.Daily -> {
                        val daily = currentInterval as RepeatSchedule.Daily
                        IntervalTextField(
                            modifier = Modifier.testTag(
                                "${NotificationDialogIntervalTestTags
                                    .INTERVAL_TF_ROOT_PREFIX}_daily",
                            ),
//                            textFieldModifier = Modifier.testTag(
//                                "${NotificationDialogIntervalTestTags
//                                    .INTERVAL_TF_TEXT_FIELD_PREFIX}_daily",
//                            ),
                            prefix = stringResource(Res.string.interval_text_field_prefix_every),
                            suffix = stringResource(Res.string.interval_text_field_suffix_day),
                            suffixPlural = stringResource(Res.string.interval_text_field_suffix_days),
                            text = daily.interval,
                            onValueChange = {
                                currentInterval = daily.copy(interval = it)
                            },

                        )

                        IntervalRepeatEnd(
                            modifier = Modifier.testTag(
                                "${NotificationDialogIntervalTestTags
                                    .REPEAT_END_ROOT_ROW_PREFIX}_daily",
                            ),
                            currentIntervalEnd = daily.intervalEnd,
                            todayDate = todayDate,
                            onValueChange = {
                                currentInterval = daily.copy(intervalEnd = it)
                            },
                        )
                    }

                    is RepeatSchedule.Weekly -> {
                        val daysOfWeek =
                            stringArrayResource(Res.array.core_ui_days_of_weeks)
                        val weekly = currentInterval as RepeatSchedule.Weekly
                        IntervalTextField(
                            modifier = Modifier.testTag(
                                "${NotificationDialogIntervalTestTags
                                    .INTERVAL_TF_ROOT_PREFIX}_weekly",
                            ),
//                            textFieldModifier = Modifier.testTag(
//                                "${NotificationDialogIntervalTestTags
//                                    .INTERVAL_TF_TEXT_FIELD_PREFIX}_weekly",
//                            ),
                            prefix = stringResource(Res.string.interval_text_field_prefix_every),
                            suffix = stringResource(Res.string.interval_text_field_suffix_week),
                            suffixPlural = stringResource(Res.string.interval_text_field_suffix_weeks),
                            text = weekly.interval,
                            onValueChange = {
                                currentInterval = weekly.copy(interval = it)
                            },
                        )
                        FlowRow(
                            modifier = Modifier.testTag(NotificationDialogIntervalTestTags.WEEKLY_DAYS_FLOW_ROW),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            daysOfWeek
                                .forEachIndexed { index, days ->
                                    val isSelected = index in weekly.days
                                    InputChip(
                                        selected = isSelected,
                                        onClick = {
                                            val newDays = weekly.days.toMutableSet()
                                            if (isSelected) {
                                                newDays.remove(index)
                                            } else {
                                                newDays.add(index)
                                            }
                                            currentInterval =
                                                weekly.copy(
                                                    days = newDays,

                                                )
                                        },
                                        label = { Text(days) },
                                        modifier = Modifier.testTag(
                                            "${NotificationDialogIntervalTestTags
                                                .WEEKLY_DAY_INPUT_CHIP_PREFIX}_${days.lowercase()}",
                                        ),
                                    )
                                }
                        }

                        IntervalRepeatEnd(
                            modifier = Modifier.testTag(
                                "${NotificationDialogIntervalTestTags
                                    .REPEAT_END_ROOT_ROW_PREFIX}_weekly",
                            ),
                            currentIntervalEnd = weekly.intervalEnd,
                            todayDate = todayDate,
                            onValueChange = {
                                currentInterval = weekly.copy(intervalEnd = it)
                            },
                        )
                    }

                    is RepeatSchedule.Monthly -> {
                        val monthly = currentInterval as RepeatSchedule.Monthly
                        val daysOfWeek = stringArrayResource(Res.array.core_ui_days_of_weeks)
                        IntervalTextField(
                            modifier = Modifier.testTag(
                                "${NotificationDialogIntervalTestTags
                                    .INTERVAL_TF_ROOT_PREFIX}_monthly",
                            ),
//                            textFieldModifier = Modifier.testTag(
//                                "${NotificationDialogIntervalTestTags
//                                    .INTERVAL_TF_TEXT_FIELD_PREFIX}_monthly",
//                            ),
                            prefix = stringResource(Res.string.interval_text_field_prefix_every),
                            suffix = stringResource(Res.string.interval_text_field_suffix_month),
                            suffixPlural = stringResource(Res.string.interval_text_field_suffix_months),
                            text = monthly.interval,
                            onValueChange = {
                                currentInterval = monthly.copy(interval = it)
                            },
                        )
                        Row(
                            modifier = Modifier
                                .clickable {
                                    currentInterval = monthly.copy(sameDay = true)
                                }
                                .testTag(NotificationDialogIntervalTestTags.MONTHLY_SAME_DAY_ROW),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = monthly.sameDay,
                                onClick = { currentInterval = monthly.copy(sameDay = true) },
                                modifier = Modifier.testTag(
                                    NotificationDialogIntervalTestTags
                                        .MONTHLY_SAME_DAY_RADIO,
                                ),
                            )
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag(NotificationDialogIntervalTestTags.MONTHLY_SAME_DAY_TEXT),
                                text = stringResource(Res.string.notification_dialog_interval_monthly_same_day),
                            )
                        }
                        Row(
                            modifier = Modifier
                                .clickable {
                                    currentInterval = monthly.copy(sameDay = false)
                                }
                                .testTag(NotificationDialogIntervalTestTags.MONTHLY_DAY_OF_WEEK_ROW),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = !monthly.sameDay,
                                onClick = { currentInterval = monthly.copy(sameDay = false) },
                                modifier = Modifier.testTag(
                                    NotificationDialogIntervalTestTags
                                        .MONTHLY_DAY_OF_WEEK_RADIO,
                                ),
                            )
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag(NotificationDialogIntervalTestTags.MONTHLY_DAY_OF_WEEK_TEXT),
                                text = stringResource(
                                    Res.string.notification_dialog_interval_monthly_day_of_week,
                                    daysOfWeek[todayDate.dayOfWeek.ordinal],
                                ),
                            )
                        }

                        IntervalRepeatEnd(
                            modifier = Modifier.testTag(
                                "${NotificationDialogIntervalTestTags
                                    .REPEAT_END_ROOT_ROW_PREFIX}_monthly",
                            ),
                            currentIntervalEnd = monthly.intervalEnd,
                            todayDate = todayDate,
                            onValueChange = {
                                currentInterval = monthly.copy(intervalEnd = it)
                            },
                        )
                    }

                    is RepeatSchedule.Yearly -> {
                        val yearly = currentInterval as RepeatSchedule.Yearly
                        IntervalTextField(
                            modifier = Modifier.testTag(
                                "${NotificationDialogIntervalTestTags
                                    .INTERVAL_TF_ROOT_PREFIX}_yearly",
                            ),
//                            textFieldModifier = Modifier.testTag(
//                                "${NotificationDialogIntervalTestTags
//                                    .INTERVAL_TF_TEXT_FIELD_PREFIX}_yearly",
//                            ),
                            prefix = stringResource(Res.string.interval_text_field_prefix_every),
                            suffix = stringResource(Res.string.interval_text_field_suffix_year),
                            suffixPlural = stringResource(Res.string.interval_text_field_suffix_years),
                            text = yearly.interval,
                            onValueChange = {
                                currentInterval = yearly.copy(interval = it)
                            },
                        )

                        IntervalRepeatEnd(
                            modifier = Modifier.testTag(
                                "${NotificationDialogIntervalTestTags
                                    .REPEAT_END_ROOT_ROW_PREFIX}_yearly",
                            ),
                            currentIntervalEnd = yearly.intervalEnd,
                            todayDate = todayDate,
                            onValueChange = {
                                currentInterval = yearly.copy(intervalEnd = it)
                            },
                        )
                    }

                    is RepeatSchedule.DoNotRepeat -> {
                        Spacer(modifier = Modifier.height(64.dp))
                    }

                    is RepeatSchedule.Custom -> {
                    }
                }

                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .testTag(NotificationDialogIntervalTestTags.ACTIONS_ROW),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    SynTextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag(NotificationDialogIntervalTestTags.CLOSE_BUTTON),
                        label = stringResource(Res.string.notification_dialog_interval_close_button),
                    )
                    SynButton(
                        onClick = {
                            onValueChange(currentInterval)
                        },
                        modifier = Modifier.testTag(NotificationDialogIntervalTestTags.SET_REPEAT_BUTTON),
                        label = stringResource(Res.string.notification_dialog_interval_set_repeat_button),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Preview
@Composable
fun NotificationDialogIntervalPreview() {
    val nowDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val currentInterval = RepeatSchedule.Monthly(
        intervalEnd = IntervalEnd.EndDate(LocalDate(2023, 1, 1)),
        sameDay = true,
        //  days = setOf(0, 4, 6),
    )
    val intervals = listOf(
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
    NotificationDialogInterval(
        initInterval = currentInterval,
        intervals = intervals,
        onValueChange = {},
        todayDate = nowDate,
    )
}

@Composable
fun IntervalTextField(
    modifier: Modifier = Modifier,
//    textFieldModifier: Modifier = Modifier, // Added for specific TextField tagging
    prefix: String = "",
    suffix: String = "",
    suffixPlural: String = "",
    text: String = "",
    onValueChange: (String) -> Unit = {},
) {
    val state: TextFieldState = rememberTextFieldState(text)
    LaunchedEffect(key1 = state.text) {
        onValueChange(state.text.toString())
    }

    TextField(
        modifier = modifier
            .width(172.dp),
//            .then(textFieldModifier), // Apply the specific text field modifier
        state = state,
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        lineLimits = TextFieldLineLimits.SingleLine,
        inputTransformation = DigitsOnlyInputTransformation(2),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            showKeyboardOnFocus = true,
        ),
        prefix = { Text(text = prefix) },
        suffix = {
            Text(
                text = when {
                    state.text.isBlank() -> suffix
                    state.text.toString().toIntOrNull()?.let { it > 1 } == true -> suffixPlural
                    else -> suffix
                },
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

        ),
    )
}

@Preview
@Composable
fun IntervalTextFieldPreview() {
    IntervalTextField(
        prefix = stringResource(Res.string.interval_text_field_prefix_every),
        suffix = stringResource(Res.string.interval_text_field_suffix_days),
        text = "44",
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun IntervalRepeatEnd(
    modifier: Modifier = Modifier,
    currentIntervalEnd: IntervalEnd,
    todayDate: LocalDate,
    onValueChange: (IntervalEnd) -> Unit = {},
) {
    val intervalEndStringArray = stringArrayResource(
        Res.array.core_ui_notification_interval_end2,
    )

    val intervalsEnds = remember {
        listOf(
            IntervalEnd.Forever,
            IntervalEnd.EndDate(todayDate),
            IntervalEnd.NumberOfTimes(1),
        )
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentIntervalEnd) {
        state.clearText()
        state.edit {
            append(intervalEndStringArray.getOrNull(currentIntervalEnd.index) ?: "")
        }
    }

    Row(
        modifier = modifier, // Root modifier for IntervalRepeatEnd instance
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier
                .weight(3f)
                .testTag(NotificationDialogIntervalTestTags.REPEAT_END_TYPE_DROPDOWN_ROOT),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true)
                    .testTag(NotificationDialogIntervalTestTags.REPEAT_END_TYPE_TEXT_FIELD),
                readOnly = true,
                state = state,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,

                ),
                lineLimits = TextFieldLineLimits.SingleLine,

            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.testTag(
                    NotificationDialogIntervalTestTags
                        .REPEAT_END_TYPE_MENU,
                ),
            ) {
                intervalsEnds.forEach { intervalEndItem ->
                    DropdownMenuItem(
                        text = { Text(text = intervalEndStringArray[intervalEndItem.index]) },
                        onClick = {
                            onValueChange(intervalEndItem)
                            expanded = false
                        },
                        modifier = Modifier.testTag(
                            "${NotificationDialogIntervalTestTags
                                .REPEAT_END_TYPE_MENU_ITEM_PREFIX}_${
                                intervalEndStringArray[intervalEndItem.index]
                                    .lowercase().replace(" ", "_")}",
                        ),
                    )
                }
            }
        }

        when (currentIntervalEnd) {
            IntervalEnd.Forever -> {}
            is IntervalEnd.EndDate -> {
                var showDateDialog by remember {
                    mutableStateOf(false)
                }
                val dateTextFiledState = rememberTextFieldState()
                LaunchedEffect(key1 = currentIntervalEnd.date) {
                    dateTextFiledState.clearText()
                    dateTextFiledState.edit {
                        append(currentIntervalEnd.date.toString())
                    }
                }
                TextField(
                    modifier = Modifier
                        .weight(3f)
                        .testTag(NotificationDialogIntervalTestTags.END_DATE_TEXT_FIELD),
                    state = dateTextFiledState,
                    readOnly = true,
                    lineLimits = TextFieldLineLimits.SingleLine,

                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,

                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = { showDateDialog = true },
                            modifier = Modifier.testTag(
                                NotificationDialogIntervalTestTags
                                    .END_DATE_ICON_BUTTON,
                            ),
                        ) {
                            Icon(
                                SynIcons.DateRange,
                                contentDescription = stringResource(
                                    Res.string.notification_dialog_interval_date_icon_cd,
                                ),
                            )
                        }
                    },
                )
                if (showDateDialog) {
                    val dateState =
                        rememberDatePickerState(
                            initialSelectedDateMillis =
                            currentIntervalEnd.date.toEpochDays() * 86400000L,
                        ) // Ensure millis

                    DatePickerDialog(
                        onDismissRequest = {
                            showDateDialog = false
                        },
                        confirmButton = {
                            SynButton(
                                onClick = {
                                    showDateDialog = false
                                    val date = dateState.selectedDateMillis?.let { millis ->
                                        Instant.fromEpochMilliseconds(millis)
                                            .toLocalDateTime(TimeZone.currentSystemDefault())
                                            .date
                                    } ?: LocalDate(1970, 1, 1) // Default or handle error
                                    onValueChange(
                                        currentIntervalEnd.copy(date = date),
                                    )
                                },
                                modifier = Modifier.testTag(
                                    NotificationDialogIntervalTestTags
                                        .END_DATE_PICKER_CONFIRM_BUTTON,
                                ),
                                label = stringResource(Res.string.date_dialog_set_date_button),
                            )
                        },
                        dismissButton = {
                            SynTextButton(
                                onClick = {
                                    showDateDialog = false
                                },
                                modifier = Modifier.testTag(
                                    NotificationDialogIntervalTestTags
                                        .END_DATE_PICKER_DISMISS_BUTTON,
                                ),
                                label = stringResource(Res.string.cancel),
                            )
                        },
                        modifier = Modifier.testTag(
                            NotificationDialogIntervalTestTags
                                .END_DATE_PICKER_DIALOG_ROOT,
                        ),
                    ) {
                        DatePicker(
                            state = dateState,
                            modifier = Modifier.testTag(
                                NotificationDialogIntervalTestTags
                                    .END_DATE_PICKER,
                            ),
                        )
                    }
                }
            }

            is IntervalEnd.NumberOfTimes -> {
                val numberOfTimesState = rememberTextFieldState(currentIntervalEnd.times.toString())
                LaunchedEffect(key1 = numberOfTimesState.text) {
                    numberOfTimesState.text.toString().toIntOrNull()?.let {
                        // Safely convert to Int
                        onValueChange(currentIntervalEnd.copy(times = it))
                    }
                }
                TextField(
                    modifier = Modifier
                        .weight(2f)
                        .testTag(NotificationDialogIntervalTestTags.NUMBER_OF_TIMES_TEXT_FIELD),
                    state = numberOfTimesState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    inputTransformation = DigitsOnlyInputTransformation(2),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        showKeyboardOnFocus = true,
                    ),
                    suffix = { Text(text = stringResource(Res.string.notification_dialog_interval_events_suffix)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,

                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun InvervalRepeatEndPreview() {
    IntervalRepeatEnd(
        currentIntervalEnd = IntervalEnd.Forever,
        todayDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    )
}

class DigitsOnlyInputTransformation(private val maxLength: Int = Int.MAX_VALUE) :
    InputTransformation {

    override fun TextFieldBuffer.transformInput() {
        val originalText = asCharSequence().toString()
        val newText = originalText.filter { it.isDigit() }

        // If newText is longer than maxLength, truncate it
        val finalText = if (newText.length > maxLength) {
            newText.substring(0, maxLength)
        } else {
            newText
        }

        // Only update if the filtered/truncated text is different from what's already in the buffer
        // or if the original text had non-digit characters that were removed.
        if (finalText != asCharSequence().toString() || originalText.any { !it.isDigit() }) {
            // Replace the entire buffer with the filtered and potentially truncated text
            replace(0, length, finalText)
        }
    }
}
