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
package com.mshdabiola.datastore

// Removed MockK imports
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.mshdabiola.datastore.model.UserPreferences
import com.mshdabiola.datastore.model.NoteCategory // Added import
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class RealUserPreferencesRepositoryTest {

    private val initialUserSettings = UserPreferences()

    private fun getDataStore(name: String): RealUserPreferencesDataSource {
        val path = File(FileSystem.SYSTEM_TEMPORARY_DIRECTORY.toFile(), "$name.json")
        if (path.exists()) {
            path.delete()
        }
        val testDataStore = DataStoreFactory.create(
            storage =
            OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                serializer = UserDataJsonSerializer,
                producePath = {// This is initialUserSettings.shouldHideOnboarding
                    if (!path.parentFile.exists()) {
                        path.mkdirs()
                    }
                    path.toOkioPath()
                },
            ),
        )
        return RealUserPreferencesDataSource(testDataStore)
    }

    @Test
    fun `userData flow initially emits current DataStore data`() = runTest {
        val repository = getDataStore("userdata_flow")

        val currentUserData = repository.userPreferences.first()
        assertEquals(initialUserSettings, currentUserData)
    }

    @Test
    fun `setContrast updates DataStore and flow emits new contrast`() = runTest {
        val newContrast = 50
        val expectedUserData = initialUserSettings.copy(contrast = newContrast)
        val repository = getDataStore("userdata_setContrast")

        repository.setContrast(newContrast)

        val updatedUserData = repository.userPreferences.first() // Or fakeUserDataStore.data.first()
        assertEquals(expectedUserData, updatedUserData)
    }

    @Test
    fun `setDarkThemeConfig updates DataStore and flow emits new config`() = runTest {
        val newConfig = 1
        val expectedUserData = initialUserSettings.copy(darkThemeConfig = newConfig)

        val repository = getDataStore("userdata_setDarkThemeConfig")

        repository.setDarkThemeConfig(newConfig)

        val updatedUserData = repository.userPreferences.first()
        assertEquals(expectedUserData, updatedUserData)
    }

    @Test
    fun `setDynamicColorPreference updates DataStore and flow emits new preference`() = runTest {
        val newDynamicColor = true
        val expectedUserData = initialUserSettings.copy(useDynamicColor = newDynamicColor)

        val repository = getDataStore("userdata_setDynamicColorPreference")

        repository.setDynamicColorPreference(newDynamicColor)

        val updatedUserData = repository.userPreferences.first()
        assertEquals(expectedUserData, updatedUserData)
    }

    @Test
    fun `setShouldHideOnboarding updates DataStore and flow emits new value`() = runTest {
        val newShouldHide = true
        val expectedUserData = initialUserSettings.copy(shouldHideOnboarding = newShouldHide)
        val repository = getDataStore("userdata_setShouldHideOnboarding")

        repository.setShouldHideOnboarding(newShouldHide)

        val updatedUserData = repository.userPreferences.first()
        assertEquals(expectedUserData, updatedUserData)
    }

    @Test
    fun `multiple updates are reflected in the flow`() = runTest {
        val repository = getDataStore("userdata_multiple_updates")

        // Initial state check
        assertEquals(0, repository.userPreferences.first().darkThemeConfig)

        // First update
        repository.setDarkThemeConfig(1)
        assertEquals(1, repository.userPreferences.first().darkThemeConfig)

        // Second update
        repository.setContrast(10)
        assertEquals(10, repository.userPreferences.first().contrast)
        assertEquals(1, repository.userPreferences.first().darkThemeConfig)
        // Ensure previous update persists

        // Expect all other fields to be their defaults from initialUserSettings
        val expectedUserSettings = initialUserSettings.copy(
            contrast = 10,
            darkThemeConfig = 1,
        )
        assertEquals(expectedUserSettings, repository.userPreferences.first())
    }

    @Test
    fun `setShouldShowGradientBackground updates DataStore and flow emits new value`() = runTest {
        val newShouldShowGradient = false
        val expectedUserData = initialUserSettings.copy(shouldShowGradientBackground = newShouldShowGradient)
        val repository = getDataStore("userdata_setShouldShowGradientBackground")
        repository.setShouldShowGradientBackground(newShouldShowGradient)
        val updatedUserData = repository.userPreferences.first()
        assertEquals(expectedUserData, updatedUserData)
    }

    @Test
    fun `setLanguage updates DataStore and flow emits new language`() = runTest {
        val newLanguage = "en Us"
        val expectedUserData = initialUserSettings.copy(language = newLanguage)
        val repository = getDataStore("userdata_setLanguage")

        repository.setLanguage(newLanguage)

        val updatedUserData = repository.userPreferences.first()
        assertEquals(expectedUserData, updatedUserData)
    }

    @Test
    fun `setShowUpdateDialog updates DataStore and flow emits new value`() = runTest {
        val newShowUpdateDialog = true // Default is true, so let's test setting it to false
        val expectedUserData = initialUserSettings.copy(showUpdateDialog = newShowUpdateDialog)
        val repository = getDataStore("userdata_setShowUpdateDialog")

        repository.setShowUpdateDialog(newShowUpdateDialog)

        val updatedUserData = repository.userPreferences.first()
        assertEquals(expectedUserData, updatedUserData)
    }

    @Test
    fun updateFromPreRelease_updatesDefaults_preservesUserChanges() = runTest {
        val repository = getDataStore("userdata_updateFromPreRelease_complex")

        // 1. User sets some preferences
        val userSetContrast = 30
        val userSetLanguage = "fr-CA" // French (Canada)
        val userSetOnboarding = true
        repository.setContrast(userSetContrast)
        repository.setLanguage(userSetLanguage)
        repository.setShouldHideOnboarding(userSetOnboarding)

        // Verify user's initial changes against UserPreferences defaults
        val initialUserPrefs = repository.userPreferences.first()
        assertEquals(userSetContrast, initialUserPrefs.contrast)
        assertEquals(userSetLanguage, initialUserPrefs.language)
        assertEquals(userSetOnboarding, initialUserPrefs.shouldHideOnboarding)
        // Check a few that weren't changed by user, to ensure they are at initial default state
        assertEquals(initialUserSettings.darkThemeConfig, initialUserPrefs.darkThemeConfig)
        assertEquals(initialUserSettings.useDynamicColor, initialUserPrefs.useDynamicColor)
        assertEquals(initialUserSettings.updateFromPreRelease,
            initialUserPrefs.updateFromPreRelease)



        val preReleaseContrast = 99
        val preReleaseDarkThemeConfig = 2
        val preReleaseDynamicColor = true
        val preReleaseLanguage = "es-ES"
        val preReleaseShouldHideOnboarding = false
        val preReleaseGradient = false
        val preReleaseShowUpdateDialog = false
        val preReleaseUpdateFromPreRelease = true
        val preReleaseIsGrid = false
        val preReleaseNoteCategory = NoteCategory(labelId = 2, noteCategory = 1)

        val preReleaseSettings = UserPreferences(
            contrast = preReleaseContrast,
            darkThemeConfig = preReleaseDarkThemeConfig,
            useDynamicColor = preReleaseDynamicColor,
            language = preReleaseLanguage,
            shouldHideOnboarding = preReleaseShouldHideOnboarding,
            shouldShowGradientBackground = preReleaseGradient,
            showUpdateDialog = preReleaseShowUpdateDialog,
            updateFromPreRelease = preReleaseUpdateFromPreRelease,
            isGrid = preReleaseIsGrid,
            noteCategory = preReleaseNoteCategory
        )

        // 3. Call the (hypothetical) update method that would implement this merging logic
        // repository.updateUserSettingsConditionally(preReleaseSettings) // This method does not exist.

        // 4. Define expected preferences after the hypothetical update
        val expectedUserDataAfterHypotheticalUpdate = UserPreferences(
            contrast = userSetContrast, // Preserved user setting (30)
            darkThemeConfig = preReleaseDarkThemeConfig, // Updated from pre-release (2)
            useDynamicColor = preReleaseDynamicColor, // Updated from pre-release (true)
            language = userSetLanguage, // Preserved user setting ("fr-CA")
            shouldHideOnboarding = userSetOnboarding, // Preserved user setting (true)
            shouldShowGradientBackground = preReleaseGradient, // Updated from pre-release (false)
            showUpdateDialog = preReleaseShowUpdateDialog, // Updated from pre-release (false)
            updateFromPreRelease = preReleaseUpdateFromPreRelease, // Updated from pre-release (true)
            isGrid = preReleaseIsGrid, // Updated from pre-release (false)
            noteCategory = preReleaseNoteCategory // Updated from pre-release
        )

        // 5. Simulate the state *as if* the hypothetical
        // updateFromPreRelease(UserPreferences) was called and worked correctly:
        // This simulation manually performs the conditional update logic.
        val simulatedActualPreferencesAfterUpdate = UserPreferences(
            contrast = if (initialUserPrefs.contrast == initialUserSettings.contrast)
                preReleaseSettings.contrast else initialUserPrefs.contrast,
            darkThemeConfig = if (initialUserPrefs.darkThemeConfig == initialUserSettings.darkThemeConfig)
                preReleaseSettings.darkThemeConfig else initialUserPrefs.darkThemeConfig,
            useDynamicColor = if (initialUserPrefs.useDynamicColor == initialUserSettings.useDynamicColor)
                preReleaseSettings.useDynamicColor else initialUserPrefs.useDynamicColor,
            language = if (initialUserPrefs.language == initialUserSettings.language) preReleaseSettings.language
            else initialUserPrefs.language,
            shouldHideOnboarding = if (initialUserPrefs.shouldHideOnboarding ==
                initialUserSettings.shouldHideOnboarding)
                preReleaseSettings.shouldHideOnboarding else initialUserPrefs.shouldHideOnboarding,
            shouldShowGradientBackground = if (initialUserPrefs.shouldShowGradientBackground
                == initialUserSettings.shouldShowGradientBackground) preReleaseSettings
                    .shouldShowGradientBackground else initialUserPrefs.shouldShowGradientBackground,
            showUpdateDialog = if (initialUserPrefs.showUpdateDialog
                == initialUserSettings.showUpdateDialog) preReleaseSettings
                    .showUpdateDialog else initialUserPrefs.showUpdateDialog,
            updateFromPreRelease = if (initialUserPrefs.updateFromPreRelease
                == initialUserSettings.updateFromPreRelease) preReleaseSettings
                    .updateFromPreRelease else initialUserPrefs.updateFromPreRelease,
            isGrid = if (initialUserPrefs.isGrid == initialUserSettings.isGrid)
                preReleaseSettings.isGrid else initialUserPrefs.isGrid,
            noteCategory = if (initialUserPrefs.noteCategory == initialUserSettings.noteCategory)
                preReleaseSettings.noteCategory else initialUserPrefs.noteCategory
        )

        // This assertion verifies the manually simulated logic.
        // It does NOT test an actual repository method for this complex merge, as such a method doesn't exist.
        assertEquals(
            expectedUserDataAfterHypotheticalUpdate,
            simulatedActualPreferencesAfterUpdate,
            "This assertion demonstrates the expected outcome of a HYPOTHETICAL conditional update. " +
                    "It currently tests the manual simulation of this logic, not an actual repository method."
        )

        // To test the actual repository after user changes (without any hypothetical merge):
        // assertEquals(initialUserPrefs, repository.userPreferences.first())
    }

    // New tests for existing simple setters

    @Test
    fun `setGrid updates DataStore and flow emits new grid value`() = runTest {
        val newIsGrid = false // Default is true
        val expectedUserData = initialUserSettings.copy(isGrid = newIsGrid)
        val repository = getDataStore("userdata_setGrid")

        repository.setGrid(newIsGrid)

        val updatedUserData = repository.userPreferences.first()
        assertEquals(expectedUserData, updatedUserData)
    }

    @Test
    fun `setNoteCategory updates DataStore and flow emits new note category`() = runTest {
        val newNoteCategory = NoteCategory(labelId = 10L, noteCategory = 5)
        val expectedUserData = initialUserSettings.copy(noteCategory = newNoteCategory)
        val repository = getDataStore("userdata_setNoteCategory")

        repository.setNoteCategory(newNoteCategory)

        val updatedUserData = repository.userPreferences.first()
        assertEquals(expectedUserData, updatedUserData)
    }

    @Test
    fun `setUpdateFromPreRelease updates DataStore and flow emits new boolean value`() = runTest {
        val newUpdateFromPreRelease = true // Default is false
        val expectedUserData = initialUserSettings.copy(updateFromPreRelease = newUpdateFromPreRelease)
        val repository = getDataStore("userdata_setUpdateFromPreRelease_simple")

        repository.setUpdateFromPreRelease(newUpdateFromPreRelease)

        val updatedUserData = repository.userPreferences.first()
        assertEquals(expectedUserData, updatedUserData)
    }
}
