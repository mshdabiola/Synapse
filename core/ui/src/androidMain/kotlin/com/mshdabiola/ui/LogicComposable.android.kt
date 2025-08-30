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
                    onNotification
                }
            },
        )

    return ReaLogics(
        context = context,
        imageLauncher = imageLauncher,
        snapPictureLauncher = snapPictureLauncher,
        audioPermission = audioPermission,
        voiceLauncher = voiceLauncher
    )
}
