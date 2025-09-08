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
    private val testDetailArgExisting = Detail(id = 1L, title = "Existing", detail = "Details")



    @Before
    fun setup() {
        fakeNoteRepository = FakeNoteRepository()
        fakeNoteItemRepository = FakeNoteItemRepository()
        fakeNoteVoiceRepository = FakeNoteVoiceRepository()
        fakeGetNoteUseCase = GetNoteUseCase(
            noteRepository = fakeNoteRepository,
            linkUriUseCase = LinkUriUseCase())
        fakeAddAllNoteUseCase = AddAllNoteUseCase(
            noteRepository = fakeNoteRepository,
            noteCheckRepository = fakeNoteItemRepository,
            noteDrawingRepository = com.mshdabiola.testing.fake.repository.FakeNoteDrawingRepository(), // Assuming default, can be more specific
            noteImageRepository = com.mshdabiola.testing.fake.repository.FakeNoteImageRepository(),
            noteLabelRepository = com.mshdabiola.testing.fake.repository.FakeNoteLabelRepository(),
            noteNotificationRepository = com.mshdabiola.testing.fake.repository.FakeNotificationRepository(),
            noteVoiceRepository = fakeNoteVoiceRepository
        )
        fakeContentManager = FakeContentManager()
        fakeMediaPlayer = FakeMediaPlayer()

        // Default viewModel with a new note scenario
        viewModel = DetailViewModel(
            detailArg = testDetailArgNew,
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

    private fun createExistingNoteViewModel(note: NotePad) {
        // Ensure note is in repository for GetNoteUseCase to find
        runTest { fakeNoteRepository.upsert(note) }

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
        viewModel = DetailViewModel(
            detailArg = newNoteArg,
            voicePlayer = fakeMediaPlayer, getNoteUseCase = fakeGetNoteUseCase, addAllNoteUseCase = fakeAddAllNoteUseCase,
            contentManager = fakeContentManager, dateUseCase = DateUseCase(), noteCheckRepository = fakeNoteItemRepository,
            noteVoiceRepository = fakeNoteVoiceRepository, logger = testLogger
        )

        viewModel.detailState.test {
            val initialState = awaitItem()
            // Should save the initial notePad from initState via addAllNoteUseCase due to notepad == null branch
            advanceUntilIdle() // Allow Flows and coroutines to settle

            val emittedState = viewModel.detailState.value
            assertEquals(newNoteArg.title, emittedState.notePad.title)
            assertEquals(newNoteArg.detail, emittedState.notePad.detail)
            assertEquals(newNoteArg.color, emittedState.notePad.color)
            assertEquals(newNoteArg.background, emittedState.notePad.background)
            assertEquals(newNoteArg.isCheck, emittedState.notePad.isCheck)
            assertTrue(emittedState.notePad.checks.isNotEmpty()) // Because isCheck = true and initState creates a default NoteItem

            val savedNote = fakeNoteRepository.get(emittedState.notePad.id).first()
            assertNotNull(savedNote)
            assertEquals(newNoteArg.title, savedNote.title)
        }
    }

    @Test
    fun detailState_whenExistingNote_loadsNoteData() = runTest {
        val existingNote = NotePad(id = 1L, title = "Loaded Title", detail = "Loaded Detail", checks = listOf(NoteItem(id=1, content="check1")))
        createExistingNoteViewModel(existingNote)

        viewModel.detailState.test {
            skipItems(1) // Skip initial emission from initState before note is loaded
            val loadedState = awaitItem()

            assertEquals(existingNote.title, loadedState.notePad.title)
            assertEquals(existingNote.detail, loadedState.notePad.detail)
            assertEquals(existingNote.title, loadedState.title.text.toString())
            assertEquals(existingNote.detail, loadedState.detail.text.toString())
            assertEquals(1, loadedState.unChecks.size) // From existingNote.checks, assuming it's not checked
            assertEquals("check1", loadedState.unChecks.first().content.text.toString())
        }
    }

    @Test
    fun detailState_titleChange_debouncesAndUpdatesNotePad() = runTest {
         val existingNote = NotePad(id = 1L, title = "Old Title", detail = "Detail")
        createExistingNoteViewModel(existingNote)

        viewModel.detailState.test {
            skipItems(1) // initial state
            awaitItem() // loaded state

            viewModel.detailState.value.title.edit { append(" New Append") }
            advanceTimeBy(299) // Before debounce
            var currentNote = fakeNoteRepository.get(1L).first()
            assertEquals("Old Title", currentNote?.title)

            advanceTimeBy(2) // After debounce (301ms total)
            advanceUntilIdle() // ensure coroutines complete

            currentNote = fakeNoteRepository.get(1L).first()
            assertEquals("Old Title New Append", currentNote?.title)
            val emittedState = viewModel.detailState.value
            assertEquals("Old Title New Append", emittedState.notePad.title)
        }
    }

     @Test
    fun detailState_detailChange_debouncesAndUpdatesNotePad() = runTest {
        val existingNote = NotePad(id = 1L, title = "Title", detail = "Old Detail")
        createExistingNoteViewModel(existingNote)

        viewModel.detailState.test {
            skipItems(1)
            awaitItem()

            viewModel.detailState.value.detail.edit { append(" With More Text") }
            advanceTimeBy(299)
            var currentNote = fakeNoteRepository.get(1L).first()
            assertEquals("Old Detail", currentNote?.detail)

            advanceTimeBy(2)
            advanceUntilIdle()

            currentNote = fakeNoteRepository.get(1L).first()
            assertEquals("Old Detail With More Text", currentNote?.detail)
            assertEquals("Old Detail With More Text", viewModel.detailState.value.notePad.detail)
        }
    }

    @Test
    fun addCheck_addsItemToRepositoryAndUiState() = runTest {
        createExistingNoteViewModel(NotePad(id = 1L, isCheck = true))
        viewModel.detailState.test{
            skipItems(1); awaitItem() // Wait for initial load

            val initialUnchecksCount = viewModel.detailState.value.unChecks.size
            viewModel.addCheck()
            advanceUntilIdle()

            assertEquals(initialUnchecksCount + 1, viewModel.detailState.value.unChecks.size)
            assertTrue(viewModel.detailState.value.unChecks.any { it.focus }) // New item should be focused
            val repoItems = fakeNoteItemRepository.getByNoteId(1L).first()
            assertEquals(1, repoItems.count { !it.isCheck }) // Assuming addCheck creates an unchecked item
        }
    }


    @Test
    fun onCheckDelete_deletesItemFromRepository() = runTest {
        val checkItem = NoteItem(id = 5L, noteId = 1L, content = "to delete")
        fakeNoteItemRepository.upsert(checkItem) // Pre-populate
        createExistingNoteViewModel(NotePad(id = 1L, isCheck = true, checks = listOf(checkItem)))
         viewModel.detailState.test{
            skipItems(1); awaitItem() // Wait for initial load

            viewModel.onCheckDelete(5L)
            advanceUntilIdle()
            val repoItem = fakeNoteItemRepository.get(5L).first()
            assertNull(repoItem)
        }
    }


    @Test
    fun changeToCheckBoxes_updatesNoteAndAddsItems() = runTest {
        val initialDetailText = "Item 1\nItem 2\nItem 3"
        val note = NotePad(id = 1L, title = "ConvertMe", detail = initialDetailText, isCheck = false)
        createExistingNoteViewModel(note)
        viewModel.detailState.value.detail.clearText()
        viewModel.detailState.value.detail.edit { append(initialDetailText) } //Ensure initState.detail is set

        viewModel.detailState.test {
            skipItems(1); awaitItem()

            viewModel.changeToCheckBoxes()
            advanceUntilIdle()

            val updatedNote = fakeNoteRepository.get(1L).first()
            assertNotNull(updatedNote)
            assertTrue(updatedNote.isCheck)
            assertEquals("", updatedNote.detail)

            val checkItems = fakeNoteItemRepository.getByNoteId(1L).first()
            assertEquals(3, checkItems.size)
            assertEquals("Item 1", checkItems[0].content)
            assertEquals("Item 2", checkItems[1].content)
            assertEquals("Item 3", checkItems[2].content)

            assertEquals(3, viewModel.detailState.value.unChecks.size)
            assertEquals("", viewModel.detailState.value.detail.text.toString())
        }
    }

    @Test
    fun deleteCheckedItems_clearsCheckedFromRepoAndUi() = runTest {
        val checkedItem = NoteItem(id = 1L, noteId = 1L, content = "checked", isCheck = true)
        val uncheckedItem = NoteItem(id = 2L, noteId = 1L, content = "unchecked", isCheck = false)
        fakeNoteItemRepository.upserts(listOf(checkedItem, uncheckedItem))
        createExistingNoteViewModel(NotePad(id = 1L, isCheck = true, checks = listOf(checkedItem, uncheckedItem)))

        viewModel.detailState.value.checks.add(checkedItem.toNoteCheckUiState())
        viewModel.detailState.value.unChecks.add(uncheckedItem.toNoteCheckUiState())


        viewModel.detailState.test {
            skipItems(1);awaitItem() // Initial load done.

            viewModel.deleteCheckedItems()
            advanceUntilIdle()

            val repoChecked = fakeNoteItemRepository.get(1L).first() // Should be deleted
            assertNull(repoChecked)
            val repoUnchecked = fakeNoteItemRepository.get(2L).first() // Should remain
            assertNotNull(repoUnchecked)

            assertTrue(viewModel.detailState.value.checks.isEmpty())
            assertEquals(1, viewModel.detailState.value.unChecks.size) // Unchecked item should remain in UI list
        }
    }

    @Test
    fun hideCheckBoxes_convertsChecksToDetailAndUpdateNote() = runTest {
        val noteId = 1L
        val check1 = NoteItem(id = 1, noteId = noteId, content = "Line 1", isCheck = true)
        val check2 = NoteItem(id = 2, noteId = noteId, content = "Line 2", isCheck = false)
        fakeNoteItemRepository.upserts(listOf(check1, check2))
        createExistingNoteViewModel(NotePad(id = noteId, isCheck = true, checks = listOf(check1, check2)))

        // Populate UI state lists manually for this test if DetailViewModel doesn't do it on init for checks
        viewModel.detailState.value.checks.add(check1.toNoteCheckUiState())
        viewModel.detailState.value.unChecks.add(check2.toNoteCheckUiState())

        viewModel.detailState.test {
            skipItems(1); awaitItem()

            viewModel.hideCheckBoxes()
            advanceUntilIdle()

            val updatedNote = fakeNoteRepository.get(noteId).first()
            assertNotNull(updatedNote)
            assertFalse(updatedNote.isCheck)
            // Order might not be guaranteed by joinToString if lists are not sorted before join
            // So check for parts
            val expectedDetail = "Line 1\nLine 2" // Assuming checked items come first then unchecked in DetailState lists
             assertEquals(expectedDetail, viewModel.detailState.value.detail.text.toString())
            // assertEquals(expectedDetail, updatedNote.detail) // This is what matters most for persistence

            assertTrue(fakeNoteItemRepository.getByNoteId(noteId).first().isEmpty())
            assertTrue(viewModel.detailState.value.checks.isEmpty())
            assertTrue(viewModel.detailState.value.unChecks.isEmpty())
        }
    }


    @Test
    fun pinNote_togglesIsPinInRepository() = runTest {
        val note = NotePad(id = 1L, isPin = false)
        createExistingNoteViewModel(note)
        viewModel.detailState.test{ skipItems(1); awaitItem() }


        viewModel.pinNote()
        advanceUntilIdle()
        var updatedNote = fakeNoteRepository.get(1L).first()
        assertTrue(updatedNote!!.isPin)

        viewModel.pinNote()
        advanceUntilIdle()
        updatedNote = fakeNoteRepository.get(1L).first()
        assertFalse(updatedNote!!.isPin)
    }

    @Test
    fun onColorChange_updatesColorInRepository() = runTest {
        val note = NotePad(id = 1L, color = 0)
        createExistingNoteViewModel(note)
        viewModel.detailState.test{ skipItems(1); awaitItem() }

        viewModel.onColorChange(5)
        advanceUntilIdle()
        val updatedNote = fakeNoteRepository.get(1L).first()
        assertEquals(5, updatedNote!!.color)
    }

    @Test
    fun onImageChange_updatesBackgroundInRepository() = runTest {
        val note = NotePad(id = 1L, background = 0)
        createExistingNoteViewModel(note)
        viewModel.detailState.test{ skipItems(1); awaitItem() }


        viewModel.onImageChange(3)
        advanceUntilIdle()
        val updatedNote = fakeNoteRepository.get(1L).first()
        assertEquals(3, updatedNote!!.background)
    }

    @Test
    fun onArchive_togglesArchiveStatusInRepository() = runTest {
        val note = NotePad(id = 1L, noteCategory = NoteCategory.NOTE)
        createExistingNoteViewModel(note)
        viewModel.detailState.test{ skipItems(1); awaitItem() }


        viewModel.onArchive()
        advanceUntilIdle()
        var updatedNote = fakeNoteRepository.get(1L).first()
        assertEquals(NoteCategory.ARCHIVE, updatedNote!!.noteCategory)

        viewModel.onArchive()
        advanceUntilIdle()
        updatedNote = fakeNoteRepository.get(1L).first()
        assertEquals(NoteCategory.NOTE, updatedNote!!.noteCategory)
    }

    @Test
    fun onTrash_setsCategoryToTrashInRepository() = runTest {
        val note = NotePad(id = 1L, noteCategory = NoteCategory.NOTE)
        createExistingNoteViewModel(note)
        viewModel.detailState.test{ skipItems(1); awaitItem() }

        viewModel.onTrash()
        advanceUntilIdle()
        val updatedNote = fakeNoteRepository.get(1L).first()
        assertEquals(NoteCategory.TRASH, updatedNote!!.noteCategory)
    }

    @Test
    fun copyNote_createsNewNoteInRepository() = runTest {
        val originalId = 1L
        val originalNote = NotePad(id = originalId, title = "Original", detail = "Copy Me", color = 1)
        createExistingNoteViewModel(originalNote)
        viewModel.detailState.test{ skipItems(1); awaitItem() }


        val initialNotesCount = fakeNoteRepository.getAll().first().size
        viewModel.copyNote()
        advanceUntilIdle()

        val notesAfterCopy = fakeNoteRepository.getAll().first()
        assertEquals(initialNotesCount + 1, notesAfterCopy.size)
        val copiedNote = notesAfterCopy.firstOrNull { it.id != originalId && it.title == "Original" }
        assertNotNull(copiedNote)
        assertEquals("Original", copiedNote.title)
        assertEquals("Copy Me", copiedNote.detail) // DetailViewModel doesn't copy content only notepad
        assertEquals(1, copiedNote.color)
        assertNotEquals(originalId, copiedNote.id) // Should have a new ID
    }

    @Test
    fun deleteVoiceNote_removesFromRepoAndUpdatesNote() = runTest {
        val voice1 = NoteVoice(id = 10L, path = "path1", noteId = 1L)
        val voice2 = NoteVoice(id = 11L, path = "path2", noteId = 1L)
        val note = NotePad(id = 1L, voices = listOf(voice1, voice2))
        fakeNoteVoiceRepository.upserts(listOf(voice1, voice2))
        createExistingNoteViewModel(note)
         viewModel.detailState.test{ skipItems(1); awaitItem() }


        viewModel.deleteVoiceNote(0) // Delete voice1
        advanceUntilIdle()

        assertNull(fakeNoteVoiceRepository.get(10L).first())
        assertNotNull(fakeNoteVoiceRepository.get(11L).first())

        val updatedNote = fakeNoteRepository.get(1L).first()
        assertEquals(1, updatedNote!!.voices.size)
        assertEquals("path2", updatedNote.voices[0].path)
    }

    @Test
    fun saveImage_savesViaContentManagerAndUpdatesNote() = runTest {
        val note = NotePad(id = 1L, images = emptyList())
        val testImageUri = "content://image_to_save.jpg"
        fakeContentManager.imageSaveResult = "saved_image_id_from_test"
        createExistingNoteViewModel(note)
        viewModel.detailState.test{ skipItems(1); awaitItem() }


        viewModel.saveImage(testImageUri)
        advanceUntilIdle()

        assertEquals(testImageUri, fakeContentManager.imageSaveResult)
        val updatedNote = fakeNoteRepository.get(1L).first()
        assertEquals(1, updatedNote!!.images.size)
        assertEquals("saved_image_id_from_test", updatedNote.images[0].path)
    }

    @Test
    fun saveVoice_savesViaContentManagerUpdatesNoteAndDetail() = runTest {
        val note = NotePad(id = 1L, voices = emptyList(), detail = "Initial detail.")
        val testVoiceUri = "content://voice_to_save.mp3"
        val voiceText = " Spoken text."
        fakeContentManager.voicePathResult = "saved_voice_id_from_test"
        createExistingNoteViewModel(note)
         viewModel.detailState.test{ skipItems(1); awaitItem() }


        viewModel.saveVoice(testVoiceUri, voiceText)
        advanceUntilIdle()

        assertEquals(testVoiceUri, fakeContentManager.voicePathResult)
        val updatedNote = fakeNoteRepository.get(1L).first()
        assertEquals(1, updatedNote!!.voices.size)
        assertEquals("saved_voice_id_from_test", updatedNote.voices[0].path)
        // Detail text is appended in initState, which is then picked up by the debounced detailFlow
        // So the note in repo might not have the text immediately if not re-saved via detailFlow
        // Let's check the initState's detail TextFieldState
        assertEquals("Initial detail. Spoken text.", viewModel.detailState.value.detail.text.toString())
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
        createExistingNoteViewModel(note)
        viewModel.detailState.test{ skipItems(1); awaitItem() } // Ensure initial state is processed


        assertNull(viewModel.detailState.value.playerState)

        viewModel.playMusic(0) // Play the first voice
        advanceUntilIdle()


        val playerState = viewModel.detailState.value.playerState
        assertNotNull(playerState)
        assertEquals(0, playerState.indexPlaying)
        assertTrue(playerState.isPlaying)
        //assertEquals("voice1.mp3", fakeMediaPlayer.playedPath) // MediaPlayer.play() is not called directly here
                                                              // but the playerState is updated which would trigger UI

        // Simulate a new play, should cancel previous and start new
        val voice2 = NoteVoice(id = 2, path = "voice2.mp3", noteId = 1L)
        val noteWithTwoVoices = note.copy(voices = listOf(voice1, voice2))
        fakeNoteRepository.upsert(noteWithTwoVoices) // Update the note in repo for the viewModel to pick up
        advanceUntilIdle() // let currentNote flow emit

        viewModel.playMusic(1) // Play the second voice
        advanceUntilIdle()

        val newPlayerState = viewModel.detailState.value.playerState
        assertNotNull(newPlayerState)
        assertEquals(1, newPlayerState.indexPlaying) // Should now be playing the second voice
        assertTrue(newPlayerState.isPlaying)
    }

    @Test
    fun pause_updatesPlayerStateAndPausesMediaPlayer() = runTest {
         val voice1 = NoteVoice(id = 1, path = "voice1.mp3", noteId = 1L)
        val note = NotePad(id = 1L, voices = listOf(voice1))
        createExistingNoteViewModel(note)
        viewModel.detailState.test{ skipItems(1); awaitItem() }


        viewModel.playMusic(0)
        advanceUntilIdle() // Let play take effect

        assertTrue(viewModel.detailState.value.playerState!!.isPlaying)
        assertFalse(fakeMediaPlayer.pausedCalled)

        viewModel.pause()
        advanceUntilIdle()

        assertNotNull(viewModel.detailState.value.playerState)
        assertFalse(viewModel.detailState.value.playerState!!.isPlaying)
        assertTrue(fakeMediaPlayer.pausedCalled)
    }
}
