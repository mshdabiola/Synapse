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
package com.mshdabiola.detail

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.AppConstant
import com.mshdabiola.model.NoteBg
import com.mshdabiola.ui.getPlatformLogics
import org.jetbrains.compose.resources.stringResource
import synapse.feature.detail.generated.resources.Res
import synapse.feature.detail.generated.resources.modules_designsystem_add_image
import synapse.feature.detail.generated.resources.modules_designsystem_checkboxes
import synapse.feature.detail.generated.resources.modules_designsystem_drawing
import synapse.feature.detail.generated.resources.modules_designsystem_recording
import synapse.feature.detail.generated.resources.modules_designsystem_take_photo

@OptIn(markerClass = [androidx.compose.material3.ExperimentalMaterial3Api::class])
@androidx.compose.runtime.Composable
fun MoreOptionsSheet(
    currentColor: Int,
    currentImage: Int,
    isNoteCheck: Boolean,
    saveImage: (String) -> Unit,
    saveVoice: (String, String) -> Unit,
    getPhotoUri: () -> String,
    changeToCheckBoxes: () -> Unit,
    onDrawing: () -> Unit,
    onDismiss: () -> Unit,
    show: Boolean,
    isVoiceSupport: Boolean,
) {
    val background = if (currentImage != -1) {
        Color(NoteBg.noteBgs [currentImage].fgColor)
    } else {
        if (currentColor != -1) {
            Color(AppConstant.noteColors[currentColor])
        } else {
            MaterialTheme.colorScheme.surface
        }
    }

    val logics = getPlatformLogics(
        saveImage = saveImage,
        savePhoto = {
            saveImage(getPhotoUri())
        },
        outputVoice = saveVoice,
    )

    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = background,

        ) {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.PhotoCamera,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.modules_designsystem_take_photo)) },
                selected = false,
                onClick = {
                    logics.snapImage(getPhotoUri())
                    onDismiss()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                modifier = androidx.compose.ui.Modifier.testTag("detail:take_photo"),
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.Image,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.modules_designsystem_add_image)) },
                selected = false,
                onClick = {
                    logics.chooseImage(getPhotoUri())
                    onDismiss()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                modifier = androidx.compose.ui.Modifier.testTag("detail:add_image"),
            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.Brush,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.modules_designsystem_drawing)) },
                selected = false,
                onClick = {
                    onDismiss()
                    onDrawing()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                modifier = androidx.compose.ui.Modifier.testTag("detail:drawing"),

            )
            if (isVoiceSupport) {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = SynIcons.KeyboardVoice,
                            contentDescription = "",
                        )
                    },
                    label = { Text(text = stringResource(Res.string.modules_designsystem_recording)) },
                    selected = false,
                    onClick = {
                        logics.openVoice()
                        onDismiss()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                    modifier = androidx.compose.ui.Modifier.testTag("detail:recording"),
                )
            }
            if (!isNoteCheck) {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = SynIcons.CheckBox,
                            contentDescription = "",
                        )
                    },
                    label = { Text(text = stringResource(Res.string.modules_designsystem_checkboxes)) },
                    selected = false,
                    onClick = {
                        onDismiss()
                        changeToCheckBoxes()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                    modifier = androidx.compose.ui.Modifier.testTag("detail:checkboxes"),
                )
            }
        }
    }
}
