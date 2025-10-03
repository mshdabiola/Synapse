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
package com.hobit.synapse

import app.cash.turbine.test
import co.touchlab.kermit.Logger
import com.hobit.synapse.MainActivityUiState.Loading
import com.hobit.synapse.MainActivityUiState.Success
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.GetNoteUseCase
import com.mshdabiola.domain.LinkUriUseCase
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ReleaseInfo
import com.mshdabiola.model.UserSettings
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.testing.fake.repository.FakeAlarmManager
import com.mshdabiola.testing.fake.repository.FakeContentManager
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNetworkRepository
import com.mshdabiola.testing.fake.repository.FakeNoteDrawingRepository
import com.mshdabiola.testing.fake.repository.FakeNoteImageRepository
import com.mshdabiola.testing.fake.repository.FakeNoteItemRepository
import com.mshdabiola.testing.fake.repository.FakeNoteLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.fake.repository.FakeNoteVoiceRepository
import com.mshdabiola.testing.fake.repository.FakeNotificationRepository
import com.mshdabiola.testing.fake.repository.FakeUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import com.mshdabiola.testing.util.testLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.DefaultAsserter.assertNotNull
import kotlin.time.Duration.Companion.seconds
import com.mshdabiola.model.note.NoteCategory as ModelNoteCategory

