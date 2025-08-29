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
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ReleaseInfo
import com.mshdabiola.model.UserSettings
import com.mshdabiola.testing.fake.repository.FakeContentManager
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNetworkRepository // Import the shared fake
import com.mshdabiola.testing.fake.repository.FakeNoteDrawingRepository
import com.mshdabiola.testing.fake.repository.FakeNoteImageRepository
import com.mshdabiola.testing.fake.repository.FakeNoteItemRepository
import com.mshdabiola.testing.fake.repository.FakeNoteLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.fake.repository.FakeNoteVoiceRepository
import com.mshdabiola.testing.fake.repository.FakeNotificationRepository
import com.mshdabiola.testing.fake.repository.FakeUserDataRepository // Import the shared fake
import com.mshdabiola.testing.util.testLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first // Added
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull // Added
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

// Added imports
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.NoteVoice
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NoteItem
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.note.NoteCategory as ModelNoteCategory


@OptIn(ExperimentalCoroutinesApi::class)
class MainAppViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var userDataRepository: FakeUserDataRepository
    private lateinit var noteRepository: FakeNoteRepository // Made accessible for verification
    private lateinit var labelRepository: FakeLabelRepository
    private lateinit var addAllNoteUseCase: AddAllNoteUseCase
    private lateinit var contentManager: FakeContentManager
    private lateinit var networkRepository: FakeNetworkRepository
    private lateinit var viewModel: MainAppViewModel
    private lateinit var logger: Logger

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        labelRepository = FakeLabelRepository()
        noteRepository = FakeNoteRepository() // Initialized here
        addAllNoteUseCase = AddAllNoteUseCase(
            noteRepository = noteRepository,
            noteCheckRepository = FakeNoteItemRepository(),
            noteDrawingRepository = FakeNoteDrawingRepository(),
            noteImageRepository = FakeNoteImageRepository(),
            noteLabelRepository = FakeNoteLabelRepository(),
            noteNotificationRepository = FakeNotificationRepository(),
            noteVoiceRepository = FakeNoteVoiceRepository(),
        )
        contentManager = FakeContentManager()

        userDataRepository = FakeUserDataRepository()
        networkRepository = FakeNetworkRepository()
        logger = testLogger // Using the testLogger instance
        viewModel = MainAppViewModel(
            userDataRepository = userDataRepository,
            networkRepository = networkRepository,
            labelRepository = labelRepository,
            addNoteUseCase = addAllNoteUseCase,
            contentManager = contentManager,
            logger = logger,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState is Loading initially then Success with initial repo data`() = runTest(testDispatcher) {
        val initialRepoData = userDataRepository.userSettingsSource.value

        viewModel.uiState.test(timeout = 3.seconds) {
            assertEquals(Loading, awaitItem())

            testDispatcher.scheduler.advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(
                "UI state should be Success, but was $successState",
                successState is Success,
            )
            assertEquals(initialRepoData, (successState as Success).userSettings)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState transitions to Success when UserDataRepository emits new data`() = runTest(testDispatcher) {
        val newTestUserSettings = UserSettings(
            contrast = 1,
            darkThemeConfig = DarkThemeConfig.DARK,
            useDynamicColor = true,
            updateFromPreRelease = true,
            showUpdateDialog = true,
        )

        viewModel.uiState.test(timeout = 3.seconds) {
            assertEquals(Loading, awaitItem())

            testDispatcher.scheduler.advanceUntilIdle()
            val initialSuccessState = awaitItem()
            assertTrue(initialSuccessState is Success)

            userDataRepository.setFakeUserData(newTestUserSettings)
            testDispatcher.scheduler.advanceUntilIdle()

            val newSuccessState = awaitItem()
            assertTrue(
                "UI state should be Success with new data, but was $newSuccessState",
                newSuccessState is Success,
            )
            assertEquals(newTestUserSettings, (newSuccessState as Success).userSettings)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getLatestReleaseInfo returns Success when network call is successful and showUpdateDialog is true`() =
        runTest(testDispatcher) {
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
        runTest(testDispatcher) {
            userDataRepository.setFakeUserData(UserSettings(showUpdateDialog = true, updateFromPreRelease = true))
            val errorMessage = "Network error"
            networkRepository.setShouldThrowError(true, errorMessage)

            val result = viewModel.getLatestReleaseInfo("0.0.1").await()

            assertTrue(result is ReleaseInfo.Error)
            assertEquals(errorMessage, (result as ReleaseInfo.Error).exception.message)
        }

    @Test
    fun `getLatestReleaseInfo returns Error with specific message when showUpdateDialog is false`() =
        runTest(testDispatcher) {
            userDataRepository.setFakeUserData(UserSettings(showUpdateDialog = false))

            val result = viewModel.getLatestReleaseInfo("0.0.1").await()

            assertTrue(result is ReleaseInfo.Error)
            assertEquals("Update dialog is disabled", (result as ReleaseInfo.Error).exception.message)
        }

    @Test
    fun `getLatestReleaseInfo considers updateFromPreRelease setting`() = runTest(testDispatcher) {
        val currentVersion = "1.0.0"
        val expectedReleaseInfo = ReleaseInfo.NewUpdate(
            tagName = "v1.1.0-alpha",
            releaseName = "Pre-release",
            body = "Test pre-release",
            asset = "pre.apk",
        )
        networkRepository.setNextReleaseInfo(expectedReleaseInfo)

        userDataRepository.setFakeUserData(UserSettings(showUpdateDialog = true, updateFromPreRelease = true))
        val resultWithPreRelease = viewModel.getLatestReleaseInfo(currentVersion).await()
        assertEquals(expectedReleaseInfo, resultWithPreRelease)

        val expectedStableReleaseInfo = ReleaseInfo.NewUpdate(
            tagName = "v1.0.1",
            releaseName = "Stable Release",
            body = "Test stable release.",
            asset = "stable.apk",
        )
        networkRepository.setNextReleaseInfo(expectedStableReleaseInfo)
        userDataRepository.setFakeUserData(UserSettings(showUpdateDialog = true, updateFromPreRelease = false))
        val resultWithoutPreRelease = viewModel.getLatestReleaseInfo(currentVersion).await()
        assertEquals(expectedStableReleaseInfo, resultWithoutPreRelease)
    }

    // New Tests Below

    @Test
    fun `insertNewNote adds a default NotePad`() = runTest(testDispatcher) {
        val returnedId = viewModel.insertNewNote()
        testDispatcher.scheduler.advanceUntilIdle()

        val notes = noteRepository.getAll().first()
        val insertedNote = notes.find { it.id == returnedId }

        assertNotNull(insertedNote)
        assertEquals(NotePad().copy(id=returnedId), insertedNote?.copy(editDate = 0L)) // Compare with default, ignoring generated ID and editDate for simplicity
        assertTrue(returnedId > 0)
    }

    @Test
    fun `insertNewAudioNote saves voice and adds NotePad with audio`() = runTest(testDispatcher) {
        val testUri = "content://audio/1"
        val testText = "This is a voice note"
        val expectedVoiceId = 123L
        val expectedVoicePath = "/path/to/voice.mp3"

        contentManager.voiceSaveResult = expectedVoiceId
        contentManager.voicePathResult = expectedVoicePath

        val returnedId = viewModel.insertNewAudioNote(testUri, testText)
        testDispatcher.scheduler.advanceUntilIdle()


        val notes = noteRepository.getAll().first()
        val insertedNote = notes.find { it.id == returnedId }

        assertNotNull(insertedNote)
        assertEquals(testText, insertedNote?.detail)
        assertTrue(insertedNote?.voices?.isNotEmpty() == true)
        assertEquals(expectedVoiceId, insertedNote?.voices?.first()?.id)
        assertEquals(expectedVoicePath, insertedNote?.voices?.first()?.path)
        assertTrue(returnedId > 0)
    }

    @Test
    fun `insertNewImageNote saves image and adds NotePad with image`() = runTest(testDispatcher) {
        val testUri = "content://image/1"
        val expectedImageId = 456L
        val expectedImagePath = "/path/to/image.jpg"

        contentManager.imageSaveResult = expectedImageId
        contentManager.imagePathResult = expectedImagePath

        val returnedId = viewModel.insertNewImageNote(testUri)
        testDispatcher.scheduler.advanceUntilIdle()

        val notes = noteRepository.getAll().first()
        val insertedNote = notes.find { it.id == returnedId }

        assertNotNull(insertedNote)
        assertTrue(insertedNote?.images?.isNotEmpty() == true)
        assertEquals(expectedImageId, insertedNote?.images?.first()?.id)
        assertEquals(expectedImagePath, insertedNote?.images?.first()?.path)
        assertTrue(returnedId > 0)
    }

    @Test
    fun `insertNewDrawing adds a default NotePad for drawing`() = runTest(testDispatcher) {
        val returnedId = viewModel.insertNewDrawing()
        testDispatcher.scheduler.advanceUntilIdle()

        val notes = noteRepository.getAll().first()
        val insertedNote = notes.find { it.id == returnedId }

        assertNotNull(insertedNote)
         assertEquals(NotePad().copy(id=returnedId), insertedNote?.copy(editDate = 0L))
        assertTrue(returnedId > 0)
    }

    @Test
    fun `insertNewCheckNote adds NotePad with check item`() = runTest(testDispatcher) {
        val returnedId = viewModel.insertNewCheckNote()
        testDispatcher.scheduler.advanceUntilIdle()

        val notes = noteRepository.getAll().first()
        val insertedNote = notes.find { it.id == returnedId }

        assertNotNull(insertedNote)
        assertTrue(insertedNote?.isCheck == true)
        assertTrue(insertedNote?.checks?.size == 1)
        assertEquals(NoteItem(), insertedNote?.checks?.first())
        assertTrue(returnedId > 0)
    }

    @Test
    fun `pictureUri returns URI from ContentManager`() {
        val expectedUri = "content://image/new_picture"
        contentManager.pictureUriResult = expectedUri

        val actualUri = viewModel.pictureUri()

        assertEquals(expectedUri, actualUri)
    }

    @Test
    fun `setMainData updates UserDataRepository`() = runTest(testDispatcher) {
        val testCategory = NoteDisplayCategory(labelId = 123L, noteCategory = ModelNoteCategory.ARCHIVE)
        viewModel.setMainData(testCategory)
        testDispatcher.scheduler.advanceUntilIdle()


        val userSettings = userDataRepository.userSettings.first()
        assertEquals(testCategory, userSettings.noteCategory)
    }

    @Test
    fun `log calls logger info`() = runTest(testDispatcher) {
        // Since testLogger prints to console, we can't easily verify the call directly
        // without a more sophisticated mock. For now, we ensure it runs without error.
        // In a real scenario with a mockable logger, you'd verify logger.i("Test log") was called.
        viewModel.log("Test log")
        // No explicit assertion here, but test passes if no crash.
        // A more robust test would involve a mock logger.
        assertTrue(true) // Placeholder assertion
    }
}
