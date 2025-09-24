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

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.speech.RecognizerIntent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil3.Bitmap
import com.mshdabiola.model.note.NotePad
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ReaLogics(
    val context: Context,
    val imageLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    val snapPictureLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    val audioPermission: ManagedActivityResultLauncher<String, Boolean>,
    val voiceLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    val notificationPermission: ManagedActivityResultLauncher<String, Boolean>,

) : Logics {
    override fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        kotlin.runCatching {
            ContextCompat.startActivity(context, intent, null)
        }.onFailure {
            it.printStackTrace()
        }
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
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now")
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

    override fun chooseImage() {
        imageLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun shareNote(notePad: NotePad) {
        val images = notePad.images
            .map {
                val file = File(it.path)
                val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
                uri
            }
        val builder = ShareCompat.IntentBuilder(context)
            .setSubject(notePad.title)
            .setText(notePad.detail)
            .setChooserTitle("Share note") // TODO: move to string resources

        if (images.isNotEmpty()) {
            builder.setType("image/*")
            images.forEach { builder.addStream(it) }
        } else {
            builder.setType("text/plain")
        }

        builder.createChooserIntent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(context, this, null)
        }
    }

    override fun askForNotificationPermission() {
        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun checkNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_DENIED
    }

    override fun shareDrawing(bitmap: ImageBitmap) {
        // 1. Save ImageBitmap to a temporary file
        val imageFile: File? = try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // Create 'images' directory if it doesn't exist
            val file = File(cachePath, "shared_drawing_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { stream ->
                // Convert Compose ImageBitmap to Android Graphics Bitmap
                // Ensure your ImageBitmap is in a compatible format, e.g., ARGB_8888
                val androidBitmap = bitmap.asAndroidBitmap()
                androidBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, stream)
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        if (imageFile != null) {
            // 2. Get content URI using FileProvider
            val imageUri: Uri? = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider", // Make sure this matches your AndroidManifest
                    imageFile,
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if (imageUri != null) {
                // 3. Share using the content URI
                val shareIntent = ShareCompat.IntentBuilder(context)
                    .setType("image/png")
                    .setStream(imageUri)
                    .setChooserTitle("Share Drawing") // TODO: Move to string resources
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant read permission

                if (shareIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(shareIntent)
                } else {
                    // Handle case where no app can handle the share intent (e.g., show a Toast)
                    println("No app found to share the drawing.")
                }

                // Optionally, you can try to delete the temp file.
                // However, the receiving app might need time to access it.
                // A more robust way is to use a ContentObserver or clear cache periodically.
                // For simplicity, we won't delete it immediately here.
                // imageFile.deleteOnExit() // This might not always work as expected.

            } else {
                println("Error getting content URI for the drawing.")
                // Handle error (e.g., show a Toast to the user)
            }
        } else {
            println("Error saving drawing to a temporary file.")
            // Handle error (e.g., show a Toast to the user)
        }
    }
}
