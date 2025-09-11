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
package com.mshdabiola.label

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.label.navigation.Label
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class LabelViewModel(
    val label: Label,
    private val labelRepository: LabelRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    private val newLabel = MutableStateFlow(LabelState())

    val labels = labelRepository
        .getAll()

    val labelUiState = combine(
        labels,
        newLabel,
    ) { labels, newLabel ->
        LabelUiState(
            labels = labels.map { it.toLabelState() }.toImmutableList(),
            newLabel = newLabel,
            isEditMode = label.isEditMode,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LabelUiState(),
        )

    fun onAddNew(index: Int) {
        viewModelScope.launch {
            if (index == -1) {
                newLabel.value = LabelState()
                labelRepository.upsert(labelUiState.value.newLabel.toLabel())
            } else {
                labelRepository.upsert(labelUiState.value.labels[index].toLabel())
            }
        }
    }

    fun onDelete(id: Long) {
        viewModelScope.launch {
            launch {
                val noteDisplayCategory = async { userDataRepository.userSettings.first().noteCategory }
                if (noteDisplayCategory.await().noteCategory == NoteCategory.LABEL && noteDisplayCategory.await().labelId == id) {
                    userDataRepository.setNoteCategory(NoteDisplayCategory())
                }
            }
            launch {
                labelRepository.delete(id)
            }
        }
    }
}
