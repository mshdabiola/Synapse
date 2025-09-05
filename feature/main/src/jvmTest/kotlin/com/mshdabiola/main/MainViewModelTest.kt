package com.mshdabiola.main

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
import kotlinx.coroutines.flow.filterIsInstance
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

    // Helper to create a NotePad
    private fun createNotePad(id: Long, title: String, isPinned: Boolean = false, category: NoteCategory = NoteCategory.NOTE, labels: List<Label> = emptyList(), color: Int = 0): NotePad {
        return NotePad(
            id = id, title = title,  labels = labels,  isPin = isPinned, color = color, noteCategory = category, notification = null
        )
    }

    @Before
    fun setup() {
        fakeNoteRepository = FakeNoteRepository()
        fakeUserDataRepository = FakeUserDataRepository()
        fakeLabelRepository = FakeLabelRepository()
        fakeGetAllNoteUseCase =  GetAllNoteUseCase(
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
        // Observe initial state
        val initialIsGrid = (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState).isGrid

        // Call the method
        viewModel.onDisplayModeChange()

        // Observe new state
        val newIsGrid = (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState).isGrid

        // Assert that isGrid is toggled
        assertEquals(!initialIsGrid, newIsGrid)
    }

    @Test
    fun pinOrUnpinNotes_whenAnyUnpinned_pinsAllSelectedNotesAndDeselects() = runTest {
        // Initial notes
        val note1 = createNotePad(id = 1, title = "Note 1", isPinned = false)
        val note2 = createNotePad(id = 2, title = "Note 2", isPinned = true)
        val note3 = createNotePad(id = 3, title = "Note 3", isPinned = false)
        fakeNoteRepository.upserts(listOf(note1, note2, note3))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure initial state is processed

        // Select notes
        viewModel.handleCardSelection(1L)
        viewModel.handleCardSelection(2L)

        // Call the method
        viewModel.pinOrUnpinNotes()

        // Assert notes are pinned
        val updatedNotes = (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState).let { it.pinNotePads + it.unPinNotePads }
        assertTrue(updatedNotes.first { it.id == 1L }.isPin)
        assertTrue(updatedNotes.first { it.id == 2L }.isPin)

        // Assert selection is cleared
        assertNull((viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState).selectState)
    }

    @Test
    fun pinOrUnpinNotes_whenAllPinned_unpinsAllSelectedNotesAndDeselects() = runTest {
        // Initial notes
        val note1 = createNotePad(id = 1, title = "Note 1", isPinned = true)
        val note2 = createNotePad(id = 2, title = "Note 2", isPinned = true)
        val note3 = createNotePad(id = 3, title = "Note 3", isPinned = true)
        fakeNoteRepository.upserts(listOf(note1, note2, note3))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure initial state is processed

        // Select notes
        viewModel.handleCardSelection(1L)
        viewModel.handleCardSelection(2L)

        // Call the method
        viewModel.pinOrUnpinNotes()

        // Assert notes are unpinned
        val updatedNotes = (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState).let { it.pinNotePads + it.unPinNotePads }
        assertFalse(updatedNotes.first { it.id == 1L }.isPin)
        assertFalse(updatedNotes.first { it.id == 2L }.isPin)
        assertTrue(updatedNotes.first { it.id == 3L }.isPin) // Note 3 was not selected, should remain pinned

        // Assert selection is cleared
        assertNull((viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState).selectState)
    }

    @Test
    fun setAllColor_updatesColorOfSelectedNotesAndDeselects() = runTest {
        // Initial notes
        val note1 = createNotePad(id = 1, title = "Note 1", color = 1)
        val note2 = createNotePad(id = 2, title = "Note 2", color = 2)
        val note3 = createNotePad(id = 3, title = "Note 3", color = 3)
        fakeNoteRepository.upserts(listOf(note1, note2, note3))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure initial state is processed

        // Select notes
        viewModel.handleCardSelection(1L)
        viewModel.handleCardSelection(3L)

        val newColor = 5
        // Call the method
        viewModel.setAllColor(newColor)

        // Assert colors are updated for selected notes
        val updatedNotes = (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState).let { it.pinNotePads + it.unPinNotePads }
        assertEquals(newColor, updatedNotes.first { it.id == 1L }.color)
        assertEquals(2, updatedNotes.first { it.id == 2L }.color) // Note 2 was not selected, color should remain unchanged
        assertEquals(newColor, updatedNotes.first { it.id == 3L }.color)

        // Assert selection is cleared
        assertNull((viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState).selectState)
    }

    @Test
    fun onArchiveNote_whenAnyArchived_movesSelectedToNoteAndDeselects() = runTest {
        // Initial notes
        val note1 = createNotePad(id = 1, title = "Note 1", category = NoteCategory.ARCHIVE)
        val note2 = createNotePad(id = 2, title = "Note 2", category = NoteCategory.NOTE)
        val note3 = createNotePad(id = 3, title = "Note 3", category = NoteCategory.ARCHIVE)
        val note4 = createNotePad(id = 4, title = "Note 4", category = NoteCategory.NOTE) // Unselected note

        fakeNoteRepository.upserts(listOf(note1, note2, note3, note4))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure initial state is processed

        // Select notes (one archived, one not)
        viewModel.handleCardSelection(1L)
        viewModel.handleCardSelection(2L)

        // Call the method
        viewModel.onArchiveNote()

        // Assert selected notes are moved to NOTE category
        val updatedNotes = fakeNoteRepository.getAll().first()
        assertEquals(NoteCategory.NOTE, updatedNotes.first { it.id == 1L }.noteCategory)
        assertEquals(NoteCategory.NOTE, updatedNotes.first { it.id == 2L }.noteCategory)
        // Assert non-selected notes remain unchanged
        assertEquals(NoteCategory.ARCHIVE, updatedNotes.first { it.id == 3L }.noteCategory)
        assertEquals(NoteCategory.NOTE, updatedNotes.first { it.id == 4L }.noteCategory)


        // Assert selection is cleared
        val currentMainState = viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState
        assertNull(currentMainState.selectState)
    }

    @Test
    fun onArchiveNote_whenNoneArchived_movesSelectedToArchiveAndDeselects() = runTest {
        // Initial notes
        val note1 = createNotePad(id = 1, title = "Note 1", category = NoteCategory.NOTE)
        val note2 = createNotePad(id = 2, title = "Note 2", category = NoteCategory.NOTE)
        val note3 = createNotePad(id = 3, title = "Note 3", category = NoteCategory.TRASH) // Different category, unselected
        val note4 = createNotePad(id = 4, title = "Note 4", category = NoteCategory.NOTE) // Unselected note

        fakeNoteRepository.upserts(listOf(note1, note2, note3, note4))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure initial state is processed

        // Select notes (all in NOTE category)
        viewModel.handleCardSelection(1L)
        viewModel.handleCardSelection(2L)

        // Call the method
        viewModel.onArchiveNote()

        // Assert selected notes are moved to ARCHIVE category
        val updatedNotes = fakeNoteRepository.getAll().first()
        assertEquals(NoteCategory.ARCHIVE, updatedNotes.first { it.id == 1L }.noteCategory)
        assertEquals(NoteCategory.ARCHIVE, updatedNotes.first { it.id == 2L }.noteCategory)
        // Assert non-selected notes remain unchanged
        assertEquals(NoteCategory.TRASH, updatedNotes.first { it.id == 3L }.noteCategory)
        assertEquals(NoteCategory.NOTE, updatedNotes.first { it.id == 4L }.noteCategory)

        // Assert selection is cleared
        val currentMainState = viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState
        assertNull(currentMainState.selectState)
    }

    @Test
    fun onDeleteNote_movesSelectedToTrashAndDeselects() = runTest {
        // Initial notes
        val note1 = createNotePad(id = 1, title = "Note 1", category = NoteCategory.NOTE)
        val note2 = createNotePad(id = 2, title = "Note 2", category = NoteCategory.ARCHIVE)
        val note3 = createNotePad(id = 3, title = "Note 3", category = NoteCategory.NOTE) // Unselected note

        fakeNoteRepository.upserts(listOf(note1, note2, note3))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure initial state is processed

        // Select notes
        viewModel.handleCardSelection(1L)
        viewModel.handleCardSelection(2L)

        // Call the method
        viewModel.onDeleteNote()

        // Assert selected notes are moved to TRASH category
        val updatedNotes = fakeNoteRepository.getAll().first()
        assertEquals(NoteCategory.TRASH, updatedNotes.first { it.id == 1L }.noteCategory)
        assertEquals(NoteCategory.TRASH, updatedNotes.first { it.id == 2L }.noteCategory)
        // Assert non-selected notes remain unchanged
        assertEquals(NoteCategory.NOTE, updatedNotes.first { it.id == 3L }.noteCategory)

        // Assert selection is cleared
        val currentMainState = viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState
        assertNull(currentMainState.selectState)
    }

    @Test
    fun onDeleteForever_deletesSelectedNotesAndDeselects() = runTest {
        // Initial notes
        val note1 = createNotePad(id = 1, title = "Note 1")
        val note2 = createNotePad(id = 2, title = "Note 2")
        val note3 = createNotePad(id = 3, title = "Note 3") // Unselected note
        fakeNoteRepository.upserts(listOf(note1, note2, note3))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure initial state is processed

        // Select notes
        viewModel.handleCardSelection(1L)
        viewModel.handleCardSelection(2L)

        // Call the method
        viewModel.onDeleteForever()

        // Assert selected notes are deleted
        val updatedNotes = fakeNoteRepository.getAll().first()
        assertFalse(updatedNotes.any { it.id == 1L })
        assertFalse(updatedNotes.any { it.id == 2L })
        // Assert non-selected notes remain
        assertTrue(updatedNotes.any { it.id == 3L })

        // Assert selection is cleared
        val currentMainState = viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState
        assertNull(currentMainState.selectState)
    }

    @Test
    fun onRestore_movesSelectedToNoteAndDeselects() = runTest {
        // Initial notes
        val note1 = createNotePad(id = 1, title = "Note 1", category = NoteCategory.TRASH)
        val note2 = createNotePad(id = 2, title = "Note 2", category = NoteCategory.NOTE)
        val note3 = createNotePad(id = 3, title = "Note 3", category = NoteCategory.TRASH) // To be selected

        fakeNoteRepository.upserts(listOf(note1, note2, note3))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure initial state is processed

        // Select trashed notes
        viewModel.handleCardSelection(1L)
        viewModel.handleCardSelection(3L)

        // Call the method
        viewModel.onRestore()

        // Assert selected notes are moved to NOTE category
        val updatedNotes = fakeNoteRepository.getAll().first()
        assertEquals(NoteCategory.NOTE, updatedNotes.first { it.id == 1L }.noteCategory)
        assertEquals(NoteCategory.NOTE, updatedNotes.first { it.id == 3L }.noteCategory)
        // Assert non-selected notes remain unchanged
        assertEquals(NoteCategory.NOTE, updatedNotes.first { it.id == 2L }.noteCategory)

        // Assert selection is cleared
        val currentMainState = viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState
        assertNull(currentMainState.selectState)
    }

    @Test
    fun onCopyNote_createsDuplicateAndDeselects() = runTest {
        // Initial notes
        val originalNote = createNotePad(id = 1, title = "Original Note", isPinned = true, category = NoteCategory.ARCHIVE, labels = listOf(Label(10L, "Test Label")), color = 5)
        val otherNote = createNotePad(id = 2, title = "Other Note")
        fakeNoteRepository.upserts(listOf(originalNote, otherNote))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure initial state is processed

        val initialNotesCount = fakeNoteRepository.getAll().first().size

        // Select the note to copy
        viewModel.handleCardSelection(originalNote.id)

        // Call the method
        viewModel.onCopyNote()

        // Assert notes count increased by one
        val finalNotes = fakeNoteRepository.getAll().first()
        assertEquals(initialNotesCount + 1, finalNotes.size)

        // Assert new note is a copy with a different ID
        val copiedNote = finalNotes.firstOrNull { it.id != originalNote.id && it.title == originalNote.title }
        assertNotNull(copiedNote)
        assertNotEquals(originalNote.id, copiedNote!!.id)
        assertEquals(originalNote.title, copiedNote.title)
        assertEquals(originalNote.isPin, copiedNote.isPin)
        assertEquals(originalNote.noteCategory, copiedNote.noteCategory)
        assertEquals(originalNote.labels, copiedNote.labels)
        assertEquals(originalNote.color, copiedNote.color)

        // Assert original note is unchanged
        val stillOriginalNote = finalNotes.first { it.id == originalNote.id }
        assertEquals(originalNote.title, stillOriginalNote.title)

        // Assert selection is cleared
        val currentMainState = viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState
        assertNull(currentMainState.selectState)
    }

    @Test
    fun deleteLabel_removesLabelAndResetsCategory() = runTest {
        val labelIdToDelete = 5L
        val labelToDelete = Label(labelIdToDelete, "Work")
        fakeLabelRepository.upserts(listOf(labelToDelete))

        val initialUserSettings = UserSettings(
            isGrid = true,
            noteCategory = NoteDisplayCategory(labelIdToDelete, NoteCategory.LABEL)
        )
        fakeUserDataRepository.setFakeUserData(initialUserSettings)

        val currentState = viewModel.mainState.first { it is MainState.ViewState && (it as MainState.ViewState).noteDisplayCategory.labelId == labelIdToDelete } as MainState.ViewState
        assertEquals(labelIdToDelete, currentState.noteDisplayCategory.labelId)

        viewModel.deleteLabel()

        assertNull(fakeLabelRepository.get(labelIdToDelete).firstOrNull())

        val finalUserSettings = fakeUserDataRepository.userSettings.first()
        assertEquals(NoteDisplayCategory(0, NoteCategory.NOTE), finalUserSettings.noteCategory)
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
            noteCategory = NoteDisplayCategory(testLabelId, NoteCategory.LABEL)
        )
        fakeUserDataRepository.setFakeUserData(initialUserSettings)

        val currentState = viewModel.mainState.first {
            it is MainState.ViewState &&
            (it as MainState.ViewState).noteDisplayCategory.labelId == testLabelId
        } as MainState.ViewState
        assertEquals(testLabelId, currentState.noteDisplayCategory.labelId)
        assertEquals(oldName, currentState.labelName)

        viewModel.renameLabel(newName)

        val renamedLabel = fakeLabelRepository.get(testLabelId).first()
        assertNotNull(renamedLabel)
        assertEquals(newName, renamedLabel!!.name)

        val updatedMainState = viewModel.mainState.first {
            it is MainState.ViewState &&
            (it as MainState.ViewState).labelName == newName
        } as MainState.ViewState
        assertEquals(newName, updatedMainState.labelName)
    }

    @Test
    fun onDeleteAllTrash_removesAllTrashedNotes() = runTest {
        // Initial notes
        val trashedNote1 = createNotePad(id = 1, title = "Trashed Note 1", category = NoteCategory.TRASH)
        val trashedNote2 = createNotePad(id = 2, title = "Trashed Note 2", category = NoteCategory.TRASH)
        val regularNote = createNotePad(id = 3, title = "Regular Note", category = NoteCategory.NOTE)
        val archivedNote = createNotePad(id = 4, title = "Archived Note", category = NoteCategory.ARCHIVE)

        fakeNoteRepository.upserts(listOf(trashedNote1, trashedNote2, regularNote, archivedNote))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure initial state is processed if needed

        val initialNotes = fakeNoteRepository.getAll().first()
        val initialTrashedCount = initialNotes.count { it.noteCategory == NoteCategory.TRASH }
        assertTrue(initialTrashedCount == 2) // Verify setup

        // Call the method
        viewModel.onDeleteAllTrash()

        // Assert trashed notes are deleted and others remain
        val finalNotes = fakeNoteRepository.getAll().first()
        assertTrue(finalNotes.none { it.noteCategory == NoteCategory.TRASH })
        assertEquals(2, finalNotes.size) // Only regularNote and archivedNote should remain
        assertTrue(finalNotes.any { it.id == regularNote.id && it.noteCategory == NoteCategory.NOTE })
        assertTrue(finalNotes.any { it.id == archivedNote.id && it.noteCategory == NoteCategory.ARCHIVE })
    }

    @Test
    fun onSendNote_returnsSelectedNoteAndDeselects() = runTest {
        // Initial notes
        val noteToSend = createNotePad(id = 1, title = "Note to Send", color = 3)
        val otherNote = createNotePad(id = 2, title = "Other Note")
        fakeNoteRepository.upserts(listOf(noteToSend, otherNote))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState) // Ensure notes are loaded

        // Select the note
        viewModel.handleCardSelection(noteToSend.id)
        var currentSelectState = (viewModel.mainState.first { it is MainState.ViewState && it.selectState?.setOfSelected?.contains(noteToSend.id) == true } as MainState.ViewState).selectState
        assertNotNull(currentSelectState)
        assertTrue(currentSelectState!!.setOfSelected.contains(noteToSend.id))

        // Call the method
        val returnedNote = viewModel.onSendNote()

        // Assert returned note is correct
        assertNotNull(returnedNote)
        assertEquals(noteToSend.id, returnedNote.id)
        assertEquals(noteToSend.title, returnedNote.title)
        assertEquals(noteToSend.color, returnedNote.color)

        // Assert selection is cleared
        currentSelectState = (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState).selectState
        assertNull(currentSelectState)
    }

    @Test
    fun onSetSearch_updatesSearchSortInSearchState() = runTest {
        // Initial Setup
        fakeNoteRepository.upserts(listOf(
            createNotePad(id = 1, title = "Searchable Note Alpha"),
            createNotePad(id = 2, title = "Searchable Note Beta")
        ))
        viewModel.searchTextFieldState.edit {
            append("Searchable")
        }
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201) // Advance past debounce time

        // Action & Assertion 1: Setting a SearchSort
        val testSearchSort = SearchSort.Type(0) // Example SearchSort
        viewModel.onSetSearch(testSearchSort)

        var searchViewState = viewModel.searchState
            .filterIsInstance<SearchState.ViewState>()
            .first { it.searchSort == testSearchSort }
        assertEquals(testSearchSort, searchViewState.searchSort)

        // Action & Assertion 2: Setting SearchSort to null
        viewModel.onSetSearch(null)

        searchViewState = viewModel.searchState
            .filterIsInstance<SearchState.ViewState>()
            .first { it.searchSort == null }
        assertNull(searchViewState.searchSort)
    }

    @Test
    fun searchState_filtersNotesBasedOnQueryText() = runTest {
        // Setup
        val noteApple = createNotePad(id = 1, title = "Apple Note")
        val noteBanana = createNotePad(id = 2, title = "Banana Article")
        val noteApplePie = createNotePad(id = 3, title = "Apple Pie Recipe")
        fakeNoteRepository.upserts(listOf(noteApple, noteBanana, noteApplePie))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState)

        // Query "Apple"
        viewModel.searchTextFieldState.edit {
            append("Apple")
        }
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
        var searchResultState = viewModel.searchState.filterIsInstance<SearchState.ViewState>().first()
        assertEquals(2, searchResultState.searches.size)
        assertTrue(searchResultState.searches.any { it.id == noteApple.id })
        assertTrue(searchResultState.searches.any { it.id == noteApplePie.id })

        // Query "Banana"
        viewModel.searchTextFieldState.edit {
            append("Banana")
        }
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
        searchResultState = viewModel.searchState.filterIsInstance<SearchState.ViewState>().first()
        assertEquals(1, searchResultState.searches.size)
        assertTrue(searchResultState.searches.any { it.id == noteBanana.id })

        // Query "Orange" (No match)
        viewModel.searchTextFieldState.edit {
            append("Orange")
        }
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
        searchResultState = viewModel.searchState.filterIsInstance<SearchState.ViewState>().first()
        assertTrue(searchResultState.searches.isEmpty())

        // Blank Query - should show FilterState
        viewModel.searchTextFieldState.edit {
            append("")
        }
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
        val blankQueryResultState = viewModel.searchState.first()
        assertTrue(blankQueryResultState is SearchState.FilterState)
    }

    @Test
    fun searchState_filtersByQueryAndAdditionalCriteria() = runTest {
        // Setup
        val labelGroceries = Label(1L, "Groceries")
        val labelWork = Label(2L, "Work")
        fakeLabelRepository.upserts(listOf(labelGroceries, labelWork))

        val noteW = createNotePad(id = 10, title = "Apples Red", color = 1, labels = listOf(labelGroceries))
        val noteX = createNotePad(id = 20, title = "Apples Green", color = 2, labels = listOf(labelWork))
        val noteY = createNotePad(id = 30, title = "Bananas Yellow", color = 3, labels = listOf(labelGroceries))
        val noteZ = createNotePad(id = 40, title = "Green Grapes", color = 2)
        fakeNoteRepository.upserts(listOf(noteW, noteX, noteY, noteZ))
        (viewModel.mainState.first { it is MainState.ViewState } as MainState.ViewState)

        // Scenario 1: Query "Apples" + Color Filter (color 1)
        viewModel.searchTextFieldState.edit {
            append("Apples")
        }
        viewModel.onSetSearch(SearchSort.Color(1))
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
        var searchResultState = viewModel.searchState.filterIsInstance<SearchState.ViewState>()
            .first { it.searchSort is SearchSort.Color && (it.searchSort as SearchSort.Color).colorIndex == 1 && viewModel.searchTextFieldState.text.toString() == "Apples" }
        assertEquals(1, searchResultState.searches.size)
        assertTrue(searchResultState.searches.any { it.id == noteW.id })
        assertEquals(SearchSort.Color(1), searchResultState.searchSort)

        // Scenario 2: Query "Apples" + Label Filter (Label1 "Groceries")
        viewModel.searchTextFieldState.edit {
            append("Apples")
        }  // Ensure query is still apples
        val groceriesLabelSort = SearchSort.Label(labelGroceries.name, 6, labelGroceries.id)
        viewModel.onSetSearch(groceriesLabelSort)
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
        searchResultState = viewModel.searchState.filterIsInstance<SearchState.ViewState>().first { it.searchSort is SearchSort.Label && (it.searchSort as SearchSort.Label).id == labelGroceries.id && viewModel.searchTextFieldState.text.toString() == "Apples"}
        assertEquals(1, searchResultState.searches.size)
        assertTrue(searchResultState.searches.any { it.id == noteW.id })
        assertEquals(groceriesLabelSort, searchResultState.searchSort)

        // Scenario 3: No Query + Color Filter (color 2)
        viewModel.searchTextFieldState.edit {
            append("")
        }
        viewModel.onSetSearch(SearchSort.Color(2))
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
        searchResultState = viewModel.searchState.filterIsInstance<SearchState.ViewState>()
            .first { it.searchSort is SearchSort.Color && (it.searchSort as SearchSort.Color).colorIndex == 2 && viewModel.searchTextFieldState.text.toString() == ""}
        assertEquals(2, searchResultState.searches.size)
        assertTrue(searchResultState.searches.any { it.id == noteX.id })
        assertTrue(searchResultState.searches.any { it.id == noteZ.id })
        assertEquals(SearchSort.Color(2), searchResultState.searchSort)

        // Scenario 4: No Query + Label Filter (Label1 "Groceries")
        viewModel.searchTextFieldState.edit {
            append("")
        }
        viewModel.onSetSearch(groceriesLabelSort) // Re-use from scenario 2
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(201)
        searchResultState = viewModel.searchState.filterIsInstance<SearchState.ViewState>().first { it.searchSort is SearchSort.Label && (it.searchSort as SearchSort.Label).id == labelGroceries.id && viewModel.searchTextFieldState.text.toString() == ""}
        assertEquals(2, searchResultState.searches.size)
        assertTrue(searchResultState.searches.any { it.id == noteW.id })
        assertTrue(searchResultState.searches.any { it.id == noteY.id })
        assertEquals(groceriesLabelSort, searchResultState.searchSort)
    }

}
