package com.mshdabiola.label

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshots.Snapshot
import app.cash.turbine.test
import com.mshdabiola.label.navigation.Label as LabelArg
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LabelViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var labelRepository: FakeLabelRepository
    private lateinit var userDataRepository: FakeUserDataRepository
    private lateinit var viewModel: LabelViewModel

    private val testLabelArgNoEdit = LabelArg(isEditMode = false)
    private val testLabelArgEditMode = LabelArg(isEditMode = true)

    @Before
    fun setUp() {
        labelRepository = FakeLabelRepository()
        userDataRepository = FakeUserDataRepository()
    }

    private fun initViewModel(labelArg: LabelArg = testLabelArgNoEdit) {
        viewModel = LabelViewModel(labelArg, labelRepository, userDataRepository)
    }

    @Test
    fun `initial state is correct with isEditMode false`() = runTest {
        initViewModel(testLabelArgNoEdit)
        viewModel.labelUiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.labels.isEmpty())
            assertEquals("", initialState.newLabel.label.text.toString())
            assertFalse(initialState.isEditMode)
        }
    }

    @Test
    fun `initial state is correct with isEditMode true`() = runTest {
        initViewModel(testLabelArgEditMode)
        viewModel.labelUiState.test {
            skipItems(1)
            val initialState = awaitItem()
            assertTrue(initialState.labels.isEmpty())
            assertEquals("", initialState.newLabel.label.text.toString())
            assertTrue(initialState.isEditMode)
        }
    }

    @Test
    fun `onAddNew with index -1 adds new label and resets newLabel state`() = runTest {
        initViewModel()
        val newLabelText = "New Label 1"
        // Simulate user typing into the newLabel TextFieldState which is part of labelUiState
        // In the ViewModel, newLabel is a separate MutableStateFlow, so we update that directly
        // then check its effect on labelUiState and repository.

        viewModel.labelUiState.test {
            // Initial state
            val initialUiState = awaitItem()
            initialUiState.newLabel.label.edit { replace(0, 0, newLabelText) }

            Snapshot.withMutableSnapshot {
                viewModel.onAddNew(-1)

            }

            // Expect an emission due to newLabel.value changing in onAddNew
            val stateAfterClear = awaitItem() // newLabel is cleared
            assertEquals("", stateAfterClear.newLabel.label.text.toString())

            // Expect an emission due to labelRepository update propagating
            val stateAfterAdd =stateAfterClear //awaitItem()
            assertEquals(1, stateAfterAdd.labels.size)
            assertEquals(newLabelText, stateAfterAdd.labels.first().label.text.toString())
            assertEquals(1, labelRepository.getAll().first().size)
            assertEquals(newLabelText, labelRepository.getAll().first().first().name)
        }
    }

    @Test
    fun `onAddNew with valid index updates existing label`() = runTest {
        val initialLabel = Label(id = 1L, name = "Old Label")
        labelRepository.upsert(initialLabel)
        initViewModel()

        val updatedText = "Updated Label"

        viewModel.labelUiState.test {
            skipItems(1)
            val initialState = awaitItem()
            assertEquals(1, initialState.labels.size)
            assertEquals("Old Label", initialState.labels.first().label.text.toString())

            // Simulate editing the existing label in the UI
            Snapshot.withMutableSnapshot {
                initialState.labels.first().label.clearText()
                initialState.labels.first().label.edit {
                    append(updatedText)
                }
            }


            viewModel.onAddNew(0) // Index of the label to update

            val updatedState = awaitItem() // Expect emission after repository update

            assertEquals(1, updatedState.labels.size)
            assertEquals(updatedText, updatedState.labels.first().label.text.toString())
            assertEquals(1, labelRepository.getAll().first().size)
            assertEquals(updatedText, labelRepository.getAll().first().first().name)
        }
    }

    @Test
    fun `onDelete removes label and does NOT reset noteCategory if not active`() = runTest {
        val labelToDelete = Label(id = 1L, name = "Label To Delete")
        val otherLabel = Label(id = 2L, name = "Other Label")
        labelRepository.upsert(labelToDelete)
        labelRepository.upsert(otherLabel)
        userDataRepository.setNoteCategory(NoteDisplayCategory(noteCategory = NoteCategory.LABEL, labelId = 2L))
        initViewModel()

        viewModel.labelUiState.test {
            awaitItem() // Initial state with two labels

            viewModel.onDelete(1L)

            val stateAfterDelete = awaitItem()
            assertEquals(1, stateAfterDelete.labels.size)
            assertEquals("Other Label", stateAfterDelete.labels.first().label.text.toString())
            assertEquals(1, labelRepository.getAll().first().size)
            assertEquals(2L, labelRepository.getAll().first().first().id)

            // Check user data was not reset
            val userSettings = userDataRepository.userSettings.first()
            assertEquals(NoteCategory.LABEL, userSettings.noteCategory.noteCategory)
            assertEquals(2L, userSettings.noteCategory.labelId)
        }
    }

    @Test
    fun `onDelete removes label AND resets noteCategory if active`() = runTest {
        val labelToDelete = Label(id = 1L, name = "Active Label To Delete")
        labelRepository.upsert(labelToDelete)
        userDataRepository.setNoteCategory(NoteDisplayCategory(noteCategory = NoteCategory.LABEL, labelId = 1L))
        initViewModel()

        viewModel.labelUiState.test {
            awaitItem() // Initial state

            viewModel.onDelete(1L)

            val stateAfterDelete = awaitItem()
            assertTrue(stateAfterDelete.labels.isEmpty())
            assertTrue(labelRepository.getAll().first().isEmpty())

            // Check user data was reset to default
            val userSettings = userDataRepository.userSettings.first()
            assertEquals(NoteCategory.NOTE, userSettings.noteCategory.noteCategory)
            assertEquals(1L, userSettings.noteCategory.labelId)
        }
    }
}
