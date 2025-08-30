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
package com.mshdabiola.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.component.SynButton
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.designsystem.theme.LocalTintTheme
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.testtag.MainScreenTestTags
import com.mshdabiola.ui.NoteCard
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.stringResource
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.features_main_empty_body
import synapse.feature.main.generated.resources.features_main_empty_title
import synapse.feature.main.generated.resources.modules_designsystem_cancel
import synapse.feature.main.generated.resources.modules_designsystem_close
import synapse.feature.main.generated.resources.modules_designsystem_delete
import synapse.feature.main.generated.resources.modules_designsystem_dialog_delete_forever
import synapse.feature.main.generated.resources.modules_designsystem_dialog_delete_forever_content
import synapse.feature.main.generated.resources.modules_designsystem_dialog_empty_trash
import synapse.feature.main.generated.resources.modules_designsystem_dialog_empty_trash_content
import synapse.feature.main.generated.resources.modules_designsystem_other
import synapse.feature.main.generated.resources.modules_designsystem_pin
import synapse.feature.main.generated.resources.modules_designsystem_rename
import synapse.feature.main.generated.resources.modules_designsystem_rename_label
import synapse.feature.main.generated.resources.modules_designsystem_rename_label_detail

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    mainState: MainState,
    navigateToNoteEditor: (Long, Int, Int) -> Unit = { _, _, _ -> },
    onNoteSelected: (Long) -> Unit = {},

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
    onDeletedForever: () -> Unit = {},
    onRestore: () -> Unit = {},

    onSearchClick: () -> Unit = {},
    onLabelNameChange: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},

    onDeleteAllTrash: () -> Unit = {},

) {
    val scrollBehavior = if ((mainState as? MainState.Success)?.selectState != null) {
        TopAppBarDefaults.pinnedScrollBehavior()
    } else {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    }

    val gridState = rememberLazyStaggeredGridState()
//    TrackScrollJank(scrollableState = gridState, stateName = "main:grid_jank_tracker") // More specific jank tracker tag

    when (mainState) {
        is MainState.Loading -> {
//            LoadingState(modifier = modifier.testTag("main:loading_state"))
        }

        is MainState.Success -> {
            val onNoteClick: (Long, Int, Int) -> Unit = { id, colorIndex, background ->
                if (mainState.selectState != null) {
                    onNoteSelected(id)
                } else {
                    navigateToNoteEditor(id, colorIndex, background)
                }
            }
            Scaffold(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("main:scaffold_success") // Tag for the success state Scaffold
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MainTopBar(
                        // Modifier for MainTopBar can be passed if needed,
                        // but test tags within MainTopBar are more granular
                        scrollBehavior = scrollBehavior,
                        noteDisplayCategory = mainState.noteDisplayCategory,
                        isGrid = mainState.isGrid,
                        selectState = mainState.selectState,
                        labelName = mainState.labelName,
                        onDisplayModeChange = onDisplayModeChange,
                        onHamburgerMenuClick = onHamburgerMenuClick,
                        onClearSelection = onClearSelection,
                        onPinNotes = onPinNotes,
                        onNotificationClick = onNotificationClick,
                        onSelectColor = onSelectColor,
                        onLabelNotes = onLabelNotes,
                        onArchive = onArchive,
                        onDeleteNotes = onDeleteNotes,
                        onShareNote = onShareNote,
                        onCopyNote = onCopyNote,
                        onSearchClick = onSearchClick,
                        onLabelNameChange = onLabelNameChange,
                        onDeleteLabel = onDeleteLabel,
                        onDeleteAllTrash = onDeleteAllTrash,
                        onDeleteForever = onDeletedForever,
                        onRestore = onRestore,
                    )
                },
            ) { paddingValues ->
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("main:notes_grid"), // Tag for the notes list/grid
                    state = gridState,
                    contentPadding = paddingValues,
                    columns = StaggeredGridCells.Fixed(if (mainState.isGrid) 2 else 1),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,
                ) {
                    if (mainState.unPinNotePads.isEmpty() && mainState.pinNotePads.isEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            EmptyState(
                                modifier = Modifier.testTag("main:empty_state_view"),
                                noteDisplayCategory = mainState.noteDisplayCategory,
                            )
                        }
                    }
                    if (mainState.pinNotePads.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .testTag("main:pinned_section_header"),
                                text = stringResource(Res.string.modules_designsystem_pin),
                            )
                        }
                    }

                    items(items = mainState.pinNotePads, key = { "pinned_${it.id}" }) { notepad ->
                        NoteCard(
                            modifier = Modifier.testTag("main:note_card_pinned_${notepad.id}"),
                            notePad = notepad,
                            onCardClick = onNoteClick,
                            onLongClick = onNoteSelected,
                            isSelect = mainState.selectState?.setOfSelected?.contains(notepad.id) ?: false,
                        )
                    }

                    if (mainState.pinNotePads.isNotEmpty() && mainState.unPinNotePads.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .testTag("main:others_section_header"),
                                text = stringResource(Res.string.modules_designsystem_other),
                            )
                        }
                    }
                    items(items = mainState.unPinNotePads, key = { "unpinned_${it.id}" }) { notepad ->
                        NoteCard(
                            modifier = Modifier.testTag("main:note_card_unpinned_${notepad.id}"),
                            notePad = notepad,
                            onCardClick = onNoteClick,
                            onLongClick = onNoteSelected,
                            isSelect = mainState.selectState?.setOfSelected?.contains(notepad.id) ?: false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    noteDisplayCategory: NoteDisplayCategory,
) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/empty_state.json").decodeToString(),
        )
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .testTag(MainScreenTestTags.EMPTY_STATE_COLUMN), // Tag for the empty state container
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val iconTint = LocalTintTheme.current.iconTint
        Image(
            modifier = Modifier
                .size(200.dp)
                .testTag(MainScreenTestTags.EMPTY_STATE_IMAGE),
            painter = rememberLottiePainter(
                composition = composition,
                iterations = Compottie.IterateForever,
            ),
            colorFilter = if (iconTint != Color.Unspecified) ColorFilter.tint(iconTint) else null,
            contentDescription = null, // Consider adding a content description for accessibility and testing
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(Res.string.features_main_empty_title),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(MainScreenTestTags.EMPTY_STATE_TITLE),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.features_main_empty_body),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(MainScreenTestTags.EMPTY_STATE_DESCRIPTION),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun RenameLabelAlertDialog(
    show: Boolean = false,
    label: String = "Label",
    onDismissRequest: () -> Unit = {},
    onChangeName: (String) -> Unit = {},
) {
    var name by remember(label) {
        mutableStateOf(label)
    }

    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Res.string.modules_designsystem_rename_label)) },
            text = {
                TextField(value = name, onValueChange = { name = it })
            },
            confirmButton = {
                SynButton(
                    onClick = {
                        onDismissRequest()
                        onChangeName(name)
                    },
                    label = stringResource(Res.string.modules_designsystem_rename),
                )
            },
            dismissButton = {
                SynTextButton(
                    onClick = { onDismissRequest() },
                    label = stringResource(Res.string.modules_designsystem_cancel),
                )
            },
        )
    }
}

