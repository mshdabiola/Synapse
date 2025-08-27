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

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.content.FileProvider
import com.mshdabiola.data.ImageToText
import java.io.File
import java.io.FileOutputStream

class RealContentManager(
    private val context: Context,
    private val imageToText: ImageToText,
) : ContentManager {
    lateinit var retriever: MediaMetadataRetriever
    private val photoDir = context.filesDir.absolutePath + "/photo"
    private val voiceDir = context.filesDir.absolutePath + "/voice"

    override fun saveImage(uri: String): Long {
        return try {
            val currentTime = System.currentTimeMillis()
            createImageDir()
            val outputStream = FileOutputStream(File(photoDir, "Image_$currentTime.jpg"))

            context.contentResolver.openInputStream(Uri.parse(uri)).use {
                it?.copyTo(outputStream)
                outputStream.close()
            }
            currentTime
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun saveVoice(uri: String): Long {
        return try {
            val currentTime = System.currentTimeMillis()
            createVoiceDir()
            val outputStream = FileOutputStream(File(voiceDir, "Voice_$currentTime.amr"))

            context.contentResolver.openInputStream(Uri.parse(uri)).use {
                it?.copyTo(outputStream)
                outputStream.close()
            }
            currentTime
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun pictureUri(): String {
        createImageDir()
        val file = File(photoDir, "Image_$2.jpg")

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

        return uri.toString()
    }

    override fun getImagePath(data: Long): String {
        return "$photoDir/Image_$data.jpg"
    }

    override fun getVoicePath(data: Long): String {
        return "$voiceDir/Voice_$data.amr"
    }

    private fun createVoiceDir() {
        val file = File(voiceDir)
        if (!file.exists()) {
            file.mkdir()
        }
    }

    private fun createImageDir() {
        val file = File(photoDir)
        if (!file.exists()) {
            file.mkdir()
        }
    }

//    override fun saveBitmap(path: String, bitmap: Bitmap) {
//        createImageDir()
//        File(path).outputStream().use {
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
//        }
//    }

    override fun dataFile(drawingId: Long): String {
        val dir = File(context.filesDir.absolutePath + "/drawingfile")
        if (dir.exists().not()) {
            dir.mkdir()
        }

        return File(dir, "data_$drawingId.json").path
    }

    override fun getAudioLength(path: String): Long {
        if (!::retriever.isInitialized) {
            retriever = MediaMetadataRetriever()
        }

        try {
            retriever.setDataSource(path)
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0
        } catch (e: IllegalArgumentException) {
            // Handle the exception, log error, etc.
            e.printStackTrace()
            return 0
        } finally {
            retriever.release()
        }
    }

    override fun imageToText(path: String): String {
        return imageToText.toText(path)
    }
}
