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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher

@Composable
actual fun getPlatformLogics(
    outputVoice: (String, String) -> Unit,
    saveImage: (String) -> Unit,
    savePhoto: () -> Unit,
    onNotification: () -> Unit,
): Logics {

    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
                files.firstOrNull()?.let { file ->

                    println("Selected file path: ${file.file.path}")
                    saveImage(file.file.path)
                }

        }
    )
    return remember {
        RealLogics(
            pickerLauncher = pickerLauncher,
            outputVoice = outputVoice,
            savePhoto = savePhoto,
            onNotification = onNotification,
        )
    }
}
