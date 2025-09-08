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
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.AppConstant
import com.mshdabiola.model.NoteBg
import org.jetbrains.compose.resources.stringResource
import synapse.feature.detail.generated.resources.Res
import synapse.feature.detail.generated.resources.modules_designsystem_background
import synapse.feature.detail.generated.resources.modules_designsystem_color

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
    rememberCoroutineScope()
    val background = if (currentImage != -1) {
        Color(NoteBg.noteBgs [currentImage].fgColor)
    } else {
        if (currentColor != -1) {
            Color(AppConstant.noteColors[currentColor])
        } else {
            MaterialTheme.colorScheme.surface
        }
    }
    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            containerColor = background,

        ) {
            Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
                Text(
                    text = stringResource(Res.string.modules_designsystem_color),
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Surface(
                            onClick = { onColorClick(-1) },
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(40.dp),
                            border = BorderStroke(
                                1.dp,
                                if (-1 == currentColor) Color.Blue else Color.Gray,
                            ),
                        ) {
                            if (-1 == currentColor) {
                                Icon(
                                    imageVector = SynIcons.Done,
                                    contentDescription = "done",
                                    tint = Color.Blue,
                                    modifier = Modifier.padding(4.dp),
                                )
                            } else {
                                Icon(
                                    imageVector = SynIcons.FormatColorReset,
                                    contentDescription = "done",
                                    tint = Color.Gray,
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }
                    }
                    itemsIndexed(AppConstant.noteColors) { index, color ->
                        Surface(
                            onClick = { onColorClick(index) },
                            shape = CircleShape,
                            color = Color(color),
                            modifier = Modifier.size(40.dp),
                            border = BorderStroke(
                                1.dp,
                                if (index == currentColor) Color.Blue else Color.Gray,
                            ),
                        ) {
                            if (index == currentColor) {
                                Icon(
                                    imageVector = SynIcons.Done,
                                    contentDescription = "done",
                                    tint = Color.Blue,
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.modules_designsystem_background),
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Box(Modifier.clickable { onImageClick(-1) }) {
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
                                contentDescription = "",
                            )
                            if (-1 == currentImage) {
                                Icon(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.Blue)
                                        .size(16.dp)
                                        .align(Alignment.TopEnd),
                                    imageVector = SynIcons.Done,
                                    contentDescription = "",
                                    tint = Color.White,

                                )
                            }
                        }
                    }
                    itemsIndexed(NoteBg.noteBgs) { index, noteBg ->

                        Box(Modifier.clickable { onImageClick(index) }) {
                            Image(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (index == currentImage) Color.Blue else Color.Gray,
                                        CircleShape,
                                    )
                                    .size(56.dp),
                                imageVector = SynIcons.getBackGround(noteBg.bg),
                                contentDescription = "",
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
                                    contentDescription = "",
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
