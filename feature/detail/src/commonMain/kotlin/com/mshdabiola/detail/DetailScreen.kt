/*
 *abiola 2022
 */

package com.mshdabiola.detail

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mshdabiola.designsystem.component.SynTextField
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.AppConstant
import com.mshdabiola.model.NoteBg
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NoteLink
import com.mshdabiola.model.note.NoteVoice
import com.mshdabiola.ui.BoardViewer
import com.mshdabiola.ui.FlowLayout2
import com.mshdabiola.ui.LabelCard
import com.mshdabiola.ui.LocalNavAnimatedContentScope
import com.mshdabiola.ui.LocalSharedTransitionScope
import com.mshdabiola.ui.ReminderCard
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.detail.generated.resources.Res
import synapse.feature.detail.generated.resources.modules_designsystem_add_list_item
import synapse.feature.detail.generated.resources.modules_designsystem_checked_items
import synapse.feature.detail.generated.resources.modules_designsystem_delete_checked_items
import synapse.feature.detail.generated.resources.modules_designsystem_edited
import synapse.feature.detail.generated.resources.modules_designsystem_hide_checkboxes
import synapse.feature.detail.generated.resources.modules_designsystem_subject
import synapse.feature.detail.generated.resources.modules_designsystem_title
import synapse.feature.detail.generated.resources.modules_designsystem_uncheck_all_items

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    state: DetailState,
    onBackClick: () -> Unit = {},
    onCheckDelete: (Long) -> Unit = {},
//    onCheck: (Boolean, Long) -> Unit = { _, _ -> },
    addItem: () -> Unit = {},
    playVoice: (Int) -> Unit = {},
    pauseVoice: () -> Unit = {},
    moreOptions: () -> Unit = {},
    noteOption: () -> Unit = {},
//    unCheckAllItems: () -> Unit = {},
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

