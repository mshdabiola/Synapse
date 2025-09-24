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

import androidx.compose.foundation.text.input.TextFieldState
import com.mshdabiola.model.note.NoteItem

data class NoteItemUiState(
    val id: Long = -1,
    val noteId: Long = 0,
    val content: TextFieldState = TextFieldState(),
    val focus: Boolean = false,
    val isCheck: Boolean = false,
)

fun NoteItemUiState.toNoteItem() = NoteItem(
    id = id,
    noteId = noteId,
    content = content.text.toString(),
    isCheck = isCheck,
)

fun NoteItem.toNoteItemUiState() = NoteItemUiState(
    id = id,
    noteId = noteId,
    content = TextFieldState(initialText = content),
    focus = false,
    isCheck = isCheck,
)
