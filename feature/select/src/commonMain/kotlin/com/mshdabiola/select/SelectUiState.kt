package com.mshdabiola.select

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.state.ToggleableState
import com.mshdabiola.model.note.Label

data class SelectUiState(
    val labels: List<LabelUiState> = emptyList(),
    val labelQuery: TextFieldState = TextFieldState(""),
    val showAddLabel: Boolean = false,
)

data class LabelUiState(
    val id: Long = -1,
    val label: String = "",
    val toggleableState: ToggleableState = ToggleableState.Off,
)

fun Label.toLabelState() = LabelUiState(id = this.id, label = this.name)
