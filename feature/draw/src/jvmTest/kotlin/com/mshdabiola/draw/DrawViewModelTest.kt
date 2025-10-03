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
import com.mshdabiola.model.note.Path
import com.mshdabiola.model.note.Point
import com.mshdabiola.testing.fake.repository.FakeNoteDrawingRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DrawViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var drawingRepository: FakeNoteDrawingRepository
    private lateinit var viewModel: DrawViewModel

    private val testNewDrawingArgs = Draw(noteId = 1, id = null)
    private val testNewDrawingWithNoteIdArgs = Draw(noteId = 1L, id = null)
    private val testExistingDrawingArgs = Draw(noteId = 2L, id = 100L)

    @Before
    fun setUp() {
        drawingRepository = FakeNoteDrawingRepository()
    }

    private fun initializeViewModel(args: Draw) {
        viewModel = DrawViewModel(
            draw = args,
            drawingRepository = drawingRepository,
        )
    }

    @Test
    fun `init with null noteId creates drawing with null noteId`() = runTest {
        initializeViewModel(testNewDrawingArgs)

        viewModel.drawingState.test {
            advanceTimeBy(600) // Advance past debounce for initial save
            val emittedState = expectMostRecentItem()

            val createdDrawing = drawingRepository.get(emittedState.drawingId!!).first()

            assertNotNull("A new NoteDrawing should be created", createdDrawing)
            assertNotNull("NoteDrawing should have a  noteId", createdDrawing?.noteId)
            assertNotNull("DrawingId in viewModel should be updated", emittedState.drawingId)
            assertTrue("Emitted drawing paths should be empty initially", emittedState.drawings.isEmpty())
        }
    }

    @Test
    fun `init with existing noteId creates drawing with correct noteId`() = runTest {
        initializeViewModel(testNewDrawingWithNoteIdArgs)

        viewModel.drawingState.test {
            advanceTimeBy(600) // Advance past debounce for initial save
            val emittedState = expectMostRecentItem()

            val createdDrawing = drawingRepository.get(emittedState.drawingId!!).first()

            assertNotNull("NoteDrawing should be created", createdDrawing)
            assertEquals(
                "NoteDrawing should use existing noteId",
                testNewDrawingWithNoteIdArgs.noteId,
                createdDrawing?.noteId,
            )
            assertNotNull("DrawingId in viewModel should be updated", emittedState.drawingId)
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
        initializeViewModel(testNewDrawingWithNoteIdArgs)

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

            var savedDrawing = drawingRepository.get(drawingId!!).first()
            assertTrue(
                "Paths should not be saved to repository before debounce",
                savedDrawing?.paths?.none { it.points == newPath.points } ?: true,
            )

            advanceTimeBy(100) // Complete debounce period (500ms total for paths change)
            advanceUntilIdle()

            val updatedState = awaitItem() // Wait for the emission after save

            savedDrawing = drawingRepository.get(drawingId).first()
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

        viewModel.deleteDrawing()
        advanceUntilIdle() // Ensure delete coroutine completes

        assertEquals("Drawing should be deleted from repository", 0, drawingRepository.getAll().first().size)
    }

    @Test
    fun `multiplePathChanges_withinDebounce_areSavedTogether`() = runTest {
        initializeViewModel(testNewDrawingWithNoteIdArgs)

        viewModel.drawingState.test {
            advanceTimeBy(600) // Allow initial save and get drawingId
            val initialState = expectMostRecentItem()
            val drawingId = initialState.drawingId
            assertNotNull("Drawing ID should be set after initial save", drawingId)
            assertTrue("Initial state drawings should be empty", initialState.drawings.isEmpty())

            val path1 = Path(points = mutableListOf(Point(10f, 10f)))
            val path2 = Path(points = mutableListOf(Point(20f, 20f)))
            val path3 = Path(points = mutableListOf(Point(30f, 30f)))

            Snapshot.withMutableSnapshot {
                viewModel.controller.drawingPaths.add(path1)
            }
            advanceTimeBy(200)

            Snapshot.withMutableSnapshot {
                viewModel.controller.drawingPaths.add(path2)
            }
            advanceTimeBy(200)

            Snapshot.withMutableSnapshot {
                viewModel.controller.drawingPaths.add(path3)
            }

            advanceTimeBy(600) // Ensure enough time for debounce after the LAST change
            advanceUntilIdle()

            val finalState = expectMostRecentItem()

            assertEquals("Final state should have 3 paths", 3, finalState.drawings.size)

            val savedDrawing = drawingRepository.get(drawingId!!).first()
            assertNotNull("Saved drawing should exist in repository", savedDrawing)
            assertEquals("Saved drawing in repository should have 3 paths", 3, savedDrawing!!.paths.size)
        }
    }
}
