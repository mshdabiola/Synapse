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
package com.mshdabiola.draw

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.ui.Board
import com.mshdabiola.ui.DrawingBar
import com.mshdabiola.ui.DrawingController
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.draw.generated.resources.Res
import synapse.feature.draw.generated.resources.modules_designsystem_copy
import synapse.feature.draw.generated.resources.modules_designsystem_delete
import synapse.feature.draw.generated.resources.modules_designsystem_drawing
import synapse.feature.draw.generated.resources.modules_designsystem_send


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(
    modifier: Modifier = Modifier,
    onBackk: () -> Unit = {},
    controller: DrawingController = remember { DrawingController() },
    drawingUiState: DrawingUiState = DrawingUiState(),
    onDeleteImage: () -> Unit = {},
    onCopy: () -> Unit = {},
    onSend: () -> Unit = {},
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackk,
                        modifier = Modifier.testTag("drawing:back_button"),
                    ) {
                        Icon(
                            imageVector = SynIcons.ArrowBack,
                            contentDescription = "back",
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.modules_designsystem_drawing),
                        modifier = Modifier.testTag("drawing:title"),
                    )
                },

                actions = {
                    IconButton(
                        enabled = controller.canUndo,
                        onClick = { controller.undo() },
                        modifier = Modifier.testTag("drawing:undo_button"),
                    ) {
                        Icon(imageVector = SynIcons.Undo, contentDescription = "undo")
                    }
                    IconButton(
                        enabled = controller.canRedo,
                        onClick = { controller.redo() },
                        modifier = Modifier.testTag("drawing:redo_button"),
                    ) {
                        Icon(imageVector = SynIcons.Redo, contentDescription = "redo")
                    }
                    Box {
                        IconButton(
                            onClick = { showDropDown = true },
                            enabled = drawingUiState.drawings.isNotEmpty(),
                            modifier = Modifier.testTag("drawing:more_options_button"),
                        ) {
                            Icon(SynIcons.MoreVert, contentDescription = "more")
                        }
                        DropdownMenu(
                            expanded = showDropDown,
                            onDismissRequest = { showDropDown = false },
                        ) {
//                            DropdownMenuItem(
//                                text = { Text(text = stringResource(Res.string.modules_designsystem_grab_image_text)) },
//                                onClick = {
//                                    showDropDown = false
//                                    //  onGrabText()
//                                },
//                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.modules_designsystem_copy)) },
                                onClick = {
                                    showDropDown = false
                                    onCopy()
                                },
                                modifier = Modifier.testTag("drawing:copy_menu_item"),
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.modules_designsystem_send)) },
                                onClick = {
                                    showDropDown = false
                                    onSend()
                                },
                                modifier = Modifier.testTag("drawing:send_menu_item"),
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.modules_designsystem_delete)) },
                                onClick = {
                                    showDropDown = false
                                    onDeleteImage()
                                },
                                modifier = Modifier.testTag("drawing:delete_menu_item"),
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            DrawingBar(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .testTag("drawing:drawing_bar"),
                controller = controller,
            )
        },
    ) { paddingValues: PaddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Board(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("drawing:board"),
                controller = controller,
            )
        }
    }
}

@Preview
@Composable
fun DrawingScreenPreview() {
    DrawScreen()
}
