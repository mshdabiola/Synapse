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
package com.mshdabiola.data.repository.doubles

import com.mshdabiola.data.repository.ContentManager

class TestContentManager : ContentManager {
    private val savedImages = mutableMapOf<Long, String>()
    private val savedVoices = mutableMapOf<Long, String>()
    private var nextImageId = 1L
    private var nextVoiceId = 1L

    val imageSaveInvocations = mutableMapOf<Long, String>()
    val voiceSaveInvocations = mutableMapOf<Long, String>()
    var pictureUriInvocationCount = 0
    val getImagePathInvocations = mutableListOf<Long>()
    val getVoicePathInvocations = mutableListOf<Long>()
    val dataFileInvocations = mutableListOf<Long>()
    val getAudioLengthInvocations = mutableListOf<String>()
    val imageToTextInvocations = mutableListOf<String>()


    override fun saveImage(uri: String): Long {
        val id = nextImageId++
        savedImages[id] = uri
        imageSaveInvocations[id] = uri
        return id
    }

    override fun saveVoice(uri: String): Long {
        val id = nextVoiceId++
        savedVoices[id] = uri
        voiceSaveInvocations[id] = uri
        return id
    }

    override fun pictureUri(): String {
        pictureUriInvocationCount++
        return "test_picture_uri_path/image_${System.currentTimeMillis()}.jpg"
    }

    override fun getImagePath(data: Long): String {
        getImagePathInvocations.add(data)
        return savedImages[data]?.let { "test_image_path_for_id_$data/$it" } ?: "unknown_image_path_for_id_$data"
    }

    override fun getVoicePath(data: Long): String {
        getVoicePathInvocations.add(data)
        return savedVoices[data]?.let { "test_voice_path_for_id_$data/$it" } ?: "unknown_voice_path_for_id_$data"
    }

    override fun dataFile(drawingId: Long): String {
        dataFileInvocations.add(drawingId)
        return "test_data_file_path_for_drawing_id_$drawingId.dat"
    }

    override fun getAudioLength(path: String): Long {
        getAudioLengthInvocations.add(path)
        // Return a predictable length, e.g., based on path hash or a fixed value
        return 1000L * (path.length % 5 + 1) // Example: 1 to 5 seconds
    }

    override fun imageToText(path: String): String {
        imageToTextInvocations.add(path)
        return "Extracted text from $path: Lorem ipsum."
    }

    fun clearInvocations() {
        savedImages.clear()
        savedVoices.clear()
        nextImageId = 1L
        nextVoiceId = 1L
        imageSaveInvocations.clear()
        voiceSaveInvocations.clear()
        pictureUriInvocationCount = 0
        getImagePathInvocations.clear()
        getVoicePathInvocations.clear()
        dataFileInvocations.clear()
        getAudioLengthInvocations.clear()
        imageToTextInvocations.clear()
    }
}
