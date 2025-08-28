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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier // Added import
import androidx.compose.ui.platform.testTag // Added import
import com.mshdabiola.designsystem.component.SynButton
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.model.testtag.DateDialogTestTags // Added import
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DateDialog(
    state: DatePickerState,
    showDialog: Boolean = true,
    onDismissRequest: () -> Unit = {},
    onSetDate: () -> Unit = {},
) {
    AnimatedVisibility(visible = showDialog) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                SynButton(
                    onClick = {
                        onSetDate()
                        onDismissRequest()
                    },
                    label = "Set date",
                    modifier = Modifier.testTag(DateDialogTestTags.CONFIRM_BUTTON),
                )
            },
            dismissButton = {
                SynTextButton(
                    label = "Cancel",
                    onClick = onDismissRequest,
                    modifier = Modifier.testTag(DateDialogTestTags.DISMISS_BUTTON),
                )
            },
            modifier = Modifier.testTag(DateDialogTestTags.DIALOG_ROOT),
        ) {
            DatePicker(
                state = state,
                modifier = Modifier.testTag(DateDialogTestTags.DATE_PICKER),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DateDialogPreview() {
    val rememberDatePickerState = rememberDatePickerState()
    DateDialog(
        state = rememberDatePickerState,
    )
}
