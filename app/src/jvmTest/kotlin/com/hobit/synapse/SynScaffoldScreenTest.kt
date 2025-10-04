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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.window.core.layout.WindowSizeClass
import com.hobit.synapse.ui.Compact
import com.hobit.synapse.ui.Medium
import com.hobit.synapse.ui.SynAppState
import com.hobit.synapse.ui.SynScaffold
import com.hobit.synapse.ui.rememberSynAppState
import com.mshdabiola.designsystem.theme.SynTheme
import com.mshdabiola.detail.navigation.navigateToDetail
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.testtag.SynScaffoldTestTags
import com.mshdabiola.ui.LocalSharedTransitionScope
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
class SynScaffoldScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    private fun TestAppScaffold(
        appState: SynAppState,
    ) {
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                SynTheme {
                    SynScaffold(
                        appState = appState,
                        isVoiceAvailable = true,
                        noteDisplayCategory = NoteDisplayCategory(),
                    ){}
                }
            }
        }
    }

    @Test
    fun synScaffold_compactState_displaysModalDrawerAndFab() {
        lateinit var appState: SynAppState
        composeTestRule.setContent {
            val windowSizeClass = WindowSizeClass ( 300,  800)
            appState = rememberSynAppState(windowSizeClass = windowSizeClass)

            // Open drawer to make its content available for testing
            LaunchedEffect(Unit) {
                (appState as? Compact)?.drawerState?.open()
            }

            TestAppScaffold(appState)
        }

        composeTestRule.onNodeWithTag(SynScaffoldTestTags.MODAL_NAVIGATION_DRAWER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.MODAL_DRAWER_SHEET).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.DrawerContentTestTags.DRAWER_CONTENT_COLUMN).assertIsDisplayed()

        // Check for FAB in compact mode
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.FAB_ANIMATED_CONTENT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.EXTENDED_FAB).assertIsDisplayed()
    }

    @Test
    fun synScaffold_mediumState_railCollapsed_displaysWideRailAndSmallFab() {
        composeTestRule.setContent {
            val windowSizeClass = WindowSizeClass( 700,  800)
            val appState = rememberSynAppState(windowSizeClass = windowSizeClass)

            // Ensure rail is collapsed
            LaunchedEffect(Unit) {
                (appState as? Medium)?.wideNavigationRailState?.collapse()
            }

            TestAppScaffold(appState)
        }

        composeTestRule.onNodeWithTag(SynScaffoldTestTags.PERMANENT_NAVIGATION_DRAWER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.WIDE_NAVIGATION_RAIL).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.DrawerContentTestTags.DRAWER_CONTENT_COLUMN).assertIsDisplayed()

        // Check for Small FAB when rail is collapsed
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.FAB_ANIMATED_CONTENT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.SMALL_FAB).assertIsDisplayed()
    }

    @Test
    fun synScaffold_mediumState_railExpanded_displaysWideRailAndExtendedFab() {
        composeTestRule.setContent {
            val windowSizeClass = WindowSizeClass(700,  800)
            val appState = rememberSynAppState(
                windowSizeClass = windowSizeClass,
                wideNavigationRailState = rememberWideNavigationRailState(WideNavigationRailValue.Expanded),
            )
            TestAppScaffold(appState)
        }

        composeTestRule.onNodeWithTag(SynScaffoldTestTags.PERMANENT_NAVIGATION_DRAWER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.WIDE_NAVIGATION_RAIL).assertIsDisplayed()

        // Check for Extended FAB when rail is expanded
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.FAB_ANIMATED_CONTENT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.EXTENDED_FAB).assertIsDisplayed()
    }

    @Test
    fun synScaffold_mediumState_railToggleButton_changesFabState() {
        composeTestRule.setContent {
            val windowSizeClass = WindowSizeClass(700, 800)
            val appState = rememberSynAppState(windowSizeClass = windowSizeClass)
            TestAppScaffold(appState)
        }

        // Initially Small FAB is visible
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.SMALL_FAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.EXTENDED_FAB).assertDoesNotExist()

        // Click to expand
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.RAIL_TOGGLE_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Now Extended FAB is visible
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.EXTENDED_FAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.SMALL_FAB).assertDoesNotExist()
    }

    @Test
    fun synScaffold_expandState_displaysPermanentDrawerSheet() {
        composeTestRule.setContent {
            val windowSizeClass = WindowSizeClass( 900, 800)
            val appState = rememberSynAppState(windowSizeClass = windowSizeClass)
            TestAppScaffold(appState)
        }

        composeTestRule.onNodeWithTag(SynScaffoldTestTags.PERMANENT_NAVIGATION_DRAWER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.PERMANENT_DRAWER_SHEET).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.DrawerContentTestTags.DRAWER_CONTENT_COLUMN).assertIsDisplayed()
    }

    @Test
    fun synScaffold_fabNotDisplayed_when_isNotMainRoute() {
        lateinit var appState: SynAppState
        composeTestRule.setContent {
            val windowSizeClass = WindowSizeClass(300, 800)
            appState = rememberSynAppState(windowSizeClass = windowSizeClass)
            TestAppScaffold(appState)
        }

        // Initially, FAB is displayed on the main route
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.FAB_ANIMATED_CONTENT).assertIsDisplayed()

        // Navigate to a different screen
        composeTestRule.runOnUiThread {
            appState.navController.navigateToDetail(NotePad()) // Use the extension function
        }

        // FAB should no longer be displayed
        composeTestRule.onNodeWithTag(SynScaffoldTestTags.FabTestTags.FAB_ANIMATED_CONTENT).assertDoesNotExist()
    }
}
