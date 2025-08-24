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
package com.hobit.sypnapsenotepad

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.window.core.layout.WindowSizeClass
import com.mshdabiola.designsystem.theme.SnpTheme
import com.mshdabiola.detail.navigation.Detail
import com.mshdabiola.detail.navigation.navigateToDetail
import com.hobit.sypnapsenotepad.ui.SnpAppState
import com.hobit.sypnapsenotepad.ui.SnpScaffold
import com.hobit.sypnapsenotepad.ui.rememberKmtAppState
import com.mshdabiola.main.navigation.Main
import com.mshdabiola.model.testtag.SnpScaffoldTestTags
import com.mshdabiola.setting.navigation.Setting
import com.mshdabiola.ui.LocalSharedTransitionScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
class SnpScaffoldScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: NavHostController
    private val testCoroutineScope = CoroutineScope(StandardTestDispatcher()) // Or TestCoroutineScope

    // Helper to create SnpAppState for different scenarios
    @Composable
    private fun createTestAppState(
        windowWidthSizeClass: Int,
        drawerInitialValue: DrawerValue = DrawerValue.Closed, // For ModalNavigationDrawer
        railInitialValue: WideNavigationRailValue = WideNavigationRailValue.Collapsed, // For WideNavigationRail
    ): SnpAppState {
        mockNavController = rememberNavController().apply {
            graph =
                createGraph(startDestination = Main) {
                    composable<Main> { }
                    composable<Detail> { }
                    composable<Setting> { }
                }
        }
        val windowSizeClass = WindowSizeClass(windowWidthSizeClass, 800)

        return rememberKmtAppState(
            windowSizeClass = windowSizeClass,
            navController = mockNavController,
            drawerState = rememberDrawerState(initialValue = drawerInitialValue),
            wideNavigationRailState = rememberWideNavigationRailState(initialValue = railInitialValue),
            coroutineScope = rememberCoroutineScope(),
        )
    }

    @Composable
    private fun TestAppScaffold(
        appState: SnpAppState,
        content: @Composable () -> Unit = {
            Text("Screen Content")
        },
    ) {
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                SnpTheme {
                    SnpScaffold(appState = appState) {
                        content()
                    }
                }
            }
        }
    }

    @Test
    fun SnpScaffold_compactState_displaysModalDrawerAndFab() {
        lateinit var appState: SnpAppState
        composeTestRule.setContent {
            appState = createTestAppState(
                windowWidthSizeClass = 300,
                drawerInitialValue = DrawerValue.Open, // Ensure drawer content is composed
            )
            TestAppScaffold(appState)
        }

        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.MODAL_NAVIGATION_DRAWER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.MODAL_DRAWER_SHEET).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags
                .DrawerContentTestTags.DRAWER_CONTENT_COLUMN,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags
                .DrawerContentTestTags.BRAND_ROW,
        ).assertIsDisplayed()

        // Check for FAB (assuming SnpAppState isMain = true)
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags.FabTestTags
                .FAB_ANIMATED_CONTENT,
        ).assertIsDisplayed()
        // Check for either small or extended FAB based on how Fab composable behaves by default in compact
        // For instance, if it defaults to extended:
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags.FabTestTags
                .EXTENDED_FAB,
        ).assertIsDisplayed()
//        composeTestRule.onNodeWithTag(FabTestTags.FAB_ADD_ICON).assertIsDisplayed()
    }

    @Test
    fun SnpScaffold_mediumState_railCollapsed_displaysWideRailAndFab() {
        lateinit var appState: SnpAppState
        composeTestRule.setContent {
            appState = createTestAppState(
                windowWidthSizeClass = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
                railInitialValue = WideNavigationRailValue.Collapsed,
            )
            TestAppScaffold(appState)
        }

        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.PERMANENT_NAVIGATION_DRAWER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.WIDE_NAVIGATION_RAIL).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.RAIL_TOGGLE_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags
                .DrawerContentTestTags.DRAWER_CONTENT_COLUMN,
        ).assertIsDisplayed()

        // Check for FAB (Small FAB when rail is collapsed in Medium)
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags
                .FabTestTags.FAB_ANIMATED_CONTENT,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.FabTestTags.SMALL_FAB).assertIsDisplayed()
