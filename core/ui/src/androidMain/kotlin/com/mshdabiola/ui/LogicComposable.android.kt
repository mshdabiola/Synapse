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

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getPlatformLogics(
    outputVoice: (String, String) -> Unit,
    saveImage: (String) -> Unit,
    savePhoto: () -> Unit,
    onNotification: () -> Unit,
): Logics {
    val context = LocalContext.current
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.let { intent ->
                val text = intent
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.joinToString() ?: ""

                outputVoice(intent.data!!.toString(), text)
            }
        },
    )
    val audioPermission =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {
                if (it) {
                    voiceLauncher.launch(
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                            )
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now Now")
                            putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")
                            putExtra("android.speech.extra.GET_AUDIO", true)
                        },
                    )
                }
            },
        )

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
//                showImageDialog = false
//                val time = System.currentTimeMillis()
                saveImage(it.toString())
//                navigateToEdit(-3, "image text", time)
            }
        },
    )

    val snapPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                savePhoto()
                // navigateToEdit(-3, "image text", photoId)
            }
        },
    )

    val notificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) {
                onNotification()
            }
        },
    )

    return ReaLogics(
        context = context,
        imageLauncher = imageLauncher,
        snapPictureLauncher = snapPictureLauncher,
        audioPermission = audioPermission,
        voiceLauncher = voiceLauncher,
        notificationPermission = notificationPermission,
    )
}
