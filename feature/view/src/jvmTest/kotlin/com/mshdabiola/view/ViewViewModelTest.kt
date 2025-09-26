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
            skipItems(1) // Skip initial emission if it uses placeholders or is empty before repo load
            val updatedState = awaitItem() // Should get the emission from the repository
            assertEquals(viewArg.index, updatedState.initIndex)
            assertEquals(imagesFromRepo.size, updatedState.images.size)
            assertEquals(imagesFromRepo.sortedBy { it.id }, updatedState.images.sortedBy { it.id })
        }
    }

    @Test
    fun `onImage updates note detail with text from contentManager`() = runTest {
        val note = NotePad(id = testNoteId, title = "Test Note", detail = "Initial detail.")
        noteRepository.upsert(note)

        val imagePath = "/test/image.jpg"
        val extractedText = "Extracted text from image."
        contentManager.imageToTextResult = extractedText

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

        // Note with testNoteId does NOT exist in the repository
        val viewArg = View(id = testNoteId, index = 0, total = 1, currentPath = imagePath)
        initializeViewModel(viewArg)

        var exceptionThrown = false
        try {
            viewModel.onImage(imagePath)
        } catch (e: Exception) {
            exceptionThrown = true
        }
        assertFalse("ViewModel should handle non-existent note gracefully", exceptionThrown)
        assertTrue(
            "No note should be created if it did not exist",
            noteRepository.getAll().first().isEmpty(),
        )
    }

    @Test
    fun `deleteImage_removesOnlySpecifiedImage_fromRepositoryAndUi`() = runTest {
        val imageToDelete = NoteImage(id = 20L, noteId = testNoteId, path = "/to/delete.jpg")
        val imageToKeep = NoteImage(id = 21L, noteId = testNoteId, path = "/to/keep.jpg")
        noteImageRepository.upserts(listOf(imageToDelete, imageToKeep))
        assertEquals(2, noteImageRepository.getAll().first().size)

        // Use total = 0 and empty currentPath to ensure ViewModel loads from repository
        val viewArg = View(id = testNoteId, index = 0, total = 0, currentPath = "")
        initializeViewModel(viewArg)

        viewModel.viewUiState.test {
            skipItems(1)
            val initialState = awaitItem() // Initial emission with both images
            assertEquals(2, initialState.images.size)
            assertTrue(initialState.images.any { it.id == imageToDelete.id })
            assertTrue(initialState.images.any { it.id == imageToKeep.id })

            viewModel.deleteImage(imageToDelete.id, 2)

            val updatedState = awaitItem() // State after deletion

            // Assert UI state update
            assertEquals(
                "UI should have one image left",
                1,
                updatedState.images.size,
            )
            assertTrue(
                "UI should not contain deleted image",
                updatedState.images.none { it.id == imageToDelete.id },
            )
            assertTrue(
                "UI should still contain the image to keep",
                updatedState.images.any { it.id == imageToKeep.id },
            )

            // Assert repository state
            val repoImagesAfterDelete = noteImageRepository.getAll().first()
            assertEquals(
                "Repository should have one image left",
                1,
                repoImagesAfterDelete.size,
            )
            assertTrue(
                "Repository should not contain deleted image",
                repoImagesAfterDelete.none { it.id == imageToDelete.id },
            )
            assertTrue(
                "Repository should still contain the image to keep",
                repoImagesAfterDelete.any { it.id == imageToKeep.id },
            )
        }
    }
}