//        composeTestRule.onNodeWithTag(FabTestTags.FAB_ADD_ICON).assertIsDisplayed()
    }

    @Test
    fun SnpScaffold_mediumState_railExpanded_displaysWideRailAndFab() {
        lateinit var appState: SnpAppState
        composeTestRule.setContent {
            appState = createTestAppState(
                windowWidthSizeClass = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
                railInitialValue = WideNavigationRailValue.Expanded, // Start expanded
            )
            TestAppScaffold(appState)
        }

        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.PERMANENT_NAVIGATION_DRAWER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.WIDE_NAVIGATION_RAIL).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.RAIL_TOGGLE_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags
                .DrawerContentTestTags.DRAWER_CONTENT_COLUMN,
        ).assertIsDisplayed()
        // Check for FAB (Extended FAB when rail is expanded in Medium)
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags
                .FabTestTags.FAB_ANIMATED_CONTENT,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.FabTestTags.EXTENDED_FAB).assertIsDisplayed()
//        composeTestRule.onNodeWithTag(FabTestTags.FAB_ADD_ICON).assertIsDisplayed()
    }

    @Test
    fun SnpScaffold_mediumState_railToggleButton_changesState() {
        lateinit var appState: SnpAppState
        composeTestRule.setContent {
            // Start with rail collapsed
            appState = createTestAppState(
                windowWidthSizeClass = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
                railInitialValue = WideNavigationRailValue.Collapsed,
            )
            TestAppScaffold(appState)
        }

        // Initially Small FAB should be visible
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.FabTestTags.SMALL_FAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.FabTestTags.EXTENDED_FAB).assertDoesNotExist()

        // Click to expand
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.RAIL_TOGGLE_BUTTON).performClick()
        composeTestRule.waitForIdle() // Allow recomposition

        // Now Extended FAB should be visible
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.FabTestTags.EXTENDED_FAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.FabTestTags.SMALL_FAB).assertDoesNotExist()

        // Click to collapse
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.RAIL_TOGGLE_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Back to Small FAB
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.FabTestTags.SMALL_FAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.FabTestTags.EXTENDED_FAB).assertDoesNotExist()
    }

    @Test
    fun SnpScaffold_expandState_displaysPermanentDrawerSheet() {
        lateinit var appState: SnpAppState
        composeTestRule.setContent {
            appState = createTestAppState(
                windowWidthSizeClass = WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
            )
            TestAppScaffold(appState)
        }

        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.PERMANENT_NAVIGATION_DRAWER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.PERMANENT_DRAWER_SHEET).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags
                .DrawerContentTestTags.DRAWER_CONTENT_COLUMN,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags
                .DrawerContentTestTags.BRAND_ROW,
        ).assertIsDisplayed()

        // FAB is not expected in the drawer part of the Expand state by default in your current scaffold logic
//        composeTestRule.onNodeWithTag(FabTestTags.FAB_ANIMATED_CONTENT).assertDoesNotExist()
    }

    @Test
    fun SnpScaffold_fabNotDisplayed_when_isMainIsFalse() {
        lateinit var appState: SnpAppState
        composeTestRule.setContent {
            appState = createTestAppState(
                windowWidthSizeClass = 300,
            )
            mockNavController.navigateToDetail(Detail(1))

            TestAppScaffold(appState)
        }

        composeTestRule.onNodeWithTag(SnpScaffoldTestTags.MODAL_NAVIGATION_DRAWER).assertIsDisplayed()
        // FAB should not be displayed
        composeTestRule.onNodeWithTag(
            SnpScaffoldTestTags
                .FabTestTags.FAB_ANIMATED_CONTENT,
        ).assertDoesNotExist()
    }
}
