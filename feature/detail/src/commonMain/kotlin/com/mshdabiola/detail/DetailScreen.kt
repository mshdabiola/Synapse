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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.designsystem.component.SynTextField
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.designsystem.theme.ColorFamily
import com.mshdabiola.designsystem.theme.LocalExtendedColorScheme
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NoteLink
import com.mshdabiola.model.note.NoteVoice
import com.mshdabiola.model.testtag.DetailScreenTestTags
import com.mshdabiola.ui.BoardViewer
import com.mshdabiola.ui.LabelCard
import com.mshdabiola.ui.LocalNavAnimatedContentScope
import com.mshdabiola.ui.LocalSharedTransitionScope
import com.mshdabiola.ui.ReminderCard
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.detail.generated.resources.Res
import synapse.feature.detail.generated.resources.feature_detail_add_list_item
import synapse.feature.detail.generated.resources.feature_detail_checked_items
import synapse.feature.detail.generated.resources.feature_detail_delete_checked_items
import synapse.feature.detail.generated.resources.feature_detail_edited
import synapse.feature.detail.generated.resources.feature_detail_hide_checkboxes
import synapse.feature.detail.generated.resources.feature_detail_subject
import synapse.feature.detail.generated.resources.feature_detail_title
import synapse.feature.detail.generated.resources.feature_detail_uncheck_all_items

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    state: DetailState,
    onBackClick: () -> Unit = {},
    onCheckDelete: (Int, Boolean) -> Unit = { _, _ -> },
    onCheckChange: (Int, Boolean) -> Unit = { _, _ -> },
    addItem: () -> Unit = {},
    playVoice: (Long) -> Unit = {},
    pauseVoice: () -> Unit = {},
    moreOptions: () -> Unit = {},
    noteOption: () -> Unit = {},
    deleteCheckItems: () -> Unit = {},
    hideCheckBoxes: () -> Unit = {},
    pinNote: () -> Unit = {},
    onLabel: () -> Unit = {},
    onColorClick: () -> Unit = {},
    onNotification: () -> Unit = {},
    showNotificationDialog: () -> Unit = {},
    onArchive: () -> Unit = {},
    deleteVoiceNote: (Int) -> Unit = {},
    navigateToGallery: (Long, Int, Int, String) -> Unit = { _, _, _, _ -> },
    navigateToDrawing: (Long?) -> Unit = {},
) {
    var expandCheck by remember {
        mutableStateOf(false)
    }

    val notepad = remember(state.notePad) {
        state.notePad
    }

    val subjectFocus = remember {
        FocusRequester()
    }

    var showCheckNote by remember {
        mutableStateOf(false)
    }

    val noteColor = if (notepad.background != -1) {
        LocalExtendedColorScheme.current.noteBackGround[notepad.background]

    } else {
        if (notepad.color != -1) {
            LocalExtendedColorScheme.current.noteColor[notepad.color]
        } else {
            ColorFamily(
                color = MaterialTheme.colorScheme.surface,
                colorContainer = MaterialTheme.colorScheme.surfaceContainer,
                onColor = MaterialTheme.colorScheme.onSurface,
                onColorContainer = MaterialTheme.colorScheme.onBackground)
        }
    }


    val painter = if (notepad.background != -1) {
        rememberVectorPainter(image = SynIcons.getBackGround(notepad.background))
    } else {
        null
    }

    val images = remember(notepad.images, notepad.drawings) {
        notepad.getVisuals().reversed().chunked(3)
    }

    LaunchedEffect(
        key1 = Unit,
        block = {
           subjectFocus.requestFocus()
        },
    )
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalNavAnimatedContentScope.current
    var currentNoteItem by remember { mutableStateOf(-1L) }

    with(sharedTransitionScope) {
        Scaffold(
            containerColor = if (notepad.background!=-1) Color.Transparent else noteColor.color,
            contentColor =  noteColor.onColor,
            modifier = modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("note_${notepad.id}"),
                    animatedVisibilityScope = animatedContentScope,
                )
                .drawBehind {
                    if (painter != null) {
                        with(painter) {
                            draw(size,
                                colorFilter = ColorFilter.tint(noteColor.color, blendMode = BlendMode.Darken),

                            )
                        }
                    }
                },
            topBar = {
                TopAppBar(
                    title = { },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier.testTag(DetailScreenTestTags.BACK_BUTTON),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = noteColor.onColor),
                            onClick = { onBackClick() },
                        ) {
                            Icon(
                                imageVector = SynIcons.ArrowBack,
                                contentDescription = "back",
                            )
                        }
                    },

                    actions = {
                        IconButton(
                            modifier = Modifier.testTag(DetailScreenTestTags.PIN_BUTTON),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = noteColor.onColor),
                            onClick = { pinNote() },
                        ) {
                            Icon(

                                imageVector = if (notepad.isPin) SynIcons.PushPinD else SynIcons.PushPin,
                                contentDescription = "pin",
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag(DetailScreenTestTags.NOTIFICATION_BUTTON),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = noteColor.onColor),
                            onClick = { onNotification() },
                        ) {
                            Icon(

                                imageVector = SynIcons.NotificationAdd,
                                contentDescription = "notification",
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag(DetailScreenTestTags.ARCHIVE_BUTTON),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = noteColor.onColor),
                            onClick = { onArchive() },
                        ) {
                            Icon(

                                imageVector = if (notepad.noteCategory ==
                                    NoteCategory.ARCHIVE
                                ) {
                                    SynIcons.Unarchive
                                } else {
                                    SynIcons.Archive
                                },
                                contentDescription = "archive",
                            )
                        }
                    },
                )
            },

        ) { paddingValues ->
            Column(
                Modifier
                    .padding(paddingValues)
                    .fillMaxHeight(),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .testTag(DetailScreenTestTags.DETAIL_LIST),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (images.isNotEmpty()) {
                        item(images) {
                            images.forEach { imageList ->
                                Row(
                                    modifier = Modifier
                                        .testTag(DetailScreenTestTags.IMAGE_LIST)
                                        .fillMaxWidth()
                                        .height(200.dp),
                                ) {
                                    imageList.forEachIndexed { index, it ->
                                        when (it) {
                                            is NoteImage -> {
                                                AsyncImage(
                                                    modifier = Modifier
                                                        .clickable {
                                                            navigateToGallery(
                                                                notepad.id,
                                                                index,
                                                                imageList.size,
                                                                it.path,
                                                            )
                                                        }
                                                        .sharedElement(
                                                            sharedContentState = rememberSharedContentState(
                                                                "image_$index",
                                                            ),
                                                            animatedVisibilityScope = animatedContentScope,

                                                        )
                                                        .weight(1f)
                                                        .height(200.dp),
                                                    model = it.path,
                                                    contentDescription = "note image",
                                                    contentScale = ContentScale.Crop,
                                                )
                                            }

                                            is NoteDrawing -> {
                                                BoardViewer(
                                                    modifier = Modifier
                                                        .testTag(DetailScreenTestTags.DRAWING_ITEM + "_$index")
                                                        .clickable {
                                                            navigateToDrawing(it.id)
                                                        }
                                                        .sharedElement(
                                                            sharedContentState = rememberSharedContentState(
                                                                "drawing_$index",
                                                            ),
                                                            animatedVisibilityScope = animatedContentScope,

                                                        )
                                                        .weight(1f)
                                                        .height(200.dp),
                                                    drawingPaths = it.paths,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SynTextField(
                                state = state.title,
                                placeholder = stringResource(Res.string.feature_detail_title),
                                imeAction = ImeAction.Next,
                                maxNum = TextFieldLineLimits.SingleLine,
                                color = noteColor.onColor,
                                modifier = Modifier
                                    .padding(0.dp)
                                    .weight(1f)
                                    .testTag(DetailScreenTestTags.TITLE_TEXT_FIELD),

                            )
                            if (notepad.isCheck) {
                                Box {
                                    IconButton(
                                        modifier = Modifier.testTag(DetailScreenTestTags.MORE_CHECK_BUTTON),
                                        onClick = { expandCheck = true },
                                    ) {
                                        Icon(
                                            imageVector = SynIcons.MoreVert,
                                            contentDescription = "",
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expandCheck,
                                        onDismissRequest = { expandCheck = false },
                                    ) {
                                        DropdownMenuItem(
                                            modifier = Modifier.testTag(DetailScreenTestTags.HIDE_CHECK_MENU_ITEM),
                                            text = {
                                                Text(
                                                    text = stringResource(
                                                        Res.string.feature_detail_hide_checkboxes,
                                                    ),
                                                )
                                            },
                                            onClick = {
                                                hideCheckBoxes()
                                                expandCheck = false
                                            },
                                        )
                                        if (state.checks.isNotEmpty()) {
                                            DropdownMenuItem(
                                                modifier = Modifier.testTag(DetailScreenTestTags.UNCHECK_ALL_MENU_ITEM),
                                                text = {
                                                    Text(
                                                        text = stringResource(
                                                            Res.string.feature_detail_uncheck_all_items,
                                                        ),
                                                    )
                                                },
                                                onClick = {
                                                    val checks = state.checks
                                                        .map { it.copy(isCheck = false) }
                                                    state.checks.clear()
                                                    state.unChecks.addAll(checks)
                                                    state.unChecks.sortBy { it.id }
//                                                unCheckAllItems()
                                                    expandCheck = false
                                                },
                                            )
                                            DropdownMenuItem(
                                                modifier = Modifier.testTag(
                                                    DetailScreenTestTags.DELETE_CHECK_MENU_ITEM,
                                                ),
                                                text = {
                                                    Text(
                                                        text = stringResource(
                                                            Res.string.feature_detail_delete_checked_items,
                                                        ),
                                                    )
                                                },
                                                onClick = {
                                                    deleteCheckItems()
                                                    expandCheck = false
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!notepad.isCheck) {
                        item {
                            SynTextField(
                                state = state.detail,
                                placeholder = stringResource(Res.string.feature_detail_subject),
                                imeAction = ImeAction.None,
                                keyboardAction = { subjectFocus.freeFocus() },
                                color = noteColor.onColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .imePadding()
                                    .focusRequester(subjectFocus)
                                    .testTag(DetailScreenTestTags.CONTENT_TEXT_FIELD),

                            )
                        }
                    }
                    if (notepad.isCheck) {
                        itemsIndexed(state.unChecks, key = { i, it -> it.id }) { index, item ->
                            NoteItemUi(
                                noteItemUiState = item,
                                isCurrentFocus = currentNoteItem==item.id,
                                onCheckDelete = {
                                    onCheckDelete(index, false)
                                },
                                onCheck = {
                                    onCheckChange(index, false)
                                },
                                onNextCheck = addItem,
                                onFocus = {currentNoteItem=item.id}
                            )
                        }

                        item {
                            SynTextButton(
                                modifier = Modifier.testTag(DetailScreenTestTags.ADD_CHECK_ITEM_BUTTON),
                                onClick = addItem,
                                icon = SynIcons.Add,
                                label = stringResource(Res.string.feature_detail_add_list_item),
                            )
                        }

                        if (state.checks.isNotEmpty()) {
                            item {
                                SynTextButton(
                                    onClick = { showCheckNote = !showCheckNote },
                                    icon = if (showCheckNote) SynIcons.More else SynIcons.Less,
                                    label = "${state.checks.size} ${stringResource(
                                        Res.string.feature_detail_checked_items,
                                    )}",
                                )
                            }
                        }

                        if (showCheckNote) {
                            itemsIndexed(state.checks, key = { i, it -> it.id }) { index, item ->
                                NoteItemUi(
                                    noteItemUiState = item,
                                    isCurrentFocus = currentNoteItem==item.id,

                                    onCheckDelete = {
                                        onCheckDelete(index, true)
                                    },
                                    onCheck = {
                                        onCheckChange(index, true)
                                    },
                                    strickText = true,
                                    onNextCheck = {},
                                    onFocus = {currentNoteItem=item.id}

                                )
                            }
                        }
                    }
                    itemsIndexed(
                        items = notepad.voices,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        val playerState =
                            if (state.playerState != null && state.playerState.currentNoteVoiceId == item.id) {
                                state.playerState
                            } else {
                                PlayerState()
                            }
                        NoteVoicePlayer(
                            item,
                            playVoice = { playVoice(item.id) },
                            pauseVoice = pauseVoice,
                            delete = { deleteVoiceNote(index) },
                            color = noteColor.color,
                            isPlay = playerState.isPlaying,
                            progress = playerState.progress,
                        )
                    }
                    items(items = notepad.uris, key = { it.id }) {
                        NoteUri(uriState = it,  color = noteColor.colorContainer,
                            contentColor = noteColor.onColorContainer)
                    }
                    item {
                        FlowRow(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            notepad.notification?.let {
                                ReminderCard(
                                    notification = it,
                                    color = noteColor.colorContainer,
                                    contentColor = noteColor.onColorContainer,
                                    style = MaterialTheme.typography.bodyLarge,
                                    onClick = showNotificationDialog,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            notepad.labels.forEach {
                                LabelCard(
                                    name = it.name,
                                    color = noteColor.colorContainer,
                                    contentColor = noteColor.onColorContainer,
                                    style = MaterialTheme.typography.bodyLarge,
                                    onClick = onLabel,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            if (notepad.background > -1 && notepad.color > -1) {
                                Box(
                                    modifier = Modifier
                                        .clickable { onColorClick() }
                                        .clip(CircleShape)
                                        .background(noteColor.color)
                                        .border(1.dp, Color.Gray, CircleShape)
                                        .size(30.dp),

                                )
                            }
                        }
                    }
//                item {
//                    AsyncImage(modifier = Modifier.size(200.dp), model = "https://icon.horse/icon/fb.com", contentDescription = "")
//                }
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        modifier = Modifier.testTag(DetailScreenTestTags.MORE_BUTTON),
                        onClick = { moreOptions() },
                    ) {
                        Icon(
                            imageVector = SynIcons.AddBox,
                            contentDescription = "more note",
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag(DetailScreenTestTags.COLORS_BUTTON),
                        onClick = { onColorClick() },
                    ) {
                        Icon(
                            imageVector = SynIcons.ColorLens,
                            contentDescription = "colors",
                        )
                    }
                    Row(
                        Modifier
                            .weight(1f)
                            .padding(end = 32.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "${stringResource(Res.string.feature_detail_edited)} ${state.updateAt}",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag(DetailScreenTestTags.OPTIONS_BUTTON),
                        onClick = { noteOption() },
                    ) {
                        Icon(
                            imageVector = SynIcons.MoreVert,
                            contentDescription = "note options",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItemUi(
    noteItemUiState: NoteItemUiState,
    isCurrentFocus:Boolean,
    onCheckDelete: (Long) -> Unit = {},
    onCheck: (Boolean) -> Unit = { },
    strickText: Boolean = false,
    onNextCheck: () -> Unit,
    onFocus:()->Unit={}
) {
    val mutableInteractionSource = remember {
        MutableInteractionSource()
    }
    LaunchedEffect(
        key1 = Unit,
        block = {
            if (noteItemUiState.id == 1L) {
                mutableInteractionSource.emit(FocusInteraction.Focus())
            }
        },
    )
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(
        key1 = noteItemUiState,
        block = {
            if (noteItemUiState.focus) {
                focusRequester.requestFocus()
            } else {
                focusRequester.freeFocus()
            }
        },
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = noteItemUiState.isCheck,
            onCheckedChange = {
                onFocus()
                onCheck(it) },
        )
        SynTextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged{
                    if(it.isFocused){
                        onFocus()
                    }
                }
                .weight(1f),
            state = noteItemUiState.content,
            textStyle = if (strickText) {
                TextStyle.Default.copy(
                    textDecoration = TextDecoration.LineThrough,
                )
            } else {
                TextStyle.Default
            },
            interactionSource = mutableInteractionSource,
            trailingIcon = {
                if (isCurrentFocus) {
                IconButton(
                    onClick = {
                        onCheckDelete(noteItemUiState.id)
                    },
                ) {
                    Icon(imageVector = SynIcons.Clear, contentDescription = "")
                }
                }
            },
            imeAction = ImeAction.Next,
            maxNum = TextFieldLineLimits.SingleLine,
            keyboardAction = { onNextCheck() },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteCheckUiPreview() {
    val noteItemUiState = NoteItemUiState(
        id = 1L,
        noteId = 1L,
        content = androidx.compose.foundation.text.input.TextFieldState("Sample content"),
        focus = false,
        isCheck = false,
    )
    NoteItemUi(noteItemUiState = noteItemUiState,isCurrentFocus = true, onNextCheck = {}, onFocus = {})
}

@Composable
fun NoteVoicePlayer(
    noteVoiceUiState: NoteVoice,
    playVoice: () -> Unit = {},
    pauseVoice: () -> Unit = {},
    delete: () -> Unit = {},
    color: Color = Color.Red,
    isPlay: Boolean = false,
    progress : Float=0f
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
        color = color,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                if (isPlay) {
                    IconButton(
                        modifier = Modifier.testTag(DetailScreenTestTags.VOICE_PAUSE_BUTTON),
                        onClick = pauseVoice,
                    ) {
                        Icon(imageVector = SynIcons.PauseCircle, contentDescription = "pause")
                    }
                } else {
                    IconButton(
                        modifier = Modifier.testTag(DetailScreenTestTags.VOICE_PLAY_BUTTON),
                        onClick = playVoice,
                    ) {
                        Icon(imageVector = SynIcons.PlayCircle, contentDescription = "play")
                    }
                }
            }
            LinearProgressIndicator(
                progress = { progress},
                modifier = Modifier.weight(1f),
            )
            IconButton(
                modifier = Modifier.testTag(DetailScreenTestTags.VOICE_DELETE_BUTTON),
                onClick = { delete() },
            ) {
                Icon(imageVector = SynIcons.Delete, contentDescription = "delete")
            }
        }
    }
}

@Preview
@Composable
fun NoteVoicePlayerPreview() {
    NoteVoicePlayer(
        NoteVoice(3, 4, "", length = 14),

    )
}

@Composable
fun NoteUri(
    uriState: NoteLink,
    color: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: (String) -> Unit = {},
) {
    ListItem(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
                onClick(uriState.path)
            },
        colors = ListItemDefaults.colors(containerColor = color, headlineColor = contentColor, supportingColor = contentColor),
        leadingContent = {
            AsyncImage(
                modifier = Modifier.size(64.dp),
                model = uriState.icon,
                contentDescription = "icon",
            )
        },
        headlineContent = { Text(text = uriState.path) },
        supportingContent = { Text(text = uriState.path, maxLines = 2) },
        shadowElevation = 8.dp,
        tonalElevation = 8.dp,
    )
}
