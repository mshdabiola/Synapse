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
import com.mshdabiola.view.GalleryScreen
import com.mshdabiola.view.ViewViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parameterSetOf
import kotlin.collections.get

fun NavController.navigateToView(view: View) {

    navigate(view)
}

@OptIn(KoinExperimentalAPI::class, ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.viewScreen(
    modifier: Modifier = Modifier,
    onBack :()->Unit
) {
    composable<View> { backStack ->

        val detail: View = backStack.toRoute()

        val viewModel: ViewViewModel =
            koinViewModel(
                parameters = {
                    parameterSetOf(
                        detail,
                    )
                },
            )

        val coroutineScope = rememberCoroutineScope()


        val galleryUiState = viewModel.galleryUiState.collectAsStateWithLifecycle()
        val pagerState = rememberPagerState(galleryUiState.value.initIndex) {
            galleryUiState.value.images.size
        }

//        LaunchedEffect(galleryUiState.value.initIndex) {
//            pagerState.scrollToPage(galleryUiState.value.initIndex)
//        }

        val onSend = {
//            val index = pagerState.currentPage
//            val image = galleryUiState.value.images[index]
//
//            val file = File(image.path)
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
//            val index = pagerState.currentPage
//            val image = galleryUiState.value.images[index]
//            val file = File(image.path)
//            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
//
//            val content = context.contentResolver
//            val clip = ClipData.newUri(content, "image", uri)
//            val c = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            c.setPrimaryClip(clip)
        }
        val delete = {
            val index = pagerState.currentPage
            val image = galleryUiState.value.images[index]
            viewModel.deleteImage(image.id)
        }
        CompositionLocalProvider(
            LocalNavAnimatedContentScope provides this,
        ) {
            GalleryScreen(
                pagerState = pagerState,
                galleryUiState = galleryUiState.value,
                onBack = onBack,
                onToText = {
                    coroutineScope.launch {
                        viewModel.onImage(it)
                        onBack()
                    }
                },
                onSend = onSend,
                onCopy = onCopy,
                delete = delete,
            )
        }
    }
}
