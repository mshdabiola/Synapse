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

import app.cash.turbine.test
import com.mshdabiola.main.model.MainState
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        noteRepository = FakeNoteRepository() // Manually instantiate the fake
    }

    @Test
    fun `initial state is Loading then transitions to Empty when repository is empty`() = runTest {
        // Arrange: Repository is empty by default after setUp

        // Act
        viewModel = MainViewModel(noteRepository)

        // Assert
        viewModel.mainState.test {
            assertEquals(MainState.Loading, awaitItem(), "Initial state should be Loading")
            assertEquals(MainState.Empty, awaitItem(), "Next state should be Empty for no notes")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial state is Loading then transitions to Success when repository has notes`() = runTest {
        // Arrange
        val testNotes = listOf(
            NotePad(id = 1L, title = "NotePad 1", detail = "Content 1"),
            NotePad(id = 2L, title = "NotePad 2", detail = "Content 2"),
        )
        noteRepository.setData(testNotes) // Pre-populate the fake repo

        // Act
        viewModel = MainViewModel(noteRepository)

        // Assert
        viewModel.mainState.test {
            assertEquals(MainState.Loading, awaitItem(), "Initial state should be Loading")

            val successState = awaitItem()
            assertTrue(successState is MainState.Success, "Next state should be Success")
            assertEquals(testNotes.size, successState.notes.size, "Number of notes should match")
            // You can add more detailed assertions here if your NotePad maps directly to NoteUiState
            // or if you want to verify specific properties.
            // For example, if NotePad is the same as what MainState.Success expects:
            assertEquals(testNotes[0].title, successState.notes[0].title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `mainState reflects updates from repository`() = runTest {
        // Arrange
        viewModel = MainViewModel(noteRepository)

        viewModel.mainState.test {
            assertEquals(MainState.Loading, awaitItem())
            assertEquals(MainState.Empty, awaitItem(), "Should be Empty initially")

            // Act: Add notes to the repository
            val newNotes = listOf(NotePad(id = 3L, title = "New NotePad", detail = "New Content"))
            noteRepository.setData(newNotes) // This should trigger an update in the Flow

            // Assert: Check for transition to Success
            val successState = awaitItem()
            assertTrue(successState is MainState.Success, "State should transition to Success after notes are added")
            assertEquals(1, successState.notes.size)
            assertEquals("New NotePad", successState.notes[0].title)

            // Act: Remove notes from the repository
            noteRepository.setData(emptyList())

            // Assert: Check for transition back to Empty
            assertEquals(MainState.Empty, awaitItem(), "State should transition back to Empty")

            cancelAndIgnoreRemainingEvents()
        }
    }
}
