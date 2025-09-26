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
package com.mshdabiola.view.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mshdabiola.ui.LocalNavAnimatedContentScope
import com.mshdabiola.ui.getPlatformLogics
import com.mshdabiola.view.ViewScreen
import com.mshdabiola.view.ViewViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parameterSetOf

fun NavController.navigateToView(view: View) {
    navigate(view)
}

@OptIn(KoinExperimentalAPI::class, ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.viewScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    composable<View> { backStack ->

        val view: View = backStack.toRoute()

        val viewModel: ViewViewModel =
            koinViewModel(
                parameters = {
                    parameterSetOf(
                        view,
                    )
                },
            )

        val coroutineScope = rememberCoroutineScope()

        val galleryUiState = viewModel.viewUiState.collectAsStateWithLifecycle()
        val pagerState = rememberPagerState(galleryUiState.value.initIndex) {
            galleryUiState.value.images.size
        }

        val logics = getPlatformLogics()

        val onSend = {
            val index = pagerState.currentPage
            val image = galleryUiState.value.images[index]

            logics.shareImage(image.path)
        }
        val onCopy = {
            val index = pagerState.currentPage
            val image = galleryUiState.value.images[index]
            logics.copyImage(image.path)
        }

        CompositionLocalProvider(
            LocalNavAnimatedContentScope provides this,
        ) {
            ViewScreen(
                pagerState = pagerState,
                viewUiState = galleryUiState.value,
                onBack = onBack,
                onToText = {
                    coroutineScope.launch {
                        viewModel.onImage(it)
                        onBack()
                    }
                },
                onSend = onSend,
                onCopy = onCopy,
                onDeleteImage = {
                    val index = pagerState.currentPage
                    val image = galleryUiState.value.images[index]
                    viewModel.deleteImage(image.id, pagerState.pageCount, it)
                },
            )
        }
    }
}
