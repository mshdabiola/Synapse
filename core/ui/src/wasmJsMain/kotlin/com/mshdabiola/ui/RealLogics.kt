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
import com.mohamedrejeb.calf.picker.FilePickerLauncher
import com.mshdabiola.model.note.NotePad
import kotlinx.browser.window

fun encodeURIComponentJs(str: String): JsString = js("encodeURIComponent(str)")

class RealLogics(
    val pickerLauncher: FilePickerLauncher,
    val outputVoice: (String, String) -> Unit = { _, _ -> },
    val savePhoto: () -> Unit = {},
    val onNotification: () -> Unit = {},
) : Logics {
    override fun openUrl(url: String) {
        window.open(url, "_blank")
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    override fun openEmail(emailAddress: String, subject: String, body: String) {
        val encodedSubject = encodeURIComponentJs(subject)
        val encodedBody = encodeURIComponentJs(body)
        window.open("mailto:$emailAddress?subject=$encodedSubject&body=$encodedBody", "_self")
    }

    override fun isVoiceAvailable(): Boolean {
        return false // Typically not available directly in browser Wasm without specific JS interop
    }

    override fun openVoice() {
        outputVoice("", "")
    }

    override fun snapImage(path: String) {
        savePhoto() // Assumes this handles Wasm/JS specific saving if needed
    }

    override fun chooseImage() {
        pickerLauncher.launch()
    }

    override fun shareNote(notePad: NotePad) {
        // Web Share API could be used here if available and desired
        println("ShareNote on Wasm: Title: ${notePad.title}, Detail: ${notePad.detail}")
        // Example using Web Share API (check for navigator.share availability)
        // if (js("typeof navigator.share") !== "undefined") { ... }
    }

    override fun askForNotificationPermission() {
        // Browser Notification API
        // js("Notification.requestPermission().then(function(permission) { console.log('Notification permission:', permission); });")
        onNotification() // Call callback, actual permission handled by browser
    }

    override fun checkNotificationPermission(): Boolean {
        onNotification()
        // return js("Notification.permission === 'granted'") as Boolean
        return false // Placeholder
    }

    override fun shareDrawing(bitmap: ImageBitmap) {
        // For sharing, you might convert to data URL and use Web Share API
        // or trigger a download.
        println("shareDrawing on Wasm: (Implementation would be similar to copyDrawing then Web Share or download)")
    }

    override fun copyDrawing(bitmap: ImageBitmap) {
    }
}
