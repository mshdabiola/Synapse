package com.mshdabiola.detail

import androidx.compose.foundation.text.input.TextFieldState
import com.mshdabiola.model.note.NoteItem

data class NoteCheckUiState(
    val id: Long = -1,
    val noteId: Long = 0,
    val content: TextFieldState = TextFieldState(),
    val focus: Boolean = false,
    val isCheck: Boolean = false,
)

fun NoteCheckUiState.toNoteCheck() = NoteItem(
    id = id,
    noteId = noteId,
    content = content.text.toString(),
    isCheck = isCheck,
)

fun NoteItem.toNoteCheckUiState() = NoteCheckUiState(
    id = id,
    noteId = noteId,
    content = TextFieldState(initialText = content),
    focus = false,
    isCheck = isCheck,
)
