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
import java.io.IOException

class RealContentManagerTest {

    private lateinit var contentManager: RealContentManager
    private lateinit var tempTestDir: File
    private lateinit var baseStoragePathManagerWillUse: String // The path RealContentManager will internally target

    @Before
    fun setUp() {
        // Create a unique root temporary directory for this test class instance
        val uniqueTestRunId = "RealContentManagerTest_${System.currentTimeMillis()}"
        val systemTempDir = System.getProperty("java.io.tmpdir")
        tempTestDir = File(systemTempDir, uniqueTestRunId)
        if (!tempTestDir.mkdirs()) {
            throw IOException("Could not create root temporary test directory: ${tempTestDir.absolutePath}")
        }

        // This is the specific path that RealContentManager will create and use its subdirectories from
        baseStoragePathManagerWillUse = File(tempTestDir, ".SynapseAppInternalTest/files").absolutePath

        contentManager = RealContentManager(baseStoragePath = baseStoragePathManagerWillUse)
    }

    @After
    fun tearDown() {
        // Clean up the entire temporary directory created for the test class instance
        if (tempTestDir.exists() && !tempTestDir.deleteRecursively()) {
            System.err.println("Warning: Could not delete temporary test directory: ${tempTestDir.absolutePath}")
        }
    }

    private fun createDummyFileInTempTestDir(name: String, content: String = "dummy content"): File {
        val file = File(tempTestDir, name) // Create dummy file outside RealContentManager's structure
        file.parentFile?.mkdirs() // Ensure parent dir for this dummy file exists
        file.writeText(content)
        return file
    }

    @Test
    fun `constructor should create base directories if they do not exist`() {
        assertTrue(
            "Base storage directory should be created by constructor",
            File(baseStoragePathManagerWillUse).exists(),
        )
        assertTrue("Photo directory should be created", File(baseStoragePathManagerWillUse, "photo").exists())
        assertTrue("Voice directory should be created", File(baseStoragePathManagerWillUse, "voice").exists())
        assertTrue("Drawing directory should be created", File(baseStoragePathManagerWillUse, "drawingfile").exists())
    }

    @Test
    fun `saveImage with valid source should copy file to photo dir and return new path`() {
        val sourceImageFile = createDummyFileInTempTestDir("source_image.jpg", "image data")
        val savedImagePath = contentManager.saveImage(sourceImageFile.absolutePath)

        assertNotEquals("Returned path should not be empty on success", "", savedImagePath)
        val savedFile = File(savedImagePath)
        assertTrue("Saved image file should exist at returned path", savedFile.exists())
        assertEquals("Saved image content should match source content", "image data", savedFile.readText())
        assertTrue("Saved image should be in the photo directory", savedFile.parentFile.name == "photo")
        assertTrue(
            "Saved image name should start with Image_ and end with .jpg",
            savedFile.name.startsWith("Image_") && savedFile.name.endsWith(".jpg"),
        )
    }

    @Test
    fun `saveImage with non-existent source should return empty string`() {
        val nonExistentFilePath = File(tempTestDir, "non_existent_image.jpg").absolutePath
        val savedImagePath = contentManager.saveImage(nonExistentFilePath)
        assertEquals("Returned path should be empty for non-existent source", "", savedImagePath)
    }

    @Test
    fun `saveVoice with valid source should copy file to voice dir and return new path`() {
        val sourceVoiceFile = createDummyFileInTempTestDir("source_voice.amr", "voice data")
        val savedVoicePath = contentManager.saveVoice(sourceVoiceFile.absolutePath)

        assertNotEquals("Returned path should not be empty on success", "", savedVoicePath)
        val savedFile = File(savedVoicePath)
        assertTrue("Saved voice file should exist at returned path", savedFile.exists())
        assertEquals("Saved voice content should match source content", "voice data", savedFile.readText())
        assertTrue("Saved voice should be in the voice directory", savedFile.parentFile.name == "voice")
        assertTrue(
            "Saved voice name should start with Voice_ and end with .amr",
            savedFile.name.startsWith("Voice_") && savedFile.name.endsWith(".amr"),
        )
    }

    @Test
    fun `saveVoice with non-existent source should return empty string`() {
        val nonExistentFilePath = File(tempTestDir, "non_existent_voice.amr").absolutePath
        val savedVoicePath = contentManager.saveVoice(nonExistentFilePath)
        assertEquals("Returned path should be empty for non-existent source", "", savedVoicePath)
    }

    @Test
    fun `pictureUri should return a valid path structure within photo directory`() {
        val generatedPath = contentManager.pictureUri()
        val generatedFile = File(generatedPath)

        assertTrue("Picture URI should represent an absolute path", generatedFile.isAbsolute)
        val expectedPhotoDir = File(baseStoragePathManagerWillUse, "photo")
        assertTrue(
            "Picture URI should be located within the photo directory",
            generatedPath.startsWith(expectedPhotoDir.absolutePath + File.separator),
        )
        assertTrue(
            "Picture URI should start with Image_ and end with .jpg",
            generatedFile.name.startsWith("Image_") && generatedFile.name.endsWith(".jpg"),
        )
        // The file itself should not exist yet, only the path is generated.
        // assertTrue("File at picture URI should not exist yet", !generatedFile.exists())
    }

    @Test
    fun `dataFile should return correct path for a given drawing ID`() {
        val drawingId = 12345L
        val expectedDrawingDir = File(baseStoragePathManagerWillUse, "drawingfile")
        val expectedPath = File(expectedDrawingDir, "data_$drawingId.json").absolutePath
        val actualPath = contentManager.dataFile(drawingId)

        assertEquals("dataFile path should match expected structure", expectedPath, actualPath)
        assertTrue("Drawing directory should be created by dataFile if not exists", expectedDrawingDir.exists())
    }

    @Test
    fun `getAudioLength on JVM should return 0L`() {
        val dummyAudioPath = createDummyFileInTempTestDir("some_audio.mp3").absolutePath
        assertEquals("getAudioLength should return 0L on JVM", 0L, contentManager.getAudioLength(dummyAudioPath))
    }

    @Test
    fun `imageToText on JVM should return empty string`() {
        val dummyImagePath = createDummyFileInTempTestDir("some_image.jpg").absolutePath
        assertEquals("imageToText should return empty string on JVM", "", contentManager.imageToText(dummyImagePath))
    }
}
