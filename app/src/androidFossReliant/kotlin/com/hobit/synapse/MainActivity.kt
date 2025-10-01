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
package com.hobit.synapse

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.hobit.synapse.ui.SynApp
import com.hobit.synapse.ui.SynAppState
import com.hobit.synapse.ui.rememberSynAppState
import com.hobit.synapse.ui.shouldUseDarkTheme
import com.mshdabiola.detail.navigation.navigateToDetail
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.NoteImage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainAppViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        installSplashScreen()
        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainActivityUiState.Loading -> true
                is MainActivityUiState.Success -> false
            }
        }

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)

            // Update the edge to edge configuration to match the theme
            // This is the same parameters as the default enableEdgeToEdge call, but we manually
            // resolve whether or not to show dark theme using uiState, since it can be different
            // than the configuration's dark theme value based on the user preference.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle =
                        SystemBarStyle.auto(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                        ) { darkTheme },
                    navigationBarStyle =
                        SystemBarStyle.auto(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                        ) { darkTheme },
                )
                onDispose {}
            }
            val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            val navController: NavHostController = rememberNavController()
            val appState: SynAppState = rememberSynAppState(
                navController = navController,
                windowSizeClass = windowSizeClass,
            )

            SynApp(
                windowSizeClass = windowSizeClass,
                appState = appState,
            )
            LaunchedEffect(Unit) {
                val notepad = getNote()
                if (notepad != null) {
                    navController.navigateToDetail(notePad = notepad)
                }
            }

        }
    }


    fun getNote(): NotePad? {
        val intent = intent
        val title = intent.getStringExtra(Intent.EXTRA_TITLE)
        val subject1 = intent.getStringExtra(Intent.EXTRA_SUBJECT)
        val title2 = intent.getStringExtra(Intent.EXTRA_TEXT)

        val size = intent.clipData?.itemCount ?: 0
        val uris = (0 until size)
            .mapNotNull { intent.clipData?.getItemAt(it)?.uri?.toString() }
        val images = viewModel.copyImageToInternal(uris = uris)
        return if (title != null || subject1 != null || title2 != null || size > 0) {
            NotePad(
                title = (title ?: "")+ (title2 ?: ""),
                detail = subject1 ?: "",
                images = images.map { NoteImage(path = it) },
            )
        } else null
    }


}
