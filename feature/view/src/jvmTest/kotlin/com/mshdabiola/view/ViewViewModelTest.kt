package com.mshdabiola.view

import app.cash.turbine.test
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.testing.fake.repository.FakeContentManager
import com.mshdabiola.testing.fake.repository.FakeNoteImageRepository
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import com.mshdabiola.view.navigation.View
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ViewViewModel
    private lateinit var noteImageRepository: FakeNoteImageRepository
    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var contentManager: FakeContentManager

    private val testNoteId = 1L
    private val initialImagePath = "/fake/path/initial.jpg"

    @Before
    fun setUp() {
        noteImageRepository = FakeNoteImageRepository()
        noteRepository = FakeNoteRepository()
        contentManager = FakeContentManager()
    }

    private fun initializeViewModel(viewArg: View) {
        viewModel = ViewViewModel(
            view = viewArg,
            noteImageRepository = noteImageRepository,
            noteRepository = noteRepository,
            contentManager = contentManager,
        )
    }

    @Test
    fun `viewUiState initialValue uses placeholder images if repo is empty`() = runTest {
        val viewArg = View(id = testNoteId, index = 0, total = 2, currentPath = initialImagePath)
        initializeViewModel(viewArg)

        viewModel.viewUiState.test {
            val initialState = awaitItem()
            assertEquals(viewArg.index, initialState.initIndex)
            assertEquals(2, initialState.images.size)
            assertTrue(initialState.images.all { it.path == initialImagePath })
            // IDs for placeholder images are 0L, 1L ...
            assertEquals(0L, initialState.images[0].id)
            assertEquals(1L, initialState.images[1].id)
        }
    }

    @Test
    fun `viewUiState updates with images from repository`() = runTest {
        val viewArg = View(id = testNoteId, index = 1, total = 0, currentPath = "")
        val imagesFromRepo = listOf(
            NoteImage(id = 10L, noteId = testNoteId, path = "/repo/path/1.jpg"),
            NoteImage(id = 11L, noteId = testNoteId, path = "/repo/path/2.jpg"),
        )
        // Simulate images being added to the repository *after* ViewModel initialization
        // but before collecting the flow in the test, or ensure the flow emits them.
        noteImageRepository.upserts(imagesFromRepo) // Pre-populate for this test structure

        initializeViewModel(viewArg)

        viewModel.viewUiState.test {
            skipItems(1)
            val updatedState = awaitItem() // Should get the emission from the repository
            assertEquals(viewArg.index, updatedState.initIndex)
            assertEquals(imagesFromRepo.size, updatedState.images.size)
            assertEquals(imagesFromRepo, updatedState.images)
        }
    }

    @Test
    fun `onImage updates note detail with text from contentManager`() = runTest {
        val note = NotePad(id = testNoteId, title = "Test Note", detail = "Initial detail.")
        noteRepository.upsert(note)

        val imagePath = "/test/image.jpg"
        val extractedText = "Extracted text from image."
        contentManager.imageToTextResult=extractedText
//        contentManager.setTextForPath(imagePath, extractedText)

        val viewArg = View(id = testNoteId, index = 0, total = 1, currentPath = imagePath)
        initializeViewModel(viewArg)

        viewModel.onImage(imagePath)

        val updatedNote = noteRepository.get(testNoteId).first()
        assertNotNull(updatedNote)
        assertEquals("Initial detail.\n$extractedText", updatedNote!!.detail)
    }

    @Test
    fun `onImage handles contentManager error gracefully`() = runTest {
        val note = NotePad(id = testNoteId, title = "Test Note", detail = "Initial detail.")
        noteRepository.upsert(note)

        val imagePath = "/test/image_error.jpg"
        contentManager.imageToTextShouldThrowError = true
//        contentManager.setErrorForPath(imagePath, Exception("OCR Failed"))

        val viewArg = View(id = testNoteId, index = 0, total = 1, currentPath = imagePath)
        initializeViewModel(viewArg)

        viewModel.onImage(imagePath)

        val currentNote = noteRepository.get(testNoteId).first()
        assertNotNull(currentNote)
        // Detail should be appended with an empty string in case of an error as per ViewModel logic
        assertEquals("Initial detail.\n", currentNote!!.detail)
    }

    @Test
    fun `onImage handles non-existent note gracefully`() = runTest {
        val imagePath = "/test/image_no_note.jpg"
        val extractedText = "Some text"
        contentManager.imageToTextResult = extractedText
//        contentManager.setTextForPath(imagePath, extractedText)

        // Note with testNoteId does NOT exist in the repository
        val viewArg = View(id = testNoteId, index = 0, total = 1, currentPath = imagePath)
        initializeViewModel(viewArg)

        // No specific assertion on crash, just that it doesn't crash.
        // The ViewModel catches exceptions internally.
        // We can check that no new note was created if that's the expected behavior.
        var exceptionThrown = false
        try {
            viewModel.onImage(imagePath)
        } catch (e: Exception) {
            exceptionThrown = true
        }
        assertFalse("ViewModel should handle non-existent note gracefully", exceptionThrown)
        assertTrue("No note should be created if it did not exist", noteRepository.getAll().first().isEmpty())
    }


    @Test
    fun `deleteImage removes image from repository`() = runTest {
        val imageToDelete = NoteImage(id = 20L, noteId = testNoteId, path = "/to/delete.jpg")
        noteImageRepository.upsert(imageToDelete)
        assertEquals(1, noteImageRepository.getAll().first().size)

        val viewArg = View(id = testNoteId, index = 0, total = 1, currentPath = imageToDelete.path)
        initializeViewModel(viewArg)

        viewModel.viewUiState.test {
//            skipItems(1)
            awaitItem() // Initial emission

            viewModel.deleteImage(imageToDelete.id)

            // Assert UI state update
            val updatedState = awaitItem()

            // Assert repository state
            assertTrue(noteImageRepository.getAll().first().none { it.id == imageToDelete.id })


            assertTrue(updatedState.images.none { it.id == imageToDelete.id })
        }
    }
}
