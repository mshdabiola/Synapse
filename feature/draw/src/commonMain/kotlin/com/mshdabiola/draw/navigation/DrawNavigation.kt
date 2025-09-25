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
package com.mshdabiola.draw.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mshdabiola.draw.DrawScreen
import com.mshdabiola.draw.DrawViewModel
import com.mshdabiola.ui.drawPathsOnImage
import com.mshdabiola.ui.getPlatformLogics
import com.mshdabiola.ui.path
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parameterSetOf
import kotlin.math.max
import kotlin.math.min

fun NavController.navigateToDraw(detail: Draw) {
    navigate(detail)
}

@OptIn(KoinExperimentalAPI::class, ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.drawScreen(
    modifier: Modifier = Modifier,
    onBack: (Long?) -> Unit,

) {
    composable<Draw> { backStack ->

        val draw: Draw = backStack.toRoute()

        val viewModel: DrawViewModel =
            koinViewModel(
                parameters = {
                    parameterSetOf(
                        draw,
                    )
                },
            )
        val state = viewModel.drawingState.collectAsStateWithLifecycle()
        val logics = getPlatformLogics()
        val density = LocalDensity.current

        val onSend = {
            val paths = state.value.drawings
            if (paths.isNotEmpty()) {
                // 1. Calculate bounds from all paths
                val bounds = paths.map { it.path.getBounds() }
                    .reduce { acc, rect ->
                        Rect(
                            left = min(acc.left, rect.left),
                            top = min(acc.top, rect.top),
                            right = max(acc.right, rect.right),
                            bottom = max(acc.bottom, rect.bottom),
                        )
                    }

                // 2. Determine target size while preserving aspect ratio
                val aspectRatio = bounds.width / bounds.height.coerceAtLeast(1f)
                val targetWidth = 1080f
                val targetHeight = targetWidth / aspectRatio

                // 3. Create bitmap and draw
                val imageBitmap = ImageBitmap(targetWidth.toInt(), targetHeight.toInt())
                val finalBitmap = drawPathsOnImage(imageBitmap, paths, density)
                logics.shareImage(finalBitmap)
            }
        }
        val onCopy = {
            val paths = state.value.drawings
            if (paths.isNotEmpty()) {
                // 1. Calculate bounds from all paths
                val bounds = paths.map { it.path.getBounds() }
                    .reduce { acc, rect ->
                        Rect(
                            left = min(acc.left, rect.left),
                            top = min(acc.top, rect.top),
                            right = max(acc.right, rect.right),
                            bottom = max(acc.bottom, rect.bottom),
                        )
                    }

                // 2. Determine target size while preserving aspect ratio
                val aspectRatio = bounds.width / bounds.height.coerceAtLeast(1f)
                val targetWidth = 1080f
                val targetHeight = targetWidth / aspectRatio

                // 3. Create bitmap and draw
                val imageBitmap = ImageBitmap(targetWidth.toInt(), targetHeight.toInt())
                val finalBitmap = drawPathsOnImage(imageBitmap, paths, density)
                logics.copyImage(finalBitmap)
            }
        }

        DrawScreen(
            modifier = modifier,
            controller = viewModel.controller,
            drawUiState = state.value,
            onBack = {
                val value = state.value
                if (draw.noteId == null) {
                    onBack(value.noteId!!)
                } else {
                    onBack(null)
                }
            },
            onCopy = onCopy,
            onSend = onSend,
            onDeleteImage = viewModel::deleteDrawing,
        )
    }
}
