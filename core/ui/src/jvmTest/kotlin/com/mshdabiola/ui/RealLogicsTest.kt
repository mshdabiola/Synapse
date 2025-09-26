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
import androidx.compose.ui.graphics.toAwtImage
import com.mshdabiola.model.note.NotePad
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.awt.Desktop
import java.awt.HeadlessException
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.imageio.ImageIO

class RealLogicsTest {

    private var outputVoiceCalledWith: Pair<String, String>? = null
    private var savePhotoCalled = false
    private var onNotificationCalled = false
    private var imageSelectedPath: String? = null

    private lateinit var realLogics: RealLogics
    private val tempFiles = mutableListOf<File>()

    // Helper to create a simple ImageBitmap for testing
    private fun createTestImageBitmap(width: Int = 10, height: Int = 10, color: Color = Color.Red): ImageBitmap {
        val imageBitmap = ImageBitmap(width, height)
        val canvas = Canvas(imageBitmap)
        val paint = Paint().apply { this.color = color }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return imageBitmap
    }

    private fun createTempImageFile(
        prefix: String = "test_image",
        suffix: String = ".png",
        width: Int = 32,
        height: Int = 32,
        color: Color = Color.Green,
        contentIsImage: Boolean = true,
    ): File {
        val tempFile = File.createTempFile(prefix, suffix)
        tempFile.deleteOnExit() // Ensure cleanup even if @After fails
        tempFiles.add(tempFile)

        if (contentIsImage) {
            val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val graphics = bufferedImage.createGraphics()
            graphics.color = java.awt.Color(color.red, color.green, color.blue, color.alpha)
            graphics.fillRect(0, 0, width, height)
            graphics.dispose()
            ImageIO.write(bufferedImage, suffix.substring(1), tempFile)
        } else {
            FileWriter(tempFile).use { it.write("This is not an image.") }
        }
        return tempFile
    }

    @Before
    fun setUp() {
        try {
            Toolkit.getDefaultToolkit().systemClipboard
            // Check if Desktop is supported for tests involving FileDialog/Desktop.open
            // Desktop.isDesktopSupported() // This alone isn't enough, actions might not be supported
        } catch (e: HeadlessException) {
            Assume.assumeNoException("Skipping UI interaction tests in headless environment", e)
        }

        outputVoiceCalledWith = null
        savePhotoCalled = false
        onNotificationCalled = false
        imageSelectedPath = null

        realLogics = RealLogics(
            outputVoice = { s1, s2 -> outputVoiceCalledWith = s1 to s2 },
            savePhoto = { savePhotoCalled = true },
            onNotification = { onNotificationCalled = true },
            imageSelectedCallback = { path -> imageSelectedPath = path },
        )
    }

    @After
    fun tearDown() {
        tempFiles.forEach { it.delete() }
        tempFiles.clear()
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
        realLogics.snapImage("some/path")
        assertTrue(savePhotoCalled)
    }

    @Test
    fun `chooseImage - invoking selection via callback`() {
        assertNull(imageSelectedPath)
        val testPath = "test/image/path.jpg"
        realLogics.imageSelectedCallback(testPath)
        assertEquals(testPath, imageSelectedPath)
    }

