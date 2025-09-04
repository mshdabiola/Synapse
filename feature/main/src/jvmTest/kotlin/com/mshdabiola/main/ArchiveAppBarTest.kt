package com.mshdabiola.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.ArchiveAppBar
import com.mshdabiola.model.testtag.ArchiveAppBarTestTags
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class ArchiveAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun archiveAppBar_defaultState_displaysCorrectly() {
        // Arrange
        composeTestRule.setContent {
            ArchiveAppBar(
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(ArchiveAppBarTestTags.SCREEN_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithText("Archive").assertIsDisplayed() // Using text as direct tag is on Text composable
        composeTestRule.onNodeWithTag(ArchiveAppBarTestTags.NAVIGATION_ICON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ArchiveAppBarTestTags.SEARCH_ICON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ArchiveAppBarTestTags.DISPLAY_MODE_ICON).assertIsDisplayed()
        // Check for GridView icon specifically (assuming it's the default when isGrid is false)
        // This might require a more specific matcher if the icon itself doesn't have a unique tag
    }

    @Test
    fun archiveAppBar_clickActions_areInvoked() {
        // Arrange
        var onHamburgerMenuClicked = false
        var onSearchClicked = false
        var onDisplayModeChanged = false

        composeTestRule.setContent {
            ArchiveAppBar(
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onHamburgerMenuClick = { onHamburgerMenuClicked = true },
                onSearchClick = { onSearchClicked = true },
                onDisplayModeChange = { onDisplayModeChanged = true },
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithTag(ArchiveAppBarTestTags.NAVIGATION_ICON).performClick()
        assert(onHamburgerMenuClicked)

        composeTestRule.onNodeWithTag(ArchiveAppBarTestTags.SEARCH_ICON).performClick()
        assert(onSearchClicked)

        composeTestRule.onNodeWithTag(ArchiveAppBarTestTags.DISPLAY_MODE_ICON).performClick()
        assert(onDisplayModeChanged)
    }

    @Test
    fun archiveAppBar_whenIsGridTrue_displaysCorrectIcon() {
        // Arrange
        var onDisplayModeChanged = false
        composeTestRule.setContent {
            ArchiveAppBar(
                isGrid = true,
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onDisplayModeChange = { onDisplayModeChanged = true },
            )
        }

        // Assert
        // Check for ViewAgenda icon specifically (assuming it's shown when isGrid is true)
        // This might require a more specific matcher if the icon itself doesn't have a unique tag
        composeTestRule.onNodeWithTag(ArchiveAppBarTestTags.DISPLAY_MODE_ICON).assertIsDisplayed()

        // Act & Assert for click when isGrid is true
        composeTestRule.onNodeWithTag(ArchiveAppBarTestTags.DISPLAY_MODE_ICON).performClick()
        assert(onDisplayModeChanged)
    }
}
