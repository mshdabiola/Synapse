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
package com.mshdabiola.select.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mshdabiola.select.SelectLabelScreen
import com.mshdabiola.select.SelectViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parameterSetOf

fun NavController.navigateToSelect(ids:Set<Long>) {

    navigate(Select(ids.joinToString()))
}

@OptIn(KoinExperimentalAPI::class, ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.selectScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    composable<Select> { backStack ->

        val detail: Select = backStack.toRoute()

        val viewModel: SelectViewModel =
            koinViewModel(
                parameters = {
                    parameterSetOf(
                        detail,
                    )
                },
            )
        val uiState = viewModel.selectLabelUiState.collectAsStateWithLifecycle()

        SelectLabelScreen(
            selectLabelUiState = uiState.value,
            onCheckClick = viewModel::onCheckClick,
            onCreateLabel = viewModel::onCreateLabel,
            onBack = onBack,
        )
            }
}
