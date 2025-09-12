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
import com.hobit.synapse.LocalAppLocale
import com.hobit.synapse.MainActivityUiState
import com.hobit.synapse.MainAppViewModel
import com.hobit.synapse.navigation.SynNavHost
import com.mshdabiola.analytics.AnalyticsHelper
import com.mshdabiola.analytics.LocalAnalyticsHelper
import com.mshdabiola.designsystem.component.SynBackground
import com.mshdabiola.designsystem.component.SynGradientBackground
import com.mshdabiola.designsystem.theme.GradientColors
import com.mshdabiola.designsystem.theme.LocalGradientColors
import com.mshdabiola.designsystem.theme.SynTheme
import com.mshdabiola.detail.navigation.navigateToDetail
import com.mshdabiola.draw.navigation.Draw
import com.mshdabiola.draw.navigation.navigateToDraw
import com.mshdabiola.model.BuildConfig
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ReleaseInfo
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.NoteType
import com.mshdabiola.model.note.NoteVoice
import com.mshdabiola.ui.ChooseImageDialog
import com.mshdabiola.ui.KmtSnackerBar
import com.mshdabiola.ui.LocalSharedTransitionScope
import com.mshdabiola.ui.ReleaseUpdateDialog
import com.mshdabiola.ui.getPlatformLogics
import com.mshdabiola.ui.semanticsCommon
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

// Test Tags for KmtApp
object SynAppTestTags {
    const val APP_ROOT_LAYOUT = "kmt_app:root_layout" // For SharedTransitionLayout or KmtBackground
    const val GRADIENT_BACKGROUND = "kmt_app:gradient_background"
    const val MAIN_SCAFFOLD = "kmt_app:main_scaffold" // Instance of KmtScaffold
    const val NAV_HOST = "kmt_app:nav_host"
}

@OptIn(
    KoinExperimentalAPI::class,
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun SynApp(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    appState: SynAppState = rememberSynAppState(
        windowSizeClass = windowSizeClass,
    ),
) {
    val viewModel: MainAppViewModel = koinViewModel()
    val analyticsHelper = koinInject<AnalyticsHelper>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val darkTheme = shouldUseDarkTheme(uiState)
    val languageCode = getLanguage(uiState)
    var releaseInfo by remember { mutableStateOf<ReleaseInfo.NewUpdate?>(null) }
    val logics = getPlatformLogics(
        outputVoice = { uri, text ->
            appState.coroutineScope.launch {
                val voice = viewModel.copyImageToInternal(uri)
                appState.navController.navigateToDetail(
                    NotePad(
                        detail = text,
                        voices = listOf(NoteVoice(id = -1, path = voice)),
                    ),
                )
            }
        },
    )
    var showImage by remember { mutableStateOf(false) }

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
        modifier = Modifier.testTag(SynAppTestTags.APP_ROOT_LAYOUT), // Tagging the outer layout
    ) {
        CompositionLocalProvider(
            LocalAnalyticsHelper provides analyticsHelper,
            LocalSharedTransitionScope provides this,
            LocalAppLocale provides languageCode,

        ) {
            key(languageCode) {
                SynTheme(
                    contrast = chooseContrast(uiState),
                    darkTheme = darkTheme,
                    disableDynamicTheming = shouldDisableDynamicTheming(uiState),
                ) {
                    SynBackground {
                        SynGradientBackground(
                            modifier = Modifier.testTag(SynAppTestTags.GRADIENT_BACKGROUND),
                            gradientColors =
                            if (shouldShowGradientBackground(uiState)) {
                                LocalGradientColors.current
                            } else {
                                GradientColors()
                            },
                        ) {
                            Box {
                                SynScaffold(
                                    modifier = Modifier
                                        .semanticsCommon {}
                                        .testTag(SynAppTestTags.MAIN_SCAFFOLD), // Tagging the KmtScaffold instance
                                    containerColor = Color.Transparent,
                                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                                    appState = appState,
                                    snackbarHost = {
                                        SnackbarHost(
                                            appState.snackbarHostState,
                                            snackbar = { snackbarData ->
                                                KmtSnackerBar(
                                                    appState.notificationType,
                                                    snackbarData,
                                                    modifier = Modifier
                                                        .windowInsetsPadding(WindowInsets.safeDrawing),
                                                )
                                            },
                                        )
                                    },
                                    noteDisplayCategory = getNoteCategory(uiState),
                                    labels = getLabels(uiState),
                                    onNavigation = viewModel::setMainData,
                                    isVoiceAvailable = logics.isVoiceAvailable(),
                                    onAddNote = {
                                        when (it) {
                                            NoteType.Text -> {
                                                appState.navController.navigateToDetail(NotePad())
                                            }
                                            NoteType.Voice -> {
                                                logics.openVoice()
                                            }
                                            NoteType.Image -> {
                                                showImage = true
                                            }
                                            NoteType.Drawing -> {
                                                appState.navController.navigateToDraw(Draw(null,null))
                                            }
                                            NoteType.List -> {
                                                appState.navController.navigateToDetail(NotePad(isCheck = true))
                                            }
                                        }
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
                                        SynNavHost(
                                            appState = appState,
                                            modifier = Modifier.testTag(SynAppTestTags.NAV_HOST), // Tagging the NavHost
                                        )
                                    }
                                }

                                ChooseImageDialog(
                                    show = showImage,
                                    dismiss = { showImage = false },
                                    getUri = viewModel::pictureUri,
                                    saveImage = {
                                        appState.coroutineScope.launch {
                                            val image = viewModel.copyImageToInternal(it)
                                            appState.navController.navigateToDetail(
                                                NotePad(
                                                    images = listOf(NoteImage(path = image)),
                                                ),
                                            )
                                        }
                                    },
                                )

                                if (releaseInfo != null) {
                                    val info = releaseInfo!!
                                    ReleaseUpdateDialog(
                                        releaseInfo = info,
                                        onDownloadClick = { logics.openUrl(info.asset) },
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

@Composable
fun getLabels(uiState: MainActivityUiState): List<Label> =
    when (uiState) {
        MainActivityUiState.Loading -> emptyList()
        is MainActivityUiState.Success ->
            uiState.labels
    }

@Composable
fun getNoteCategory(uiState: MainActivityUiState): NoteDisplayCategory =
    when (uiState) {
        MainActivityUiState.Loading -> NoteDisplayCategory()
        is MainActivityUiState.Success ->
            uiState.userSettings.noteCategory
    }
