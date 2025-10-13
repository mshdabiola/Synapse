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
package com.hobit.synapse.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.hobit.synapse.ui.Compact
import com.hobit.synapse.ui.SynAppState
import com.hobit.synapse.ui.pop
import com.mshdabiola.detail.navigation.detailScreen
import com.mshdabiola.detail.navigation.navigateToDetail
import com.mshdabiola.draw.navigation.Draw
import com.mshdabiola.draw.navigation.drawScreen
import com.mshdabiola.draw.navigation.navigateToDraw
import com.mshdabiola.label.navigation.labelScreen
import com.mshdabiola.main.navigation.mainScreen
import com.mshdabiola.select.navigation.navigateToSelect
import com.mshdabiola.select.navigation.selectScreen
import com.mshdabiola.setting.navigation.settingScreen
import com.mshdabiola.view.navigation.View
import com.mshdabiola.view.navigation.navigateToView
import com.mshdabiola.view.navigation.viewScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SynNavHost(
    appState: SynAppState,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    val onDrawer = if (appState is Compact) {
        {
            appState.coroutineScope.launch {
                appState.drawerState.open()
            }
            Unit
        }
    } else {
        null
    }
    NavDisplay(
        backStack = navController,
        onBack = { navController.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            //  rememberViewModelStoreNavEntryDecorator() //TODO
        ),
        entryProvider = entryProvider {
            mainScreen(
                modifier = Modifier,
                onDrawer = onDrawer,
                navigateToDetail = navController::navigateToDetail,
                navigateToSelectLevel = navController::navigateToSelect,
            )

            detailScreen(
                modifier = Modifier,
                onBack = {
                    appState.dismissIndefiniteSnackbar()
                    navController.pop()
                },
                setNotification = appState::onNotification,
                navigateToGallery = { id, index, total, currentPath ->
                    navController.navigateToView(
                        View(id, index, total, currentPath),
                    )
                },
                navigateToDrawing = { noteId, image ->

                    navController.navigateToDraw(
                        Draw(
                            noteId,
                            image,
                        ),
                    )
                },
                navigateToSelectLevel = navController::navigateToSelect,

            )
            settingScreen(
                modifier = Modifier,
                onDrawer = onDrawer,
                setNotification = appState::onNotification,
            )
            drawScreen(
                onBack = {
                    navController.pop()
                },
            )
            viewScreen(
                onBack = navController::pop,
            )
            labelScreen(
                onBack = navController::pop,
            )
            selectScreen(
                onBack = navController::pop,
            )
        },
    )
}
