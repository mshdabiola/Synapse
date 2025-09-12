package com.mshdabiola.select

import com.mshdabiola.model.note.Label
import com.mshdabiola.select.navigation.Select
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNoteLabelRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule

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

    private val label1 = Label(id = 101L, name = "Work")
    private val label2 = Label(id = 102L, name = "Personal")
    private val label3 = Label(id = 103L, name = "Urgent")

    @Before
    fun setUp() {
        labelRepository = FakeLabelRepository()
        noteLabelRepository = FakeNoteLabelRepository()

        // Pre-populate repositories with some data
        runTest {
            labelRepository.upsert(label1)
            labelRepository.upsert(label2)
            labelRepository.upsert(label3)
        }

        viewModel = SelectViewModel(
            select = sampleArgs,
            labelRepository = labelRepository,
            noteLabelRepository = noteLabelRepository,
        )
    }

}
