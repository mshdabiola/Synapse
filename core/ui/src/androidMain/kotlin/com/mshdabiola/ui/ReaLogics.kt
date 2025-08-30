package com.mshdabiola.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

class ReaLogics(
    val context: Context,
    val imageLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    val snapPictureLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    val audioPermission: ManagedActivityResultLauncher<String, Boolean>,
    val voiceLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

) :  Logics {
    override fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }

    override fun openEmail(emailAddress: String, subject: String, body: String) {
        val mailto = "mailto:${Uri.encode(emailAddress)}" +
            "?subject=${Uri.encode(subject)}" +
            "&body=${Uri.encode(body)}"

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = mailto.toUri()
        }

        // Check if there's an app to handle this intent
        if (emailIntent.resolveActivity(context.packageManager) != null) {
            ContextCompat.startActivity(context, emailIntent, null)
        } else {
            // Optionally handle the case where no email app is installed
            // e.g., show a Toast or log a message
            println("No email app found to handle the intent.")
        }
    }

    override fun isVoiceAvailable(): Boolean {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        val pm = context.packageManager
        val activities = pm.queryIntentActivities(intent, 0)
       return activities.isNotEmpty()
    }

    override fun openVoice() {
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            voiceLauncher.launch(
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                    )
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now Now")
                    putExtra(
                        "android.speech.extra.GET_AUDIO_FORMAT",
                        "audio/AMR",
                    )
                    putExtra("android.speech.extra.GET_AUDIO", true)
                },
            )
        } else {
            audioPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    override fun snapImage(path: String) {
        snapPictureLauncher.launch(path.toUri())
    }

    override fun chooseImage(path: String) {
        imageLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
