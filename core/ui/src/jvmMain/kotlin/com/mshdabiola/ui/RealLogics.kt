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
import com.mshdabiola.model.note.NotePad
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.image.BufferedImage
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO

// Helper class for transferring images to the clipboard (remains the same)
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
    val imageSelectedCallback: (List<String>) -> Unit = { _ -> },
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
            imageSelectedCallback(listOf(selectedFile.absolutePath))
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

    override fun shareImage(bitmap: ImageBitmap) {
        val bufferedImage: BufferedImage = bitmap.toAwtImage()
        // Delegate to the helper function that handles saving BufferedImage
        saveAndPotentiallyOpenImage(bufferedImage, "shared_drawing.png")
    }

    override fun copyImage(bitmap: ImageBitmap) {
        try {
            val bufferedImage: BufferedImage = bitmap.toAwtImage()
            val transferable = ImageTransferable(bufferedImage)
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(transferable, null)
            println("Image (from ImageBitmap) copied to clipboard.")
        } catch (e: Exception) {
            println("Error copying image (from ImageBitmap) to clipboard: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun shareImage(path: String) {
        val imageFile = File(path)
        if (!imageFile.exists() || !imageFile.isFile) {
            println("Share image: File does not exist or is not a file at path: $path")
            return
        }
        try {
            val bufferedImage: BufferedImage? = ImageIO.read(imageFile)
            if (bufferedImage != null) {
                // Use the same save dialog logic as sharing an ImageBitmap
                saveAndPotentiallyOpenImage(bufferedImage, imageFile.name)
            } else {
                println("Share image: Could not read image from path: $path (unsupported format or corrupted)")
            }
        } catch (e: IOException) {
            println("Share image: Error reading image from path '$path': ${e.message}")
            e.printStackTrace()
        }
    }

    override fun copyImage(path: String) {
        val imageFile = File(path)
        if (!imageFile.exists() || !imageFile.isFile) {
            println("Copy image: File does not exist or is not a file at path: $path")
            return
        }
        try {
            val bufferedImage: BufferedImage? = ImageIO.read(imageFile)
            if (bufferedImage != null) {
                val transferable = ImageTransferable(bufferedImage)
                val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(transferable, null)
                println("Image (from path: $path) copied to clipboard.")
            } else {
                println("Copy image: Could not read image from path: $path (unsupported format or corrupted)")
            }
        } catch (e: IOException) {
            println("Copy image: Error reading image from path '$path': ${e.message}")
            e.printStackTrace()
        } catch (e: Exception) { // Catch other potential AWT/clipboard errors
            println("Copy image: Error during clipboard operation for path '$path': ${e.message}")
            e.printStackTrace()
        }
    }

    // Helper function to handle saving a BufferedImage and optionally opening it
    private fun saveAndPotentiallyOpenImage(bufferedImage: BufferedImage, suggestedFileName: String) {
        val frame: Frame? = null // Parent frame for the dialog, can be null
        val fileDialog = FileDialog(frame, "Save Image As...", FileDialog.SAVE)
        fileDialog.file = suggestedFileName // Suggest a filename

        // Set a filter for common image types (optional but good practice)
        fileDialog.filenameFilter = FilenameFilter { _, name ->
            val lowerName = name.lowercase()
            lowerName.endsWith(".png") ||
                lowerName.endsWith(".jpg") ||
                lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".gif") ||
                lowerName.endsWith(".bmp")
        }
        fileDialog.isVisible = true

        val selectedFileDir = fileDialog.directory
        val selectedFileName = fileDialog.file

        if (selectedFileDir != null && selectedFileName != null) {
            var fileToSave = File(selectedFileDir, selectedFileName)
            val extension = fileToSave.extension.lowercase()

            // Ensure a valid extension, default to png if none or unknown
            val formatName = when (extension) {
                "jpg", "jpeg" -> "jpeg"
                "gif" -> "gif"
                "bmp" -> "bmp"
                "png" -> "png"
                else -> {
                    // If no extension or unknown, default to .png and adjust filename
                    fileToSave = File(selectedFileDir, "$selectedFileName.png")
                    "png"
                }
            }

            try {
                val success = ImageIO.write(bufferedImage, formatName, fileToSave)
                if (success) {
                    println("Image saved successfully to: ${fileToSave.absolutePath}")
                    // Optionally, try to open the saved file with the default system application
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        try {
                            Desktop.getDesktop().open(fileToSave)
                        } catch (e: IOException) {
                            println("Error opening the saved file: ${e.message}")
                            e.printStackTrace()
                        }
                    } else if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.EDIT)) {
                        // Fallback to edit if open is not supported
                        try {
                            Desktop.getDesktop().edit(fileToSave)
                        } catch (e: IOException) {
                            println("Error editing the saved file: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                } else {
                    println("Failed to save image (ImageIO.write returned false) to: ${fileToSave.absolutePath}")
                }
            } catch (e: IOException) {
                println("Error saving image to '${fileToSave.absolutePath}': ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("Save image operation cancelled by the user.")
        }
    }
}
