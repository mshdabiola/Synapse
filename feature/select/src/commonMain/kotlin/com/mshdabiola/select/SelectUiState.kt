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
