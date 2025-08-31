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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.main.model.SelectState
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.modules_designsystem_archive
import synapse.feature.main.generated.resources.modules_designsystem_delete
import synapse.feature.main.generated.resources.modules_designsystem_delete_forever
import synapse.feature.main.generated.resources.modules_designsystem_delete_label
import synapse.feature.main.generated.resources.modules_designsystem_empty_trash
import synapse.feature.main.generated.resources.modules_designsystem_make_a_copy
import synapse.feature.main.generated.resources.modules_designsystem_rename_label
import synapse.feature.main.generated.resources.modules_designsystem_search_note
import synapse.feature.main.generated.resources.modules_designsystem_send
import synapse.feature.main.generated.resources.modules_designsystem_unarchive

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    noteDisplayCategory: NoteDisplayCategory,
    isGrid: Boolean = false,
    selectState: SelectState? = null,
    labelName: String? = null,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onDisplayModeChange: () -> Unit = {},
    onHamburgerMenuClick: () -> Unit = {},

    onClearSelection: () -> Unit = {},
    onPinNotes: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSelectColor: () -> Unit = {},
    onLabelNotes: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDeleteNotes: () -> Unit = {},
    onShareNote: () -> Unit = {},
    onCopyNote: () -> Unit = {},
    onDeleteForever: () -> Unit = {},
    onRestore: () -> Unit = {},

    onSearchClick: () -> Unit = {},
    onLabelNameChange: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},

    onDeleteAllTrash: () -> Unit = {},

    ) {
    val navigationAction: @Composable () -> Unit = {
        if (selectState != null) {
            IconButton(
                onClick = onClearSelection,
                modifier = Modifier.testTag("main:topbar_clear_selection_button"),
            ) {
                Icon(imageVector = SynIcons.Clear, contentDescription = "clear note")
            }
        } else {
            IconButton(
                onClick = onHamburgerMenuClick,
                modifier = Modifier.testTag("main:topbar_hamburger_menu_button"),
            ) {
                Icon(imageVector = SynIcons.Menu, contentDescription = "menu")
            }
        }
    }
    val actions: @Composable RowScope.() -> Unit =
        {
            if (selectState != null) {
                when (noteDisplayCategory.noteCategory) {
                    NoteCategory.TRASH -> {
                        var showDropDown by remember {
                            mutableStateOf(false)
                        }

                        IconButton(
                            modifier = Modifier.testTag("main:topbar_restore_button"),
                            onClick = onRestore,
                        ) {
                            Icon(
                                imageVector = SynIcons.RestoreFromTrash,
                                contentDescription = "restore note",
                            )
                        }
                        Box {
                            IconButton(
                                modifier = Modifier.testTag("main:topbar_more_options_button"),
                                onClick = { showDropDown = true },
                            ) {
                                Icon(SynIcons.MoreVert, contentDescription = "more")
                            }
                            DropdownMenu(
                                expanded = showDropDown,
                                onDismissRequest = { showDropDown = false },
                                modifier = Modifier.testTag("main:topbar_trash_options_dropdown"),
                            ) {
                                DropdownMenuItem(
                                    modifier = Modifier.testTag("main:topbar_delete_forever_menu_item"),
                                    text = {
                                        Text(
                                            text =
                                            stringResource(Res.string.modules_designsystem_delete_forever),
                                        )
                                    },
                                    onClick = {
                                        showDropDown = false
                                        onDeleteForever()
                                    },
                                )
                            }
                        }
                    }

                    else -> { // Covers NOTE, ARCHIVE, LABEL, REMINDER when selectState is not null
                        var showDropDown by remember {
                            mutableStateOf(false)
                        }

                        IconButton(
                            modifier = Modifier.testTag("main:topbar_pin_button"),
                            onClick = onPinNotes,
                        ) {
                            Icon(
                                imageVector = if (selectState.isAllPin) SynIcons.PushPinD else SynIcons.PushPin,
                                contentDescription = "pin note",
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag("main:topbar_notification_button"),
                            onClick = onNotificationClick,
                        ) {
                            Icon(
                                imageVector = SynIcons.Notification,
                                contentDescription = "notification",
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag("main:topbar_color_button"),
                            onClick = onSelectColor,
                        ) {
                            Icon(
                                imageVector = SynIcons.ColorLens,
                                contentDescription = "color",
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag("main:topbar_label_button"),
                            onClick = onLabelNotes,
                        ) {
                            Icon(imageVector = SynIcons.Label, contentDescription = "Label")
                        }
                        Box {
                            IconButton(
                                modifier = Modifier.testTag("main:topbar_more_options_button"),
                                onClick = { showDropDown = true },
                            ) {
                                Icon(SynIcons.MoreVert, contentDescription = "more")
                            }
                            DropdownMenu(
                                expanded = showDropDown,
                                onDismissRequest = { showDropDown = false },
                                modifier = Modifier.testTag("main:topbar_general_options_dropdown"),
                            ) {
                                DropdownMenuItem(
                                    modifier = Modifier.testTag("main:topbar_archive_unarchive_menu_item"),
                                    text = {
                                        Text(
                                            text =
                                            if (noteDisplayCategory.noteCategory == NoteCategory.ARCHIVE) {
                                                stringResource(Res.string.modules_designsystem_unarchive)
                                            } else {
                                                stringResource(Res.string.modules_designsystem_archive)
                                            },
                                        )
                                    },
                                    onClick = {
                                        showDropDown = false
                                        onArchive()
                                    },
                                )
                                DropdownMenuItem(
                                    modifier = Modifier.testTag("main:topbar_delete_menu_item"),
                                    text = { Text(text = stringResource(Res.string.modules_designsystem_delete)) },
                                    onClick = {
                                        showDropDown = false
                                        onDeleteNotes()
                                    },
                                )
                                if (selectState.setOfSelected.size == 1) {
                                    DropdownMenuItem(
                                        modifier = Modifier.testTag("main:topbar_make_copy_menu_item"),
                                        text = {
                                            Text(text = stringResource(Res.string.modules_designsystem_make_a_copy))
                                        },
                                        onClick = {
                                            showDropDown = false
                                            onCopyNote()
                                        },
                                    )
                                    DropdownMenuItem(
                                        modifier = Modifier.testTag("main:topbar_send_menu_item"),
                                        text = { Text(text = stringResource(Res.string.modules_designsystem_send)) },
                                        onClick = {
                                            showDropDown = false
                                            onShareNote()
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            } else { // selectState is null (normal viewing mode)
                when (noteDisplayCategory.noteCategory) {
                    NoteCategory.NOTE -> {
                        IconButton(
                            onClick = { onDisplayModeChange() },
                            modifier = Modifier.testTag("main:topbar_display_mode_button"),
                        ) {
                            if (!isGrid) {
                                Icon(imageVector = SynIcons.GridView, contentDescription = "grid")
                            } else {
                                Icon(
                                    imageVector = SynIcons.ViewAgenda,
                                    contentDescription = "column",
                                )
                            }
                        }
                    }

                    NoteCategory.REMINDER -> {
                        IconButton(
                            onClick = onSearchClick,
                            modifier = Modifier.testTag("main:topbar_search_button"),
                        ) {
                            Icon(
                                imageVector = SynIcons.Search,
                                contentDescription = "search",
                            )
                        }
                        IconButton(
                            onClick = { onDisplayModeChange() },
                            modifier = Modifier.testTag("main:topbar_display_mode_button"),
                        ) {
                            if (!isGrid) {
                                Icon(imageVector = SynIcons.GridView, contentDescription = "grid")
                            } else {
                                Icon(
                                    imageVector = SynIcons.ViewAgenda,
                                    contentDescription = "column",
                                )
                            }
                        }
                    }

                    NoteCategory.LABEL -> {
                        var showDropDown by remember {
                            mutableStateOf(false)
                        }

                        IconButton(
                            onClick = onSearchClick,
                            modifier = Modifier.testTag("main:topbar_search_button"),
                        ) {
                            Icon(
                                imageVector = SynIcons.Search,
                                contentDescription = "search",
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { showDropDown = true },
                                modifier = Modifier.testTag("main:topbar_more_options_button"),
                            ) {
                                Icon(SynIcons.MoreVert, contentDescription = "more")
                            }
                            DropdownMenu(
                                expanded = showDropDown,
                                onDismissRequest = { showDropDown = false },
                                modifier = Modifier.testTag("main:topbar_label_options_dropdown"),
                            ) {
                                DropdownMenuItem(
                                    modifier = Modifier.testTag("main:topbar_rename_label_menu_item"),
                                    text = {
                                        Text(text = stringResource(Res.string.modules_designsystem_rename_label))
                                    },
                                    onClick = {
                                        showDropDown = false
                                        onLabelNameChange()
                                    },
                                )
                                DropdownMenuItem(
                                    modifier = Modifier.testTag("main:topbar_delete_label_menu_item"),
                                    text = {
                                        Text(text = stringResource(Res.string.modules_designsystem_delete_label))
                                    },
                                    onClick = {
                                        showDropDown = false
                                        onDeleteLabel()
                                    },
                                )
                            }
                        }
                    }

                    NoteCategory.TRASH -> {
                        var showDropDown by remember {
                            mutableStateOf(false)
                        }
                        Box {
                            IconButton(
                                onClick = { showDropDown = true },
                                modifier = Modifier.testTag("main:topbar_more_options_button"),
                            ) {
                                Icon(SynIcons.MoreVert, contentDescription = "more")
                            }
                            DropdownMenu(
                                expanded = showDropDown,
                                onDismissRequest = { showDropDown = false },
                                modifier = Modifier.testTag("main:topbar_trash_empty_options_dropdown"),
                            ) {
                                DropdownMenuItem(
                                    modifier = Modifier.testTag("main:topbar_empty_trash_menu_item"),
                                    text = { Text(text = stringResource(Res.string.modules_designsystem_empty_trash)) },
                                    onClick = {
                                        showDropDown = false
                                        onDeleteAllTrash()
                                    },
                                )
                            }
                        }
                    }

                    NoteCategory.ARCHIVE -> {
                        IconButton(
                            onClick = onSearchClick,
                            modifier = Modifier.testTag("main:topbar_search_button"),
                        ) {
                            Icon(
                                imageVector = SynIcons.Search,
                                contentDescription = "search",
                            )
                        }
                        IconButton(
                            onClick = { onDisplayModeChange() },
                            modifier = Modifier.testTag("main:topbar_display_mode_button"),
                        ) {
                            if (!isGrid) {
                                Icon(imageVector = SynIcons.GridView, contentDescription = "grid")
                            } else {
                                Icon(
                                    imageVector = SynIcons.ViewAgenda,
                                    contentDescription = "column",
                                )
                            }
                        }
                    }
                }
            }
        }

    val labelText = selectState?.setOfSelected?.size?.toString()
        ?: when (noteDisplayCategory.noteCategory) {
            NoteCategory.NOTE -> "Note"
            NoteCategory.REMINDER -> "Reminder"
            NoteCategory.LABEL -> labelName ?: ""
            NoteCategory.TRASH -> "Trash"
            NoteCategory.ARCHIVE -> "Archive"
        }
    val color = if (selectState != null) {
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    } else {
        TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    }

    TopAppBar(
        modifier = modifier.testTag("main:topbar_root"), // Tag for the root TopAppBar
        scrollBehavior = scrollBehavior,
        navigationIcon = navigationAction,
        title = {
            when {
                selectState != null -> {
                    Text(
                        text = labelText, // Show selection count
                        modifier = Modifier.testTag("main:topbar_selection_title"),
                    )
                }
                noteDisplayCategory.noteCategory == NoteCategory.NOTE -> {
                    OutlinedCard(
                        onClick = onSearchClick,
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        modifier = Modifier.testTag("main:topbar_search_notes_card"),
                    ) {
                        Box(
                            modifier = Modifier.padding(
                                horizontal = 64.dp,
                                vertical = 4.dp,
                            ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                style = MaterialTheme.typography.labelLarge,
                                text = stringResource(Res.string.modules_designsystem_search_note),
                                modifier = Modifier.testTag("main:topbar_search_notes_text"),
                            )
                        }
                    }
                }
                else -> Text(
                    text = labelText,
                    modifier = Modifier.testTag("main:topbar_title"),
                )
            }
        },
        subtitle = {},
        actions = actions,
        colors = color,
        titleHorizontalAlignment = if (noteDisplayCategory.noteCategory == NoteCategory.NOTE && selectState == null) {
            Alignment.CenterHorizontally
        } else {
            Alignment.Start
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun MainTopBarPreview() {
    MainTopBar(
        noteDisplayCategory = NoteDisplayCategory(
            labelId = 1,
            noteCategory = NoteCategory.NOTE,
        ),
        isGrid = false,
        selectState = null,
        labelName = "Label Name",
    )
}
