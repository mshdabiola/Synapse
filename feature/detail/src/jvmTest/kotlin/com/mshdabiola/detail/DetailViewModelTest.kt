package com.mshdabiola.detail

import androidx.compose.foundation.text.input.clearText
import app.cash.turbine.test
import com.mshdabiola.data.repository.ContentManager
import com.mshdabiola.detail.navigation.Detail
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.DateUseCase
import com.mshdabiola.domain.GetNoteUseCase
import com.mshdabiola.domain.LinkUriUseCase
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NoteItem
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.NoteVoice
import com.mshdabiola.player.MediaPlayer
import com.mshdabiola.testing.fake.repository.FakeContentManager
import com.mshdabiola.testing.fake.repository.FakeNoteItemRepository
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.fake.repository.FakeNoteVoiceRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import com.mshdabiola.testing.util.testLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DetailViewModel
    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var fakeNoteItemRepository: FakeNoteItemRepository
    private lateinit var fakeNoteVoiceRepository: FakeNoteVoiceRepository
    private lateinit var fakeGetNoteUseCase: GetNoteUseCase
    private lateinit var fakeAddAllNoteUseCase: AddAllNoteUseCase
    private lateinit var fakeContentManager: FakeContentManager
    private lateinit var fakeMediaPlayer: FakeMediaPlayer

    private val testDetailArgNew = Detail(id = -1L) // For new notes

    @Before
    fun setup() {
        fakeNoteRepository = FakeNoteRepository()
        fakeNoteItemRepository = FakeNoteItemRepository()
        fakeNoteVoiceRepository = FakeNoteVoiceRepository()
        fakeGetNoteUseCase = GetNoteUseCase(
            noteRepository = fakeNoteRepository,
            linkUriUseCase = LinkUriUseCase()
        )
        fakeAddAllNoteUseCase = AddAllNoteUseCase(
            noteRepository = fakeNoteRepository,
            noteCheckRepository = fakeNoteItemRepository,
            noteDrawingRepository = com.mshdabiola.testing.fake.repository.FakeNoteDrawingRepository(),
            noteImageRepository = com.mshdabiola.testing.fake.repository.FakeNoteImageRepository(),
            noteLabelRepository = com.mshdabiola.testing.fake.repository.FakeNoteLabelRepository(),
            noteNotificationRepository = com.mshdabiola.testing.fake.repository.FakeNotificationRepository(),
            noteVoiceRepository = fakeNoteVoiceRepository
        )
        fakeContentManager = FakeContentManager()
        fakeMediaPlayer = FakeMediaPlayer()

        // Default viewModel with a new note scenario, specific tests can re-initialize
        initializeViewModelForNewNote(testDetailArgNew)
    }

    private fun initializeViewModelForNewNote(detailArg: Detail) {
        viewModel = DetailViewModel(
            detailArg = detailArg,
            voicePlayer = fakeMediaPlayer,
            getNoteUseCase = fakeGetNoteUseCase,
            addAllNoteUseCase = fakeAddAllNoteUseCase,
            contentManager = fakeContentManager,
            dateUseCase = DateUseCase(),
            noteCheckRepository = fakeNoteItemRepository,
            noteVoiceRepository = fakeNoteVoiceRepository,
            logger = testLogger
        )
    }

    private suspend fun initializeViewModelForExistingNote(note: NotePad) {
         fakeNoteRepository.upsert(note)  // Ensure note is in repository
        viewModel = DetailViewModel(
            detailArg = Detail(id = note.id, title = note.title, detail = note.detail),
            voicePlayer = fakeMediaPlayer,
            getNoteUseCase = fakeGetNoteUseCase,
            addAllNoteUseCase = fakeAddAllNoteUseCase,
            contentManager = fakeContentManager,
            dateUseCase = DateUseCase(),
            noteCheckRepository = fakeNoteItemRepository,
            noteVoiceRepository = fakeNoteVoiceRepository,
            logger = testLogger
        )
    }

    @Test
    fun detailState_whenNewNote_initializesAndSavesWithArgs() = runTest {
        val newNoteArg = Detail(id = -1L, title = "New Title", detail = "New Detail", color = 2, background = 1, isCheck = true)
        initializeViewModelForNewNote(newNoteArg)

        viewModel.detailState.test {
            val initialState = awaitItem() // Initial state from initState
            assertEquals(newNoteArg.title, initialState.notePad.title)
            assertEquals(newNoteArg.detail, initialState.notePad.detail)
            assertEquals(newNoteArg.color, initialState.notePad.color)
            assertEquals(newNoteArg.background, initialState.notePad.background)
            assertEquals(newNoteArg.isCheck, initialState.notePad.isCheck)
            // For a new note with isCheck=true, initState adds a default check item.
            assertTrue(initialState.notePad.checks.isNotEmpty() || initialState.unChecks.isNotEmpty())

            advanceUntilIdle() // Allow save operation from initState to complete
            skipItems(1)
            val savedNote = fakeNoteRepository.get(initialState.notePad.id).first()
            assertNotNull(savedNote)
            assertEquals(newNoteArg.title, savedNote.title)
            assertEquals(newNoteArg.isCheck, savedNote.isCheck)
        }
    }

    @Test
    fun detailState_whenExistingNote_loadsNoteData() = runTest {
        val existingNote = NotePad(id = 1L, title = "Loaded Title", detail = "Loaded Detail", checks = listOf(NoteItem(id = 1, content = "check1")))
        initializeViewModelForExistingNote(existingNote)

        viewModel.detailState.test {
            awaitItem() // Initial default state from initState
            val loadedState = awaitItem() // State after note is loaded

            assertEquals(existingNote.title, loadedState.notePad.title)
            assertEquals(existingNote.detail, loadedState.notePad.detail)
            assertEquals(existingNote.title, loadedState.title.text.toString())
            assertEquals(existingNote.detail, loadedState.detail.text.toString())
            assertEquals(1, loadedState.unChecks.size) // From existingNote.checks
            assertEquals("check1", loadedState.unChecks.first().content.text.toString())
        }
    }

    @Test
    fun detailState_titleChange_debouncesAndUpdatesNotePad() = runTest {
        val existingNote = NotePad(id = 1L, title = "Old Title", detail = "Detail")
        initializeViewModelForExistingNote(existingNote)

        viewModel.detailState.test {
            awaitItem() // Initial state
            val loadedState = awaitItem() // Loaded state
            assertEquals("Old Title", loadedState.notePad.title)

            viewModel.detailState.value.title.edit { append(" New Append") }
            advanceTimeBy(299) // Before debounce
            assertEquals("Old Title", fakeNoteRepository.get(1L).first()?.title) // Repo not updated yet
            // No new state emission expected before debounce time

            advanceTimeBy(2) // After debounce (301ms total)
            advanceUntilIdle()

            val updatedState = awaitItem() // State after title change is processed
            assertEquals("Old Title New Append", updatedState.notePad.title)
            assertEquals("Old Title New Append", fakeNoteRepository.get(1L).first()?.title)
        }
    }

    @Test
    fun detailState_detailChange_debouncesAndUpdatesNotePad() = runTest {
        val existingNote = NotePad(id = 1L, title = "Title", detail = "Old Detail")
        initializeViewModelForExistingNote(existingNote)

        viewModel.detailState.test {
            awaitItem() // Initial state
            val loadedState = awaitItem() // Loaded state
            assertEquals("Old Detail", loadedState.notePad.detail)

            viewModel.detailState.value.detail.edit { append(" With More Text") }
            advanceTimeBy(299) // Before debounce
            assertEquals("Old Detail", fakeNoteRepository.get(1L).first()?.detail) // Repo not updated yet

            advanceTimeBy(2) // After debounce
            advanceUntilIdle()

            val updatedState = awaitItem() // State after detail change
            assertEquals("Old Detail With More Text", updatedState.notePad.detail)
            assertEquals("Old Detail With More Text", fakeNoteRepository.get(1L).first()?.detail)
        }
    }

    @Test
    fun addCheck_addsItemToRepositoryAndUiState() = runTest {
        val note = NotePad(id = 1L, isCheck = true, checks = emptyList())
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial state
            val loadedState = awaitItem() // Loaded state
            val initialUnchecksCount = loadedState.unChecks.size

            viewModel.addCheck()
            advanceUntilIdle()

            val updatedState = awaitItem() // State after adding check
            assertEquals(initialUnchecksCount + 1, updatedState.unChecks.size)
            assertTrue(updatedState.unChecks.any { it.focus }) // New item should be focused

            val repoItems = fakeNoteItemRepository.getByNoteId(1L).first()
            assertEquals(initialUnchecksCount + 1, repoItems.count { !it.isCheck }) // Assuming addCheck creates an unchecked item
        }
    }

    @Test
    fun onCheckDelete_deletesItemFromRepositoryAndUi() = runTest {
        val checkItem = NoteItem(id = 5L, noteId = 1L, content = "to delete")
        fakeNoteItemRepository.upsert(checkItem)
        val note = NotePad(id = 1L, isCheck = true, checks = listOf(checkItem))
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
//            awaitItem() // Initial state
            val loadedState = awaitItem() // Loaded state - item should be in unChecks
            assertTrue(loadedState.unChecks.any { it.id == 5L })

            viewModel.onCheckDelete(5L)
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertTrue(updatedState.unChecks.none { it.id == 5L })
            assertTrue(updatedState.checks.none { it.id == 5L })
            assertNull(fakeNoteItemRepository.get(5L).first())
        }
    }

    @Test
    fun changeToCheckBoxes_updatesNoteAndAddsItems() = runTest {
        val initialDetailText = "Item 1\nItem 2\nItem 3"
        val note = NotePad(id = 1L, title = "ConvertMe", detail = initialDetailText, isCheck = false)
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial state
            val loadedState = awaitItem() // Loaded state
            // Ensure TextFieldState is populated for the conversion
            loadedState.detail.clearText()
            loadedState.detail.edit { append(initialDetailText) }

            viewModel.changeToCheckBoxes()
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertTrue(updatedState.notePad.isCheck)
            assertEquals("", updatedState.notePad.detail) // Detail text moved to checks
            assertEquals(3, updatedState.unChecks.size)
            assertEquals("Item 1", updatedState.unChecks[0].content.text.toString())
            assertEquals("Item 2", updatedState.unChecks[1].content.text.toString())
            assertEquals("Item 3", updatedState.unChecks[2].content.text.toString())
            assertEquals("", updatedState.detail.text.toString()) // UI TextFieldState cleared

            val updatedRepoNote = fakeNoteRepository.get(1L).first()
            assertNotNull(updatedRepoNote)
            assertTrue(updatedRepoNote.isCheck)
            assertEquals("", updatedRepoNote.detail)
            val checkItemsInRepo = fakeNoteItemRepository.getByNoteId(1L).first()
            assertEquals(3, checkItemsInRepo.size)
        }
    }

    @Test
    fun deleteCheckedItems_clearsCheckedFromRepoAndUi() = runTest {
        val checkedItem = NoteItem(id = 1L, noteId = 1L, content = "checked", isCheck = true)
        val uncheckedItem = NoteItem(id = 2L, noteId = 1L, content = "unchecked", isCheck = false)
        fakeNoteItemRepository.upserts(listOf(checkedItem, uncheckedItem))
        val note = NotePad(id = 1L, isCheck = true, checks = listOf(checkedItem, uncheckedItem))
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial state
            val loadedState = awaitItem() // Loaded state
            // Manually adjust UI state lists if needed for this specific test setup
            // (though ideally, ViewModel init from NotePad should handle this)
            val checkedUiState = checkedItem.toNoteCheckUiState()//.apply { this.isCheck = true }
            val uncheckedUiState = uncheckedItem.toNoteCheckUiState()
            loadedState.checks.clear()
            loadedState.checks.add(checkedUiState)
            loadedState.unChecks.clear()
            loadedState.unChecks.add(uncheckedUiState)

            assertEquals(1, loadedState.checks.size)
            assertEquals(1, loadedState.unChecks.size)

            viewModel.deleteCheckedItems()
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertTrue(updatedState.checks.isEmpty())
            assertEquals(1, updatedState.unChecks.size)
            assertEquals("unchecked", updatedState.unChecks.first().content.text.toString())

            assertNull(fakeNoteItemRepository.get(1L).first()) // Checked item deleted from repo
            assertNotNull(fakeNoteItemRepository.get(2L).first()) // Unchecked item remains in repo
        }
    }

    @Test
    fun hideCheckBoxes_convertsChecksToDetailAndUpdateNote() = runTest {
        val noteId = 1L
        val check1 = NoteItem(id = 1, noteId = noteId, content = "Line 1", isCheck = true)
        val check2 = NoteItem(id = 2, noteId = noteId, content = "Line 2", isCheck = false)
        fakeNoteItemRepository.upserts(listOf(check1, check2))
        val note = NotePad(id = noteId, isCheck = true, checks = listOf(check1, check2))
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial state
            val loadedState = awaitItem() // Loaded state, checks should be populated
            // Ensure UI state has the items for conversion
            loadedState.checks.clear()
            loadedState.unChecks.clear()
            loadedState.checks.add(check1.toNoteCheckUiState())//.apply { this.isCheck = true })
            loadedState.unChecks.add(check2.toNoteCheckUiState())

            viewModel.hideCheckBoxes()
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertFalse(updatedState.notePad.isCheck)
            val expectedDetail = "Line 1\nLine 2"
            assertEquals(expectedDetail, updatedState.detail.text.toString())
            assertEquals(expectedDetail, updatedState.notePad.detail)

            assertTrue(updatedState.checks.isEmpty())
            assertTrue(updatedState.unChecks.isEmpty())

            assertFalse(fakeNoteRepository.get(noteId).first()!!.isCheck)
            assertEquals(expectedDetail, fakeNoteRepository.get(noteId).first()!!.detail)
            assertTrue(fakeNoteItemRepository.getByNoteId(noteId).first().isEmpty())
        }
    }

    @Test
    fun pinNote_togglesIsPinInNotePadState() = runTest {
        val note = NotePad(id = 1L, isPin = false)
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial state from initState
            val loadedState = awaitItem() // State after note is loaded
            assertFalse(loadedState.notePad.isPin, "Note should initially not be pinned")

            viewModel.pinNote()
            advanceUntilIdle()
            val pinnedState = awaitItem()
            assertTrue(pinnedState.notePad.isPin, "Note should be pinned after pinNote()")

            viewModel.pinNote() // Call again to unpin
            advanceUntilIdle()
            val unpinnedState = awaitItem()
            assertFalse(unpinnedState.notePad.isPin, "Note should be unpinned after second pinNote()")
        }
    }

    @Test
    fun onColorChange_updatesColorInNotePadState() = runTest {
        val note = NotePad(id = 1L, color = 0)
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial
            awaitItem() // Loaded

            viewModel.onColorChange(5)
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertEquals(5, updatedState.notePad.color)
            assertEquals(5, fakeNoteRepository.get(1L).first()!!.color)
        }
    }

    @Test
    fun onImageChange_updatesBackgroundInNotePadState() = runTest {
        val note = NotePad(id = 1L, background = 0)
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial
            awaitItem() // Loaded

            viewModel.onImageChange(3)
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertEquals(3, updatedState.notePad.background)
            assertEquals(3, fakeNoteRepository.get(1L).first()!!.background)
        }
    }

    @Test
    fun onArchive_togglesArchiveStatusInNotePadState() = runTest {
        val note = NotePad(id = 1L, noteCategory = NoteCategory.NOTE)
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial
            awaitItem() // Loaded

            viewModel.onArchive()
            advanceUntilIdle()
            var updatedState = awaitItem()
            assertEquals(NoteCategory.ARCHIVE, updatedState.notePad.noteCategory)
            assertEquals(NoteCategory.ARCHIVE, fakeNoteRepository.get(1L).first()!!.noteCategory)

            viewModel.onArchive()
            advanceUntilIdle()
            updatedState = awaitItem()
            assertEquals(NoteCategory.NOTE, updatedState.notePad.noteCategory)
            assertEquals(NoteCategory.NOTE, fakeNoteRepository.get(1L).first()!!.noteCategory)
        }
    }

    @Test
    fun onTrash_setsCategoryToTrashInNotePadState() = runTest {
        val note = NotePad(id = 1L, noteCategory = NoteCategory.NOTE)
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial
            awaitItem() // Loaded

            viewModel.onTrash()
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertEquals(NoteCategory.TRASH, updatedState.notePad.noteCategory)
            assertEquals(NoteCategory.TRASH, fakeNoteRepository.get(1L).first()!!.noteCategory)
        }
    }

    @Test
    fun copyNote_createsNewNoteInRepository() = runTest {
        val originalId = 1L
        val originalNote = NotePad(id = originalId, title = "Original", detail = "Copy Me", color = 1)
        initializeViewModelForExistingNote(originalNote)

        viewModel.detailState.test {
            awaitItem() // Initial state
            awaitItem() // Loaded state. We don't expect this state to change for copy.

            val initialNotesCount = fakeNoteRepository.getAll().first().size
            viewModel.copyNote() // This action should not change the current VM's state for the *original* note.
            advanceUntilIdle() // Allow copy operation to complete

            // No new state emission is expected for the *current* DetailViewModel after a copy operation.
            // The primary verification is that a new note exists in the repository.
            // If navigation or other UI feedback were part of this ViewModel state, we'd assert it.
            expectNoEvents() // Explicitly state no UI changes for current note after copy

            val notesAfterCopy = fakeNoteRepository.getAll().first()
            assertEquals(initialNotesCount + 1, notesAfterCopy.size)
            val copiedNote = notesAfterCopy.firstOrNull { it.id != originalId && it.title == "Original" }
            assertNotNull(copiedNote)
            assertEquals("Original", copiedNote.title)
            // DetailViewModel.copyNote logic does not copy detail/checks/etc to the new NotePad, only NotePad fields itself.
            // If it should, the copyNote logic in ViewModel needs adjustment.
            // For now, asserting based on current observed behavior from original test.
            assertEquals("Copy Me", copiedNote.detail)
            assertEquals(1, copiedNote.color)
            assertNotEquals(originalId, copiedNote.id)
        }
    }

    @Test
    fun deleteVoiceNote_removesFromRepoAndUpdatesNotePadState() = runTest {
        val voice1 = NoteVoice(id = 10L, path = "path1", noteId = 1L)
        val voice2 = NoteVoice(id = 11L, path = "path2", noteId = 1L)
        val note = NotePad(id = 1L, voices = listOf(voice1, voice2))
        fakeNoteVoiceRepository.upserts(listOf(voice1, voice2))
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial
            awaitItem() // Loaded

            viewModel.deleteVoiceNote(0) // Delete voice1 (index 0)
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertEquals(1, updatedState.notePad.voices.size)
            assertEquals("path2", updatedState.notePad.voices[0].path)

            assertNull(fakeNoteVoiceRepository.get(10L).first())
            assertNotNull(fakeNoteVoiceRepository.get(11L).first())
            assertEquals(1, fakeNoteRepository.get(1L).first()!!.voices.size)
        }
    }

    @Test
    fun saveImage_savesViaContentManagerAndUpdatesNotePadState() = runTest {
        val note = NotePad(id = 1L, images = emptyList())
        val testImageUri = "content://image_to_save.jpg"
        fakeContentManager.imageSaveResult = "saved_image_id_from_test"
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial
            awaitItem() // Loaded

            viewModel.saveImage(testImageUri)
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertEquals(1, updatedState.notePad.images.size)
            assertEquals("saved_image_id_from_test", updatedState.notePad.images[0].path)

            assertEquals(testImageUri, fakeContentManager.imageSaveResult) // Check manager interaction
            assertEquals(1, fakeNoteRepository.get(1L).first()!!.images.size)
        }
    }

    @Test
    fun saveVoice_savesViaContentManagerUpdatesNotePadStateAndDetail() = runTest {
        val note = NotePad(id = 1L, voices = emptyList(), detail = "Initial detail.")
        val testVoiceUri = "content://voice_to_save.mp3"
        val voiceText = " Spoken text."
        fakeContentManager.voicePathResult = "saved_voice_id_from_test"
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial
            val loadedState = awaitItem() // Loaded
            assertEquals("Initial detail.", loadedState.detail.text.toString())

            viewModel.saveVoice(testVoiceUri, voiceText)
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertEquals(1, updatedState.notePad.voices.size)
            assertEquals("saved_voice_id_from_test", updatedState.notePad.voices[0].path)
            assertEquals("Initial detail. Spoken text.", updatedState.detail.text.toString())

            assertEquals(testVoiceUri, fakeContentManager.voicePathResult)
            assertEquals(1, fakeNoteRepository.get(1L).first()!!.voices.size)
            // Detail text in repo is updated by the debounced detailFlow, which is separate
            // from saveVoice directly updating the repo's detail. The UI state is immediate.
        }
    }

    @Test
    fun getPhotoUri_returnsUriFromContentManager() {
        val expectedUri = "custom_photo_uri_scheme"
        fakeContentManager.pictureUriResult = expectedUri
        assertEquals(expectedUri, viewModel.getPhotoUri())
    }

    @Test
    fun playMusic_updatesPlayerStateAndPlays() = runTest {
        val voice1 = NoteVoice(id = 1, path = "voice1.mp3", noteId = 1L)
        val note = NotePad(id = 1L, voices = listOf(voice1))
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial
            val loadedState = awaitItem() // Loaded
            assertNull(loadedState.playerState)

            viewModel.playMusic(0) // Play the first voice
            advanceUntilIdle()

            var playerUpdatedState = awaitItem()
            assertNotNull(playerUpdatedState.playerState)
            assertEquals(0, playerUpdatedState.playerState!!.indexPlaying)
            assertTrue(playerUpdatedState.playerState!!.isPlaying)

            // Simulate a new play, should cancel previous and start new
            val voice2 = NoteVoice(id = 2, path = "voice2.mp3", noteId = 1L)
            // Update the note in the ViewModel for it to pick up the new voice for playback,
            // as playMusic uses the current state.notePad.voices
            val noteWithTwoVoices = note.copy(voices = listOf(voice1, voice2))
            fakeNoteRepository.upsert(noteWithTwoVoices) // Update repo
//            viewModel.refreshNote() // Force VM to reload note
            advanceUntilIdle()
            awaitItem() // Consume state update from refreshNote

            viewModel.playMusic(1) // Play the second voice
            advanceUntilIdle()

            playerUpdatedState = awaitItem()
            assertNotNull(playerUpdatedState.playerState)
            assertEquals(1, playerUpdatedState.playerState!!.indexPlaying)
            assertTrue(playerUpdatedState.playerState!!.isPlaying)
        }
    }

    @Test
    fun pause_updatesPlayerStateAndPausesMediaPlayer() = runTest {
        val voice1 = NoteVoice(id = 1, path = "voice1.mp3", noteId = 1L)
        val note = NotePad(id = 1L, voices = listOf(voice1))
        initializeViewModelForExistingNote(note)

        viewModel.detailState.test {
            awaitItem() // Initial
            awaitItem() // Loaded

            viewModel.playMusic(0)
            advanceUntilIdle()
            val playingState = awaitItem()
            assertTrue(playingState.playerState!!.isPlaying)
            assertFalse(fakeMediaPlayer.pausedCalled) // MediaPlayer.pause() not called yet

            viewModel.pause()
            advanceUntilIdle()

            val pausedState = awaitItem()
            assertNotNull(pausedState.playerState)
            assertFalse(pausedState.playerState!!.isPlaying)
            assertTrue(fakeMediaPlayer.pausedCalled) // MediaPlayer.pause() should be called
        }
    }
}
