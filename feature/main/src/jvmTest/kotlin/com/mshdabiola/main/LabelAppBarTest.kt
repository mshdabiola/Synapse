package com.mshdabiola.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.LabelAppBar
import com.mshdabiola.model.testtag.LabelAppBarTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(RobolectricTestRunner::class)
class LabelAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleLabelName = "My Test Label"

    @Test
    fun labelAppBar_defaultState_displaysCorrectly() {
        // Arrange
        composeTestRule.setContent {
            LabelAppBar(
                labelName = sampleLabelName,
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.APP_BAR_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.NAVIGATION_ICON).assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleLabelName).assertIsDisplayed() // Checks title text
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.SEARCH_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.MORE_OPTIONS_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.OPTIONS_DROPDOWN_MENU).assertDoesNotExist()
    }

    @Test
    fun labelAppBar_iconClickActions_areInvoked() {
        // Arrange
        var onHamburgerMenuClicked = false
        var onSearchClicked = false

        composeTestRule.setContent {
            LabelAppBar(
                labelName = sampleLabelName,
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onHamburgerMenuClick = { onHamburgerMenuClicked = true },
                onSearchClick = { onSearchClicked = true }
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.NAVIGATION_ICON).performClick()
        assertTrue(onHamburgerMenuClicked, "onHamburgerMenuClick should be called")

        composeTestRule.onNodeWithTag(LabelAppBarTestTags.SEARCH_ICON_BUTTON).performClick()
        assertTrue(onSearchClicked, "onSearchClick should be called")
    }

    @Test
    fun labelAppBar_dropdownMenu_functionalityWorks() {
        // Arrange
        var onLabelNameChangeClicked = false
        var onDeleteLabelClicked = false

        composeTestRule.setContent {
            LabelAppBar(
                labelName = sampleLabelName,
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onLabelNameChange = { onLabelNameChangeClicked = true },
                onDeleteLabel = { onDeleteLabelClicked = true }
            )
        }

        // Act: Open dropdown
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.MORE_OPTIONS_ICON_BUTTON).performClick()

        // Assert: Dropdown and items are visible
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.OPTIONS_DROPDOWN_MENU).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.RENAME_LABEL_MENU_ITEM).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.DELETE_LABEL_MENU_ITEM).assertIsDisplayed()

        // Act: Click Rename Label
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.RENAME_LABEL_MENU_ITEM).performClick()

        // Assert: Callback invoked and dropdown closes
        assertTrue(onLabelNameChangeClicked, "onLabelNameChange should be called")
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.OPTIONS_DROPDOWN_MENU).assertDoesNotExist()

        // Act: Reopen dropdown and Click Delete Label
        onLabelNameChangeClicked = false // Reset for next check
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.MORE_OPTIONS_ICON_BUTTON).performClick()
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.DELETE_LABEL_MENU_ITEM).performClick()

        // Assert: Callback invoked and dropdown closes
        assertTrue(onDeleteLabelClicked, "onDeleteLabel should be called")
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.OPTIONS_DROPDOWN_MENU).assertDoesNotExist()
    }
}