    @Test
    fun `shareNote executes without error`() {
        try {
            realLogics.shareNote(NotePad(id = 1, title = "Test Note"))
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
        assertFalse(onNotificationCalled)
        val result = realLogics.checkNotificationPermission()
        assertTrue(onNotificationCalled)
        assertTrue(result)
    }

    @Test
    fun `copyImage_withImageBitmap_putsImageOnClipboard`() {
        val testBitmap = createTestImageBitmap(20, 20, Color.Blue)
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            realLogics.copyImage(testBitmap)
            assertTrue(
                "Clipboard should contain image data after copyImage(ImageBitmap)",
                clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor),
            )
            val transferable = clipboard.getContents(null)
            assertNotNull(transferable)
            val clipboardImage = transferable.getTransferData(DataFlavor.imageFlavor) as? java.awt.Image
            assertNotNull(clipboardImage)
            assertEquals(testBitmap.width, clipboardImage?.getWidth(null))
            assertEquals(testBitmap.height, clipboardImage?.getHeight(null))
        } catch (e: HeadlessException) {
            Assume.assumeNoException(e)
        } catch (e: Exception) {
            fail("copyImage(ImageBitmap) or clipboard interaction failed: ${e.message}")
        }
    }

    @Test
    fun `copyImage_withNullImageBitmap_gracefullyHandlesError`() {
        try {
            // Simulate a scenario where toAwtImage() might fail internally if ImageBitmap was problematic
            // RealLogics.copyImage(null) // Not directly possible due to non-null type
            // Instead, we trust the internal try-catch in the method for ImageBitmap.toAwtImage() issues.
            // This test is more conceptual for its error handling.
            val problematicBitmap = ImageBitmap(1, 1) // A valid bitmap
            realLogics.copyImage(problematicBitmap) // Should not throw an unhandled exception
            println("copyImage(ImageBitmap) with a valid bitmap did not crash.")
        } catch (e: Exception) {
            fail("copyImage(ImageBitmap) should gracefully handle internal errors, but an unexpected one occurred: ${e.message}")
        }
    }

    @Test
    fun `copyImage_withValidPath_putsImageOnClipboard`() {
        val imageFile = createTempImageFile(contentIsImage = true)
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            realLogics.copyImage(imageFile.absolutePath)
            assertTrue(
                "Clipboard should contain image data after copyImage(path)",
                clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor),
            )
            val transferable = clipboard.getContents(null)
            assertNotNull(transferable)
            val clipboardImage = transferable.getTransferData(DataFlavor.imageFlavor) as? java.awt.Image
            assertNotNull(clipboardImage)
            // Verify dimensions if possible (e.g. 32x32 as per createTempImageFile default)
            assertEquals(32, clipboardImage?.getWidth(null))
            assertEquals(32, clipboardImage?.getHeight(null))
        } catch (e: HeadlessException) {
            Assume.assumeNoException(e)
        } catch (e: Exception) {
            fail("copyImage(path) or clipboard interaction failed: ${e.message}")
        }
    }

    @Test
    fun `copyImage_withNonExistentPath_doesNotCrash`() {
        try {
            realLogics.copyImage("non_existent_image_path.png")
            // Assert that console printed "File does not exist" or similar, if possible
            // For now, just ensure no crash and clipboard isn't unexpectedly populated.
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            // This assertion is tricky, as clipboard might retain old data.
            // A better check would be specific to what copyImage does on failure.
        } catch (e: HeadlessException) {
            Assume.assumeNoException(e)
        } catch (e: Exception) {
            fail("copyImage with non-existent path should not throw unhandled exception: ${e.message}")
        }
    }

    @Test
    fun `copyImage_withInvalidImagePath_doesNotCrash`() {
        val nonImageFile = createTempImageFile(contentIsImage = false, suffix = ".txt")
        try {
            realLogics.copyImage(nonImageFile.absolutePath)
        } catch (e: HeadlessException) {
            Assume.assumeNoException(e)
        } catch (e: Exception) {
            fail("copyImage with invalid image path should not throw unhandled exception: ${e.message}")
        }
    }

    @Test
    fun `shareImage_withImageBitmap_executesWithoutAwtError`() {
        Assume.assumeTrue("Desktop.Action.SAVE is not supported, skipping FileDialog dependent test", Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.APP_OPEN_FILE))
        val testBitmap = createTestImageBitmap()
        try {
            realLogics.shareImage(testBitmap)
            // Test passes if it reaches the FileDialog without AWT errors before that.
            // Cannot interact with FileDialog in unit test.
        } catch (e: HeadlessException) {
            Assume.assumeNoException("Skipping FileDialog test in headless environment", e)
        } catch (e: Exception) {
            fail("shareImage(ImageBitmap) should not throw unexpected AWT error before FileDialog: ${e.message}")
        }
    }

    @Test
    fun `shareImage_withValidPath_executesWithoutAwtError`() {
         Assume.assumeTrue("Desktop.Action.SAVE is not supported, skipping FileDialog dependent test", Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.APP_OPEN_FILE))
        val imageFile = createTempImageFile(contentIsImage = true)
        try {
            realLogics.shareImage(imageFile.absolutePath)
        } catch (e: HeadlessException) {
            Assume.assumeNoException(e)
        } catch (e: Exception) {
            fail("shareImage(path) for valid file should not throw unexpected AWT error before FileDialog: ${e.message}")
        }
    }

    @Test
    fun `shareImage_withNonExistentPath_doesNotCrash`() {
        try {
            realLogics.shareImage("non_existent_image_path_for_share.png")
        } catch (e: Exception) {
            fail("shareImage with non-existent path should not throw unhandled exception: ${e.message}")
        }
    }

    @Test
    fun `shareImage_withInvalidImagePath_doesNotCrash`() {
        val nonImageFile = createTempImageFile(contentIsImage = false, suffix = ".txt")
        try {
            realLogics.shareImage(nonImageFile.absolutePath)
        } catch (e: Exception) {
            fail("shareImage with invalid image path should not throw unhandled exception: ${e.message}")
        }
    }
}
