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
package com.mshdabiola.ui.state

data class DateDialogUiData(
    val isEdit: Boolean = false,
    val currentTime: Int = 0,
    val timeData: List<DateListUiState> = emptyList<DateListUiState>(),
    val timeError: Boolean = false,
    val currentDate: Int = 0,
    val dateData: List<DateListUiState> = emptyList<DateListUiState>(),
    val currentInterval: Int = 0,
    val interval: List<DateListUiState> = emptyList<DateListUiState>(),
    val showTimeDialog: Boolean = false,
    val showDateDialog: Boolean = false,
)
