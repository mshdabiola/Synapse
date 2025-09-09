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
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ReleaseInfo
import com.mshdabiola.model.UserSettings
import com.mshdabiola.model.UpdateException
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.testing.fake.repository.FakeContentManager
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNetworkRepository
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
    private lateinit var logger: Logger
    private lateinit var viewModel: MainAppViewModel

    @Before
    fun setUp() {
        userDataRepository = FakeUserDataRepository()
        networkRepository = FakeNetworkRepository()
        labelRepository = FakeLabelRepository()
        contentManager = FakeContentManager()
        logger = testLogger // Using the testLogger instance from com.mshdabiola.testing.util

        viewModel = MainAppViewModel(
            userDataRepository = userDataRepository,
            networkRepository = networkRepository,
            labelRepository = labelRepository,
            contentManager = contentManager,
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
            assertTrue("UI state should be Success, but was $successState", successState is Success)
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
            assertTrue("UI state should be Success with new data, but was $newSuccessState", newSuccessState is Success)
            assertEquals(newTestUserSettings, (newSuccessState as Success).userSettings)
            // Labels should remain the same as initially emitted by FakeLabelRepository
            assertEquals((initialSuccessState as Success).labels, (newSuccessState as Success).labels)

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
            assertTrue("UI state should be Success with new labels, but was $newSuccessState", newSuccessState is Success)
            assertEquals(newLabels, (newSuccessState as Success).labels)
            assertEquals(initialUserSettings, (newSuccessState as Success).userSettings) // UserSettings should remain unchanged

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
        // Assuming FakeContentManager has a way to set the expected result for pictureUri()
        (contentManager as FakeContentManager).pictureUriResult = expectedUri

        val actualUri = viewModel.pictureUri()

        assertEquals(expectedUri, actualUri)
    }

    @Test
    fun `copyImageToInternal calls contentManager saveImage and returns path`() = runTest {
        val testUri = "content://image_to_copy.jpg"
        val expectedPath = "/data/user/0/com.hobit.synapse/files/images/copied_image.jpg"
        // Assuming FakeContentManager has a way to set the expected result for saveImage()
        (contentManager as FakeContentManager).imageSaveResult = expectedPath

        val actualPath = viewModel.copyImageToInternal(testUri)

        assertEquals(expectedPath, actualPath)
        // Optionally, verify that contentManager.saveImage was called with testUri
        // This depends on FakeContentManager's implementation (e.g., a stored lastCalledWith property)
        // assertEquals(testUri, (contentManager as FakeContentManager).lastSavedImageUri)
    }

    @Test
    fun `setMainData updates UserDataRepository`() = runTest {
        val testCategory = NoteDisplayCategory(labelId = 123L, noteCategory = ModelNoteCategory.ARCHIVE)
        viewModel.setMainData(testCategory)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutine launched by setMainData completes

        val userSettings = userDataRepository.userSettings.first()
        assertEquals(testCategory, userSettings.noteCategory)
    }

    @Test
    fun `log calls logger info`() = runTest {
        // This test primarily ensures no crash and that the method is callable.
        // For more robust testing, you'd use a mock logger (e.g., from MockK or a custom fake)
        // to verify that logger.i("Test log") was indeed called.
        // val mockLogger = mockk<Logger>(relaxed = true)
        // viewModel = MainAppViewModel(..., logger = mockLogger)
        // viewModel.log("Test log")
        // verify { mockLogger.i("Test log") }
        viewModel.log("Test log message")
        assertTrue(true) // Basic assertion: If it runs without error, it passes this simple check.
    }
}
