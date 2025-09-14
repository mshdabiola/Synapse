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
import androidx.navigation.compose.NavHost
import com.hobit.synapse.ui.Compact
import com.hobit.synapse.ui.SynAppState
import com.mshdabiola.detail.navigation.detailScreen
import com.mshdabiola.detail.navigation.navigateToDetail
import com.mshdabiola.draw.navigation.Draw
import com.mshdabiola.draw.navigation.drawScreen
import com.mshdabiola.draw.navigation.navigateToDraw
import com.mshdabiola.label.navigation.labelScreen
import com.mshdabiola.main.navigation.Main
import com.mshdabiola.main.navigation.mainScreen
import com.mshdabiola.model.note.NotePad
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

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Main,
    ) {
        mainScreen(
            modifier = Modifier,
            onDrawer = onDrawer,
            navigateToDetail = navController::navigateToDetail,
            navigateToSelectLevel = navController::navigateToSelect,
            navigateToSearch = {},
        )

        detailScreen(
            modifier = Modifier,
            onBack = {
                appState.dismissIndefiniteSnackbar()
                navController.popBackStack()
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
                navController.popBackStack()
                if (it != null) {
                    navController.navigateToDetail(NotePad(id = it))
                }
            },
        )
        viewScreen(
            onBack = navController::popBackStack,
        )
        labelScreen(
            onBack = navController::popBackStack,
        )
        selectScreen(
            onBack = navController::popBackStack,
        )
    }
}
