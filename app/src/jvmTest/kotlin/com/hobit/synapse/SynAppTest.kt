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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.window.core.layout.WindowSizeClass
import co.touchlab.kermit.koin.getLoggerWithTag
import co.touchlab.kermit.koin.kermitLoggerModule
import com.hobit.synapse.ui.SynApp
import com.hobit.synapse.ui.SynAppState
import com.hobit.synapse.ui.pop
import com.hobit.synapse.ui.rememberSynAppState
import com.hobit.synapse.util.KoinTestRule
import com.hobit.synapse.util.TestLifecycleOwner
import com.mshdabiola.data.di.dataModule
import com.mshdabiola.detail.detailModule
import com.mshdabiola.detail.navigation.Detail
import com.mshdabiola.domain.di.domainModule
import com.mshdabiola.draw.drawModule
import com.mshdabiola.draw.navigation.Draw
import com.mshdabiola.label.labelModule
import com.mshdabiola.main.mainModule
import com.mshdabiola.main.navigation.Main
import com.mshdabiola.model.Platform
import com.mshdabiola.model.testtag.DetailScreenTestTags
import com.mshdabiola.model.testtag.DrawScreenTestTags
import com.mshdabiola.model.testtag.MainScreenTestTags
import com.mshdabiola.model.testtag.MoreOptionsSheetTestTags
import com.mshdabiola.model.testtag.SettingScreenTestTags
import com.mshdabiola.model.testtag.SynAppTestTags
import com.mshdabiola.model.testtag.SynScaffoldTestTags
import com.mshdabiola.select.selectModule
import com.mshdabiola.setting.navigation.Setting
import com.mshdabiola.setting.settingModule
import com.mshdabiola.testing.util.testLogger
import com.mshdabiola.view.viewModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest

@OptIn(ExperimentalCoroutinesApi::class)
class SynAppTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var testLifecycleOwner: TestLifecycleOwner
    private lateinit var appState: SynAppState

    val applicationModule = module {
        single { getPlatform() } bind Platform::class
    }
    val appModule =
        module {
            includes(
                applicationModule,
                domainModule,
                dataModule,
                detailModule,
                mainModule,
                settingModule,
                drawModule,
                viewModule,
                labelModule,
                selectModule,
            )
            viewModel {
                MainAppViewModel(
                    userDataRepository = get(),
                    networkRepository = get(),
                    labelRepository = get(),
                    contentManager = get(),
                    getNoteUseCase = get(),
                    addAllNoteUseCase = get(),
                    logger = getLoggerWithTag("MainAppViewModel"),
                )
            }
        }

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            appModule,
            kermitLoggerModule(testLogger),
        ),
    )

    @Before
    fun init() {
        testLifecycleOwner = TestLifecycleOwner(composeTestRule)
        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    @After
    fun tearDown() {
        composeTestRule.waitForIdle()

        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        composeTestRule.waitForIdle()
        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        composeTestRule.waitForIdle()
        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        composeTestRule.waitForIdle()
    }

    private fun initializeApp(widthSizeClass: Int =300) {
        val testCoroutineScope = CoroutineScope(StandardTestDispatcher())

        val windowSizeClass = WindowSizeClass(widthSizeClass, 800)


        composeTestRule.setContent {
            appState = rememberSynAppState(windowSizeClass, testCoroutineScope)
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides object : ViewModelStoreOwner {
                    override val viewModelStore = ViewModelStore()
                },
                LocalLifecycleOwner provides testLifecycleOwner,
            ) {

                    SynApp()

            }
        }
    }

    @Test
    fun `initial_screen_is_main`() = runTest {
        initializeApp()
        advanceUntilIdle()

        // Verify that the NavHost and the Main screen are displayed
        composeTestRule.onNodeWithTag(SynAppTestTags.NAV_HOST).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_EMPTY_STATE_VIEW).assertIsDisplayed()

        // Verify the current route in the app state
        assertEquals(Main, appState.navController.lastOrNull())
    }

    @Test
    fun `navigation_to_settings_and_back`() = runTest {
        initializeApp()
        advanceUntilIdle()

        // Navigate to Settings by clicking the settings button in the main screen's top bar
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.DrawerContentTestTags.navigationItemTag(Setting)).performClick()
        advanceUntilIdle()

        // Verify that the Settings screen is displayed
        composeTestRule.onNodeWithTag(SettingScreenTestTags.SCREEN_ROOT).assertIsDisplayed()
        assertEquals(Setting, appState.navController.lastOrNull())

        // Navigate back
        appState.navController.pop()
        advanceUntilIdle()

        // Verify we are back on the Main screen
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_EMPTY_STATE_VIEW).assertIsDisplayed()
        assertEquals(Main, appState.navController.lastOrNull())
    }

    @Test
    fun `navigate to detail and back`() = runTest {
        initializeApp()
        advanceUntilIdle()

        // Simulate navigating to Detail. This is typically done by an action in Main screen.
        // For this test, we'll navigate programmatically.
        val testNoteId = 123L
        appState.navController.add(Detail(testNoteId))
        advanceUntilIdle()

        // Verify Detail screen is displayed
        composeTestRule.onNodeWithTag(DetailScreenTestTags.DETAIL_LIST).assertIsDisplayed()
        assertTrue(appState.navController.last() is Detail)
        assertEquals(testNoteId, (appState.navController.last() as Detail).id)


        // Navigate back
        appState.navController.pop()
        advanceUntilIdle()

        // Verify Main screen is displayed again
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTES_GRID).assertIsDisplayed()
        assertEquals(Main, appState.navController.lastOrNull())
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `navigate to draw from detail and back`() = runTest {
        initializeApp()
        advanceUntilIdle()

        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.EXTENDED_FAB).performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag(DetailScreenTestTags.DETAIL_LIST),4000)
        composeTestRule.onNodeWithTag(SynAppTestTags.APP_ROOT_LAYOUT).printToLog("root")

        composeTestRule.onNodeWithTag(DetailScreenTestTags.DETAIL_LIST,true).assertIsDisplayed()


        composeTestRule.onNodeWithTag(DetailScreenTestTags.MORE_BUTTON).performClick()
        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.DRAWING).performClick()


        // Verify Draw screen is displayed
        composeTestRule.onNodeWithTag(DrawScreenTestTags.BOARD).assertIsDisplayed()

        // Navigate back to Detail
        composeTestRule.onNodeWithTag(DrawScreenTestTags.BACK_BUTTON).performClick()


        composeTestRule.onNodeWithTag(DetailScreenTestTags.DETAIL_LIST).assertIsDisplayed()

        // Navigate back to Main
        composeTestRule.onNodeWithTag(DetailScreenTestTags.BACK_BUTTON).performClick()

        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTES_GRID).assertIsDisplayed()
    }
}
