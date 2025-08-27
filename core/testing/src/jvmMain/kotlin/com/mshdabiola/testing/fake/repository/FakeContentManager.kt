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
package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.ContentManager

class FakeContentManager : ContentManager {

    var imageToTextResult: String = "Extracted text from content manager"
    var imageToTextShouldThrowError: Boolean = false
    var lastPathForImageToText: String? = null

    override fun saveImage(uri: String): Long {
        return 1
    }

    override fun saveVoice(uri: String): Long {
        return 1
    }

    override fun pictureUri(): String {
        return ""
    }

    override fun getImagePath(id: Long): String {
        return "/fake/content/path/image_$id.jpg"
    }

    override fun getVoicePath(data: Long): String {
        return ""
    }
//
//    override fun saveBitmap(path: String, bitmap: Bitmap) {
//    }

    override fun dataFile(drawingId: Long): String {
        return ""
    }

    override fun getAudioLength(path: String): Long {
        return 2
    }

    // Add this method based on your IContentManager interface
    override fun imageToText(path: String): String {
        lastPathForImageToText = path
        if (imageToTextShouldThrowError) {
            throw Exception("ContentManager imageToText error")
        }
        return imageToTextResult
    }
}
