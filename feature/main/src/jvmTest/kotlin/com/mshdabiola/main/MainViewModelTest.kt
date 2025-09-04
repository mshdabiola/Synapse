package com.mshdabiola.main

import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.GetAllNoteUseCase
import com.mshdabiola.domain.LinkUriUseCase
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNoteDrawingRepository
import com.mshdabiola.testing.fake.repository.FakeNoteImageRepository
import com.mshdabiola.testing.fake.repository.FakeNoteItemRepository
import com.mshdabiola.testing.fake.repository.FakeNoteLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.fake.repository.FakeNoteVoiceRepository
import com.mshdabiola.testing.fake.repository.FakeNotificationRepository
import com.mshdabiola.testing.fake.repository.FakeUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var fakeLabelRepository: FakeLabelRepository
    private lateinit var fakeGetAllNoteUseCase: GetAllNoteUseCase
    private lateinit var fakeAddAllNoteUseCase: AddAllNoteUseCase
    private lateinit var viewModel: MainViewModel

    // Helper to create a NotePad
    private fun createNotePad(id: Long, title: String, isPinned: Boolean = false, category: NoteCategory = NoteCategory.NOTE, labels: List<Label> = emptyList(), color: Int = 0): NotePad {
        return NotePad(
            id = id, title = title,  labels = labels,  isPin = isPinned, color = color, noteCategory = category, notification = null
        )
    }

    @Before
    fun setup() {
        fakeNoteRepository = FakeNoteRepository()
        fakeUserDataRepository = FakeUserDataRepository()
        fakeLabelRepository = FakeLabelRepository()
        fakeGetAllNoteUseCase =  GetAllNoteUseCase(
            noteRepository = fakeNoteRepository,
            linkUriUseCase = LinkUriUseCase(),
        )
        val noteCheckRepository = FakeNoteItemRepository()
        val noteDrawingRepository = FakeNoteDrawingRepository()
        val noteImageRepository = FakeNoteImageRepository()
        val noteLabelRepository = FakeNoteLabelRepository()
        val noteNotificationRepository = FakeNotificationRepository()
        val noteVoiceRepository = FakeNoteVoiceRepository()

        fakeAddAllNoteUseCase = AddAllNoteUseCase(
            noteRepository = fakeNoteRepository,
            noteCheckRepository = noteCheckRepository,
            noteDrawingRepository = noteDrawingRepository,
            noteImageRepository = noteImageRepository,
            noteLabelRepository = noteLabelRepository,
            noteNotificationRepository = noteNotificationRepository,
            noteVoiceRepository = noteVoiceRepository,
        )

        viewModel = MainViewModel(
            noteRepository = fakeNoteRepository,
            userDataRepository = fakeUserDataRepository,
            labelRepository = fakeLabelRepository,
            getAllNoteUseCase = fakeGetAllNoteUseCase,
            addAllNoteUseCase = fakeAddAllNoteUseCase,
        )
    }

}
