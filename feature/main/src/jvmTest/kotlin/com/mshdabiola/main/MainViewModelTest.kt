package com.mshdabiola.main

import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.GetAllNoteUseCase
import com.mshdabiola.domain.LinkUriUseCase
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteCategory
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
import org.junit.Before
import org.junit.Rule

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
//
//    @Test
//    fun `mainState initial emission is Loading then ViewState with default settings`() = runTest {
//        viewModel.mainState.test {
//            assertEquals(MainState.Loading, awaitItem(), "Initial state should be Loading")
//
//            val viewState = awaitItem() as MainState.ViewState
//            assertTrue(viewState.pinNotePads.isEmpty(), "Pin notes should be empty initially")
//            assertTrue(viewState.unPinNotePads.isEmpty(), "Unpin notes should be empty initially")
//            assertEquals(NoteDisplayCategory(noteCategory = NoteCategory.NOTE), viewState.noteDisplayCategory)
//            assertFalse(viewState.isGrid, "isGrid should be false by default")
//            assertNull(viewState.selectState, "selectState should be null initially")
//            cancelAndConsumeRemainingEvents()
//        }
//    }
//
//    @Test
//    fun `mainState updates when UserDataRepository emits new settings`() = runTest {
//        val newCategory = NoteDisplayCategory(noteCategory = NoteCategory.ARCHIVE)
//        val newSettings = UserSettings(noteCategory = newCategory, isGrid = true)
//
//        viewModel.mainState.test {
//            awaitItem() // Skip Loading
//            awaitItem() // Skip initial ViewState
//
//            fakeUserDataRepository.setFakeUserData(newSettings)
//
//            val updatedState = awaitItem() as MainState.ViewState
//            assertEquals(newCategory, updatedState.noteDisplayCategory)
//            assertTrue(updatedState.isGrid)
//            cancelAndConsumeRemainingEvents()
//        }
//    }
//
//     @Test
//    fun `mainState reflects notes from GetAllNoteUseCase based on NoteCategory NOTE`() = runTest {
//        val note1 = createNotePad(1, "Note 1", category = NoteCategory.NOTE)
//        val note2 = createNotePad(2, "Pinned Note 2", isPinned = true, category = NoteCategory.NOTE)
//        val archiveNote = createNotePad(3, "Archive Note", category = NoteCategory.ARCHIVE)
//        fakeNoteRepository.upserts(list(note1, note2, archiveNote))
//
//        // Ensure UserDataRepository is set to NOTE category
//        fakeUserDataRepository.setFakeUserData(UserSettings(noteCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE)))
//
//        viewModel.mainState.test {
//            awaitItem() // Loading
//            val viewState = awaitItem() as MainState.ViewState // Initial state from empty repo
//
//            // This awaitItem will capture the state after notes are loaded based on the category
//            val loadedState = awaitItem() as MainState.ViewState
//            assertEquals(1, loadedState.pinNotePads.size)
//            assertEquals(note2, loadedState.pinNotePads.first())
//            assertEquals(1, loadedState.unPinNotePads.size)
//            assertEquals(note1, loadedState.unPinNotePads.first())
//            cancelAndConsumeRemainingEvents()
//        }
//    }
//
//    @Test
//    fun `mainState reflects labelName when label is available for LABEL category`() = runTest {
//        val label1 = Label(1L, "Work")
//        fakeLabelRepository.upsert(label1)
//        val labelCategory = NoteDisplayCategory(1,noteCategory = NoteCategory.LABEL)
//        fakeUserDataRepository.setFakeUserData(UserSettings(noteCategory = labelCategory))
//
//        viewModel.mainState.test {
//            awaitItem() // Loading
//            awaitItem() // Initial ViewState
//
//            val updatedState = awaitItem() as MainState.ViewState
//            assertEquals(labelCategory, updatedState.noteDisplayCategory)
//            assertEquals("Work", updatedState.labelName)
//            cancelAndConsumeRemainingEvents()
//        }
//    }
//
//    // More tests for mainState based on other NoteCategories will be similar to the NOTE category one.
//
//    // --- searchState Tests ---
//    @Test
//    fun `searchState initial emission is FilterState when query is blank`() = runTest {
//         val note1 = createNotePad(1, "Test Note", labels = listOf(Label(10L, "TagA")), color = 1)
//         fakeNoteRepository.upserts(note1)
//         fakeUserDataRepository.setFakeUserData(UserSettings(noteCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE))) // Trigger mainState to process notes
//
//        // Await mainState to settle and process notes before checking searchState
//        viewModel.mainState.test {
//            awaitItem() // Loading
//            awaitItem() // Initial view state
//            awaitItem() // View state with notes
//        }
//
//        viewModel.searchTextFieldState.clearText()
//        viewModel.onSetSearch(null) // Ensure searchSort is null
//
//        viewModel.searchState.test {
//            val filterState = awaitItem() as SearchState.FilterState
//            assertTrue(filterState.types.isNotEmpty(), "Types should not be empty")
//            assertEquals(1, filterState.labels.size, "Labels should reflect notes")
//            assertEquals("TagA", (filterState.labels.first() as SearchSort.Label).name)
//            assertEquals(1, filterState.colors.size, "Colors should reflect notes")
//            assertEquals(1, (filterState.colors.first() as SearchSort.Color).colorIndex)
//            cancelAndConsumeRemainingEvents()
//        }
//    }
//
//    @Test
//    fun `searchState emits ViewState with results when query is entered`() = runTest {
//        val note1 = createNotePad(1, "Apple Pie")
//        val note2 = createNotePad(2, "Banana Bread")
//        fakeNoteRepository.upserts(list(note1, note2))
//        fakeUserDataRepository.setFakeUserData(UserSettings(noteCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE)))
//         // Await mainState to settle
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem() }
//
//
//        viewModel.searchTextFieldState.edit {
//            append("Apple")
//        }
//
//        viewModel.searchState.test {
//            val result = awaitItem() // May initially be FilterState if text change is quick
//            val viewState = if (result is SearchState.FilterState) awaitItem() as SearchState.ViewState else result as SearchState.ViewState
//
//            assertEquals(1, viewState.searches.size)
//            assertEquals("Apple Pie", viewState.searches.first().title)
//            assertNull(viewState.searchSort)
//            cancelAndConsumeRemainingEvents()
//        }
//    }
//
//     @Test
//    fun `searchState filters by SearchSort Label`() = runTest {
//        val labelDev = Label(1L, "dev")
//        val noteWithDev = createNotePad(1, "Note Dev", labels = listOf(labelDev))
//        val noteWithoutDev = createNotePad(2, "Note Other")
//        fakeNoteRepository.upserts(list(noteWithDev, noteWithoutDev))
//        fakeUserDataRepository.setFakeUserData(UserSettings(noteCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem() } // Settle mainState
//
//        viewModel.searchTextFieldState.clearText()
//        val searchSortLabel = SearchSort.Label("dev", 6, 1L)
//        viewModel.onSetSearch(searchSortLabel)
//
//        viewModel.searchState.test {
//            val viewState = awaitItem() as SearchState.ViewState
//            assertEquals(1, viewState.searches.size)
//            assertEquals(noteWithDev.id, viewState.searches.first().id)
//            assertEquals(searchSortLabel, viewState.searchSort)
//            cancelAndConsumeRemainingEvents()
//        }
//    }
//
//    // --- Action/Event Tests ---
//    @Test
//    fun `handleCardSelection selects and deselects single card`() = runTest {
//        val note1 = createNotePad(1, "Note 1")
//        fakeNoteRepository.addNotes(note1)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.NOTE, null)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem() } // Settle
//
//        // Select
//        viewModel.handleCardSelection(1L)
//        var currentSelectState = (viewModel.mainState.value as MainState.ViewState).selectState
//        assertNotNull(currentSelectState)
//        assertEquals(setOf(1L), currentSelectState.setOfSelected)
//
//        // Deselect by selecting again
//        viewModel.handleCardSelection(1L)
//        currentSelectState = (viewModel.mainState.value as MainState.ViewState).selectState
//        assertNull(currentSelectState)
//    }
//
//    @Test
//    fun `handleCardSelection adds to existing selection`() = runTest {
//        val note1 = createNotePad(1, "Note 1")
//        val note2 = createNotePad(2, "Note 2")
//        fakeNoteRepository.upserts(listOf(note1, note2))
//        fakeUserDataRepository.setFakeUserData(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.NOTE, null)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem() } // Settle
//
//        viewModel.handleCardSelection(1L)
//        viewModel.handleCardSelection(2L)
//
//        val currentSelectState = (viewModel.mainState.value as MainState.ViewState).selectState
//        assertNotNull(currentSelectState)
//        assertEquals(setOf(1L, 2L), currentSelectState.setOfSelected)
//    }
//
//    @Test
//    fun `deselectNotes clears selection`() = runTest {
//        val note1 = createNotePad(1, "Note 1")
//        fakeNoteRepository.addNotes(note1)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.NOTE, null)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem() } // Settle
//
//        viewModel.handleCardSelection(1L)
//        assertNotNull((viewModel.mainState.value as MainState.ViewState).selectState)
//
//        viewModel.deselectNotes()
//        assertNull((viewModel.mainState.value as MainState.ViewState).selectState)
//    }
//
//    @Test
//    fun `pinOrUnpinNotes pins unpinned notes`() = runTest {
//        val note1 = createNotePad(1, "Note to Pin", isPinned = false)
//        fakeNoteRepository.addNotes(note1)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.NOTE, null)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem(); awaitItem()} // Settle mainState and its reaction to notes
//
//        viewModel.handleCardSelection(1L) // Select the note
//        val selectedId = (viewModel.mainState.value as MainState.ViewState).selectState!!.setOfSelected
//
//        viewModel.pinOrUnpinNotes()
//
//        assertTrue(fakeNoteRepository.updatePinForIdsCalledWith!!.second) // true for isPinned
//        assertEquals(selectedId, fakeNoteRepository.updatePinForIdsCalledWith!!.first)
//        assertNull((viewModel.mainState.value as MainState.ViewState).selectState, "Selection should be cleared after pinning")
//    }
//
//    @Test
//    fun `setAllColor updates color for selected notes`() = runTest {
//        val note1 = createNotePad(1, "Color Note")
//        fakeNoteRepository.addNotes(note1)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.NOTE, null)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem(); awaitItem() }
//
//        viewModel.handleCardSelection(1L)
//        val selectedIds = (viewModel.mainState.value as MainState.ViewState).selectState!!.setOfSelected
//        val newColor = 5
//
//        viewModel.setAllColor(newColor)
//
//        assertEquals(newColor, fakeNoteRepository.updateColorForIdsCalledWith!!.second)
//        assertEquals(selectedIds, fakeNoteRepository.updateColorForIdsCalledWith!!.first)
//        assertNull((viewModel.mainState.value as MainState.ViewState).selectState)
//    }
//
//     @Test
//    fun `onArchiveNote archives notes when current category is NOTE`() = runTest {
//        val note1 = createNotePad(1, "Note to Archive", category = NoteCategory.NOTE)
//        fakeNoteRepository.addNotes(note1)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.NOTE, null)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem(); awaitItem() }
//
//        viewModel.handleCardSelection(1L)
//        val selectedIds = (viewModel.mainState.value as MainState.ViewState).selectState!!.setOfSelected
//
//        viewModel.onArchiveNote()
//
//        assertEquals(NoteCategory.ARCHIVE, fakeNoteRepository.updateNoteTypeForIdsCalledWith!!.second)
//        assertEquals(selectedIds, fakeNoteRepository.updateNoteTypeForIdsCalledWith!!.first)
//        assertNull((viewModel.mainState.value as MainState.ViewState).selectState)
//    }
//
//    @Test
//    fun `onDeleteNote moves selected notes to TRASH`() = runTest {
//        val note1 = createNotePad(1, "Note to Delete")
//        fakeNoteRepository.addNotes(note1)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.NOTE, null)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem(); awaitItem() }
//
//        viewModel.handleCardSelection(1L)
//        val selectedIds = (viewModel.mainState.value as MainState.ViewState).selectState!!.setOfSelected
//
//        viewModel.onDeleteNote()
//
//        assertEquals(NoteCategory.TRASH, fakeNoteRepository.updateNoteTypeForIdsCalledWith!!.second)
//        assertEquals(selectedIds, fakeNoteRepository.updateNoteTypeForIdsCalledWith!!.first)
//        assertNull((viewModel.mainState.value as MainState.ViewState).selectState)
//    }
//
//    @Test
//    fun `onDeleteForever deletes selected notes permanently`() = runTest {
//        val note1 = createNotePad(1, "Note to Delete Forever", category = NoteCategory.TRASH)
//        fakeNoteRepository.addNotes(note1)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.TRASH, null)))
//         viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem(); awaitItem() }
//
//        viewModel.handleCardSelection(1L)
//        val selectedIds = (viewModel.mainState.value as MainState.ViewState).selectState!!.setOfSelected
//
//        viewModel.onDeleteForever()
//
//        assertEquals(selectedIds, fakeNoteRepository.deleteIdsCalledWith)
//        assertNull((viewModel.mainState.value as MainState.ViewState).selectState)
//    }
//
//    @Test
//    fun `onRestore restores selected notes from TRASH to NOTE`() = runTest {
//        val note1 = createNotePad(1, "Note to Restore", category = NoteCategory.TRASH)
//        fakeNoteRepository.addNotes(note1)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.TRASH, null)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem(); awaitItem() }
//
//        viewModel.handleCardSelection(1L)
//        val selectedIds = (viewModel.mainState.value as MainState.ViewState).selectState!!.setOfSelected
//
//        viewModel.onRestore()
//
//        assertEquals(NoteCategory.NOTE, fakeNoteRepository.updateNoteTypeForIdsCalledWith!!.second)
//        assertEquals(selectedIds, fakeNoteRepository.updateNoteTypeForIdsCalledWith!!.first)
//        assertNull((viewModel.mainState.value as MainState.ViewState).selectState)
//    }
//
//    @Test
//    fun `onCopyNote calls AddAllNoteUseCase with a copy of the selected note`() = runTest {
//        val originalNote = createNotePad(1L, "Original Note")
//        fakeNoteRepository.addNotes(originalNote)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.NOTE, null)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem(); awaitItem() }
//
//        viewModel.handleCardSelection(1L) // Select the note
//        assertNotNull((viewModel.mainState.value as MainState.ViewState).selectState, "Note should be selected")
//
//        viewModel.onCopyNote()
//
//        assertNull((viewModel.mainState.value as MainState.ViewState).selectState, "Selection should be cleared after copy")
//        assertNotNull(fakeAddAllNoteUseCase.addAllNoteCalledWith, "AddAllNoteUseCase should be called")
//        assertEquals(originalNote.title, fakeAddAllNoteUseCase.addAllNoteCalledWith!!.title)
//        assertEquals(originalNote.content, fakeAddAllNoteUseCase.addAllNoteCalledWith!!.content)
//        assertEquals(-1L, fakeAddAllNoteUseCase.addAllNoteCalledWith!!.id, "Copied note should have ID -1 before insertion by use case")
//    }
//
//    @Test
//    fun `deleteLabel resets category and calls repository delete`() = runTest {
//        val labelIdToDelete = 5L
//        fakeLabelRepository.addLabel(Label(labelIdToDelete, "Old Label"))
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.LABEL, labelIdToDelete)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem() }
//
//        viewModel.deleteLabel()
//
//        assertEquals(NoteDisplayCategory(0, NoteCategory.NOTE), fakeUserDataRepository.setNoteCategoryCalledWith)
//        assertEquals(labelIdToDelete, fakeLabelRepository.deleteCalledWith)
//    }
//
//    @Test
//    fun `renameLabel calls repository upserts`() = runTest {
//        val labelToRename = Label(3L, "Old Name")
//        val newName = "New Name"
//        fakeLabelRepository.addLabel(labelToRename)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.LABEL, 3L)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem() }
//
//        viewModel.renameLabel(newName)
//
//        assertNotNull(fakeLabelRepository.upsertsCalledWith)
//        assertEquals(1, fakeLabelRepository.upsertsCalledWith!!.size)
//        assertEquals(Label(3L, newName), fakeLabelRepository.upsertsCalledWith!!.first())
//    }
//
//    @Test
//    fun `onDeleteAllTrash calls repository deleteTrash`() = runTest {
//        viewModel.onDeleteAllTrash()
//        assertTrue(fakeNoteRepository.deleteTrashCalled)
//    }
//
//    @Test
//    fun `onDisplayModeChange toggles grid preference in UserDataRepository`() = runTest {
//        // Initial state is isGrid = false
//        viewModel.mainState.test { awaitItem(); awaitItem() }
//
//        viewModel.onDisplayModeChange()
//        assertTrue(fakeUserDataRepository.setGridCalledWith!!, "Should set grid to true")
//
//        // Simulate update in repo
//        fakeUserDataRepository.emitUserSettings(UserSettings(isGrid = true))
//        viewModel.mainState.test { awaitItem() } // consume the update
//
//        viewModel.onDisplayModeChange()
//        assertFalse(fakeUserDataRepository.setGridCalledWith!!, "Should set grid to false")
//    }
//
//    @Test
//    fun `onSendNote returns selected note and clears selection`() = runTest {
//        val noteToSend = createNotePad(1L, "Send Me")
//        fakeNoteRepository.addNotes(noteToSend)
//        fakeUserDataRepository.emitUserSettings(UserSettings(noteCategory = NoteDisplayCategory(NoteCategory.NOTE, null)))
//        viewModel.mainState.test { awaitItem(); awaitItem(); awaitItem(); awaitItem() }
//
//        viewModel.handleCardSelection(1L)
//        assertNotNull((viewModel.mainState.value as MainState.ViewState).selectState)
//
//        val sentNote = viewModel.onSendNote()
//
//        assertEquals(noteToSend.id, sentNote.id)
//        assertEquals(noteToSend.title, sentNote.title)
//        assertNull((viewModel.mainState.value as MainState.ViewState).selectState, "Selection should be cleared after sending")
//    }
//
//    @Test
//    fun `onSetSearch updates internal searchSort StateFlow`() = runTest {
//        val searchSortColor = SearchSort.Color(3)
//        viewModel.onSetSearch(searchSortColor)
//
//        // Test internal state indirectly through searchState if possible, or directly if exposed for test
//        // For this, we'll check its effect on searchState
//        viewModel.searchState.test {
//            val state = awaitItem() // Could be FilterState or ViewState depending on notes
//            if (state is SearchState.ViewState) {
//                 assertEquals(searchSortColor, state.searchSort)
//            } else if (state is SearchState.FilterState) {
//                // If no notes, searchSort might not be directly in FilterState's public API, but it's set internally
//                // We'll rely on other tests to show searchSort's effect when notes are present
//            }
//            cancelAndConsumeRemainingEvents()
//        }
//    }
}
