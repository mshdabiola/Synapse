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
package com.mshdabiola.label.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.mshdabiola.label.LabelScreen
import com.mshdabiola.label.LabelViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parameterSetOf

fun NavBackStack<NavKey>.navigateToLabel(isEdit: Boolean) {
    add(Label(isEdit))
}

@OptIn(KoinExperimentalAPI::class, ExperimentalSharedTransitionApi::class)
fun EntryProviderBuilder<NavKey>.labelScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    entry<Label> { label ->

        val viewModel: LabelViewModel =
            koinViewModel(
                parameters = {
                    parameterSetOf(
                        label,
                    )
                },
            )
        val labelUiState = viewModel.labelUiState.collectAsStateWithLifecycle()

        LabelScreen(
            labelUiState = labelUiState.value,
            onBack = onBack,
            onDelete = viewModel::onDelete,
            onAdd = viewModel::onAddNew,
        )
    }
}
