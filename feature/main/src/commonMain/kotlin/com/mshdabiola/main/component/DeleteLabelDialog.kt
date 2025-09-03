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
package com.mshdabiola.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.model.testtag.DeleteLabelDialogTestTags
import org.jetbrains.compose.resources.stringResource
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.modules_designsystem_cancel
import synapse.feature.main.generated.resources.modules_designsystem_delete
import synapse.feature.main.generated.resources.modules_designsystem_rename_label
import synapse.feature.main.generated.resources.modules_designsystem_rename_label_detail

@Composable
fun DeleteLabelAlertDialog(
    modifier: Modifier = Modifier, // Added modifier parameter
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            modifier = modifier.testTag(DeleteLabelDialogTestTags.DIALOG_ROOT),
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Res.string.modules_designsystem_rename_label), modifier = Modifier.testTag(DeleteLabelDialogTestTags.TITLE_TEXT)) },
            text = {
                Text(text = stringResource(Res.string.modules_designsystem_rename_label_detail), modifier = Modifier.testTag(DeleteLabelDialogTestTags.CONTENT_TEXT))
            },
            confirmButton = {
                SynTextButton(
                    onClick = {
                        onDismissRequest()
                        onDelete()
                    },
                    label = stringResource(Res.string.modules_designsystem_delete),
                    modifier = Modifier.testTag(DeleteLabelDialogTestTags.CONFIRM_BUTTON)
                )
            },
            dismissButton = {
                SynTextButton(
                    onClick = { onDismissRequest() },
                    label = stringResource(Res.string.modules_designsystem_cancel),
                    modifier = Modifier.testTag(DeleteLabelDialogTestTags.DISMISS_BUTTON)
                )
            },
        )
    }
}
