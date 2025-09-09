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
package com.mshdabiola.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.ReminderAppBar
import com.mshdabiola.model.testtag.ReminderAppBarTestTags
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalMaterial3Api::class)
class ReminderAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun reminderAppBar_defaultState_displaysCorrectly() {
        // Arrange
        composeTestRule.setContent {
            ReminderAppBar(
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(ReminderAppBarTestTags.APP_BAR_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderAppBarTestTags.NAVIGATION_ICON).assertIsDisplayed()
        composeTestRule.onNodeWithText("Reminder").assertIsDisplayed() // Title text
        composeTestRule.onNodeWithTag(ReminderAppBarTestTags.SEARCH_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderAppBarTestTags.DISPLAY_MODE_ICON_BUTTON).assertIsDisplayed()
        // More specific check for GridView icon can be added if icons have unique content descriptions or tags
    }

    @Test
    fun reminderAppBar_clickActions_areInvoked() {
        // Arrange
        var hamburgerClicked = false
        var searchClicked = false
        var displayModeChanged = false

        composeTestRule.setContent {
            ReminderAppBar(
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onHamburgerMenuClick = { hamburgerClicked = true },
                onSearchClick = { searchClicked = true },
                onDisplayModeChange = { displayModeChanged = true },
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithTag(ReminderAppBarTestTags.NAVIGATION_ICON).performClick()
        assertTrue(hamburgerClicked, "onHamburgerMenuClick should be called")

        composeTestRule.onNodeWithTag(ReminderAppBarTestTags.SEARCH_ICON_BUTTON).performClick()
        assertTrue(searchClicked, "onSearchClick should be called")

        composeTestRule.onNodeWithTag(ReminderAppBarTestTags.DISPLAY_MODE_ICON_BUTTON).performClick()
        assertTrue(displayModeChanged, "onDisplayModeChange should be called")
    }

    @Test
    fun reminderAppBar_whenIsGridTrue_displaysCorrectIcon() {
        // Arrange
        var displayModeChanged = false
        composeTestRule.setContent {
            ReminderAppBar(
                isGrid = true,
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onDisplayModeChange = { displayModeChanged = true },
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(ReminderAppBarTestTags.DISPLAY_MODE_ICON_BUTTON).assertIsDisplayed()
        // More specific check for ViewAgenda icon can be added

        // Act & Assert
        displayModeChanged = false // Reset for this specific click assertion
        composeTestRule.onNodeWithTag(ReminderAppBarTestTags.DISPLAY_MODE_ICON_BUTTON).performClick()
        assertTrue(displayModeChanged, "onDisplayModeChange should be called when isGrid is true")
    }
}
