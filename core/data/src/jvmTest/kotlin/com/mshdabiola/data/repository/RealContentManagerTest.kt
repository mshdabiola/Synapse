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
package com.mshdabiola.data.repository

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class RealContentManagerTest {

    private lateinit var contentManager: RealContentManager
    private lateinit var tempTestDir: File
    private lateinit var baseStoragePath: String

    @Before
    fun setUp() {
        // Create a temporary directory for test files
        tempTestDir = File(System.getProperty("java.io.tmpdir"), "RealContentManagerTest_${System.currentTimeMillis()}")
        if (!tempTestDir.mkdirs()) {
            throw Exception("Could not create temp test directory: ${tempTestDir.absolutePath}")
        }
        baseStoragePath = tempTestDir.absolutePath + File.separator + ".SynapseAppTest" + File.separator + "files"

        contentManager = RealContentManager()
    }

    @After
    fun tearDown() {
        // Clean up the temporary directory
        tempTestDir.deleteRecursively()
    }

    private fun createDummyFile(name: String, content: String = "dummy content"): File {
        val file = File(tempTestDir, name)
        file.writeText(content)
        return file
    }

    @Test
    fun `constructor creates base directories`() {
        assertTrue("Photo directory should be created", File(baseStoragePath, "photo").exists())
        assertTrue("Voice directory should be created", File(baseStoragePath, "voice").exists())
        assertTrue("Drawing directory should be created", File(baseStoragePath, "drawingfile").exists())
    }

    @Test
    fun `saveImage copies file and returns timestamp`() {
        val dummyImageFile = createDummyFile("source_image.jpg", "image data")
        val startTime = System.currentTimeMillis()
        // Allow for slight clock differences
        Thread.sleep(10)

        val imageId = contentManager.saveImage(dummyImageFile.absolutePath)
        Thread.sleep(10)
        val endTime = System.currentTimeMillis()

        assertNotEquals("Image ID should not be -1 on success", -1L, imageId)
        assertTrue("Image ID should be a timestamp around now", imageId >= startTime && imageId <= endTime)
        val expectedSavedFile = File(File(baseStoragePath, "photo"), "Image_$imageId.jpg")
        assertTrue("Saved image file should exist", expectedSavedFile.exists())
        assertEquals("Saved image content should match source", "image data", expectedSavedFile.readText())
    }

    @Test
    fun `saveImage with non existent source returns minus 1`() {
        val imageId = contentManager.saveImage("non_existent_file.jpg")
        assertEquals(-1L, imageId)
    }

    @Test
    fun `saveVoice copies file and returns timestamp`() {
        val dummyVoiceFile = createDummyFile("source_voice.amr", "voice data")
        val startTime = System.currentTimeMillis()
        Thread.sleep(10)

        val voiceId = contentManager.saveVoice(dummyVoiceFile.absolutePath)
        Thread.sleep(10)
        val endTime = System.currentTimeMillis()

        assertNotEquals("Voice ID should not be -1 on success", -1L, voiceId)
        assertTrue("Voice ID should be a timestamp around now", voiceId >= startTime && voiceId <= endTime)
        val expectedSavedFile = File(File(baseStoragePath, "voice"), "Voice_$voiceId.amr")
        assertTrue("Saved voice file should exist", expectedSavedFile.exists())
        assertEquals("Saved voice content should match source", "voice data", expectedSavedFile.readText())
    }

    @Test
    fun `saveVoice with non existent source returns minus 1`() {
        val voiceId = contentManager.saveVoice("non_existent_file.amr")
        assertEquals(-1L, voiceId)
    }

    @Test
    fun `pictureUri returns valid path structure`() {
        val path = contentManager.pictureUri()
        assertTrue("Picture URI should be an absolute path", File(path).isAbsolute)
        assertTrue(
            "Picture URI should be in the photo directory",
            path.startsWith(File(baseStoragePath, "photo").absolutePath),
        )
        assertTrue("Picture URI should end with .jpg", path.endsWith(".jpg"))
    }

    @Test
    fun `getImagePath returns correct path`() {
        val imageId = 12345L
        val expectedPath = File(File(baseStoragePath, "photo"), "Image_$imageId.jpg").absolutePath
        assertEquals(expectedPath, contentManager.getImagePath(imageId))
    }

    @Test
    fun `getVoicePath returns correct path`() {
        val voiceId = 67890L
        val expectedPath = File(File(baseStoragePath, "voice"), "Voice_$voiceId.amr").absolutePath
        assertEquals(expectedPath, contentManager.getVoicePath(voiceId))
    }

    @Test
    fun `dataFile returns correct path`() {
        val drawingId = 11223L
        val expectedPath = File(File(baseStoragePath, "drawingfile"), "data_$drawingId.json").absolutePath
        assertEquals(expectedPath, contentManager.dataFile(drawingId))
    }

    @Test
    fun `getAudioLength returns 0L`() {
        // This test just confirms the placeholder behavior for JVM
        assertEquals(0L, contentManager.getAudioLength("some/path/audio.mp3"))
    }
}
