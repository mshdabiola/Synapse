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
package com.mshdabiola.view

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import coil3.compose.AsyncImage
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.ui.LocalNavAnimatedContentScope
import com.mshdabiola.ui.LocalSharedTransitionScope
import com.mshdabiola.ui.SharedTransitionContainer
import com.mshdabiola.zoomable.rememberZoomableState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.view.generated.resources.Res
import synapse.feature.view.generated.resources.modules_designsystem_copy
import synapse.feature.view.generated.resources.modules_designsystem_delete
import synapse.feature.view.generated.resources.modules_designsystem_grab_image_text
import synapse.feature.view.generated.resources.modules_designsystem_send
import kotlin.collections.get

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryScreen(
    modifier: Modifier = Modifier,
    galleryUiState: GalleryUiState,
    pagerState: PagerState,
    onBack: () -> Unit = {},
    onToText: (String) -> Unit = {},
    onSend: () -> Unit = {},
    onCopy: () -> Unit = {},
    delete: () -> Unit = {},
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalNavAnimatedContentScope.current
    Scaffold(
        modifier = modifier,
        topBar = {
            GalleryTopAppBar(
                onBack = onBack,
                onDelete = delete,
                onGrabText = { onToText(galleryUiState.images[pagerState.currentPage].path) },
                name = "${pagerState.currentPage + 1} of ${galleryUiState.images.size}",
                onSend = onSend,
                onCopy = onCopy,
            )
        },
    ) { paddingValues ->

        HorizontalPager(
            modifier = Modifier
                .padding(paddingValues)
                .testTag("gallery:pager"),
            state = pagerState,
        ) { page ->
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                val image = galleryUiState.images.getOrNull(page)
                // / currIndex=page
                if (image != null) {
                    with(sharedTransitionScope) {
//                        (
//                            model = "https://example.com/image.jpg",
//                            contentDescription = null,
//                        )
                        AsyncImage(
                            modifier = Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState("image_$page"),
                                    animatedVisibilityScope = animatedContentScope,
                                )
                                .fillMaxSize()
                                .zoomable(rememberZoomableState())
                                .testTag("gallery:image_$page"),
                            model = image.path,
                            contentDescription = "",
                            alignment = Alignment.Center,

                            )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun GalleryScreenPreview() {
    SharedTransitionContainer {
        GalleryScreen(
            galleryUiState = GalleryUiState(
                images = listOf(
                    NoteImage(id = 1),
                    NoteImage(id = 1),
                    NoteImage(id = 1),

                    ),

                ),
            pagerState = rememberPagerState(1) { 2 },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryTopAppBar(
    name: String = "label",
    onBack: () -> Unit = {},
    onDelete: () -> Unit = {},
    onGrabText: () -> Unit = {},
    onSend: () -> Unit = {},
    onCopy: () -> Unit = {},

    ) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("gallery:back_button"),
            ) {
                Icon(
                    imageVector = SynIcons.ArrowBack,
                    contentDescription = "back",
                )
            }
        },
        title = { Text(text = name, modifier = Modifier.testTag("gallery:title")) },
        actions = {
            Box {
                IconButton(
                    onClick = { showDropDown = true },
                    modifier = Modifier.testTag("gallery:more_options_button"),
                ) {
                    Icon(
                        SynIcons.MoreVert,
                        contentDescription = "more",
                    )
                }
                DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Res.string.modules_designsystem_grab_image_text)) },
                        onClick = {
                            showDropDown = false
                            onGrabText()
                        },
                        modifier = Modifier.testTag("gallery:grab_text_menu_item"),
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Res.string.modules_designsystem_copy)) },
                        onClick = {
                            showDropDown = false
                            onCopy()
                        },
                        modifier = Modifier.testTag("gallery:copy_menu_item"),
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Res.string.modules_designsystem_send)) },
                        onClick = {
                            showDropDown = false
                            onSend()
                        },
                        modifier = Modifier.testTag("gallery:send_menu_item"),
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Res.string.modules_designsystem_delete)) },
                        onClick = {
                            showDropDown = false
                            onDelete()
                        },
                        modifier = Modifier.testTag("gallery:delete_menu_item"),
                    )
                }
            }
        },

        )
}
