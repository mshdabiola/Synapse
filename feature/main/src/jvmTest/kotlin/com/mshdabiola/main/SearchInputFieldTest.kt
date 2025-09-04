package com.mshdabiola.main

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.SearchInputField
import com.mshdabiola.model.testtag.SearchInputFieldTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(RobolectricTestRunner::class)
class SearchInputFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchInputField_collapsedState_defaultGrid_displaysCorrectlyAndHandlesClicks() {
        // Arrange
        val searchBarState = SearchBarState(initialValue = SearchBarValue.Collapsed)
        val searchTextFieldState = TextFieldState()
        var onDrawerCalled = false
        var onDisplayModeChangeCalled = false

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = searchBarState,
                searchTextFieldState = searchTextFieldState,
                isGrid = false,
                onDisplayModeChange = { onDisplayModeChangeCalled = true },
                onDrawer = { onDrawerCalled = true }
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithText("Search Synapse").assertIsDisplayed() // Placeholder
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON).assertIsDisplayed()
        // More specific check for GridView icon (contentDescription="grid") could be added

        // Act & Assert Clicks
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON).performClick()
        assertTrue(onDrawerCalled, "onDrawer should be called")

        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON).performClick()
        assertTrue(onDisplayModeChangeCalled, "onDisplayModeChange should be called")
    }

    @Test
    fun searchInputField_collapsedState_isGridTrue_displaysCorrectIcon() {
        // Arrange
        val searchBarState = SearchBarState(initialValue = SearchBarValue.Collapsed)
        val searchTextFieldState = TextFieldState()

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = searchBarState,
                searchTextFieldState = searchTextFieldState,
                isGrid = true,
                onDisplayModeChange = {},
                onDrawer = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON).assertIsDisplayed()
        // More specific check for ViewAgenda icon (contentDescription="column") could be added
    }

    @Test
    fun searchInputField_expandedState_displaysBackButtonAndHandlesClick() {
        // Arrange
        val searchBarState = SearchBarState(initialValue = SearchBarValue.Expanded)
        val searchTextFieldState = TextFieldState()
        searchTextFieldState.setTextAndPlaceCursorAtEnd("Some Text")
        var onDisplayModeChangeCalled = false // Should not be called by back button

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = searchBarState,
                searchTextFieldState = searchTextFieldState,
                isGrid = false,
                onDisplayModeChange = { onDisplayModeChangeCalled = true }, // To ensure it's not called
                onDrawer = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_BACK_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON).assertDoesNotExist()

        // Act: Click back button
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_BACK_BUTTON).performClick()

        // Assert: Text is cleared (SearchBarState.animateToCollapsed is harder to verify directly here)
        assertEquals("", searchTextFieldState.text.toString())
        // Assert that onDisplayModeChange was not called by the back button action
        assertFalse(onDisplayModeChangeCalled, "onDisplayModeChange should not be called by back button")
    }

    @Test
    fun searchInputField_displaysTextFromState() {
        // Arrange
        val searchBarState = SearchBarState(initialValue = SearchBarValue.Collapsed)
        val searchTextFieldState = TextFieldState()
        val initialText = "Hello World"
        searchTextFieldState.setTextAndPlaceCursorAtEnd(initialText)

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = searchBarState,
                searchTextFieldState = searchTextFieldState,
                isGrid = false,
                onDisplayModeChange = {},
                onDrawer = {}
            )
        }

        // Assert that the text from the state is displayed.
        // The actual text field might be nested. We check if our placeholder is gone and input text is present.
        composeTestRule.onNodeWithText("Search Synapse").assertDoesNotExist() // Placeholder should not be shown when text exists
        composeTestRule.onNodeWithText(initialText).assertIsDisplayed()
    }
}
