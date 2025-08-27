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

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class RealContentManager(
    baseStoragePath: String = System.getProperty("user.home") + File.separator + ".SynapseApp" + File.separator +
        "files",
) : ContentManager {

    private val photoDir: String
    private val voiceDir: String
    private val drawingDir: String

    init {
        val baseDirFile = File(baseStoragePath)
        if (!baseDirFile.exists()) {
            baseDirFile.mkdirs()
        }

        photoDir = baseStoragePath + File.separator + "photo"
        voiceDir = baseStoragePath + File.separator + "voice"
        drawingDir = baseStoragePath + File.separator + "drawingfile"

        createDirectoryIfNotExists(photoDir)
        createDirectoryIfNotExists(voiceDir)
        createDirectoryIfNotExists(drawingDir)
    }

    private fun createDirectoryIfNotExists(path: String) {
        val dir = File(path)
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("Warning: Could not create directory: $path")
            }
        }
    }

    override fun saveImage(uri: String): Long { // On JVM, uri is expected to be a file path
        return try {
            val currentTime = System.currentTimeMillis()
            val inputFile = File(uri)
            if (!inputFile.exists() || !inputFile.isFile) {
                System.err.println("Error in saveImage: Input file is not a valid file or does not exist: $uri")
                return -1L
            }
            val outputFile = File(photoDir, "Image_$currentTime.jpg")

            FileInputStream(inputFile).use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            currentTime
        } catch (e: IOException) {
            e.printStackTrace()
            -1L
        } catch (e: SecurityException) {
            e.printStackTrace()
            -1L
        }
    }

    override fun saveVoice(uri: String): Long { // On JVM, uri is expected to be a file path
        return try {
            val currentTime = System.currentTimeMillis()
            val inputFile = File(uri)
            if (!inputFile.exists() || !inputFile.isFile) {
                System.err.println("Error in saveVoice: Input file is not a valid file or does not exist: $uri")
                return -1L
            }
            val outputFile = File(voiceDir, "Voice_$currentTime.amr")

            FileInputStream(inputFile).use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            currentTime
        } catch (e: IOException) {
            e.printStackTrace()
            -1L
        } catch (e: SecurityException) {
            e.printStackTrace()
            -1L
        }
    }

    override fun pictureUri(): String {
        // For JVM, this returns a destination file path for a potential new picture.
        // It's not a "content URI" in the Android sense.
        val currentTime = System.currentTimeMillis()
        createDirectoryIfNotExists(photoDir) // Ensure dir exists
        val newImageFile = File(photoDir, "Image_$currentTime.jpg")
        return newImageFile.absolutePath
    }

    override fun getImagePath(data: Long): String {
        return File(photoDir, "Image_$data.jpg").absolutePath
    }

    override fun getVoicePath(data: Long): String {
        return File(voiceDir, "Voice_$data.amr").absolutePath
    }

    override fun dataFile(drawingId: Long): String {
        createDirectoryIfNotExists(drawingDir) // Ensure dir exists
        return File(drawingDir, "data_$drawingId.json").absolutePath
    }

    override fun getAudioLength(path: String): Long {
        System.err.println("Warning: getAudioLength called on JVM for path: $path. Not implemented, returning 0L.")
        // JVM implementation for audio length would require a dedicated library (e.g., JAudioTagger, Apache Tika, Java Sound API).
        return 0L
    }

    override fun imageToText(path: String): String {
        System.err.println("Warning: JvmPlaceholderImageToText.toText called for path: $path. Returning empty string.")
        return ""
    }
}
