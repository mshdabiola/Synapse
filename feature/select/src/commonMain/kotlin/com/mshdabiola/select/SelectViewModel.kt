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
package com.mshdabiola.select

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteLabelCrossRef
import com.mshdabiola.select.navigation.Select
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SelectViewModel(
    val select: Select,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
) : ViewModel() {
    private val ids = select.ids.split(",")
        .map { it.toLong() }
        .toSet()

    private val notePadLabels = noteLabelRepository
        .getByNoteIds(ids)
    private val labels = labelRepository
        .getAll()
    val initLabelState = SelectUiState()

    @OptIn(FlowPreview::class)
    val selectUiState = combine(
        snapshotFlow { initLabelState.labelQuery.text }
            .debounce(500),
        notePadLabels,
        labels,
    ) { query, notePadLabels, labels ->
        val labelsCount = notePadLabels
            .groupingBy { it.labelId }.eachCount()
        val labelUiStates = labels.map {
            val state = when (labelsCount[it.id]) {
                ids.size -> ToggleableState.On
                null -> ToggleableState.Off
                else -> ToggleableState.Indeterminate
            }
            LabelUiState(it.id, it.name, state)
        }
        var showAddLabel = false
        val list = if (query.isBlank()) {
            labelUiStates
        } else {
            showAddLabel = !labelUiStates.any { it.label == query }
            labelUiStates.filter { it.label.contains(query) }
        }

        SelectUiState(
            labels = list,
            labelQuery = initLabelState.labelQuery,
            showAddLabel = showAddLabel,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = initLabelState,
        )

    fun onCheckClick(index: Int) {
        val labels = selectUiState.value.labels
        var label = labels[index]

        if (label.toggleableState == ToggleableState.Off || label.toggleableState == ToggleableState.Indeterminate) {
            label = label.copy(toggleableState = ToggleableState.On)
            val labelsList = ids.map { NoteLabelCrossRef(noteId = it, labelId = label.id) }
            viewModelScope.launch {
                noteLabelRepository.upserts(labelsList)
            }
        } else {
            label = label.copy(toggleableState = ToggleableState.Off)

            viewModelScope.launch {
                ids.forEach {
                    noteLabelRepository.deleteByNoteIdAndLabelId(it, label.id)
                }
            }
        }
    }

    fun onCreateLabel() {
        viewModelScope.launch {
            val label = Label(
                -1,
                selectUiState.value.labelQuery.text.toString(),
            )
            selectUiState.value.labelQuery.clearText()

            val noteId = labelRepository.upsert(
                label,

                )
            val labelsList = ids.map { NoteLabelCrossRef(noteId = it, labelId = noteId) }
            noteLabelRepository.upserts(labelsList)
        }
    }
}
