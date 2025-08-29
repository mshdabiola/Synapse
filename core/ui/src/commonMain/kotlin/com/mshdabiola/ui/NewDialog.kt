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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag // Added import
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.component.SynButton
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.model.testtag.NewDialogTestTags // Added import
import com.mshdabiola.ui.state.DateDialogUiData
import com.mshdabiola.ui.state.DateListUiState
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NotificationDialogNew(
    dateDialogUiData: DateDialogUiData,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onSetAlarm: () -> Unit = { },
    onDeleteAlarm: () -> Unit = {},
    onTimeChange: (Int) -> Unit = {},
    onDateChange: (Int) -> Unit = {},
    onIntervalChange: (Int) -> Unit = {},

) {
    if(showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = if (dateDialogUiData.isEdit) "Edit Reminder" else "Add Reminder",
                    modifier = Modifier.testTag(NewDialogTestTags.DIALOG_TITLE),
                )
            },
            text = {
                Column {
                    TextDropbox(
                        modifier = Modifier.testTag(NewDialogTestTags.TIME_DROPBOX_ROOT),
                        textFieldTestTag = NewDialogTestTags.TIME_DROPBOX_TEXT_FIELD,
                        currentIndex = dateDialogUiData.currentTime,
                        showError = dateDialogUiData.timeError,
                        onValueChange = onTimeChange,
                        times = dateDialogUiData.timeData,
                    )
                    TextDropbox(
                        modifier = Modifier.testTag(NewDialogTestTags.DATE_DROPBOX_ROOT),
                        textFieldTestTag = NewDialogTestTags.DATE_DROPBOX_TEXT_FIELD,
                        currentIndex = dateDialogUiData.currentDate,
                        showError = false,
                        onValueChange = onDateChange,
                        times = dateDialogUiData.dateData,
                    )
                    TextDropbox(
                        modifier = Modifier.testTag(NewDialogTestTags.INTERVAL_DROPBOX_ROOT),
                        textFieldTestTag = NewDialogTestTags.INTERVAL_DROPBOX_TEXT_FIELD,
                        currentIndex = dateDialogUiData.currentInterval,
                        showError = false,
                        onValueChange = onIntervalChange,
                        times = dateDialogUiData.interval,
                    )
                }
            },
            confirmButton = {
                SynButton(
                    onClick = {
                        onSetAlarm()
                        onDismissRequest()
                    },
                    enabled = !dateDialogUiData.timeError,
                    label = "Save",
                    modifier = Modifier.testTag(NewDialogTestTags.CONFIRM_BUTTON),
                )
            },
            dismissButton = {
                Row {
                    if (dateDialogUiData.isEdit) {
                        SynTextButton(
                            onClick = {
                                onDismissRequest()
                                onDeleteAlarm()
                            },
                            label = "Delete",
                            modifier = Modifier.testTag(NewDialogTestTags.DELETE_BUTTON),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    SynTextButton(
                        onClick = { onDismissRequest() },
                        label = "Cancel",
                        modifier = Modifier.testTag(NewDialogTestTags.CANCEL_BUTTON),
                    )
                }
            },
            modifier = Modifier.testTag(NewDialogTestTags.DIALOG_ROOT),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextDropbox(
    modifier: Modifier = Modifier, // Added modifier parameter
    textFieldTestTag: String, // Added test tag parameter for the TextField
    currentIndex: Int,
    onValueChange: (Int) -> Unit = {},
    times: List<DateListUiState> = emptyList<DateListUiState>(),
    showError: Boolean,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        modifier = modifier, // Applied incoming modifier
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .testTag(textFieldTestTag), // Applied specific TextField test tag
            readOnly = true,
            value = times[currentIndex].value,
            supportingText = {
                if (showError) {
                    Text(
                        text = "Time as past",
                        modifier = Modifier.testTag(NewDialogTestTags.DROPBOX_ERROR_TEXT),
                    )
                }
            },
            isError = showError,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true,

        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.testTag(NewDialogTestTags.EXPOSED_DROPDOWN_MENU),
        ) {
            times.forEachIndexed { index, pair ->
                DropdownMenuItem(
                    text = { Text(text = pair.title) },
                    onClick = {
                        onValueChange(index)
                        expanded = false
                    },
                    enabled = pair.enable,
                    trailingIcon = {
                        pair.trail?.let {
                            Text(
                                text = it,
                            )
                        }
                    },
                    modifier = Modifier.testTag(
                        "${NewDialogTestTags.MENU_ITEM_PREFIX}_${pair.title.replace(" ", "_").lowercase()}",
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
fun NewDialogPreview() {
    val dateDialog = DateDialogUiData(
        isEdit = false,
        currentTime = 0,
        timeData = listOf(
            DateListUiState(
                title = "Morning",
                value = "8:00PM",
                trail = "8:00PM",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Afternoon",
                value = "8:00PM",
                trail = "8:00PM",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Evening",
                value = "8:00PM",
                trail = "8:00PM",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Night",
                value = "8:00PM",
                trail = "8:00PM",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Pick time",
                value = "8:00PM",
                isOpenDialog = true,
                enable = true,
            ),

        ).toImmutableList(),
        timeError = false,
        currentDate = 0,
        dateData = listOf(
            DateListUiState(
                title = "Today",
                value = "Today",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Tomorrow",
                value = "Tomorrow",
                isOpenDialog = true,
                enable = true,
            ),
            DateListUiState(
                title = "Pick date",
                value = "Jan 1",
                isOpenDialog = true,
                enable = true,
            ),
        ).toImmutableList(),
        currentInterval = 0,
        interval = listOf(
            DateListUiState(
                title = "Does not repeat",
                value = "Does not repeat",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Daily",
                value = "Daily",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Weekly",
                value = "Weekly",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Monthly",
                value = "Monthly",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Yearly",
                value = "Yearly",
                isOpenDialog = false,
                enable = true,
            ),
        ).toImmutableList(),

    )

    NotificationDialogNew(
        showDialog = true,
        dateDialogUiData = dateDialog,
    )
}
