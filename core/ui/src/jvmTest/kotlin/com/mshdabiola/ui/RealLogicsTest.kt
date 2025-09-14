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
package com.mshdabiola.ui

import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerLauncher
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mshdabiola.model.note.NotePad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class RealLogicsTest {

    private var outputVoiceCalledWith: Pair<String, String>? = null
    private var saveImageCalledWith: String? = null
    private var savePhotoCalled = false
    private var onNotificationCalled = false

    private lateinit var realLogics: RealLogics

    @Before
    fun setUp() {
        outputVoiceCalledWith = null
        saveImageCalledWith = null
        savePhotoCalled = false
        onNotificationCalled = false

        realLogics = RealLogics(
            outputVoice = { s1, s2 -> outputVoiceCalledWith = s1 to s2 },
            pickerLauncher = FilePickerLauncher(
                type = FilePickerFileType.Image,
                selectionMode = FilePickerSelectionMode.Single,
                onLaunch = { saveImageCalledWith = "test/image/path.jpg" },
            ),
            savePhoto = { savePhotoCalled = true },
            onNotification = { onNotificationCalled = true },
        )
    }
//
//    @Test
//    fun `openUrl with valid URL does not crash`() {
//        // This test mainly ensures that URI creation and Desktop.getDesktop() don't throw an unexpected error.
//        // Verifying actual browser opening is beyond a simple unit test.
//        // We rely on the fact that if Desktop is not supported, it will print to console.
//        try {
//            realLogics.openUrl("https://www.example.com")
//            // No assertion needed if it doesn't crash
//        } catch (e: Exception) {
//            fail("openUrl with valid URL should not throw an exception: ${e.message}")
//        }
//    }
//
//    @Test
//    fun `openUrl with invalid URL is handled gracefully`() {
//        // Example of an invalid URI that would throw URISyntaxException
//        try {
//            realLogics.openUrl("htp://www.example.com")
//            // The method catches Exception and prints stack trace, so no crash is expected.
//        } catch (e: Exception) {
//            fail("openUrl with invalid URL should be handled by the catch block: ${e.message}")
//        }
//    }
//
//    @Test
//    fun `openEmail with valid parameters does not crash`() {
//        // Similar to openUrl, this checks URI creation, encoding, and Desktop interaction.
//        try {
//            realLogics.openEmail("test@example.com", "Test Subject", "Test Body")
//            // No assertion needed if it doesn't crash
//        } catch (e: Exception) {
//            fail("openEmail with valid parameters should not throw an exception: ${e.message}")
//        }
//    }
//
//    @Test
//    fun `openEmail encodes subject and body`() {
//        // We can't directly check the URI passed to Desktop.mail,
//        // but we can infer the encoding by checking if specific characters would cause issues if not encoded.
//        // This is an indirect way to test the URLEncoder part.
//         try {
//            realLogics.openEmail("test@example.com", "Subject with spaces & symbols!", "Body with / and ?")
//            // If it reaches here without URI syntax issues due to unencoded characters, it's a good sign.
//        } catch (e: Exception) {
//            fail("openEmail with special characters in subject/body should not throw an exception: ${e.message}")
//        }
//    }
//

    @Test
    fun `isVoiceAvailable returns false`() {
        assertFalse(realLogics.isVoiceAvailable())
    }

    @Test
    fun `openVoice calls outputVoice lambda`() {
        assertNull(outputVoiceCalledWith)
        realLogics.openVoice()
        assertEquals("" to "", outputVoiceCalledWith)
    }

    @Test
    fun `snapImage calls savePhoto lambda`() {
        assertFalse(savePhotoCalled)
        realLogics.snapImage("some/path") // Path is not used by savePhoto in RealLogics
        assertTrue(savePhotoCalled)
    }

    @Test
    fun `chooseImage calls saveImage lambda with path`() {
        assertNull(saveImageCalledWith)
        val testPath = "test/image/path.jpg"
        realLogics.chooseImage()
        assertEquals(testPath, saveImageCalledWith)
    }

    @Test
    fun `shareNote executes without error`() {
        try {
            realLogics.shareNote(NotePad(id = 1, title = "Test Note"))
            // No assertion needed, just checking for no crash
        } catch (e: Exception) {
            fail("shareNote should execute without error: ${e.message}")
        }
    }

    @Test
    fun `askForNotificationPermission executes without error`() {
        try {
            realLogics.askForNotificationPermission()
            // No assertion needed, just checking for no crash
        } catch (e: Exception) {
            fail("askForNotificationPermission should execute without error: ${e.message}")
        }
    }

    @Test
    fun `checkNotificationPermission calls onNotification lambda and returns false`() {
        assertFalse(onNotificationCalled)
        val result = realLogics.checkNotificationPermission()
        assertTrue(onNotificationCalled)
        assertFalse(result)
    }
}
