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

import androidx.compose.ui.graphics.ImageBitmap
import com.mshdabiola.model.note.NotePad

interface Logics {
    fun openUrl(url: String)
    fun openEmail(emailAddress: String, subject: String, body: String)

    fun isVoiceAvailable(): Boolean
    fun openVoice()
    fun snapImage(path: String)
    fun chooseImage()
    fun shareNote(notePad: NotePad)

    fun askForNotificationPermission()

    fun checkNotificationPermission(): Boolean

    fun shareImage(bitmap: ImageBitmap)
    fun copyImage(bitmap: ImageBitmap)
    fun shareImage(path:String)
    fun copyImage(path: String)
}
