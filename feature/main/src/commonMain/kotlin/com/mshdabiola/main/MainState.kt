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
package com.mshdabiola.main

import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.Notification

sealed class MainState {
    data object Loading : MainState()
    data class Success(
        val isGrid: Boolean = true,
        val labelName: String? = null,
        val pinNotePads: List<NotePad> = emptyList(),
        val unPinNotePads: List<NotePad> = emptyList(),
        val noteDisplayCategory: NoteDisplayCategory = NoteDisplayCategory(),
        val selectState: SelectState? = null,
    ) : MainState()

    //    data class Error(val message: String) : MainStateN()
}

data class SelectState(
    val colorIndex: Int = -1,
    val isAllPin: Boolean = false,
    val setOfSelected: Set<Long> = emptySet(),
    val notificationUiState: Notification? = null,

    )
