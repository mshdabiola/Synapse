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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.designsystem.theme.ColorFamily
import com.mshdabiola.designsystem.theme.LocalExtendedColorScheme
import com.mshdabiola.model.testtag.NoteAppearanceSheetTestTags
import org.jetbrains.compose.resources.stringResource
import synapse.feature.detail.generated.resources.Res
import synapse.feature.detail.generated.resources.feature_detail_background
import synapse.feature.detail.generated.resources.feature_detail_background_image_item_cd
import synapse.feature.detail.generated.resources.feature_detail_color
import synapse.feature.detail.generated.resources.feature_detail_reset_color_cd
import synapse.feature.detail.generated.resources.feature_detail_reset_image_cd
import synapse.feature.detail.generated.resources.feature_detail_selected_cd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAppearanceSheet(
    currentColor: Int,
    currentImage: Int,
    onColorClick: (Int) -> Unit = {},
    onImageClick: (Int) -> Unit = {},
    show: Boolean,
    onDismissRequest: () -> Unit = {},
) {
    if (show) {
        rememberCoroutineScope()
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
        val noteColors = LocalExtendedColorScheme.current.noteColor
        val noteBgs = LocalExtendedColorScheme.current.noteBackGround

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            containerColor = noteColor.colorContainer,
            contentColor = noteColor.onColorContainer,

        ) {
            Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
                Text(
                    text = stringResource(Res.string.feature_detail_color),
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Surface(
                            onClick = { onColorClick(-1) },
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag(NoteAppearanceSheetTestTags.RESET_COLOR_BUTTON),
                            border = BorderStroke(
                                1.dp,
                                if (-1 == currentColor) Color.Blue else Color.Gray,
                            ),
                        ) {
                            if (-1 == currentColor) {
                                Icon(
                                    imageVector = SynIcons.Done,
                                    contentDescription = stringResource(Res.string.feature_detail_selected_cd),
                                    tint = Color.Blue,
                                    modifier = Modifier.padding(4.dp),
                                )
                            } else {
                                Icon(
                                    imageVector = SynIcons.FormatColorReset,
                                    contentDescription = stringResource(Res.string.feature_detail_reset_color_cd),
                                    tint = Color.Gray,
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }
                    }
                    itemsIndexed(noteColors) { index, color ->
                        Surface(
                            onClick = { onColorClick(index) },
                            shape = CircleShape,
                            color = color.color,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag(NoteAppearanceSheetTestTags.colorItem(index)),
                            border = BorderStroke(
                                1.dp,
                                if (index == currentColor) Color.Blue else Color.Gray,
                            ),
                        ) {
                            if (index == currentColor) {
                                Icon(
                                    imageVector = SynIcons.Done,
                                    contentDescription = stringResource(Res.string.feature_detail_selected_cd),
                                    tint = Color.Blue,
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.feature_detail_background),
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Box(
                            Modifier
                                .clickable { onImageClick(-1) }
                                .testTag(NoteAppearanceSheetTestTags.RESET_IMAGE_BUTTON),
                        ) {
                            Icon(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (-1 == currentImage) Color.Blue else Color.Gray,
                                        CircleShape,
                                    )
                                    .size(56.dp)
                                    .padding(8.dp),
                                imageVector = SynIcons.ImageNotSupported,
                                contentDescription = stringResource(Res.string.feature_detail_reset_image_cd),
                            )
                            if (-1 == currentImage) {
                                Icon(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.Blue)
                                        .size(16.dp)
                                        .align(Alignment.TopEnd),
                                    imageVector = SynIcons.Done,
                                    contentDescription = stringResource(Res.string.feature_detail_selected_cd),
                                    tint = Color.White,

                                )
                            }
                        }
                    }
                    itemsIndexed(noteBgs) { index, noteBg ->

                        Box(
                            Modifier
                                .clickable { onImageClick(index) }
                                .testTag(NoteAppearanceSheetTestTags.imageItem(index)),
                        ) {
                            Image(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (index == currentImage) Color.Blue else Color.Gray,
                                        CircleShape,
                                    )
                                    .size(56.dp),
                                imageVector = SynIcons.getBackGround(index),
                                contentDescription = stringResource(Res.string.feature_detail_background_image_item_cd),
                                contentScale = ContentScale.Crop,
                            )
                            if (index == currentImage) {
                                Icon(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.Blue)
                                        .size(16.dp)
                                        .align(Alignment.TopEnd),
                                    imageVector = SynIcons.Done,
                                    contentDescription = stringResource(Res.string.feature_detail_selected_cd),
                                    tint = Color.White,

                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
