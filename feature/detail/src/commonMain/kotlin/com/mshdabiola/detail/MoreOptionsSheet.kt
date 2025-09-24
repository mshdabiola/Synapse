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
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.designsystem.theme.ColorFamily
import com.mshdabiola.designsystem.theme.LocalExtendedColorScheme
import com.mshdabiola.model.testtag.MoreOptionsSheetTestTags
import com.mshdabiola.ui.Logics
import org.jetbrains.compose.resources.stringResource
import synapse.feature.detail.generated.resources.Res
import synapse.feature.detail.generated.resources.feature_detail_add_image
import synapse.feature.detail.generated.resources.feature_detail_checkboxes
import synapse.feature.detail.generated.resources.feature_detail_drawing
import synapse.feature.detail.generated.resources.feature_detail_recording
import synapse.feature.detail.generated.resources.feature_detail_take_photo

@OptIn(markerClass = [androidx.compose.material3.ExperimentalMaterial3Api::class])
@androidx.compose.runtime.Composable
fun MoreOptionsSheet(
    currentColor: Int,
    currentImage: Int,
    isNoteCheck: Boolean,
    getPhotoUri: () -> String,
    changeToCheckBoxes: () -> Unit,
    onDrawing: () -> Unit,
    onDismiss: () -> Unit,
    show: Boolean,
    logics: Logics,
) {
    if (show) {
        val noteColor = if (currentImage != -1) {
            LocalExtendedColorScheme.current.noteBackGround[currentImage]
        } else {
            if (currentColor != -1) {
                LocalExtendedColorScheme.current.noteColor[currentColor]
            } else {
                ColorFamily(
                    color = MaterialTheme.colorScheme.surface,
                    colorContainer = MaterialTheme.colorScheme.surfaceContainer,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onColorContainer = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        val navColor = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = noteColor.colorContainer,
            unselectedIconColor = noteColor.onColorContainer,
            unselectedTextColor = noteColor.onColorContainer,
        )

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = noteColor.colorContainer,
            contentColor = noteColor.onColorContainer,

        ) {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.PhotoCamera,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.feature_detail_take_photo)) },
                selected = false,
                onClick = {
                    logics.snapImage(getPhotoUri())
                    onDismiss()
                },
                colors = navColor,
                modifier = androidx.compose.ui.Modifier.testTag(MoreOptionsSheetTestTags.TAKE_PHOTO),
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.Image,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.feature_detail_add_image)) },
                selected = false,
                onClick = {
                    logics.chooseImage()
                    onDismiss()
                },
                colors = navColor,
                modifier = androidx.compose.ui.Modifier.testTag(MoreOptionsSheetTestTags.ADD_IMAGE),
            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.Brush,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.feature_detail_drawing)) },
                selected = false,
                onClick = {
                    onDismiss()
                    onDrawing()
                },
                colors = navColor,
                modifier = androidx.compose.ui.Modifier.testTag(MoreOptionsSheetTestTags.DRAWING),

            )
            if (logics.isVoiceAvailable()) {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = SynIcons.KeyboardVoice,
                            contentDescription = "",
                        )
                    },
                    label = { Text(text = stringResource(Res.string.feature_detail_recording)) },
                    selected = false,
                    onClick = {
                        logics.openVoice()
                        onDismiss()
                    },
                    colors = navColor,
                    modifier = androidx.compose.ui.Modifier.testTag(MoreOptionsSheetTestTags.RECORDING),
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
                    label = { Text(text = stringResource(Res.string.feature_detail_checkboxes)) },
                    selected = false,
                    onClick = {
                        onDismiss()
                        changeToCheckBoxes()
                    },
                    colors = navColor,
                    modifier = androidx.compose.ui.Modifier.testTag(MoreOptionsSheetTestTags.CHECKBOXES),
                )
            }
        }
    }
}
