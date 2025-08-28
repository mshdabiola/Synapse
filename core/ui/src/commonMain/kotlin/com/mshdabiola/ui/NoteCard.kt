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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag // Ensure this import is present
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.AppConstant
import com.mshdabiola.model.NoteBg
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.testtag.NoteCardTestTags // Added import
import org.jetbrains.compose.resources.stringResource
import synapse.core.ui.generated.resources.Res
import synapse.core.ui.generated.resources.modules_designsystem_checked_items_value

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    notePad: NotePad,
    isSelect: Boolean = false,
    onCardClick: (Long, Int, Int) -> Unit = { _, _, _ -> },
    onLongClick: (Long) -> Unit = {},
    type: String = "note",
) {
    val unCheckNote by remember(notePad.checks) {
        derivedStateOf { notePad.checks.filter { !it.isCheck } }
    }
    val numberOfChecked by remember(key1 = notePad.checks) {
        derivedStateOf { notePad.checks.count { it.isCheck } }
    }
    val haveVoice by remember(notePad.voices) {
        derivedStateOf { notePad.voices.isNotEmpty() }
    }
    val bg = if (notePad.background != -1) {
        Color.Transparent
    } else {
        if (notePad.color != -1) {
            Color(AppConstant.noteColors[notePad.color])
        } else {
            MaterialTheme.colorScheme.background
        }
    }

    val sColor = if (notePad.background != -1) {
        Color(NoteBg.noteBgs[notePad.background].fgColor)
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val images = remember(notePad.images, notePad.drawings) {
        notePad.getVisuals().reversed().chunked(3)
    }

    val de = LocalDensity.current
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current

    with(sharedTransitionScope) {
        OutlinedCard(
            modifier = modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("${type}_${notePad.id}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                .combinedClickable(
                    onClick = {
                        onCardClick(
                            notePad.id,
                            notePad.color,
                            notePad.background,
                        )
                    },
                    onLongClick = { onLongClick(notePad.id) },
                )
                .testTag(NoteCardTestTags.ROOT_CARD), // Added ROOT_CARD tag
            border = if (isSelect) {
                BorderStroke(3.dp, Color.Blue)
            } else {
                BorderStroke(
                    1.dp,
                    sColor,
                )
            },
            colors = CardDefaults.outlinedCardColors(containerColor = bg),
        ) {
            Box {
                if (notePad.background != -1) {
                    Image(
                        imageVector = SynIcons.getBackGround(NoteBg.noteBgs[notePad.background].bg),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(
                                with(de) { size.width.toDp() },
                                with(de) { size.height.toDp() },
                            )
                            .testTag(NoteCardTestTags.BACKGROUND_IMAGE), // Added BACKGROUND_IMAGE tag
                    )
                }

                Column(
                    Modifier
                        .onSizeChanged {
                            size = it
                        },
                ) {
                    if (images.isNotEmpty()) {
                        images.forEachIndexed { imageRowIndex, imageList -> // Changed to forEachIndexed
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .testTag("${NoteCardTestTags.IMAGE_ROW_PREFIX}_$imageRowIndex"), // Added IMAGE_ROW_PREFIX tag
                            ) {
                                imageList.forEachIndexed { itemIndex, visualItem -> // Changed to forEachIndexed
                                    when (visualItem) {
                                        is NoteImage -> {
                                            AsyncImage(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(100.dp)
                                                    .testTag("${NoteCardTestTags.ASYNC_IMAGE_PREFIX}_${imageRowIndex}_$itemIndex"), // Added ASYNC_IMAGE_PREFIX tag
                                                model = visualItem.path,
                                                contentDescription = "",
                                                contentScale = ContentScale.Crop,
                                            )
                                        }

                                        is NoteDrawing -> {
                                            BoardViewer(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(100.dp)
                                                    .testTag("${NoteCardTestTags.BOARD_VIEWER_PREFIX}_${imageRowIndex}_$itemIndex"), // Added BOARD_VIEWER_PREFIX tag
                                                drawingPaths = visualItem.paths,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!notePad.isImageOnly()) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .testTag(NoteCardTestTags.CONTENT_COLUMN), // Added CONTENT_COLUMN tag
                        ) {
                            Text(
                                text = notePad.title.ifEmpty { notePad.detail },
                                style = if (notePad.title.isNotEmpty()) {
                                    MaterialTheme.typography.titleMedium
                                } else {
                                    MaterialTheme.typography.bodyMedium
                                },
                                maxLines = 10,
                                modifier = Modifier.testTag(NoteCardTestTags.TITLE_TEXT), // Added TITLE_TEXT tag
                            )
                            if (!notePad.isCheck) {
                                if (notePad.title.isNotEmpty()) {
                                    if (notePad.detail.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = notePad.detail,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 10,
                                            modifier = Modifier.testTag(NoteCardTestTags.DETAIL_TEXT), // Added DETAIL_TEXT tag
                                        )
                                    }
                                }
                            } else {
                                unCheckNote.take(10)
                                    .forEachIndexed { index, checkItem -> // Changed to forEachIndexed
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.testTag("${NoteCardTestTags.CHECKLIST_ITEM_ROW_PREFIX}_$index"), // Added CHECKLIST_ITEM_ROW_PREFIX tag
                                        ) {
                                            Icon(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .testTag("${NoteCardTestTags.CHECKLIST_ITEM_ICON_PREFIX}_$index"), // Added CHECKLIST_ITEM_ICON_PREFIX tag
                                                imageVector = SynIcons.CheckBoxOutlineBlank,
                                                contentDescription = "",
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                checkItem.content,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1,
                                                modifier = Modifier.testTag("${NoteCardTestTags.CHECKLIST_ITEM_TEXT_PREFIX}_$index"), // Added CHECKLIST_ITEM_TEXT_PREFIX tag
                                            )
                                        }
                                    }
                                if (unCheckNote.size > 10) {
                                    Text(
                                        text = "....",
                                        modifier = Modifier.testTag(NoteCardTestTags.CHECKLIST_ELLIPSIS_TEXT), // Added CHECKLIST_ELLIPSIS_TEXT tag
                                    )
                                }
                                if (numberOfChecked > 0) {
                                    Text(
                                        text = stringResource(
                                            Res.string.modules_designsystem_checked_items_value,
                                            numberOfChecked,
                                        ),
                                        modifier = Modifier.testTag(NoteCardTestTags.CHECKLIST_COUNT_TEXT), // Added CHECKLIST_COUNT_TEXT tag
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            FlowLayout2(
                                verticalSpacing = 4.dp,
                                modifier = Modifier.testTag(NoteCardTestTags.FOOTER_FLOW_LAYOUT), // Added FOOTER_FLOW_LAYOUT tag
                            ) {
                                if (haveVoice) {
                                    Icon(
                                        imageVector = SynIcons.PlayCircle,
                                        contentDescription = "play",
                                        modifier = Modifier.testTag(NoteCardTestTags.VOICE_ICON), // Added VOICE_ICON tag
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                notePad.notification?.let {
                                    ReminderCard(
                                        notification = it,
                                        color = sColor,
                                        modifier = Modifier.testTag(NoteCardTestTags.REMINDER_CARD), // Added REMINDER_CARD tag
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                notePad.labels.forEachIndexed { index, label -> // Changed to forEachIndexed
                                    LabelCard(
                                        name = label.name,
                                        color = sColor,
                                        modifier = Modifier.testTag("${NoteCardTestTags.LABEL_CARD_PREFIX}_$index"), // Added LABEL_CARD_PREFIX tag
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
