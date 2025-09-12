package com.mshdabiola.view

import com.mshdabiola.testing.fake.repository.FakeContentManager
import com.mshdabiola.testing.fake.repository.FakeNoteImageRepository
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import com.mshdabiola.view.navigation.View
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class ViewViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ViewViewModel
    private lateinit var noteImageRepository: FakeNoteImageRepository
    private lateinit var noteRepository: FakeNoteRepository // Updated
    private lateinit var contentManager: FakeContentManager // Updated

    private val testNoteId = 1L
    private val testGalleryArg = View(
        id = testNoteId,
        index = 0,
        total = 0, // Will be overridden by actual images
        currentPath = "/fake/content/path/image_1.jpg",
    )

    @Before
    fun setUp() {
        noteImageRepository = FakeNoteImageRepository()
        noteRepository = FakeNoteRepository() // Initialize
        contentManager = FakeContentManager() // Initialize
    }

    private fun initializeViewModel(galleryArg: View = testGalleryArg) {
        viewModel = ViewViewModel(
            view = galleryArg,
            noteImageRepository = noteImageRepository,
            noteRepository = noteRepository, // Pass updated dependency
            contentManager = contentManager, // Pass updated dependency
        )
    }

}
