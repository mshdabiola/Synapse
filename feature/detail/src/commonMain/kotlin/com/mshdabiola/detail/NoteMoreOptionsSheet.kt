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

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.designsystem.theme.ColorFamily
import com.mshdabiola.designsystem.theme.LocalExtendedColorScheme
import com.mshdabiola.designsystem.theme.unspecified_scheme
import com.mshdabiola.model.testtag.NoteMoreOptionsSheetTestTags
import org.jetbrains.compose.resources.stringResource
import synapse.feature.detail.generated.resources.Res
import synapse.feature.detail.generated.resources.feature_detail_delete
import synapse.feature.detail.generated.resources.feature_detail_labels
import synapse.feature.detail.generated.resources.feature_detail_make_a_copy
import synapse.feature.detail.generated.resources.feature_detail_send

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteOptionsMenu(
    show: Boolean,
    currentColor: Int,
    currentImage: Int,
    onDelete: () -> Unit = {},
    onCopy: () -> Unit = {},
    onSendNote: () -> Unit = {},
    onLabel: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    if (show) {
        val noteColor = if (currentImage != -1) {
            LocalExtendedColorScheme.current.noteBackGround[currentImage]
        } else {
            if (currentColor != -1) {
                LocalExtendedColorScheme.current.noteColor[currentColor]
            } else {
                ColorFamily(
                    color = MaterialTheme.colorScheme.surface,
                    colorContainer = MaterialTheme.colorScheme.surfaceContainer,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onColorContainer = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        val navColor = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = noteColor.colorContainer,
            unselectedIconColor = noteColor.onColorContainer,
            unselectedTextColor = noteColor.onColorContainer,
        )

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            containerColor = noteColor.colorContainer,
        ) {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.Delete,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.feature_detail_delete)) },
                selected = false,
                onClick = {
                    onDelete()
                    onDismissRequest()
                },
                colors = navColor,
                modifier = Modifier.testTag(NoteMoreOptionsSheetTestTags.DELETE_BUTTON),
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.ContentCopy,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.feature_detail_make_a_copy)) },
                selected = false,
                onClick = {
                    onCopy()
                    onDismissRequest()
                },
                colors = navColor,
                modifier = Modifier.testTag(NoteMoreOptionsSheetTestTags.COPY_BUTTON),
            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.Share,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.feature_detail_send)) },
                selected = false,
                onClick = {
                    onSendNote()
                    onDismissRequest()
                },
                colors = navColor,
                modifier = Modifier.testTag(NoteMoreOptionsSheetTestTags.SEND_BUTTON),
            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.Label,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.feature_detail_labels)) },
                selected = false,
                onClick = {
                    onLabel()
                    onDismissRequest()
                },
                colors = navColor,
                modifier = Modifier.testTag(NoteMoreOptionsSheetTestTags.LABEL_BUTTON),
            )

//                NavigationDrawerItem(icon = {
//                    Icon(
//                        imageVector = ImageVector.vectorResource(id = NoteIcon.Save),
//                        contentDescription = ""
//                    )
//                }, label = { Text(text = "Save as txt") },
//                    selected = false, onClick = {
//                        onSaveText()
//                        coroutineScope.launch { modalState.hide() }
//
//                    })
        }
    }
}
