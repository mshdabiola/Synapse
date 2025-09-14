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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.component.SynButton
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.designsystem.component.SynTextField
import com.mshdabiola.model.testtag.RenameLabelDialogTestTags
import org.jetbrains.compose.resources.stringResource
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.feature_main_cancel
import synapse.feature.main.generated.resources.feature_main_rename
import synapse.feature.main.generated.resources.feature_main_rename_label

@Composable
fun RenameLabelAlertDialog(
    modifier: Modifier = Modifier,
    show: Boolean = false,
    label: String = "Label",
    onDismissRequest: () -> Unit = {},
    onChangeName: (String) -> Unit = {},
) {
    val name = rememberTextFieldState(label)

    AnimatedVisibility(visible = show) {
        AlertDialog(
            modifier = modifier.testTag(RenameLabelDialogTestTags.DIALOG_ROOT),
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = stringResource(Res.string.feature_main_rename_label),
                    modifier = Modifier.testTag(RenameLabelDialogTestTags.TITLE_TEXT),
                )
            },
            text = {
                SynTextField(
                    state = name,
                    modifier = Modifier.testTag(RenameLabelDialogTestTags.NAME_TEXT_FIELD),
                )
            },
            confirmButton = {
                SynButton(
                    onClick = {
                        onDismissRequest()
                        onChangeName(name.text.toString())
                    },
                    label = stringResource(Res.string.feature_main_rename),
                    modifier = Modifier.testTag(RenameLabelDialogTestTags.CONFIRM_BUTTON),
                )
            },
            dismissButton = {
                SynTextButton(
                    onClick = { onDismissRequest() },
                    label = stringResource(Res.string.feature_main_cancel),
                    modifier = Modifier.testTag(RenameLabelDialogTestTags.DISMISS_BUTTON),
                )
            },
        )
    }
}
