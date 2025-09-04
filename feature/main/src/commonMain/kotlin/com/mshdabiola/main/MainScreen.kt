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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mshdabiola.main.component.ArchiveAppBar
import com.mshdabiola.main.component.EmptyState
import com.mshdabiola.main.component.LabelAppBar
import com.mshdabiola.main.component.LoadingState
import com.mshdabiola.main.component.MainAppBar
import com.mshdabiola.main.component.ReminderAppBar
import com.mshdabiola.main.component.SelectAppBar
import com.mshdabiola.main.component.SelectTrashAppBar
import com.mshdabiola.main.component.TrashAppBar
import com.mshdabiola.main.model.MainState
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.testtag.MainScreenTestTags // Added import
import com.mshdabiola.ui.NoteCard
import org.jetbrains.compose.resources.stringResource
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.modules_designsystem_other
import synapse.feature.main.generated.resources.modules_designsystem_pin

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    mainState: MainState,
    searchBarState: SearchBarState,
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

    onLabelNameChange: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},

    onDeleteAllTrash: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    inputField: @Composable () -> Unit = {},

) {
    val scrollBehavior = if ((mainState as? MainState.ViewState)?.selectState != null) {
        TopAppBarDefaults.pinnedScrollBehavior()
    } else {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    }
    val searchScrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    val gridState = rememberLazyStaggeredGridState()
//    TrackScrollJank(scrollableState = gridState, stateName = MainScreenTestTags.MAIN_GRID_JANK_TRACKER) // More specific jank tracker tag

    when (mainState) {
        is MainState.Loading -> {
            LoadingState(modifier = modifier.testTag(MainScreenTestTags.MAIN_LOADING_STATE))
        }

        is MainState.ViewState -> {
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
                    .testTag(MainScreenTestTags.MAIN_SCAFFOLD_SUCCESS) // Tag for the success state Scaffold
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    if (mainState.selectState != null) {
                        when (mainState.noteDisplayCategory.noteCategory) {
                            NoteCategory.TRASH -> {
                                SelectTrashAppBar(
                                    modifier = Modifier,
                                    scrollBehavior = scrollBehavior,
                                    selectState = mainState.selectState,
                                    onClearSelection = onClearSelection,
                                    onRestore = onRestore,
                                    onDeleteForever = onDeletedForever,
                                )
                            }
                            else -> { // Covers NOTE, ARCHIVE, LABEL, REMINDER when selectState is not null
                                SelectAppBar(
                                    modifier = Modifier,
                                    scrollBehavior = scrollBehavior,
                                    selectState = mainState.selectState,
                                    noteDisplayCategory = mainState.noteDisplayCategory,
                                    onClearSelection = onClearSelection,
                                    onPinNotes = onPinNotes,
                                    onNotificationClick = onNotificationClick,
                                    onSelectColor = onSelectColor,
                                    onLabelNotes = onLabelNotes,
                                    onArchive = onArchive,
                                    onDeleteNotes = onDeleteNotes,
                                    onShareNote = onShareNote,
                                    onCopyNote = onCopyNote,
                                )
                            }
                        }
                    } else { // selectState is null (normal viewing mode)

                        when (mainState.noteDisplayCategory.noteCategory) {
                            NoteCategory.NOTE -> {
                                MainAppBar(
                                    scrollBehavior = searchScrollBehavior,
                                    searchBarState = searchBarState,
                                    inputField = inputField,
                                )
                            }

                            NoteCategory.REMINDER -> {
                                ReminderAppBar(
                                    isGrid = mainState.isGrid,
                                    scrollBehavior = scrollBehavior,
                                    onDisplayModeChange = onDisplayModeChange,
                                    onHamburgerMenuClick = onHamburgerMenuClick,
                                    onSearchClick = onSearchClick,
                                )
                            }

                            NoteCategory.LABEL -> {
                                LabelAppBar(
                                    modifier = Modifier,
                                    labelName = mainState.labelName,
                                    scrollBehavior = scrollBehavior,
                                    onHamburgerMenuClick = onHamburgerMenuClick,
                                    onLabelNameChange = onLabelNameChange,
                                    onDeleteLabel = onDeleteLabel,
                                    onSearchClick = onSearchClick,
                                )
                            }

                            NoteCategory.TRASH -> {
                                TrashAppBar(
                                    scrollBehavior = scrollBehavior,
                                    onDeleteAllTrash = onDeleteAllTrash,
                                    onHamburgerMenuClick = onHamburgerMenuClick,
                                )
                            }

                            NoteCategory.ARCHIVE -> {
                                ArchiveAppBar(
                                    isGrid = mainState.isGrid,
                                    scrollBehavior = scrollBehavior,
                                    onHamburgerMenuClick = onHamburgerMenuClick,
                                    onSearchClick = onSearchClick,
                                    onDisplayModeChange = onDisplayModeChange,
                                )
                            }
                        }
                    }
                },
            ) { paddingValues ->
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag(MainScreenTestTags.MAIN_NOTES_GRID), // Tag for the notes list/grid
                    state = gridState,
                    contentPadding = paddingValues,
                    columns = StaggeredGridCells.Fixed(if (mainState.isGrid) 2 else 1),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,
                ) {
                    if (mainState.unPinNotePads.isEmpty() && mainState.pinNotePads.isEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            EmptyState(
                                modifier = Modifier.testTag(MainScreenTestTags.MAIN_EMPTY_STATE_VIEW),
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
                                    .testTag(MainScreenTestTags.MAIN_PINNED_SECTION_HEADER),
                                text = stringResource(Res.string.modules_designsystem_pin),
                            )
                        }
                    }

                    items(items = mainState.pinNotePads, key = { "${MainScreenTestTags.MAIN_NOTE_CARD_PINNED_PREFIX}${it.id}" }) { notepad ->
                        NoteCard(
                            modifier = Modifier.testTag("${MainScreenTestTags.MAIN_NOTE_CARD_PINNED_PREFIX}${notepad.id}"),
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
                                    .testTag(MainScreenTestTags.MAIN_OTHERS_SECTION_HEADER),
                                text = stringResource(Res.string.modules_designsystem_other),
                            )
                        }
                    }
                    items(items = mainState.unPinNotePads, key = { "${MainScreenTestTags.MAIN_NOTE_CARD_UNPINNED_PREFIX}${it.id}" }) { notepad ->
                        NoteCard(
                            modifier = Modifier.testTag("${MainScreenTestTags.MAIN_NOTE_CARD_UNPINNED_PREFIX}${notepad.id}"),
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
