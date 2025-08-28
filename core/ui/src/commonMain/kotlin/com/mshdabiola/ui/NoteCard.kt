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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.AppConstant
import com.mshdabiola.model.NoteBg
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NotePad
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
           Color( AppConstant.noteColors[notePad.color])
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
                ),
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
                        imageVector = SynIcons.getBackGround(NoteBg.noteBgs[notePad.background].bg), //painterResource(id = NoteIcon.background[notePad.note.background].bg),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(
                            with(de) { size.width.toDp() },
                            with(de) { size.height.toDp() },
                        ),
                    )
                }

                Column(
                    Modifier
                        .onSizeChanged {
                            size = it
                        },
                ) {
                    if (images.isNotEmpty()) {
                        images.forEach { imageList ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                            ) {
                                imageList.forEach {
                                    when (it) {
                                        is NoteImage -> {
                                            AsyncImage(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(100.dp),
                                                model = it.path,
                                                contentDescription = "",
                                                contentScale = ContentScale.Crop,
                                            )
                                        }

                                        is NoteDrawing -> {
                                            BoardViewer(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(100.dp),
                                                drawingPaths = it.paths,
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
                                .padding(8.dp),
                        ) {
                            Text(
                                text = notePad.title.ifEmpty { notePad.detail },
                                style = if (notePad.title.isNotEmpty()) {
                                    MaterialTheme.typography.titleMedium
                                } else {
                                    MaterialTheme.typography.bodyMedium
                                },
                                maxLines = 10,
                            )
                            if (!notePad.isCheck) {
                                if (notePad.title.isNotEmpty()) {
                                    if (notePad.detail.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = notePad.detail,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 10,

                                        )
                                    }
                                }
                            } else {
                                unCheckNote.take(10).forEach {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            modifier = Modifier.size(16.dp),
                                            imageVector = SynIcons.CheckBoxOutlineBlank,
                                            contentDescription = "",
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            it.content,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                        )
                                    }
                                }
                                if (unCheckNote.size > 10) {
                                    Text(text = "....")
                                }
                                if (numberOfChecked > 0) {
                                    Text(
                                        text = stringResource(
                                            Res.string.modules_designsystem_checked_items_value,
                                            numberOfChecked,
                                        ),
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            FlowLayout2(
                                verticalSpacing = 4.dp,
                            ) {
                                if (haveVoice) {
                                    Icon(
                                        imageVector = SynIcons.PlayCircle,
                                        contentDescription = "play",
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                notePad.notification?.let {
                                    ReminderCard(
                                        notification = it,
                                        color = sColor,
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                notePad.labels.forEach {
                                    LabelCard(name = it.name, color = sColor)
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
