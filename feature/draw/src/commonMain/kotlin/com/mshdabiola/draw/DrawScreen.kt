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
import com.mshdabiola.model.testtag.DrawScreenTestTags // Added import
import com.mshdabiola.ui.Board
import com.mshdabiola.ui.DrawingBar
import com.mshdabiola.ui.DrawingController
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.draw.generated.resources.Res
import synapse.feature.draw.generated.resources.feature_draw_back_cd
import synapse.feature.draw.generated.resources.feature_draw_copy
import synapse.feature.draw.generated.resources.feature_draw_delete
import synapse.feature.draw.generated.resources.feature_draw_drawing
import synapse.feature.draw.generated.resources.feature_draw_more_options_cd
import synapse.feature.draw.generated.resources.feature_draw_redo_cd
import synapse.feature.draw.generated.resources.feature_draw_send
import synapse.feature.draw.generated.resources.feature_draw_undo_cd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    controller: DrawingController = remember { DrawingController() },
    drawUiState: DrawUiState = DrawUiState(),
    onDeleteImage: (onComplete: (() -> Unit)?) -> Unit = {},
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
                        onClick = onBack,
                        modifier = Modifier.testTag(DrawScreenTestTags.BACK_BUTTON),
                    ) {
                        Icon(
                            imageVector = SynIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.feature_draw_back_cd),
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.feature_draw_drawing),
                        modifier = Modifier.testTag(DrawScreenTestTags.TITLE),
                    )
                },

                actions = {
                    IconButton(
                        enabled = controller.canUndo,
                        onClick = { controller.undo() },
                        modifier = Modifier.testTag(DrawScreenTestTags.UNDO_BUTTON),
                    ) {
                        Icon(
                            imageVector = SynIcons.Undo,
                            contentDescription = stringResource(Res.string.feature_draw_undo_cd),
                        )
                    }
                    IconButton(
                        enabled = controller.canRedo,
                        onClick = { controller.redo() },
                        modifier = Modifier.testTag(DrawScreenTestTags.REDO_BUTTON),
                    ) {
                        Icon(
                            imageVector = SynIcons.Redo,
                            contentDescription = stringResource(Res.string.feature_draw_redo_cd),
                        )
                    }
                    Box {
                        IconButton(
                            onClick = { showDropDown = true },
                            enabled = drawUiState.drawings.isNotEmpty(),
                            modifier = Modifier.testTag(DrawScreenTestTags.MORE_OPTIONS_BUTTON),
                        ) {
                            Icon(
                                SynIcons.MoreVert,
                                contentDescription = stringResource(Res.string.feature_draw_more_options_cd),
                            )
                        }
                        DropdownMenu(
                            expanded = showDropDown,
                            onDismissRequest = { showDropDown = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.feature_draw_copy)) },
                                onClick = {
                                    showDropDown = false
                                    onCopy()
                                },
                                modifier = Modifier.testTag(DrawScreenTestTags.COPY_MENU_ITEM),
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.feature_draw_send)) },
                                onClick = {
                                    showDropDown = false
                                    onSend()
                                },
                                modifier = Modifier.testTag(DrawScreenTestTags.SEND_MENU_ITEM),
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.feature_draw_delete)) },
                                onClick = {
                                    showDropDown = false
                                    onDeleteImage { onBack() }
                                },
                                modifier = Modifier.testTag(DrawScreenTestTags.DELETE_MENU_ITEM),
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
                    .testTag(DrawScreenTestTags.DRAWING_BAR),
                controller = controller,
            )
        },
    ) { paddingValues: PaddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Board(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(DrawScreenTestTags.BOARD),
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
