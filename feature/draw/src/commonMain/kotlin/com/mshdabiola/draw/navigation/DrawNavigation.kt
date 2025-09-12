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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mshdabiola.draw.DrawScreen
import com.mshdabiola.draw.DrawViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parameterSetOf

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

        val onSend = {
//            val file = File(state.value.filePath!!)
//            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
//            val intent = ShareCompat.IntentBuilder(context)
//                .setType("image/*")
//                .setStream(uri)
//                .setChooserTitle("NotePad")
//                .createChooserIntent()
//
//            context.startActivity(intent)
        }
        val onCopy = {
//            val file = File(state.value.filePath!!)
//            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
//
//            val content = context.contentResolver
//            val clip = ClipData.newUri(content, "image", uri)
//            val c = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            c.setPrimaryClip(clip)
        }

        DrawScreen(
            modifier = modifier,
            controller = viewModel.controller,
            drawUiState = state.value,
            onBack = {
                val value = state.value
                if (draw.noteId==null){
                    onBack(value.noteId!!)
                }else{
                    onBack(null)
                }
            },
            onCopy = onCopy,
            onSend = onSend,
            onDeleteImage = {},
        )
    }
}