//    val checkNote by remember(state.checks) {
//        derivedStateOf { state.checks.filter { it.isCheck } }
//    }
//    val notCheckNote by remember(state.checks) {
//        derivedStateOf { state.checks.filter { !it.isCheck } }
//    }
    var showCheckNote by remember {
        mutableStateOf(false)
    }

    val bg = if (notepad.background != -1) {
        Color.Transparent


    } else {
        if (notepad.color != -1) {
            Color(AppConstant.noteColors[notepad.color])
        } else {
            MaterialTheme.colorScheme.surface
        }
    }

    val color =
        if(notepad.color!=-1)
        Color(AppConstant.noteColors[notepad.color])
            else  Color.Transparent

    val sColor = if (notepad.background != -1) {
        Color( NoteBg.noteBgs [notepad.background].fgColor)
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val painter = if (notepad.background != -1) {
        rememberVectorPainter(image = SynIcons.getBackGround(NoteBg.noteBgs [notepad.background].bg))
    } else {
        null
    }

    val images = remember(notepad.images, notepad.drawings) {
        notepad.getVisuals().reversed().chunked(3)
    }

//    LaunchedEffect(
//        key1 = notepad,
//        block = {
//            if (notepad.focus) {
//                subjectFocus.requestFocus()
//            }
//        },
//    )
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalNavAnimatedContentScope.current
    with(sharedTransitionScope) {
        Scaffold(
            containerColor = bg,
            modifier = modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("note_${notepad.id}"),
                    animatedVisibilityScope = animatedContentScope,
                )
                .drawBehind {
                    if (painter != null) {
                        with(painter) {
                            draw(size)
                        }
                    }
                },
            topBar = {
                TopAppBar(
                    title = { },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier.testTag("detail:back"),
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
                            modifier = Modifier.testTag("detail:pin"),
                            onClick = { pinNote() },
                        ) {
                            Icon(

                                imageVector = if (notepad.isPin) SynIcons.PushPinD else SynIcons.PushPin,
                                contentDescription = "pin",
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag("detail:notification"),

                            onClick = { onNotification() },
                        ) {
                            Icon(

                                imageVector = SynIcons.NotificationAdd,
                                contentDescription = "notification",
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag("detail:archive"),

                            onClick = { onArchive() },
                        ) {
                            Icon(

                                imageVector = if (notepad.noteCategory == NoteCategory.ARCHIVE) SynIcons.Unarchive else SynIcons.Archive,
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
                        .testTag("detail:list"),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (images.isNotEmpty()) {
                        item(images) {
                            images.forEach { imageList ->
                                Row(
                                    modifier = Modifier
                                        .testTag("detail:images")
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
                                                        .testTag("detail:drawing_$index")
                                                        .clickable {
                                                            navigateToDrawing(it.id)
                                                        }
                                                        .sharedElement(
                                                            sharedContentState = rememberSharedContentState(
                                                                "drwaing_$index",
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
                                placeholder = stringResource(Res.string.modules_designsystem_title),
                                imeAction = ImeAction.Next,
                                modifier = Modifier
                                    .padding(0.dp)
                                    .weight(1f)
                                    .testTag("detail:title"),

                                )
                            if (notepad.isCheck) {
                                Box {
                                    IconButton(
                                        modifier = Modifier.testTag("detail:morecheck"),
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
                                            modifier = Modifier.testTag("detail:hidecheck"),
                                            text = { Text(text = stringResource(Res.string.modules_designsystem_hide_checkboxes)) },
                                            onClick = {
                                                hideCheckBoxes()
                                                expandCheck = false
                                            },
                                        )
                                        if (state.checks.isNotEmpty()) {
                                            DropdownMenuItem(
                                                modifier = Modifier.testTag("detail:uncheckall"),
                                                text = { Text(text = stringResource(Res.string.modules_designsystem_uncheck_all_items)) },
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
                                                modifier = Modifier.testTag("detail:deletecheck"),
                                                text = { Text(text = stringResource(Res.string.modules_designsystem_delete_checked_items)) },
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
                                placeholder = stringResource(Res.string.modules_designsystem_subject),
                                imeAction = ImeAction.None,
                                keyboardAction = { subjectFocus.freeFocus() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .imePadding()
                                    .focusRequester(subjectFocus)
                                    .testTag("detail:content"),

                                )
                        }
                    }
                    if (notepad.isCheck) {
                        itemsIndexed(state.unChecks, key = { i, it -> it.id }) { index, item ->
                            NoteCheckUi(
                                noteCheckUiState = item,
                                onCheckDelete = {
                                    onCheckDelete(it)
                                    state.unChecks.removeAt(index)
                                },
                                onCheck = {
                                    val value = state.unChecks.removeAt(index)
                                    state.checks.add(value.copy(isCheck = true))
                                    state.checks.sortBy { it.id }
                                },
                                onNextCheck = addItem,
                            )
                        }

                        item {
                            TextButton(
                                modifier = Modifier.testTag("detail:add_check_item_button"),
                                onClick = addItem,
                            ) {
                                Icon(imageVector = SynIcons.Add, contentDescription = "")

                                Text(text = stringResource(Res.string.modules_designsystem_add_list_item))
                            }
                        }

                        if (state.checks.isNotEmpty()) {
                            item {
                                TextButton(onClick = { showCheckNote = !showCheckNote }) {
                                    Icon(
                                        imageVector = if (showCheckNote) SynIcons.More else SynIcons.Less,
                                        contentDescription = "",
                                    )
                                    Text(
                                        text = "${state.checks.size} ${stringResource(Res.string.modules_designsystem_checked_items)}",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                        }

                        if (showCheckNote) {
                            itemsIndexed(state.checks, key = { i, it -> it.id }) { index, item ->
                                NoteCheckUi(
                                    noteCheckUiState = item,
                                    onCheckDelete = {
                                        onCheckDelete(it)
                                        state.checks.removeAt(index)
                                    },
                                    onCheck = {
                                        val value = state.checks.removeAt(index)
                                        state.unChecks.add(value.copy(isCheck = false))
                                        state.unChecks.sortBy { it.id }
                                    },
                                    strickText = true,
                                    onNextCheck = {},
                                )
                            }
                        }
                    }
                    itemsIndexed(
                        items = notepad.voices,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        val playerState =
                            if (state.playerState != null && state.playerState.indexPlaying == index) {
                                state.playerState
                            } else {
                                PlayerState()
                            }
                        NoteVoicePlayer(
                            item,
                            playVoice = { playVoice(index) },
                            pauseVoice = pauseVoice,
                            delete = { deleteVoiceNote(index) },
                            color = sColor,
                            isPlay = playerState.isPlaying,
                            currentProgress = playerState.currentPosition,
                        )
                    }
                    items(items = notepad.uris, key = { it.id }) {
                        NoteUri(uriState = it, sColor)
                    }
                    item {
                        FlowLayout2(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalSpacing = 8.dp,
                        ) {
                            notepad.notification?.let {
                                ReminderCard(
                                    notification = it,
                                    color = sColor,
                                    style = MaterialTheme.typography.bodyLarge,
                                    onClick = showNotificationDialog,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            notepad.labels.forEach {
                                LabelCard(
                                    name = it.name,
                                    color = sColor,
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
                                        .background(color)
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
                        modifier = Modifier.testTag("detail:more"),
                        onClick = { moreOptions() },
                    ) {
                        Icon(
                            imageVector = SynIcons.AddBox,
                            contentDescription = "more note",
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag("detail:colors"),
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
                            text = "${stringResource(Res.string.modules_designsystem_edited)} ${state.updateAt}",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag("detail:options"),
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
fun NoteCheckUi(
    noteCheckUiState: NoteCheckUiState,
    onCheckDelete: (Long) -> Unit = {},
    onCheck: (Boolean) -> Unit = { },
    strickText: Boolean = false,
    onNextCheck: () -> Unit,
) {
    val mutableInteractionSource = remember {
        MutableInteractionSource()
    }
    LaunchedEffect(
        key1 = Unit,
        block = {
            if (noteCheckUiState.id == 1L) {
                mutableInteractionSource.emit(FocusInteraction.Focus())
            }
        },
    )
    val focused by mutableInteractionSource.collectIsFocusedAsState()
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(
        key1 = noteCheckUiState,
        block = {
            if (noteCheckUiState.focus) {
                focusRequester.requestFocus()
            } else {
                focusRequester.freeFocus()
            }
        },
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = noteCheckUiState.isCheck,
            onCheckedChange = { onCheck(it) },
        )
        SynTextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .weight(1f),
            state = noteCheckUiState.content,
            textStyle = if (strickText) TextStyle.Default.copy(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
            interactionSource = mutableInteractionSource,
            trailingIcon = {
                if (focused) {
                    IconButton(
                        onClick = {
                            onCheckDelete(noteCheckUiState.id)
                        },
                    ) {
                        Icon(imageVector = SynIcons.Clear, contentDescription = "")
                    }
                }
            },
            imeAction = ImeAction.Next,
            keyboardAction = { onNextCheck() },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteCheckUiPreview() {
    val noteCheckUiState = NoteCheckUiState(
        id = 1L,
        noteId = 1L,
        content = androidx.compose.foundation.text.input.TextFieldState("Sample content"),
        focus = false,
        isCheck = false
    )
    NoteCheckUi(noteCheckUiState = noteCheckUiState, onNextCheck = {})
}

@Composable
fun NoteVoicePlayer(
    noteVoiceUiState: NoteVoice,
    playVoice: () -> Unit = {},
    pauseVoice: () -> Unit = {},
    delete: () -> Unit = {},
    color: Color = Color.Red,
    isPlay: Boolean = false,
    currentProgress: Int = 0,
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
                        modifier = Modifier.testTag("detail:voice:pause"),
                        onClick = pauseVoice,
                    ) {
                        Icon(imageVector = SynIcons.PauseCircle, contentDescription = "pause")
                    }
                } else {
                    IconButton(
                        modifier = Modifier.testTag("detail:voice:play"),
                        onClick = playVoice,
                    ) {
                        Icon(imageVector = SynIcons.PlayCircle, contentDescription = "play")
                    }
                }
            }
            LinearProgressIndicator(
                progress = { (currentProgress.toFloat() / noteVoiceUiState.length) },
                modifier = Modifier.weight(1f),
            )
            Text(text = noteVoiceUiState.length.toString())
            IconButton(
                modifier = Modifier.testTag("detail:voice:delete"),
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
    onClick:(String)->Unit={}
) {

    ListItem(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
//                val intent = Intent(Intent.ACTION_VIEW).apply {
//                    data = uriState.uri.toUri()
//                }
//                context.startActivity(intent)
            },
        colors = ListItemDefaults.colors(containerColor = color),
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
