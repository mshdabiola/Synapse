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
package com.hobit.synapse.ui

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.window.core.layout.WindowSizeClass
import com.mshdabiola.detail.navigation.Detail
import com.mshdabiola.draw.navigation.Draw
import com.mshdabiola.label.navigation.Label
import com.mshdabiola.main.navigation.Main
import com.mshdabiola.main.navigation.navigateToMain
import com.mshdabiola.model.Notification
import com.mshdabiola.model.SnackbarDuration
import com.mshdabiola.model.Type
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.select.navigation.Select
import com.mshdabiola.setting.navigation.Setting
import com.mshdabiola.setting.navigation.navigateToSetting
import com.mshdabiola.view.navigation.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


 val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Main::class, Main.serializer())
            subclass(Detail::class, Detail.serializer())
            subclass(Draw::class, Draw.serializer())
            subclass(Label::class, Label.serializer())
            subclass(Select::class, Select.serializer())
            subclass(Setting::class, Setting.serializer())
            subclass(View::class, View.serializer())
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun rememberSynAppState(
    windowSizeClass: WindowSizeClass,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavBackStack<NavKey> =rememberNavBackStack(config,Main),
    wideNavigationRailState: WideNavigationRailState = rememberWideNavigationRailState(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): SynAppState {
    return remember(
        navController,
        windowSizeClass,
    ) {
        when {
            windowSizeClass.isWidthExpanded -> Expand(navController, snackbarHostState, coroutineScope)
            windowSizeClass.isWidthMedium -> Medium(
                navController,
                snackbarHostState,
                coroutineScope,
                wideNavigationRailState,
            )

            else -> Compact(navController, snackbarHostState, coroutineScope, drawerState)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Stable
sealed class SynAppState(
    open val navController: NavBackStack<NavKey>,
    open val snackbarHostState: SnackbarHostState,
    open val coroutineScope: CoroutineScope,
) {

    val levels = listOf(Main, Setting)

    val currentRoute = snapshotFlow { navController.toList() }
        .map { it.lastOrNull() }
    val isMain = currentRoute
        .map { it == Main }

    val isTopRoute=currentRoute
        .map {curr-> levels.any { it == curr } }
    open val isExpanded = true
    var notificationType: Type = Type.Default

    open fun navigateTopRoute(route: Route) {
        when (route) {
            is Route.Main -> navController.navigateToMain()
            is Route.Setting -> navController.navigateToSetting()
        }
    }

    fun isInCurrentRoute(route: Route, noteDisplayCategory: NoteDisplayCategory): Boolean {

        if (navController.contains(route.path)) {
            return false
        }

        return when (route) {
            is Route.Setting -> true
            is Route.Main -> {
                if (route.noteDisplayCategory.noteCategory == NoteCategory.LABEL) {
                    noteDisplayCategory == route.noteDisplayCategory
                } else {
                    noteDisplayCategory.noteCategory == route.noteDisplayCategory.noteCategory
                }
            }
        }
    }

    fun onNotification(notification: Notification) {
        notificationType = notification.type
        val duration = when (notification.duration) {
            SnackbarDuration.Short -> androidx.compose.material3.SnackbarDuration.Short
            SnackbarDuration.Long -> androidx.compose.material3.SnackbarDuration.Long
            SnackbarDuration.Indefinite -> androidx.compose.material3.SnackbarDuration.Indefinite
        }
        coroutineScope.launch {
            when (notification) {
                is Notification.Message ->
                    snackbarHostState.showSnackbar(
                        notification.message,
                        duration = duration,
                    )

                is Notification.MessageWithAction -> {
                    val result = snackbarHostState.showSnackbar(
                        message = notification.message,
                        actionLabel = notification.action,
                        duration = duration,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        notification.actionCallback()
                    }
                }
            }
        }
    }

    fun dismissIndefiniteSnackbar() {
        snackbarHostState.currentSnackbarData?.let { snackbarData ->
            if (snackbarData.visuals.duration == androidx.compose.material3.SnackbarDuration.Indefinite) {
                snackbarData.dismiss()
            }
        }
    }
}

data class Compact(
    override val navController: NavBackStack<NavKey>,
    override val snackbarHostState: SnackbarHostState,
    override val coroutineScope: CoroutineScope,

    val drawerState: DrawerState,
) : SynAppState(navController, snackbarHostState, coroutineScope) {

    suspend fun onDrawerToggle() {
        if (drawerState.isOpen) {
            drawerState.close()
        } else {
            drawerState.open()
        }
    }
}

data class Medium
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
constructor(
    override val navController: NavBackStack<NavKey>,
    override val snackbarHostState: SnackbarHostState,
    override val coroutineScope: CoroutineScope,

    val wideNavigationRailState: WideNavigationRailState,
) : SynAppState(navController, snackbarHostState, coroutineScope) {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override val isExpanded: Boolean
        get() = wideNavigationRailState.currentValue == WideNavigationRailValue.Expanded

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    fun expand() {
        coroutineScope.launch {
            wideNavigationRailState.expand()
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    fun collapse() {
        coroutineScope.launch {
            wideNavigationRailState.collapse()
        }
    }
}

data class Expand(
    override val navController: NavBackStack<NavKey>,
    override val snackbarHostState: SnackbarHostState,
    override val coroutineScope: CoroutineScope,

) : SynAppState(navController, snackbarHostState, coroutineScope)

@Stable
val WindowSizeClass.isWidthCompact: Boolean
    get() = minWidthDp < WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND

@Stable
inline val WindowSizeClass.isWidthMedium: Boolean
    get() = minWidthDp >= WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND &&
        minWidthDp < WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND

@Stable
inline val WindowSizeClass.isWidthExpanded: Boolean
    get() = minWidthDp >= WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND


fun NavBackStack<NavKey>.pop() {
    removeAt(lastIndex)
}
