package com.mshdabiola.select

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.state.ToggleableState
import app.cash.turbine.test
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteLabelCrossRef
import com.mshdabiola.select.navigation.Select
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNoteLabelRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.DefaultAsserter.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class SelectViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var labelRepository: FakeLabelRepository
    private lateinit var noteLabelRepository: FakeNoteLabelRepository
    private lateinit var viewModel: SelectViewModel

    private val noteId1 = 1L
    private val noteId2 = 2L
    private val sampleArgs = Select(ids = "$noteId1,$noteId2")
    private val singleNoteArgs = Select(ids = "$noteId1")

    private val labelWork = Label(id = 101L, name = "Work")
    private val labelPersonal = Label(id = 102L, name = "Personal")
    private val labelUrgent = Label(id = 103L, name = "Urgent")

    @Before
    fun setUp() {
        labelRepository = FakeLabelRepository()
        noteLabelRepository = FakeNoteLabelRepository()

        runTest {
            labelRepository.upsert(labelWork)
            labelRepository.upsert(labelPersonal)
            labelRepository.upsert(labelUrgent)
        }
    }

    private fun initializeViewModel(args: Select = sampleArgs) {
        viewModel = SelectViewModel(
            select = args,
            labelRepository = labelRepository,
            noteLabelRepository = noteLabelRepository,
        )
    }

    @Test
    fun `initial selectUiState is correct with no associations`() = runTest {
        initializeViewModel()
        viewModel.selectUiState.test {
            val initialState = awaitItem()
            assertEquals(3, initialState.labels.size)
            assertTrue(initialState.labels.all { it.toggleableState == ToggleableState.Off })
            assertEquals("", initialState.labelQuery.text.toString())
            assertFalse(initialState.showAddLabel)
        }
    }

    @Test
    fun `initial selectUiState reflects existing associations - all ON`() = runTest {
        noteLabelRepository.upsert(NoteLabelCrossRef(noteId1, labelWork.id))
        noteLabelRepository.upsert(NoteLabelCrossRef(noteId2, labelWork.id))
        initializeViewModel()

        viewModel.selectUiState.test {
            val state = awaitItem()
            val workLabelState = state.labels.find { it.id == labelWork.id }
            assertEquals(ToggleableState.On, workLabelState?.toggleableState)
            val personalLabelState = state.labels.find { it.id == labelPersonal.id }
            assertEquals(ToggleableState.Off, personalLabelState?.toggleableState)
        }
    }

    @Test
    fun `initial selectUiState reflects existing associations - INDETERMINATE`() = runTest {
        noteLabelRepository.upsert(NoteLabelCrossRef(noteId1, labelPersonal.id)) // Only one note has this label
        initializeViewModel()

        viewModel.selectUiState.test {
            val state = awaitItem()
            val personalLabelState = state.labels.find { it.id == labelPersonal.id }
            assertEquals(ToggleableState.Indeterminate, personalLabelState?.toggleableState)
        }
    }

    @Test
    fun `labelQuery filters labels and showAddLabel updates correctly`() = runTest {
        initializeViewModel()
        viewModel.selectUiState.test {
            awaitItem() // Initial state

            viewModel.initLabelState.labelQuery.setTextAndPlaceCursorAtEnd("Work")
            advanceTimeBy(600) // Debounce
            var state = awaitItem()
            assertEquals(1, state.labels.size)
            assertEquals("Work", state.labels.first().label)
            assertFalse("showAddLabel should be false as 'Work' exists", state.showAddLabel)

            viewModel.initLabelState.labelQuery.setTextAndPlaceCursorAtEnd("NewTag")
            advanceTimeBy(600) // Debounce
            state = awaitItem()
            assertTrue(state.labels.none { it.label.contains("NewTag") })
            assertTrue("showAddLabel should be true for 'NewTag'", state.showAddLabel)

            viewModel.initLabelState.labelQuery.clearText()
            advanceTimeBy(600) // Debounce
            state = awaitItem()
            assertEquals(3, state.labels.size) // All labels shown again
            assertFalse(state.showAddLabel)
        }
    }

    @Test
    fun `onCheckClick from Off to On updates state and repository`() = runTest {
        initializeViewModel(singleNoteArgs) // Use single note for simpler repo check
        viewModel.selectUiState.test {
            val initialState = awaitItem()
            val workLabelIndex = initialState.labels.indexOfFirst { it.id == labelWork.id }
            assertNotEquals(-1, workLabelIndex)
            assertEquals(ToggleableState.Off, initialState.labels[workLabelIndex].toggleableState)

            viewModel.onCheckClick(workLabelIndex)
            // No direct emission from onCheckClick, relies on DB flow for update
            advanceTimeBy(100) // Allow repository operations and flow to emit

            val updatedState = awaitItem()
            assertEquals(ToggleableState.On, updatedState.labels[workLabelIndex].toggleableState)
            assertTrue(noteLabelRepository.getAll().first().contains(NoteLabelCrossRef(noteId1, labelWork.id)))
        }
    }

    @Test
    fun `onCheckClick from Indeterminate to On updates state and repository`() = runTest {
        noteLabelRepository.upsert(NoteLabelCrossRef(noteId1, labelPersonal.id)) // note1 has Personal
        initializeViewModel() // ViewModel uses noteId1 and noteId2

        viewModel.selectUiState.test {
            val initialState = awaitItem()
            val personalLabelIndex = initialState.labels.indexOfFirst { it.id == labelPersonal.id }
            assertEquals(ToggleableState.Indeterminate, initialState.labels[personalLabelIndex].toggleableState)

            viewModel.onCheckClick(personalLabelIndex)
            advanceTimeBy(100)

            val updatedState = awaitItem()
            assertEquals(ToggleableState.On, updatedState.labels[personalLabelIndex].toggleableState)
            assertTrue(noteLabelRepository.getAll().first().contains(NoteLabelCrossRef(noteId1, labelPersonal.id)))
            assertTrue(noteLabelRepository.getAll().first().contains(NoteLabelCrossRef(noteId2, labelPersonal.id)))
        }
    }

    @Test
    fun `onCheckClick from On to Off updates state and repository`() = runTest {
        noteLabelRepository.upsert(NoteLabelCrossRef(noteId1, labelUrgent.id))
        noteLabelRepository.upsert(NoteLabelCrossRef(noteId2, labelUrgent.id))
        initializeViewModel()

        viewModel.selectUiState.test {
            val initialState = awaitItem()
            val urgentLabelIndex = initialState.labels.indexOfFirst { it.id == labelUrgent.id }
            assertEquals(ToggleableState.On, initialState.labels[urgentLabelIndex].toggleableState)

            viewModel.onCheckClick(urgentLabelIndex)
            advanceTimeBy(100)

            val updatedState = awaitItem()
            assertEquals(ToggleableState.Off, updatedState.labels[urgentLabelIndex].toggleableState)
            assertFalse(noteLabelRepository.getAll().first().contains(NoteLabelCrossRef(noteId1, labelUrgent.id)))
            assertFalse(noteLabelRepository.getAll().first().contains(NoteLabelCrossRef(noteId2, labelUrgent.id)))
        }
    }

    @Test
    fun `onCreateLabel creates label, clears query, associates with notes, and updates UI`() = runTest {
        initializeViewModel(singleNoteArgs)
        val newLabelName = "Brand New Label"

        viewModel.selectUiState.test {
            awaitItem() // Initial state

            viewModel.initLabelState.labelQuery.setTextAndPlaceCursorAtEnd(newLabelName)
            // No need to wait for debounce here as onCreateLabel reads current query text directly

            viewModel.onCreateLabel()
            advanceTimeBy(200) // Allow repo and flow processing

            val stateAfterCreate = awaitItem() // This should be the state after query clear & label add

            // 1. Query should be cleared
            assertEquals("", stateAfterCreate.labelQuery.text.toString())

            // 2. New label should exist in the label repository
            val createdRepoLabel = labelRepository.getAll().first().find { it.name == newLabelName }
            assertNotNull("Label should be created in repository", createdRepoLabel)

            // 3. New label should be associated with the noteId
            assertTrue(
                "New label should be associated with noteId",
                noteLabelRepository.getAll().first().contains(NoteLabelCrossRef(noteId1, createdRepoLabel!!.id))
            )

            // 4. UI should reflect the new label, likely as 'On'
            val newLabelInUi = stateAfterCreate.labels.find { it.id == createdRepoLabel.id }
            assertNotNull("New label should be in UI state", newLabelInUi)
            assertEquals(newLabelName, newLabelInUi!!.label)
            assertEquals("New label in UI should be ON", ToggleableState.On, newLabelInUi.toggleableState)
        }
    }
}
