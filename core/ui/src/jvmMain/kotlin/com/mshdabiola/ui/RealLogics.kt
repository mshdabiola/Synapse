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
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.res.loadImageBitmap
import com.mohamedrejeb.calf.picker.FilePickerLauncher
import com.mshdabiola.model.note.NotePad
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.awt.image.BufferedImage
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO
import kotlin.text.endsWith
import kotlin.text.lowercase

class RealLogics(
    val outputVoice: (String, String) -> Unit = { _, _ -> },
    val savePhoto: () -> Unit = {},
    val onNotification: () -> Unit = {},
    val imageSelectedCallback: (String) -> Unit = { _ -> }
) : Logics {
    override fun openUrl(url: String) {
        val desktop = Desktop.getDesktop()
        if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI(url))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Desktop browse action not supported.")
        }
    }

    override fun openEmail(emailAddress: String, subject: String, body: String) {
        val desktop = Desktop.getDesktop()
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.MAIL)) {
                try {
                    val mailtoUri = "mailto:$emailAddress?subject=${
                        java.net.URLEncoder.encode(subject, "UTF-8")
                    }&body=${java.net.URLEncoder.encode(body, "UTF-8")}"
                    desktop.mail(URI(mailtoUri))
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Fallback or error handling
                    println("Error opening email client: ${e.message}")
                }
            } else {
                println("Desktop.Action.MAIL is not supported.")
                // You might try opening a mailto: link in the default browser as a fallback
                try {
                    val mailtoUri = "mailto:$emailAddress?subject=${
                        java.net.URLEncoder.encode(subject, "UTF-8")
                    }&body=${java.net.URLEncoder.encode(body, "UTF-8")}"
                    desktop.browse(URI(mailtoUri))
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Error opening mailto link in browser: ${e.message}")
                }
            }
        } else {
            println("Desktop is not supported.")
        }
    }

    override fun isVoiceAvailable(): Boolean {
        return false
    }

    override fun openVoice() {
        outputVoice("", "")
    }

    override fun snapImage(path: String) {
        savePhoto()
    }

    override fun chooseImage() {
        val frame: Frame? = null // Or get your main app Frame if available
        // Configure FileDialog for loading/opening a file
        val fileDialog = FileDialog(frame, "Select Image File", FileDialog.LOAD)

        // Optional: Set a filename filter to show only image files
        fileDialog.filenameFilter = FilenameFilter { _, name ->
            val lowercaseName = name.lowercase()
            lowercaseName.endsWith(".png") ||
                lowercaseName.endsWith(".jpg") ||
                lowercaseName.endsWith(".jpeg") ||
                lowercaseName.endsWith(".gif") ||
                lowercaseName.endsWith(".bmp")
        }

        fileDialog.isVisible = true // Show the dialog (this is blocking)

        val directory = fileDialog.directory
        val filename = fileDialog.file

        if (directory != null && filename != null) {
            val selectedFile = File(directory, filename)
            println("Selected file: ${selectedFile.absolutePath}")
           imageSelectedCallback(selectedFile.absolutePath)
        } else {
            println("No file selected or dialog cancelled.")
        }
    }

    override fun shareNote(notePad: NotePad) {
    }

    override fun askForNotificationPermission() {
    }

    override fun checkNotificationPermission(): Boolean {
        onNotification()
        return false
    }

    override fun shareDrawing(bitmap: ImageBitmap) {
        // 1. Convert Compose ImageBitmap to AWT BufferedImage
        val bufferedImage: BufferedImage = bitmap.toAwtImage()

        // 2. Show a FileDialog to let the user choose where to save
        //    Using a Frame as the parent for the FileDialog.
        //    If your app has a main window (Frame), use that. Otherwise, a temporary Frame.
        val frame: Frame? = null // Or get your main app Frame if available
        val fileDialog = FileDialog(frame, "Save Drawing As...", FileDialog.SAVE)
        fileDialog.file = "drawing.png" // Default filename
        fileDialog.isVisible = true

        val selectedFile = fileDialog.directory?.let { dir ->
            fileDialog.file?.let { filename ->
                // Ensure the filename has a .png extension if the user didn't add one
                val finalFilename = if (filename.lowercase().endsWith(".png")) filename else "$filename.png"
                File(dir, finalFilename)
            }
        }

        if (selectedFile != null) {
            try {
                // 3. Save the BufferedImage to the selected file
                val success = ImageIO.write(bufferedImage, "png", selectedFile)
                if (success) {
                    println("Drawing saved successfully to: ${selectedFile.absolutePath}")

                    // Optional: Try to open the saved file with the default application
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        try {
                            Desktop.getDesktop().open(selectedFile)
                        } catch (e: IOException) {
                            println("Error opening the saved file: ${e.message}")
                            e.printStackTrace()
                        }
                    } else {
                        println("Cannot open file: Desktop.Action.OPEN not supported.")
                    }
                } else {
                    println("Failed to save drawing. ImageIO.write returned false.")
                }
            } catch (e: IOException) {
                println("Error saving drawing: ${e.message}")
                e.printStackTrace()
                // You might want to show an error message to the user here
            }
        } else {
            println("Save operation cancelled by the user.")
        }
    }
}
