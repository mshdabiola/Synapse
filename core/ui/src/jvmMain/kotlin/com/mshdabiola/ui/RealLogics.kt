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
// import androidx.compose.ui.res.loadImageBitmap // Keep if used elsewhere
import com.mshdabiola.model.note.NotePad
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.awt.Image // Required for ImageTransferable
import java.awt.Toolkit // Required for Clipboard
import java.awt.datatransfer.Clipboard // Required for Clipboard
import java.awt.datatransfer.DataFlavor // Required for ImageTransferable
import java.awt.datatransfer.Transferable // Required for ImageTransferable
import java.awt.datatransfer.UnsupportedFlavorException // Required for ImageTransferable
import java.awt.image.BufferedImage
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO

class ImageTransferable(private val image: Image) : Transferable {
    override fun getTransferDataFlavors(): Array<DataFlavor> {
        return arrayOf(DataFlavor.imageFlavor)
    }

    override fun isDataFlavorSupported(flavor: DataFlavor?): Boolean {
        return DataFlavor.imageFlavor.equals(flavor)
    }

    @Throws(UnsupportedFlavorException::class, IOException::class)
    override fun getTransferData(flavor: DataFlavor?): Any {
        if (!DataFlavor.imageFlavor.equals(flavor)) {
            throw UnsupportedFlavorException(flavor)
        }
        return image
    }
}

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
            if (desktop.isSupported(Desktop.Action.MAIL)) {
                try {
                    val mailtoUri = "mailto:$emailAddress?subject=${
                        java.net.URLEncoder.encode(subject, "UTF-8")
                    }&body=${java.net.URLEncoder.encode(body, "UTF-8")}"
                    desktop.mail(URI(mailtoUri))
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Error opening email client: ${e.message}")
                }
            } else {
                println("Desktop.Action.MAIL is not supported.")
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
        val frame: Frame? = null
        val fileDialog = FileDialog(frame, "Select Image File", FileDialog.LOAD)
        fileDialog.filenameFilter = FilenameFilter { _, name ->
            val lowercaseName = name.lowercase()
            lowercaseName.endsWith(".png") ||
                lowercaseName.endsWith(".jpg") ||
                lowercaseName.endsWith(".jpeg") ||
                lowercaseName.endsWith(".gif") ||
                lowercaseName.endsWith(".bmp")
        }
        fileDialog.isVisible = true

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
        // Not typically implemented for JVM desktop in the same way as mobile
        println("Share note: $notePad (no standard share sheet on JVM)")
    }

    override fun askForNotificationPermission() {
        // No standard notification permission model like Android/iOS on general JVM
        onNotification() // Call the callback directly if it represents a general notification action
    }

    override fun checkNotificationPermission(): Boolean {
        onNotification() // Or however you handle "notifications" in JVM
        return true // Or false, depending on what this means in your JVM context
    }

    override fun shareDrawing(bitmap: ImageBitmap) {
        val bufferedImage: BufferedImage = bitmap.toAwtImage()
        val frame: Frame? = null
        val fileDialog = FileDialog(frame, "Save Drawing As...", FileDialog.SAVE)
        fileDialog.file = "drawing.png"
        fileDialog.isVisible = true

        val selectedFile = fileDialog.directory?.let { dir ->
            fileDialog.file?.let { filename ->
                val finalFilename = if (filename.lowercase().endsWith(".png")) filename else "$filename.png"
                File(dir, finalFilename)
            }
        }

        if (selectedFile != null) {
            try {
                val success = ImageIO.write(bufferedImage, "png", selectedFile)
                if (success) {
                    println("Drawing saved successfully to: ${selectedFile.absolutePath}")
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
            }
        } else {
            println("Save operation cancelled by the user.")
        }
    }

    override fun copyDrawing(bitmap: ImageBitmap) {
        try {
            val bufferedImage: BufferedImage = bitmap.toAwtImage()
            val transferable = ImageTransferable(bufferedImage)
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(transferable, null)
            println("Drawing copied to clipboard.")
        } catch (e: Exception) {
            println("Error copying drawing to clipboard: ${e.message}")
            e.printStackTrace()
            // Optionally, provide feedback to the user that copying failed
        }
    }
}
