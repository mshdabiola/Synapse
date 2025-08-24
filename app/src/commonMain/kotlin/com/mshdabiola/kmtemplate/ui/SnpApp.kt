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
package com.hobit.sypnapsenotepad.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.hobit.sypnapsenotepad.LocalAppLocale
import com.hobit.sypnapsenotepad.MainActivityUiState
import com.hobit.sypnapsenotepad.MainAppViewModel
import com.hobit.sypnapsenotepad.navigation.SnpNavHost
import com.mshdabiola.analytics.AnalyticsHelper
import com.mshdabiola.analytics.LocalAnalyticsHelper
import com.mshdabiola.designsystem.component.SnpBackground
import com.mshdabiola.designsystem.component.SnpGradientBackground
import com.mshdabiola.designsystem.theme.GradientColors
import com.mshdabiola.designsystem.theme.LocalGradientColors
import com.mshdabiola.designsystem.theme.SnpTheme
import com.mshdabiola.model.BuildConfig
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ReleaseInfo
import com.mshdabiola.setting.navigation.getWindowRepository
import com.mshdabiola.ui.LocalSharedTransitionScope
import com.mshdabiola.ui.ReleaseUpdateDialog
import com.mshdabiola.ui.SnpSnackerBar
import com.mshdabiola.ui.semanticsCommon
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

// Test Tags for SnpApp
object SnpAppTestTags {
    const val APP_ROOT_LAYOUT = "Snp_app:root_layout" // For SharedTransitionLayout or SnpBackground
    const val GRADIENT_BACKGROUND = "Snp_app:gradient_background"
    const val MAIN_SCAFFOLD = "Snp_app:main_scaffold" // Instance of SnpScaffold
    const val NAV_HOST = "Snp_app:nav_host"
}

@OptIn(
    KoinExperimentalAPI::class,
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun SnpApp(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    appState: SnpAppState = rememberKmtAppState(
        windowSizeClass = windowSizeClass,
    ),
) {
    val viewModel: MainAppViewModel = koinViewModel()
    val analyticsHelper = koinInject<AnalyticsHelper>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val darkTheme = shouldUseDarkTheme(uiState)
    val languageCode = getLanguage(uiState)
    var releaseInfo by remember { mutableStateOf<ReleaseInfo.NewUpdate?>(null) }
    val windowRepository = getWindowRepository()

    LaunchedEffect(Unit) {
        val info = viewModel.getLatestReleaseInfo(BuildConfig.VERSION_NAME).await()
        when (info) {
            is ReleaseInfo.NewUpdate -> {
                viewModel.log("$info")
                releaseInfo = info
            }
            is ReleaseInfo.Error -> {
                viewModel.log("$info")
            }
            is ReleaseInfo.UpToDate -> {
                viewModel.log("$info")
            }
        }
    }
    SharedTransitionLayout(
        modifier = Modifier.testTag(SnpAppTestTags.APP_ROOT_LAYOUT), // Tagging the outer layout
    ) {
        CompositionLocalProvider(
            LocalAnalyticsHelper provides analyticsHelper,
            LocalSharedTransitionScope provides this,
            LocalAppLocale provides languageCode,

        ) {
            key(languageCode) {
                SnpTheme(
                    contrast = chooseContrast(uiState),
                    darkTheme = darkTheme,
                    disableDynamicTheming = shouldDisableDynamicTheming(uiState),
                ) {
                    SnpBackground {
                        // This could also be APP_ROOT_LAYOUT if preferred
                        SnpGradientBackground(
                            modifier = Modifier.testTag(SnpAppTestTags.GRADIENT_BACKGROUND),
                            gradientColors =
                            if (shouldShowGradientBackground(uiState)) {
                                LocalGradientColors.current
                            } else {
                                GradientColors()
                            },
                        ) {
                            Box {
                                SnpScaffold(
                                    modifier = Modifier
                                        .semanticsCommon {}
                                        .testTag(SnpAppTestTags.MAIN_SCAFFOLD), // Tagging the SnpScaffold instance
                                    containerColor = Color.Transparent,
                                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                                    appState = appState,
                                    snackbarHost = {
                                        SnackbarHost(
                                            appState.snackbarHostState,
                                            snackbar = { snackbarData ->
                                                SnpSnackerBar(
                                                    appState.notificationType,
                                                    snackbarData,
                                                    modifier = Modifier
                                                        .windowInsetsPadding(WindowInsets.safeDrawing),
                                                )
                                            },
                                        )
                                    },
                                ) { padding ->
                                    Column(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(padding)
                                            .consumeWindowInsets(padding)
                                            .windowInsetsPadding(
                                                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                                            ),
                                    ) {
                                        SnpNavHost(
                                            appState = appState,
                                            modifier = Modifier.testTag(SnpAppTestTags.NAV_HOST), // Tagging the NavHost
                                        )
                                    }
                                }

                                if (releaseInfo != null) {
                                    val info = releaseInfo!!
                                    ReleaseUpdateDialog(
                                        releaseInfo = info,
                                        onDownloadClick = { windowRepository.openUrl(info.asset) },
                                        onDismissRequest = { releaseInfo = null },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun chooseContrast(uiState: MainActivityUiState): Int =
    when (uiState) {
        MainActivityUiState.Loading -> 0
        is MainActivityUiState.Success -> uiState.userSettings.contrast
    }

@Composable
private fun shouldDisableDynamicTheming(uiState: MainActivityUiState): Boolean =
    when (uiState) {
        MainActivityUiState.Loading -> false
        is MainActivityUiState.Success -> !uiState.userSettings.useDynamicColor
    }

@Composable
fun shouldUseDarkTheme(uiState: MainActivityUiState): Boolean =
    when (uiState) {
        MainActivityUiState.Loading -> isSystemInDarkTheme()
        is MainActivityUiState.Success ->
            when (uiState.userSettings.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }
    }

@Composable
fun shouldShowGradientBackground(uiState: MainActivityUiState): Boolean =
    when (uiState) {
        MainActivityUiState.Loading -> false
        is MainActivityUiState.Success ->
            uiState.userSettings.shouldShowGradientBackground
    }

@Composable
fun getLanguage(uiState: MainActivityUiState): String =
    when (uiState) {
        MainActivityUiState.Loading -> "en"
        is MainActivityUiState.Success ->
            uiState.userSettings.language
    }