@Composable
fun DeleteLabelAlertDialog(
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Res.string.modules_designsystem_rename_label)) },
            text = {
                Text(text = stringResource(Res.string.modules_designsystem_rename_label_detail))
            },
            confirmButton = {
                SynTextButton(
                    onClick = {
                        onDismissRequest()
                        onDelete()
                    },
                    label = stringResource(Res.string.modules_designsystem_delete),
                )
            },
            dismissButton = {
                SynTextButton(
                    onClick = { onDismissRequest() },
                    label = stringResource(Res.string.modules_designsystem_cancel),
                )
            },
        )
    }
}

@Composable
fun EmptyTrashDialog(
    modifier: Modifier = Modifier,
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDelete: () -> Unit = {},

) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Res.string.modules_designsystem_dialog_empty_trash)) },
            text = {
                Text(text = stringResource(Res.string.modules_designsystem_dialog_empty_trash_content))
            },
            confirmButton = {
                SynButton(
                    onClick = {
                        onDismissRequest()
                    },
                    label = stringResource(Res.string.modules_designsystem_close),
                )
            },
            dismissButton = {
                SynTextButton(
                    onClick = {
                        onDelete()
                        onDismissRequest()
                    },
                    label = stringResource(Res.string.modules_designsystem_delete),
                )
            },
        )
    }
}

@Composable
fun DeleteForeverDialog(
    modifier: Modifier = Modifier,
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDelete: () -> Unit = {},

) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Res.string.modules_designsystem_dialog_delete_forever)) },
            text = {
                Text(text = stringResource(Res.string.modules_designsystem_dialog_delete_forever_content))
            },
            confirmButton = {
                SynButton(
                    onClick = {
                        onDismissRequest()
                    },
                    label = stringResource(Res.string.modules_designsystem_close),
                )
            },
            dismissButton = {
                SynTextButton(
                    onClick = {
                        onDelete()
                        onDismissRequest()
                    },
                    label = stringResource(Res.string.modules_designsystem_delete),
                )
            },
        )
    }
}
