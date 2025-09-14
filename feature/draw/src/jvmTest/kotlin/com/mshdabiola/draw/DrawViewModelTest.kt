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
package com.mshdabiola.draw

import androidx.compose.runtime.snapshots.Snapshot
import app.cash.turbine.test
import com.mshdabiola.draw.navigation.Draw
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.Path
import com.mshdabiola.model.note.Point
import com.mshdabiola.testing.fake.repository.FakeNoteDrawingRepository
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DrawViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var drawingRepository: FakeNoteDrawingRepository
    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var viewModel: DrawViewModel

    private val testNewDrawingArgs = Draw(noteId = null, id = null)
    private val testNewDrawingWithNoteIdArgs = Draw(noteId = 1L, id = null)
    private val testExistingDrawingArgs = Draw(noteId = 2L, id = 100L)

    @Before
    fun setUp() {
        drawingRepository = FakeNoteDrawingRepository()
        noteRepository = FakeNoteRepository()
    }

    private fun initializeViewModel(args: Draw) {
        viewModel = DrawViewModel(
            draw = args,
            drawingRepository = drawingRepository,
            noteRepository = noteRepository,
        )
    }

    @Test
    fun `init with new drawing (null noteId and null drawingId) creates NotePad and NoteDrawing`() = runTest {
        initializeViewModel(testNewDrawingArgs)

        viewModel.drawingState.test {
            advanceTimeBy(600) // Advance past debounce for initial save

            val emittedState = expectMostRecentItem()

            assertNotNull("NotePad should be created", noteRepository.getAll().firstOrNull())
            val createdNoteId = noteRepository.getAll().first().first().id
            assertNotNull("NoteDrawing should be created", drawingRepository.getAll().firstOrNull())
            assertEquals(
                "NoteDrawing should have the new noteId",
                createdNoteId,
                drawingRepository.getAll().first().first().noteId,
            )
            assertNotNull("DrawingId in viewModel should be updated", emittedState.drawingId)
            assertEquals(
                "Drawing paths should be empty initially in controller",
                0,
                viewModel.controller.drawingPaths.size,
            )
            assertTrue("Emitted drawing paths should be empty initially", emittedState.drawings.isEmpty())
            // No need to cancel, Turbine handles it
        }
    }

    @Test
    fun `init with new drawing (existing noteId and null drawingId) creates NoteDrawing`() = runTest {
        noteRepository.upsert(NotePad(id = testNewDrawingWithNoteIdArgs.noteId!!))
        initializeViewModel(testNewDrawingWithNoteIdArgs)

        viewModel.drawingState.test {
            advanceTimeBy(600) // Advance past debounce for initial save
            val emittedState = expectMostRecentItem()

            assertEquals("Should not create a new NotePad", 1, noteRepository.getAll().first().size)
            assertNotNull("NoteDrawing should be created", drawingRepository.getAll().firstOrNull())
            assertEquals(
                "NoteDrawing should use existing noteId",
                testNewDrawingWithNoteIdArgs.noteId,
                drawingRepository.getAll().first().first().noteId,
            )
            assertNotNull("DrawingId in viewModel should be updated", emittedState.drawingId)
            assertEquals(
                "Drawing paths should be empty initially in controller",
                0,
                viewModel.controller.drawingPaths.size,
            )
            assertTrue("Emitted drawing paths should be empty initially", emittedState.drawings.isEmpty())
        }
    }

    @Test
    fun `init with existing drawing loads paths into controller and state`() = runTest {
        val existingPaths = listOf(Path(points = mutableListOf(Point(1f, 2f))))
        drawingRepository.upsert(
            NoteDrawing(
                id = testExistingDrawingArgs.id!!,
                noteId = testExistingDrawingArgs.noteId!!,
                paths = existingPaths,
            ),
        )
        initializeViewModel(testExistingDrawingArgs)

        viewModel.drawingState.test {
            advanceTimeBy(600) // Advance past debounce for initial load
            val emittedState = expectMostRecentItem()

            assertEquals(
                "Controller should contain loaded paths",
                existingPaths.size,
                viewModel.controller.drawingPaths.size,
            )
            assertEquals(
                "Controller path content should match",
                existingPaths.first().points,
                viewModel.controller.drawingPaths.first().points,
            )
            assertEquals("Emitted state should contain loaded paths", existingPaths.size, emittedState.drawings.size)
            assertEquals(
                "Emitted path content should match",
                existingPaths.first().points,
                emittedState.drawings.first().points,
            )
            assertEquals(testExistingDrawingArgs.id, emittedState.drawingId)
        }
    }

    @Test
    fun `drawing path changes are saved after debounce and reflected in state`() = runTest {
        initializeViewModel(testNewDrawingWithNoteIdArgs) // Start with a state where drawingId will be set

        viewModel.drawingState.test {
            advanceTimeBy(600) // Initial save
            val initialState = expectMostRecentItem() // consume initial state
            val drawingId = initialState.drawingId
            assertNotNull("Drawing ID should be set after initial save", drawingId)

            val newPath = Path(points = mutableListOf(Point(3f, 4f)))
            Snapshot.withMutableSnapshot {
                viewModel.controller.drawingPaths.add(newPath)
            }

            advanceTimeBy(400) // Before debounce period (500ms)

            var savedDrawing = drawingRepository.getAll().first().find { it.id == drawingId }
            // Depending on exact ViewModel logic, the controller change might emit an intermediate state before saving.
            // If DrawUiState is updated immediately on controller change, we might get an item here.
            // For this test, we are primarily concerned with the state *after* repository save.
            // So, we don't necessarily expect a new item *yet* due to the path *save*.

            assertTrue(
                "Paths should not be saved to repository before debounce",
                savedDrawing?.paths?.none { it.points == newPath.points } ?: true,
            )

            advanceTimeBy(100) // Complete debounce period (500ms total for paths change)

            val updatedState = awaitItem() // Wait for the emission after save

            savedDrawing = drawingRepository.getAll().first().find { it.id == drawingId }
            assertNotNull("Saved drawing should exist", savedDrawing)
            assertEquals("Saved drawing should have 1 path in repository", 1, savedDrawing!!.paths.size)
            assertEquals(
                "Saved path in repository should match controller path",
                newPath.points,
                savedDrawing.paths.first().points,
            )

            assertEquals("Updated state should have 1 path", 1, updatedState.drawings.size)
            assertEquals(
                "Updated state path should match new path",
                newPath.points,
                updatedState.drawings.first().points,
            )
        }
    }

    @Test
    fun `deleteDrawing removes drawing from repository`() = runTest {
        val drawingToSave =
            NoteDrawing(
                id = testExistingDrawingArgs.id!!,
                noteId = testExistingDrawingArgs.noteId!!,
                paths = listOf(Path()),
            )
        drawingRepository.upsert(drawingToSave)

        initializeViewModel(testExistingDrawingArgs)

        // Ensure viewModel initializes and knows about the drawing ID
        viewModel.drawingState.test {
            advanceTimeBy(600) // allow initial state collection and setup
            expectMostRecentItem()
        }

        assertEquals("Drawing should exist before delete", 1, drawingRepository.getAll().first().size)

        viewModel.deleteDrawing() // This is a suspend function

        assertEquals("Drawing should be deleted from repository", 0, drawingRepository.getAll().first().size)
        // Note: This test doesn't assert drawingState changes post-delete, as the current
        // ViewModel logic might not re-emit based on repository deletion in a way that this flow captures directly.
        // If drawingState *should* reflect the deletion (e.g. by becoming null or empty), further assertions on the flow would be needed.
    }
}
