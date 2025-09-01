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
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.main.model.SelectState
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.modules_designsystem_archive
import synapse.feature.main.generated.resources.modules_designsystem_delete
import synapse.feature.main.generated.resources.modules_designsystem_make_a_copy
import synapse.feature.main.generated.resources.modules_designsystem_send
import synapse.feature.main.generated.resources.modules_designsystem_unarchive

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectAppBar(
    modifier: Modifier = Modifier,
    noteDisplayCategory: NoteDisplayCategory,
    selectState: SelectState ,
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
    TopAppBar(
        modifier=modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = onClearSelection,
                modifier = Modifier.testTag("main:topbar_clear_selection_button"),
            ) {
                Icon(imageVector = SynIcons.Clear, contentDescription = "clear note")
            }
        },
        title = {
            Text(selectState?.setOfSelected?.size?.toString()?:"")
        },
        subtitle = {},
        actions = { // Covers NOTE, ARCHIVE, LABEL, REMINDER when selectState is not null
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
                    if (selectState?.setOfSelected?.size == 1) {
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
        },
        titleHorizontalAlignment =
            Alignment.CenterHorizontally,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),

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
