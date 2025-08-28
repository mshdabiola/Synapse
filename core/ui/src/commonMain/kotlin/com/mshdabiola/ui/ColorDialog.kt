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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag // Added import
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.AppConstant
import com.mshdabiola.model.testtag.ColorDialogTestTags // Added import
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorDialog(
    show: Boolean = false,
    currentColor: Int = -1,
    onDismissRequest: () -> Unit = {},
    onColorClick: (Int) -> Unit = {},
) {
    AnimatedVisibility(visible = show) { // DIALOG_ROOT will be on AlertDialog
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = "Note Color",
                    modifier = Modifier.testTag(ColorDialogTestTags.TITLE)
                )
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(40.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag(ColorDialogTestTags.COLOR_GRID)
                ) {
                    item {
                        Surface(
                            onClick = {
                                onDismissRequest()
                                onColorClick(-1)
                            },
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier
                                .width(40.dp)
                                .aspectRatio(1f)
                                .testTag(ColorDialogTestTags.RESET_COLOR_ITEM),
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
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .testTag(ColorDialogTestTags.RESET_COLOR_ICON),
                                )
                            } else {
                                Icon(
                                    imageVector = SynIcons.FormatColorReset,
                                    contentDescription = "done",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .testTag(ColorDialogTestTags.RESET_COLOR_ICON),
                                )
                            }
                        }
                    }

                    itemsIndexed(AppConstant.noteColors) { index, color ->
                        Surface(
                            onClick = {
                                onDismissRequest()
                                onColorClick(index)
                            },
                            shape = CircleShape,
                            color = Color(color),
                            modifier = Modifier
                                .width(40.dp)
                                .aspectRatio(1f)
                                .testTag("${ColorDialogTestTags.COLOR_PICKER_ITEM}_$index"),
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
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .testTag(ColorDialogTestTags.COLOR_PICKER_ITEM_ICON),
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            modifier = Modifier.testTag(ColorDialogTestTags.DIALOG_ROOT) // DIALOG_ROOT applied here
        )
    }
}

@Preview
@Composable
fun ColorDialogPreview() {
    ColorDialog(
        show = true,
    )
}
