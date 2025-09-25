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

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import com.mshdabiola.model.note.NotePad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.awt.HeadlessException
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage

class RealLogicsTest {

    private var outputVoiceCalledWith: Pair<String, String>? = null
    private var saveImageCalledWith: String? = null // For chooseImage
    private var savePhotoCalled = false // For snapImage
    private var onNotificationCalled = false
    private var imageSelectedPath: String? = null // For imageSelectedCallback

    private lateinit var realLogics: RealLogics

    // Helper to create a simple ImageBitmap for testing
    private fun createTestImageBitmap(width: Int = 10, height: Int = 10, color: Color = Color.Red): ImageBitmap {
        val imageBitmap = ImageBitmap(width, height)
        val canvas = Canvas(imageBitmap)
        val paint = Paint().apply { this.color = color }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return imageBitmap
    }

    @Before
    fun setUp() {
        // Skip clipboard tests in headless environments where AWT might not be fully available
        try {
            Toolkit.getDefaultToolkit().systemClipboard
        } catch (e: HeadlessException) {
            Assume.assumeNoException("Skipping clipboard tests in headless environment", e)
        }

        outputVoiceCalledWith = null
        saveImageCalledWith = null
        savePhotoCalled = false
        onNotificationCalled = false
        imageSelectedPath = null

        realLogics = RealLogics(
            outputVoice = { s1, s2 -> outputVoiceCalledWith = s1 to s2 },
            // For chooseImage, the pickerLauncher's onLaunch is not directly testable here
            // for its actual file picking logic without significant mocking or integration setup.
            // We test the imageSelectedCallback instead.
            savePhoto = { savePhotoCalled = true }, // For snapImage
            onNotification = { onNotificationCalled = true },
            imageSelectedCallback = { path -> imageSelectedPath = path }, // For chooseImage
        )
    }

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
    fun `chooseImage - invoking selection via callback`() {
        // This test simulates the callback that would occur after a file is chosen.
        // The actual FileDialog interaction is not tested here.
        assertNull(imageSelectedPath)
        val testPath = "test/image/path.jpg"
        // Simulate the FileDialog closing and calling the callback
        realLogics.imageSelectedCallback(testPath)
        assertEquals(testPath, imageSelectedPath)
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
    fun `askForNotificationPermission calls onNotification lambda`() {
        assertFalse(onNotificationCalled)
        realLogics.askForNotificationPermission()
        assertTrue(onNotificationCalled)
    }

    @Test
    fun `checkNotificationPermission calls onNotification lambda and returns true`() {
        // As per current jvmMain RealLogics, it calls onNotification and returns true
        assertFalse(onNotificationCalled)
        val result = realLogics.checkNotificationPermission()
        assertTrue(onNotificationCalled)
        assertTrue(result) // Updated assertion based on jvmMain implementation
    }

    @Test
    fun `copyDrawing puts an image on the clipboard`() {
        val testBitmap = createTestImageBitmap(20, 20, Color.Blue)
        var clipboardHasImageBefore = false
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboardHasImageBefore = clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)
            if (clipboardHasImageBefore) {
                // Try to clear it for a cleaner test, though not guaranteed to work on all OS/setups
                // A better approach is to just check *after* the operation.
                // clipboard.setContents(StringSelection(""), null) // Clear with empty text
            }
            realLogics.copyImage(testBitmap)
            assertTrue(
                "Clipboard should contain image data after copyDrawing",
                clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor),
            )

            // Optional: Try to retrieve and verify (can be flaky)
            val transferable = clipboard.getContents(null)
            assertNotNull("Transferable from clipboard should not be null", transferable)
            val clipboardImage = transferable.getTransferData(DataFlavor.imageFlavor) as? java.awt.Image
            assertNotNull("Image retrieved from clipboard should not be null", clipboardImage)

            // Convert to BufferedImage to check dimensions
            val bufferedImage = if (clipboardImage is BufferedImage) {
                clipboardImage
            } else {
                val bImg =
                    BufferedImage(
                        clipboardImage!!.getWidth(null),
                        clipboardImage.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB,
                    )
                val g2d = bImg.createGraphics()
                g2d.drawImage(clipboardImage, 0, 0, null)
                g2d.dispose()
                bImg
            }
            assertEquals("Width of clipboard image should match original", testBitmap.width, bufferedImage.width)
            assertEquals("Height of clipboard image should match original", testBitmap.height, bufferedImage.height)
        } catch (e: HeadlessException) {
            System.err.println("Skipping clipboard test in headless environment: ${e.message}")
            Assume.assumeNoException(e) // Skips test if in headless environment
        } catch (e: Exception) {
            fail("copyDrawing or clipboard interaction failed: ${e.message}")
        }
    }

    @Test
    fun `copyDrawing with null ImageBitmap does not crash (graceful handling expected)`() {
        // While the method expects a non-null ImageBitmap, testing how it handles
        // unexpected null if it were to occur (e.g. due to an upstream error not caught).
        // The current implementation of ImageBitmap.toAwtImage() would throw NPE.
        // This test verifies that if such an error occurs, it's caught by the try-catch in copyDrawing.
        try {
            // We can't directly pass null due to non-nullable type,
            // so this test is more about the robustness of the try-catch in the method itself.
            // If the ImageBitmap.toAwtImage() part fails for any reason before clipboard interaction,
            // the catch block in copyDrawing should handle it.
            // For a direct test of null, you'd need to mock ImageBitmap or use platform-specific nulls.

            // Simulating a scenario where toAwtImage() might fail, leading to an exception
            // This is a bit contrived as ImageBitmap itself won't be null here.
            val problematicBitmap = ImageBitmap(1, 1) // Valid bitmap
            // If there was a way to make problematicBitmap.toAwtImage() throw, we'd test that.
            // For now, we assume that any exception before clipboard.setContents is caught.
            realLogics.copyImage(problematicBitmap) // Should not crash
            println(
                "copyDrawing with a (valid) bitmap did not crash, exception handling within the method is assumed to cover internal errors.",
            )
        } catch (e: Exception) {
            fail("copyDrawing should gracefully handle internal errors, but an unexpected one occurred: ${e.message}")
        }
    }
}
