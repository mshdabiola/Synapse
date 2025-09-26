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
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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

    override fun shareImage(bitmap: ImageBitmap) {
        // 1. Save ImageBitmap to a temporary file
        val imageFile: File? = saveBitmapToCache(bitmap, "shared_drawing")
        shareImage(imageFile?.absolutePath ?: "")
    }



    private fun saveBitmapToCache(bitmap: ImageBitmap, filenamePrefix: String): File? {
        var file: File? = null
        return try {
            val parent = File(context.cacheDir, "images")
            if (!parent.exists() && !parent.mkdirs()) {
                throw IOException("Unable to create cache dir: ${parent.absolutePath}")
            }
            file = File(parent, "${filenamePrefix}_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { stream ->
                val ok = bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)
                if (!ok) throw IOException("Bitmap.compress returned false")
                stream.flush()
            }
            file
        } catch (e: IOException) {
            runCatching { file?.delete() }
            android.util.Log.e("ReaLogics", "Failed to save bitmap to cache for $filenamePrefix", e)
            null
        }
    }

    override fun copyImage(bitmap: ImageBitmap) {
        val imageFile: File? = saveBitmapToCache(bitmap, "copied_drawing")

        copyImage(imageFile?.absolutePath ?: "")
    }

    override fun shareImage(path: String) {
        val imageFile = if (path.isBlank()) null else File(path)

        if (imageFile != null) {
            val imageUri: Uri? = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    imageFile,
                )
            } catch (e: Exception) {
                println("FileProvider Error: Is the file path covered by file_paths.xml?")
                println("File path trying to share: ${imageFile.absolutePath}")
                e.printStackTrace()
                null
            }

            if (imageUri != null) {
                val shareIntent = ShareCompat.IntentBuilder(context)
                    .setType("image/png")
                    .setStream(imageUri)
                    .setChooserTitle("Share Drawing")
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                if (shareIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(shareIntent)
                } else {
                    println("No app found to share the drawing.")
                }
            } else {
                println("Error getting content URI for the drawing.")
            }
        } else {
            println("Error saving drawing to a temporary file.")
        }
    }

    override fun copyImage(path: String) {
        val imageFile = if (path.isBlank()) null else File(path)

        if (imageFile != null) {
            val imageUri: Uri? = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    imageFile,
                )
            } catch (e: Exception) {
                println("FileProvider Error for copy: Is the file path covered by file_paths.xml?")
                println("File path trying to copy: ${imageFile.absolutePath}")
                e.printStackTrace()
                null
            }

            if (imageUri != null) {
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newUri(context.contentResolver, "Image", imageUri)
                clipboardManager.setPrimaryClip(clipData)
                // Optionally, show a toast or notification that image has been copied
                println("Drawing copied to clipboard.")
            } else {
                println("Error getting content URI for copying the drawing.")
            }
        } else {
            println("Error saving drawing to a temporary file for copying.")
        }
    }
}
