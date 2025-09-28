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

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.component.SynTopAppBar
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.main.model.SelectState
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.testtag.SelectAppBarTestTags
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.feature_main_archive
import synapse.feature.main.generated.resources.feature_main_delete
import synapse.feature.main.generated.resources.feature_main_make_a_copy
import synapse.feature.main.generated.resources.feature_main_send
import synapse.feature.main.generated.resources.feature_main_unarchive
import synapse.feature.main.generated.resources.select_app_bar_clear_note_cd
import synapse.feature.main.generated.resources.select_app_bar_color_cd
import synapse.feature.main.generated.resources.select_app_bar_label_cd
import synapse.feature.main.generated.resources.select_app_bar_more_cd
import synapse.feature.main.generated.resources.select_app_bar_notification_cd
import synapse.feature.main.generated.resources.select_app_bar_pin_note_cd

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectAppBar(
    modifier: Modifier = Modifier,
    noteDisplayCategory: NoteDisplayCategory,
    selectState: SelectState,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onClearSelection: () -> Unit = {},
    onPinNotes: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSelectColor: () -> Unit = {},
    onLabelNotes: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDeleteNotes: () -> Unit = {},
    onShareNote: () -> Unit = {},
    onCopyNote: () -> Unit = {},
) {
    SynTopAppBar(
        modifier = modifier.testTag(SelectAppBarTestTags.ROOT_APP_BAR),
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = onClearSelection,
                modifier = Modifier.testTag(SelectAppBarTestTags.CLEAR_SELECTION_BUTTON),
            ) {
                Icon(
                    imageVector = SynIcons.Clear,
                    contentDescription = stringResource(Res.string.select_app_bar_clear_note_cd),
                )
            }
        },
        title = {
            Text(
                text = selectState.setOfSelected.size.toString(),
                modifier = Modifier.testTag(SelectAppBarTestTags.TITLE_TEXT),
            )
        },
        subtitle = {},
        actions = {
            var showDropDown by remember {
                mutableStateOf(false)
            }

            IconButton(
                modifier = Modifier.testTag(SelectAppBarTestTags.PIN_BUTTON),
                onClick = onPinNotes,
            ) {
                Icon(
                    imageVector = if (selectState.isAllPin) SynIcons.PushPin else SynIcons.PushPinOutlined,
                    contentDescription = stringResource(Res.string.select_app_bar_pin_note_cd),
                )
            }
            IconButton(
                modifier = Modifier.testTag(SelectAppBarTestTags.NOTIFICATION_BUTTON),
                onClick = onNotificationClick,
            ) {
                Icon(
                    imageVector = SynIcons.NotificationAdd,
                    contentDescription = stringResource(Res.string.select_app_bar_notification_cd),
                )
            }
            IconButton(
                modifier = Modifier.testTag(SelectAppBarTestTags.COLOR_BUTTON),
                onClick = onSelectColor,
            ) {
                Icon(
                    imageVector = SynIcons.ColorLens,
                    contentDescription = stringResource(Res.string.select_app_bar_color_cd),
                )
            }
            IconButton(
                modifier = Modifier.testTag(SelectAppBarTestTags.LABEL_BUTTON),
                onClick = onLabelNotes,
            ) {
                Icon(
                    imageVector = SynIcons.Label,
                    contentDescription = stringResource(Res.string.select_app_bar_label_cd),
                )
            }
            Box {
                IconButton(
                    modifier = Modifier.testTag(SelectAppBarTestTags.MORE_OPTIONS_BUTTON),
                    onClick = { showDropDown = true },
                ) {
                    Icon(
                        SynIcons.MoreVert,
                        contentDescription = stringResource(Res.string.select_app_bar_more_cd),
                    )
                }
                DropdownMenu(
                    expanded = showDropDown,
                    onDismissRequest = { showDropDown = false },
                    modifier = Modifier.testTag(SelectAppBarTestTags.GENERAL_OPTIONS_DROPDOWN),
                ) {
                    DropdownMenuItem(
                        modifier = Modifier.testTag(SelectAppBarTestTags.ARCHIVE_UNARCHIVE_MENU_ITEM),
                        text = {
                            Text(
                                text =
                                if (noteDisplayCategory.noteCategory == NoteCategory.ARCHIVE) {
                                    stringResource(Res.string.feature_main_unarchive)
                                } else {
                                    stringResource(Res.string.feature_main_archive)
                                },
                            )
                        },
                        onClick = {
                            showDropDown = false
                            onArchive()
                        },
                    )
                    DropdownMenuItem(
                        modifier = Modifier.testTag(SelectAppBarTestTags.DELETE_MENU_ITEM),
                        text = { Text(text = stringResource(Res.string.feature_main_delete)) },
                        onClick = {
                            showDropDown = false
                            onDeleteNotes()
                        },
                    )
                    if (selectState.setOfSelected.size == 1) {
                        DropdownMenuItem(
                            modifier = Modifier.testTag(SelectAppBarTestTags.MAKE_COPY_MENU_ITEM),
                            text = {
                                Text(text = stringResource(Res.string.feature_main_make_a_copy))
                            },
                            onClick = {
                                showDropDown = false
                                onCopyNote()
                            },
                        )
                        DropdownMenuItem(
                            modifier = Modifier.testTag(SelectAppBarTestTags.SEND_MENU_ITEM),
                            text = { Text(text = stringResource(Res.string.feature_main_send)) },
                            onClick = {
                                showDropDown = false
                                onShareNote()
                            },
                        )
                    }
                }
            }
        },
        color = MaterialTheme.colorScheme.secondaryContainer,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun SelectAppBarPreview() {
    SelectAppBar(
        noteDisplayCategory = NoteDisplayCategory(
            labelId = 1,
            noteCategory = NoteCategory.NOTE,
        ),
        selectState = SelectState(
            colorIndex = -1,
            isAllPin = false,
            setOfSelected = setOf(1L, 2L, 3L),
            notificationUiState = null,
        ),
    )
}