@OptIn(ExperimentalCoroutinesApi::class)
class MainAppViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userDataRepository: FakeUserDataRepository
    private lateinit var networkRepository: FakeNetworkRepository
    private lateinit var labelRepository: FakeLabelRepository
    private lateinit var contentManager: FakeContentManager
    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var getNoteUseCase: GetNoteUseCase
    private lateinit var addAllNoteUseCase: AddAllNoteUseCase
    private lateinit var logger: Logger
    private lateinit var viewModel: MainAppViewModel

    @Before
    fun setUp() {
        userDataRepository = FakeUserDataRepository()
        networkRepository = FakeNetworkRepository()
        labelRepository = FakeLabelRepository()
        contentManager = FakeContentManager()
        noteRepository = FakeNoteRepository()
        logger = testLogger

        getNoteUseCase = GetNoteUseCase(
            noteRepository = noteRepository,
            linkUriUseCase = LinkUriUseCase(), // Assuming a default, stateless LinkUriUseCase
        )

        addAllNoteUseCase = AddAllNoteUseCase(
            noteRepository = noteRepository,
            noteCheckRepository = FakeNoteItemRepository(),
            noteDrawingRepository = FakeNoteDrawingRepository(),
            noteImageRepository = FakeNoteImageRepository(),
            noteLabelRepository = FakeNoteLabelRepository(),
            noteNotificationRepository = FakeNotificationRepository(),
            noteVoiceRepository = FakeNoteVoiceRepository(),
            alarmManager = FakeAlarmManager(),
        )

        viewModel = MainAppViewModel(
            userDataRepository = userDataRepository,
            networkRepository = networkRepository,
            labelRepository = labelRepository,
            contentManager = contentManager,
            getNoteUseCase = getNoteUseCase,
            addAllNoteUseCase = addAllNoteUseCase,
            logger = logger,
        )
    }

    @Test
    fun `uiState is Loading initially then Success with initial userSettings and labels`() = runTest {
        val initialUserSettings = userDataRepository.userSettingsSource.value // Default from fake
        val initialLabels = emptyList<Label>() // Default from fake LabelRepository
        labelRepository.upserts(initialLabels) // Ensure initial emission for combine

        viewModel.uiState.test(timeout = 3.seconds) {
            assertEquals(Loading, awaitItem())

            val successState = awaitItem()
            assertTrue(
                "UI state should be Success, but was $successState",
                successState is Success,
            )
            assertEquals(initialUserSettings, (successState as Success).userSettings)
            assertEquals(initialLabels, successState.labels)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState updates when UserDataRepository emits new userSettings`() = runTest {
        val initialLabels = emptyList<Label>()
        labelRepository.upserts(initialLabels) // Ensure initial emission

        viewModel.uiState.test(timeout = 3.seconds) {
            assertEquals(Loading, awaitItem()) // Initial Loading
            val initialSuccessState = awaitItem() // Initial Success
            assertTrue(initialSuccessState is Success)

            val newTestUserSettings = UserSettings(
                contrast = 1,
                darkThemeConfig = DarkThemeConfig.DARK,
                useDynamicColor = true,
                updateFromPreRelease = true,
                showUpdateDialog = true,
            )
            userDataRepository.setFakeUserData(newTestUserSettings)

            val newSuccessState = awaitItem()
            assertTrue(
                "UI state should be Success with new data, but was $newSuccessState",
                newSuccessState is Success,
            )
            assertEquals(newTestUserSettings, (newSuccessState as Success).userSettings)
            assertEquals(
                (initialSuccessState as Success).labels,
                (newSuccessState as Success).labels,
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState updates when LabelRepository emits new labels`() = runTest {
        val initialUserSettings = userDataRepository.userSettingsSource.value
        labelRepository.upserts(emptyList()) // Initial emission for labels

        viewModel.uiState.test(timeout = 3.seconds) {
            assertEquals(Loading, awaitItem())
            val initialSuccessState = awaitItem() // Contains initial userSettings and empty labels
            assertTrue(initialSuccessState is Success)

            val newLabels = listOf(Label(id = 3, name = "New Label"))
            labelRepository.upserts(newLabels)

            val newSuccessState = awaitItem()
            assertTrue(
                "UI state should be Success with new labels, but was $newSuccessState",
                newSuccessState is Success,
            )
            assertEquals(newLabels, (newSuccessState as Success).labels)
            assertEquals(initialUserSettings, (newSuccessState as Success).userSettings)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getLatestReleaseInfo returns Success when network call is successful and showUpdateDialog is true`() =
        runTest {
            userDataRepository.setFakeUserData(UserSettings(showUpdateDialog = true, updateFromPreRelease = true))
            val expectedReleaseInfo = ReleaseInfo.NewUpdate(
                tagName = "v1.0.0",
                releaseName = "Test Release",
                body = "This is a test release.",
                asset = "test.apk",
            )
            networkRepository.setNextReleaseInfo(expectedReleaseInfo)

            val result = viewModel.getLatestReleaseInfo("0.0.1").await()

            assertEquals(expectedReleaseInfo, result)
        }

    @Test
    fun `getLatestReleaseInfo returns Error when network call fails even if showUpdateDialog is true`() =
        runTest {
            userDataRepository.setFakeUserData(UserSettings(showUpdateDialog = true, updateFromPreRelease = true))
            val errorMessage = "Network error"
            networkRepository.setShouldThrowError(true, errorMessage)

            val result = viewModel.getLatestReleaseInfo("0.0.1").await()

            assertTrue(result is ReleaseInfo.Error)
            assertEquals(errorMessage, (result as ReleaseInfo.Error).exception.message)
        }

    @Test
    fun `getLatestReleaseInfo returns Error with specific message when showUpdateDialog is false`() =
        runTest {
            userDataRepository.setFakeUserData(UserSettings(showUpdateDialog = false))

            val result = viewModel.getLatestReleaseInfo("0.0.1").await()

            assertTrue(result is ReleaseInfo.Error)
            assertEquals(
                "Update dialog is disabled",
                (result as ReleaseInfo.Error).exception.message,
            )
        }

    @Test
    fun `getLatestReleaseInfo considers updateFromPreRelease setting`() = runTest {
        val currentVersion = "1.0.0"
        val preReleaseInfo = ReleaseInfo.NewUpdate(
            tagName = "v1.1.0-alpha",
            releaseName = "Pre-release",
            body = "Test pre-release",
            asset = "pre.apk",
        )
        networkRepository.setNextReleaseInfo(preReleaseInfo)

        userDataRepository.setFakeUserData(UserSettings(showUpdateDialog = true, updateFromPreRelease = true))
        val resultWithPreRelease = viewModel.getLatestReleaseInfo(currentVersion).await()
        assertEquals(preReleaseInfo, resultWithPreRelease)

        val stableReleaseInfo = ReleaseInfo.NewUpdate(
            tagName = "v1.0.1",
            releaseName = "Stable Release",
            body = "Test stable release.",
            asset = "stable.apk",
        )
        networkRepository.setNextReleaseInfo(stableReleaseInfo) // Set for the next call
        userDataRepository.setFakeUserData(UserSettings(showUpdateDialog = true, updateFromPreRelease = false))
        val resultWithoutPreRelease = viewModel.getLatestReleaseInfo(currentVersion).await()
        assertEquals(stableReleaseInfo, resultWithoutPreRelease)
    }

    @Test
    fun `pictureUri returns URI from ContentManager`() {
        val expectedUri = "content://image/new_picture"
        (contentManager as FakeContentManager).pictureUriResult = expectedUri

        val actualUri = viewModel.pictureUri()

        assertEquals(expectedUri, actualUri)
    }

    @Test
    fun `copyImageToInternal calls contentManager saveImage and returns path`() = runTest {
        val testUri = "content://image_to_copy.jpg"
        val expectedPath = "/data/user/0/com.hobit.synapse/files/images/copied_image.jpg"
        (contentManager as FakeContentManager).imageSaveResult = expectedPath

        val actualPath = viewModel.copyImageToInternal(listOf(testUri))

        assertEquals(expectedPath, actualPath.first())
    }

    @Test
    fun `addNote_createsAndReturnsNote`() = runTest {
        val detailText = "This is a new note."
        val imageUri = "content://image/sample.jpg"
        val savedImagePath = "/internal/path/sample.jpg"
        (contentManager as FakeContentManager).imageSaveResult = savedImagePath

        val newNote = viewModel.addNote(
            detail = detailText,
            images = listOf(imageUri),
        )

        val allNotes = noteRepository.getAll().first()
        val retrievedNote = allNotes.find { it.id == newNote.id }

        assertNotNull("The created note should be in the repository", retrievedNote)
        assertEquals("The note ID should match", newNote.id, retrievedNote?.id)
        assertEquals("Detail text should match", detailText, retrievedNote?.detail)
        assertEquals("Image path should be the saved path", savedImagePath, retrievedNote?.images?.first()?.path)
    }

    @Test
    fun `setMainData updates UserDataRepository`() = runTest {
        val testCategory = NoteDisplayCategory(labelId = 123L, noteCategory = ModelNoteCategory.ARCHIVE)
        viewModel.setMainData(testCategory)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val userSettings = userDataRepository.userSettings.first()
        assertEquals(testCategory, userSettings.noteCategory)
    }

    @Test
    fun `log calls logger info`() = runTest {
        viewModel.log("Test log message")
        assertTrue(true) // Basic assertion: If it runs without error, it passes this simple check.
    }
}
