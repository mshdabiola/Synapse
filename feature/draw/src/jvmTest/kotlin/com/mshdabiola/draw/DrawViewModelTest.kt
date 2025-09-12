package com.mshdabiola.draw

import com.mshdabiola.draw.navigation.Draw
import com.mshdabiola.testing.fake.repository.FakeNoteDrawingRepository
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class DrawViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var drawingRepository: FakeNoteDrawingRepository
    private lateinit var viewModel: DrawViewModel

    private val testDrawingArgs = Draw(noteId = 1L, id = null)
    private val testExistingDrawingArgs = Draw(noteId = 2L, id = 100L)

    @Before
    fun setUp() {
        drawingRepository = FakeNoteDrawingRepository()
    }

    private fun initializeViewModel(args: Draw = testDrawingArgs) {
        viewModel = DrawViewModel(
            draw = args,
            drawingRepository = drawingRepository,
            noteRepository = FakeNoteRepository(),
        )
    }

}
