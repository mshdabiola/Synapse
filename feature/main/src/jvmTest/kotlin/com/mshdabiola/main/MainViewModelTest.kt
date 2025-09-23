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

import androidx.compose.foundation.text.input.clearText
import app.cash.turbine.test
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.GetAllNoteUseCase
import com.mshdabiola.domain.LinkUriUseCase
import com.mshdabiola.main.model.MainState
import com.mshdabiola.main.model.SearchSort
import com.mshdabiola.main.model.SearchState
import com.mshdabiola.model.UserSettings
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.testing.fake.repository.FakeAlarmManager
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNoteDrawingRepository
import com.mshdabiola.testing.fake.repository.FakeNoteImageRepository
import com.mshdabiola.testing.fake.repository.FakeNoteItemRepository
import com.mshdabiola.testing.fake.repository.FakeNoteLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.fake.repository.FakeNoteVoiceRepository
import com.mshdabiola.testing.fake.repository.FakeNotificationRepository
import com.mshdabiola.testing.fake.repository.FakeUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var fakeLabelRepository: FakeLabelRepository
    private lateinit var fakeGetAllNoteUseCase: GetAllNoteUseCase
    private lateinit var fakeAddAllNoteUseCase: AddAllNoteUseCase
    private lateinit var viewModel: MainViewModel

    private fun createNotePad(
        id: Long,
        title: String,
        isPinned: Boolean = false,
        category: NoteCategory = NoteCategory.NOTE,
        labels: List<Label> = emptyList(),
        color: Int = 0,
    ): NotePad {
        return NotePad(
            id = id,
            title = title,
            labels = labels,
            isPin = isPinned,
            color = color,
            noteCategory = category,
            notification = null,
        )
    }

    @Before
    fun setup() {
        fakeNoteRepository = FakeNoteRepository()
        fakeUserDataRepository = FakeUserDataRepository()
        fakeLabelRepository = FakeLabelRepository()
        fakeGetAllNoteUseCase = GetAllNoteUseCase(
            noteRepository = fakeNoteRepository,
            linkUriUseCase = LinkUriUseCase(),
        )
        val noteCheckRepository = FakeNoteItemRepository()
        val noteDrawingRepository = FakeNoteDrawingRepository()
        val noteImageRepository = FakeNoteImageRepository()
        val noteLabelRepository = FakeNoteLabelRepository()
        val noteNotificationRepository = FakeNotificationRepository()
        val noteVoiceRepository = FakeNoteVoiceRepository()

        fakeAddAllNoteUseCase = AddAllNoteUseCase(
            noteRepository = fakeNoteRepository,
            noteCheckRepository = noteCheckRepository,
            noteDrawingRepository = noteDrawingRepository,
            noteImageRepository = noteImageRepository,
            noteLabelRepository = noteLabelRepository,
            noteNotificationRepository = noteNotificationRepository,
            noteVoiceRepository = noteVoiceRepository,
            alarmManager = FakeAlarmManager()
        )

        viewModel = MainViewModel(
            noteRepository = fakeNoteRepository,
            userDataRepository = fakeUserDataRepository,
            labelRepository = fakeLabelRepository,
            getAllNoteUseCase = fakeGetAllNoteUseCase,
            addAllNoteUseCase = fakeAddAllNoteUseCase,
        )
    }

    @Test
    fun onDisplayModeChange_togglesIsGrid() = runTest {
        viewModel.mainState.test {
            val initialLoadingState = awaitItem() // MainState.Loading
            assertTrue(initialLoadingState is MainState.Loading)

            val initialViewState = awaitItem() as MainState.ViewState
            val initialIsGrid = initialViewState.isGrid

            viewModel.onDisplayModeChange()

            val newViewState = awaitItem() as MainState.ViewState
            assertEquals(!initialIsGrid, newViewState.isGrid)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun pinOrUnpinNotes_whenAnyUnpinned_pinsAllSelectedNotesAndDeselects() = runTest {
        val note1 = createNotePad(id = 1, title = "Note 1", isPinned = false)
        val note2 = createNotePad(id = 2, title = "Note 2", isPinned = true)
        val note3 = createNotePad(id = 3, title = "Note 3", isPinned = false)
        fakeNoteRepository.upserts(listOf(note1, note2, note3))

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState // Initial notes loaded

            viewModel.handleCardSelection(1L)
            viewState = awaitItem() as MainState.ViewState
            assertNotNull(viewState.selectState)
            assertEquals(1, viewState.selectState?.setOfSelected?.size)

            viewModel.handleCardSelection(2L)
            viewState = awaitItem() as MainState.ViewState
            assertNotNull(viewState.selectState)
            assertEquals(2, viewState.selectState?.setOfSelected?.size)

            viewModel.pinOrUnpinNotes()
            skipItems(1)

            viewState = awaitItem() as MainState.ViewState
            val updatedNotes = viewState.pinNotePads + viewState.unPinNotePads
            assertTrue(updatedNotes.first { it.id == 1L }.isPin)
            assertTrue(updatedNotes.first { it.id == 2L }.isPin)
            assertNull(viewState.selectState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun pinOrUnpinNotes_whenAllPinned_unpinsAllSelectedNotesAndDeselects() = runTest {
        val note1 = createNotePad(id = 1, title = "Note 1", isPinned = true)
        val note2 = createNotePad(id = 2, title = "Note 2", isPinned = true)
        val note3 = createNotePad(id = 3, title = "Note 3", isPinned = true) // Unselected
        fakeNoteRepository.upserts(listOf(note1, note2, note3))

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState

            viewModel.handleCardSelection(1L)
            viewState = awaitItem() as MainState.ViewState

            viewModel.handleCardSelection(2L)
            viewState = awaitItem() as MainState.ViewState

            viewModel.pinOrUnpinNotes()
            skipItems(1)

            viewState = awaitItem() as MainState.ViewState
            val updatedNotes = viewState.pinNotePads + viewState.unPinNotePads
            assertFalse(updatedNotes.first { it.id == 1L }.isPin)
            assertFalse(updatedNotes.first { it.id == 2L }.isPin)
            assertTrue(updatedNotes.first { it.id == 3L }.isPin)
            assertNull(viewState.selectState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun setAllColor_updatesColorOfSelectedNotesAndDeselects() = runTest {
        val note1 = createNotePad(id = 1, title = "Note 1", color = 1)
        val note2 = createNotePad(id = 2, title = "Note 2", color = 2) // Unselected
        val note3 = createNotePad(id = 3, title = "Note 3", color = 3)
        fakeNoteRepository.upserts(listOf(note1, note2, note3))
        val newColor = 5

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState

            viewModel.handleCardSelection(1L)
            awaitItem() // Selection update

            viewModel.handleCardSelection(3L)
            awaitItem() // Selection update

            viewModel.setAllColor(newColor)
            skipItems(1)

            viewState = awaitItem() as MainState.ViewState
            val updatedNotes = viewState.pinNotePads + viewState.unPinNotePads
            assertEquals(newColor, updatedNotes.first { it.id == 1L }.color)
            assertEquals(2, updatedNotes.first { it.id == 2L }.color)
            assertEquals(newColor, updatedNotes.first { it.id == 3L }.color)
            assertNull(viewState.selectState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun onArchiveNote_whenAnyArchived_movesSelectedToNoteAndDeselects() = runTest {
        val note1 = createNotePad(id = 1, title = "Note 1", category = NoteCategory.ARCHIVE)
        val note2 = createNotePad(id = 2, title = "Note 2", category = NoteCategory.ARCHIVE)
        val note3 = createNotePad(id = 3, title = "Note 3", category = NoteCategory.ARCHIVE) // Unselected
        val note4 = createNotePad(id = 4, title = "Note 4", category = NoteCategory.NOTE) // Unselected
        fakeNoteRepository.upserts(listOf(note1, note2, note3, note4))
        fakeUserDataRepository.setFakeUserData(
            UserSettings(noteCategory = NoteDisplayCategory(noteCategory = NoteCategory.ARCHIVE)),
        )

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState

            viewModel.handleCardSelection(1L)
            awaitItem()
            viewModel.handleCardSelection(2L)
            awaitItem()

            viewModel.onArchiveNote()
            skipItems(1)

            // Note category changes trigger repository updates, which re-emit mainState
            viewState = awaitItem() as MainState.ViewState

            val updatedRepoNotes = fakeNoteRepository.getAll().first()
            assertEquals(NoteCategory.NOTE, updatedRepoNotes.first { it.id == 1L }.noteCategory)
            assertEquals(NoteCategory.NOTE, updatedRepoNotes.first { it.id == 2L }.noteCategory)
            assertEquals(NoteCategory.ARCHIVE, updatedRepoNotes.first { it.id == 3L }.noteCategory)
            assertEquals(NoteCategory.NOTE, updatedRepoNotes.first { it.id == 4L }.noteCategory)
            assertNull(viewState.selectState)
            cancelAndConsumeRemainingEvents()
        }
    }

    //
    @Test
    fun onArchiveNote_whenNoneArchived_movesSelectedToArchiveAndDeselects() = runTest {
        val note1 = createNotePad(id = 1, title = "Note 1", category = NoteCategory.NOTE)
        val note2 = createNotePad(id = 2, title = "Note 2", category = NoteCategory.NOTE)
        val note3 = createNotePad(id = 3, title = "Note 3", category = NoteCategory.TRASH) // Unselected
        val note4 = createNotePad(id = 4, title = "Note 4", category = NoteCategory.NOTE) // Unselected
        fakeNoteRepository.upserts(listOf(note1, note2, note3, note4))

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState

            viewModel.handleCardSelection(1L)
            awaitItem()
            viewModel.handleCardSelection(2L)
            awaitItem()

            viewModel.onArchiveNote()
            skipItems(1)

            viewState = awaitItem() as MainState.ViewState

            val updatedRepoNotes = fakeNoteRepository.getAll().first()
            assertEquals(NoteCategory.ARCHIVE, updatedRepoNotes.first { it.id == 1L }.noteCategory)
            assertEquals(NoteCategory.ARCHIVE, updatedRepoNotes.first { it.id == 2L }.noteCategory)
            assertEquals(NoteCategory.TRASH, updatedRepoNotes.first { it.id == 3L }.noteCategory)
            assertEquals(NoteCategory.NOTE, updatedRepoNotes.first { it.id == 4L }.noteCategory)
            assertNull(viewState.selectState)
            cancelAndConsumeRemainingEvents()
        }
    }

    //
    @Test
    fun onDeleteNote_movesSelectedToTrashAndDeselects() = runTest {
        val note1 = createNotePad(id = 1, title = "Note 1", category = NoteCategory.NOTE)
        val note2 = createNotePad(id = 2, title = "Note 2", category = NoteCategory.NOTE)
        val note3 = createNotePad(id = 3, title = "Note 3", category = NoteCategory.NOTE) // Unselected
        fakeNoteRepository.upserts(listOf(note1, note2, note3))

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState

            viewModel.handleCardSelection(1L)
            awaitItem()
            viewModel.handleCardSelection(2L)
            awaitItem()

            viewModel.onDeleteNote()
            skipItems(1)

            viewState = awaitItem() as MainState.ViewState

            val updatedRepoNotes = fakeNoteRepository.getAll().first()
            assertEquals(NoteCategory.TRASH, updatedRepoNotes.first { it.id == 1L }.noteCategory)
            assertEquals(NoteCategory.TRASH, updatedRepoNotes.first { it.id == 2L }.noteCategory)
            assertEquals(NoteCategory.NOTE, updatedRepoNotes.first { it.id == 3L }.noteCategory)
            assertNull(viewState.selectState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun onDeleteForever_deletesSelectedNotesAndDeselects() = runTest {
        val note1 = createNotePad(id = 1, title = "Note 1")
        val note2 = createNotePad(id = 2, title = "Note 2")
        val note3 = createNotePad(id = 3, title = "Note 3") // Unselected
        fakeNoteRepository.upserts(listOf(note1, note2, note3))

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState

            viewModel.handleCardSelection(1L)
            awaitItem()
            viewModel.handleCardSelection(2L)
            awaitItem()

            viewModel.onDeleteForever()
            skipItems(1)

            viewState = awaitItem() as MainState.ViewState

            val updatedRepoNotes = fakeNoteRepository.getAll().first()
            assertFalse(updatedRepoNotes.any { it.id == 1L })
            assertFalse(updatedRepoNotes.any { it.id == 2L })
            assertTrue(updatedRepoNotes.any { it.id == 3L })
            assertNull(viewState.selectState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun onRestore_movesSelectedToNoteAndDeselects() = runTest {
        val note1 = createNotePad(id = 1, title = "Note 1", category = NoteCategory.TRASH)
        val note2 = createNotePad(id = 2, title = "Note 2", category = NoteCategory.NOTE) // Unselected
        val note3 = createNotePad(id = 3, title = "Note 3", category = NoteCategory.TRASH)
        fakeNoteRepository.upserts(listOf(note1, note2, note3))
        fakeUserDataRepository.setFakeUserData(
            UserSettings(noteCategory = NoteDisplayCategory(noteCategory = NoteCategory.TRASH)),
        )

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState

            viewModel.handleCardSelection(1L)
            awaitItem()
            viewModel.handleCardSelection(3L)
            awaitItem()

            viewModel.onRestore()
            skipItems(1)

            viewState = awaitItem() as MainState.ViewState

            val updatedRepoNotes = fakeNoteRepository.getAll().first()
            assertEquals(NoteCategory.NOTE, updatedRepoNotes.first { it.id == 1L }.noteCategory)
            assertEquals(NoteCategory.NOTE, updatedRepoNotes.first { it.id == 3L }.noteCategory)
            assertEquals(NoteCategory.NOTE, updatedRepoNotes.first { it.id == 2L }.noteCategory)
            assertNull(viewState.selectState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun onCopyNote_createsDuplicateAndDeselects() = runTest {
        val originalNote = createNotePad(
            id = 1,
            title = "Original Note",
            isPinned = true,
            category = NoteCategory.NOTE,
            labels = listOf(Label(10L, "Test Label")),
            color = 5,
        )
        val otherNote = createNotePad(id = 2, title = "Other Note")
        fakeNoteRepository.upserts(listOf(originalNote, otherNote))

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState
            val initialNotesCount = fakeNoteRepository.getAll().first().size

            viewModel.handleCardSelection(originalNote.id)
            awaitItem() // selection update

            viewModel.onCopyNote()
            skipItems(1)
            viewState = awaitItem() as MainState.ViewState // Note list changes, re-emission

            val finalNotes = fakeNoteRepository.getAll().first()
            assertEquals(initialNotesCount + 1, finalNotes.size)
            val copiedNote = finalNotes.firstOrNull { it.id != originalNote.id && it.title == originalNote.title }
            assertNotNull(copiedNote)
            assertNotEquals(originalNote.id, copiedNote!!.id)
            assertEquals(originalNote.title, copiedNote.title)
            assertEquals(originalNote.isPin, copiedNote.isPin)
            assertEquals(originalNote.noteCategory, copiedNote.noteCategory)
            assertEquals(originalNote.labels, copiedNote.labels)
            assertEquals(originalNote.color, copiedNote.color)
            val stillOriginalNote = finalNotes.first { it.id == originalNote.id }
            assertEquals(originalNote.title, stillOriginalNote.title)
            assertNull(viewState.selectState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun deleteLabel_removesLabelAndResetsCategory() = runTest {
        val labelIdToDelete = 5L
        val labelToDelete = Label(labelIdToDelete, "Work")
        fakeLabelRepository.upserts(listOf(labelToDelete))
        val initialUserSettings = UserSettings(
            isGrid = true,
            noteCategory = NoteDisplayCategory(labelIdToDelete, NoteCategory.LABEL),
        )
        fakeUserDataRepository.setFakeUserData(initialUserSettings)

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState
            assertEquals(labelIdToDelete, viewState.noteDisplayCategory.labelId)

            viewModel.deleteLabel()
            // UserDataRepository change should trigger mainState re-emission
            viewState = awaitItem() as MainState.ViewState

            assertNull(fakeLabelRepository.get(labelIdToDelete).firstOrNull())
            assertEquals(NoteDisplayCategory(0, NoteCategory.NOTE), viewState.noteDisplayCategory)
            // Also check repository directly for user settings
            val finalUserSettings = fakeUserDataRepository.userSettings.first()
            assertEquals(NoteDisplayCategory(0, NoteCategory.NOTE), finalUserSettings.noteCategory)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun renameLabel_updatesLabelNameInRepository() = runTest {
        val testLabelId = 7L
        val oldName = "Old Label Name"
        val newName = "New Label Name"
        val labelToRename = Label(testLabelId, oldName)
        fakeLabelRepository.upserts(listOf(labelToRename))
        val initialUserSettings = UserSettings(
            isGrid = true,
            noteCategory = NoteDisplayCategory(testLabelId, NoteCategory.LABEL),
        )
        fakeUserDataRepository.setFakeUserData(initialUserSettings)

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState
            assertEquals(testLabelId, viewState.noteDisplayCategory.labelId)
            assertEquals(oldName, viewState.labelName)

            viewModel.renameLabel(newName)
            // Label rename should trigger mainState re-emission
            viewState = awaitItem() as MainState.ViewState

            val renamedLabel = fakeLabelRepository.get(testLabelId).first()
            assertNotNull(renamedLabel)
            assertEquals(newName, renamedLabel!!.name)
            assertEquals(newName, viewState.labelName)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun onDeleteAllTrash_removesAllTrashedNotes() = runTest {
        val trashedNote1 = createNotePad(id = 1, title = "Trashed Note 1", category = NoteCategory.TRASH)
        val trashedNote2 = createNotePad(id = 2, title = "Trashed Note 2", category = NoteCategory.TRASH)
        val regularNote = createNotePad(id = 3, title = "Regular Note", category = NoteCategory.NOTE)
        val archivedNote = createNotePad(id = 4, title = "Archived Note", category = NoteCategory.ARCHIVE)
        fakeNoteRepository.upserts(listOf(trashedNote1, trashedNote2, regularNote, archivedNote))
        fakeUserDataRepository.setFakeUserData(
            UserSettings(noteCategory = NoteDisplayCategory(noteCategory = NoteCategory.TRASH)),
        )

        viewModel.mainState.test {
            awaitItem() // Loading
            val initialViewState = awaitItem() as MainState.ViewState
            val initialRepoNotes = fakeNoteRepository.getAll().first()
            assertEquals(2, initialRepoNotes.count { it.noteCategory == NoteCategory.TRASH })
            // Check that notes are present in the view state if relevant
            assertTrue(
                initialViewState.unPinNotePads.any { it.id == trashedNote1.id } ||
                    initialViewState.pinNotePads.any { it.id == trashedNote1.id },
            )

            viewModel.onDeleteAllTrash()

            // Expect mainState to update as notes are removed
            val finalViewState = awaitItem() as MainState.ViewState
            val finalRepoNotes = fakeNoteRepository.getAll().first()
            assertTrue(finalRepoNotes.none { it.noteCategory == NoteCategory.TRASH })
            assertEquals(2, finalRepoNotes.size)
            assertTrue(finalRepoNotes.any { it.id == regularNote.id })
            assertTrue(finalRepoNotes.any { it.id == archivedNote.id })

            // Check view state reflects deletions
            assertFalse(
                finalViewState.unPinNotePads.any { it.noteCategory == NoteCategory.TRASH } ||
                    finalViewState.pinNotePads.any { it.noteCategory == NoteCategory.TRASH },
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun onSendNote_returnsSelectedNoteAndDeselects() = runTest {
        val noteToSend = createNotePad(id = 1, title = "Note to Send", color = 3)
        val otherNote = createNotePad(id = 2, title = "Other Note")
        fakeNoteRepository.upserts(listOf(noteToSend, otherNote))

        viewModel.mainState.test {
            awaitItem() // Loading
            var viewState = awaitItem() as MainState.ViewState // Initial load

            viewModel.handleCardSelection(noteToSend.id)
            viewState = awaitItem() as MainState.ViewState // Selection update
            assertNotNull(viewState.selectState)
            assertTrue(viewState.selectState!!.setOfSelected.contains(noteToSend.id))

            val returnedNote = viewModel.onSendNote()

            assertNotNull(returnedNote)
            assertEquals(noteToSend.id, returnedNote.id)
            assertEquals(noteToSend.title, returnedNote.title)
            assertEquals(noteToSend.color, returnedNote.color)

            viewState = awaitItem() as MainState.ViewState // Selection cleared
            assertNull(viewState.selectState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun onSetSearch_updatesSearchSortInSearchState() = runTest {
        fakeNoteRepository.upserts(
            listOf(
                createNotePad(id = 1, title = "Searchable Note Alpha"),
                createNotePad(id = 2, title = "Searchable Note Beta"),
            ),
        )

        viewModel.searchState.test {
            var searchState = awaitItem() // Initial: SearchState.FilterState
            assertTrue(searchState is SearchState.FilterState)

            viewModel.searchTextFieldState.edit {
                append("Searchable")
            }
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
            searchState = awaitItem() // SearchState.ViewState due to text
            assertTrue(searchState is SearchState.ViewState)
            assertEquals(2, (searchState as SearchState.ViewState).searches.size)
            assertNull((searchState as SearchState.ViewState).searchSort) // Initially null

            val testSearchSort = SearchSort.Type(0)
            viewModel.onSetSearch(testSearchSort)
            // Setting search sort will trigger a new emission
            searchState = awaitItem() as SearchState.ViewState
            assertEquals(testSearchSort, searchState.searchSort)

            viewModel.onSetSearch(null)
            searchState = awaitItem() as SearchState.ViewState
            assertNull(searchState.searchSort)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun searchState_filtersNotesBasedOnQueryText() = runTest {
        val noteApple = createNotePad(id = 1, title = "Apple Note")
        val noteBanana = createNotePad(id = 2, title = "Banana Article")
        val noteApplePie = createNotePad(id = 3, title = "Apple Pie Recipe")
        fakeNoteRepository.upserts(listOf(noteApple, noteBanana, noteApplePie))
        // Ensure mainState is processed so currentNotepads is up-to-date for searchState
        viewModel.mainState.test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        viewModel.searchState.test {
            var currentSearchState = awaitItem() // Initial FilterState
            assertTrue(currentSearchState is SearchState.FilterState)

            viewModel.searchTextFieldState.edit {
                append("Apple")
            }
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
            var viewState = awaitItem() as SearchState.ViewState
            assertEquals(2, viewState.searches.size)
            assertTrue(viewState.searches.any { it.id == noteApple.id })
            assertTrue(viewState.searches.any { it.id == noteApplePie.id })
        }

        viewModel.searchTextFieldState.clearText()
        viewModel.searchState.test {
            skipItems(1)
            viewModel.searchTextFieldState.edit {
                append("Banana")
            }
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
            val viewState = awaitItem() as SearchState.ViewState
            assertEquals(1, viewState.searches.size)
            assertTrue(viewState.searches.any { it.id == noteBanana.id })
        }
        viewModel.searchTextFieldState.clearText()
        viewModel.searchState.test {
            skipItems(1)
            viewModel.searchTextFieldState.edit {
                append("Orange")
            }
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
            val viewState = awaitItem() as SearchState.ViewState
            assertTrue(viewState.searches.isEmpty())
        }
        viewModel.searchTextFieldState.clearText()
        viewModel.searchState.test {
            skipItems(1)
            viewModel.searchTextFieldState.edit {
                append("")
            }
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
            val currentSearchState = awaitItem() // Back to FilterState
            assertTrue(currentSearchState is SearchState.FilterState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun searchState_filtersByQueryAndAdditionalCriteria() = runTest {
        val labelGroceries = Label(1L, "Groceries")
        val labelWork = Label(2L, "Work")
        fakeLabelRepository.upserts(listOf(labelGroceries, labelWork))

        val noteW = createNotePad(id = 10, title = "Apples Red", color = 1, labels = listOf(labelGroceries))
        val noteX = createNotePad(id = 20, title = "Apples Green", color = 2, labels = listOf(labelWork))
        val noteY = createNotePad(id = 30, title = "Bananas Yellow", color = 3, labels = listOf(labelGroceries))
        val noteZ = createNotePad(id = 40, title = "Green Grapes", color = 2)
        fakeNoteRepository.upserts(listOf(noteW, noteX, noteY, noteZ))
        viewModel.mainState.test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        } // Ensure notes are loaded

        viewModel.searchState.test {
            var currentSearchState = awaitItem() // Initial FilterState
            assertTrue(currentSearchState is SearchState.FilterState)

            // Scenario 1: Query "Apples" + Color Filter (color 1)
            viewModel.searchTextFieldState.edit {
                append("Apples")
            }
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
            awaitItem() // Consume state change from text
            viewModel.onSetSearch(SearchSort.Color(1))
            var viewState = awaitItem() as SearchState.ViewState
            assertEquals(1, viewState.searches.size)
            assertTrue(viewState.searches.any { it.id == noteW.id })
            assertEquals(SearchSort.Color(1), viewState.searchSort)
        }
        viewModel.searchTextFieldState.clearText()
        viewModel.searchState.test {
            skipItems(1)
            // Scenario 2: Query "Apples" + Label Filter (Label1 "Groceries")
            viewModel.searchTextFieldState.edit {
                append("Apples")
            } // Reset text, debounce, consume
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
//            awaitItem()
            val groceriesLabelSort = SearchSort.Label(labelGroceries.name, 6, labelGroceries.id)
            viewModel.onSetSearch(groceriesLabelSort)
            val viewState = awaitItem() as SearchState.ViewState
            assertEquals(1, viewState.searches.size)
            assertTrue(viewState.searches.any { it.id == noteW.id })
            assertEquals(groceriesLabelSort, viewState.searchSort)
        }
        viewModel.searchTextFieldState.clearText()

        viewModel.searchState.test {
            skipItems(2)
            // Scenario 3: No Query + Color Filter (color 2)
            viewModel.onSetSearch(SearchSort.Color(2))
            viewModel.searchTextFieldState.edit {
                append("")
            }
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
            awaitItem() // Consume state change from text (to FilterState or ViewState with empty query)

            val viewState = awaitItem() as SearchState.ViewState // Should be ViewState due to sort
            print(viewState)
            assertEquals(2, viewState.searches.size)
            assertTrue(viewState.searches.any { it.id == noteX.id })
            assertTrue(viewState.searches.any { it.id == noteZ.id })
            assertEquals(SearchSort.Color(2), viewState.searchSort)
        }
        viewModel.searchTextFieldState.clearText()
        viewModel.searchState.test {
            skipItems(1)

            val groceriesLabelSort = SearchSort.Label(labelGroceries.name, 6, labelGroceries.id)
            viewModel.onSetSearch(groceriesLabelSort)

            // Scenario 4: No Query + Label Filter (Label1 "Groceries")
            viewModel.searchTextFieldState.edit {
                append("")
            }
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
//            awaitItem()

            val viewState = awaitItem() as SearchState.ViewState
            assertEquals(2, viewState.searches.size)
            assertTrue(viewState.searches.any { it.id == noteW.id })
            assertTrue(viewState.searches.any { it.id == noteY.id })
            assertEquals(groceriesLabelSort, viewState.searchSort)
            cancelAndConsumeRemainingEvents()
        }
    }
}
