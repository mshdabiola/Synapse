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

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.SearchInputField
import com.mshdabiola.model.testtag.SearchInputFieldTestTags
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalMaterial3Api::class)
class SearchInputFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun collapsedState_noText_isGridFalse_displaysCorrectlyAndHandlesClicks() {
        // Arrange
        val searchTextFieldState = TextFieldState()
        var onDrawerCalled = false
        var onDisplayModeChangeCalled = false

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = rememberSearchBarState(SearchBarValue.Collapsed),
                searchTextFieldState = searchTextFieldState,
                isGrid = false,
                onDisplayModeChange = { onDisplayModeChangeCalled = true },
                onDrawer = { onDrawerCalled = true },
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithText("Search Synapse").assertIsDisplayed() // Placeholder
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON).assertIsDisplayed()
        // TODO: Optionally, assert specific icon for !isGrid (e.g., SynIcons.GridView's contentDescription "grid")
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_CLEAR_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_BACK_BUTTON).assertDoesNotExist()

        // Act & Assert Clicks
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON).performClick()
        assertTrue(onDrawerCalled, "onDrawer should be called")

        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON).performClick()
        assertTrue(onDisplayModeChangeCalled, "onDisplayModeChange should be called")
    }

    @Test
    fun collapsedState_withText_isGridTrue_displaysCorrectly() {
        // Arrange
        val searchTextFieldState = TextFieldState()
        val initialText = "Test Query"
        searchTextFieldState.setTextAndPlaceCursorAtEnd(initialText)
        var onDisplayModeChangeCalled = false

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = rememberSearchBarState(SearchBarValue.Collapsed),
                searchTextFieldState = searchTextFieldState,
                isGrid = true,
                onDisplayModeChange = { onDisplayModeChangeCalled = true },
                onDrawer = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithText("Search Synapse").assertDoesNotExist() // Placeholder not shown
        composeTestRule.onNodeWithText(initialText).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON,
        ).assertIsDisplayed()
        // TODO: Optionally, assert specific icon for isGrid (e.g., SynIcons.ViewAgenda's contentDescription "column")
        composeTestRule.onNodeWithTag(
            SearchInputFieldTestTags.MAIN_TOPBAR_CLEAR_BUTTON,
        ).assertDoesNotExist()
        // Clear button not shown when collapsed

        // Act & Assert Clicks
        composeTestRule.onNodeWithTag(
            SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON,
        ).performClick()
        assertTrue(
            onDisplayModeChangeCalled,
            "onDisplayModeChange should be called for display mode button",
        )
    }

    @Test
    fun expandedState_withText_displaysBackButtonAndClearButton_handlesClicks() {
        // Arrange
        val searchTextFieldState = TextFieldState()
        val initialText = "Some Text"
        searchTextFieldState.setTextAndPlaceCursorAtEnd(initialText)
        lateinit var searchBarState: SearchBarState
        var onDisplayModeChangeCalled = false

        composeTestRule.setContent {
            searchBarState = rememberSearchBarState(SearchBarValue.Expanded)

            SearchInputField(
                searchBarState = searchBarState,
                searchTextFieldState = searchTextFieldState,
                isGrid = false, // Irrelevant for expanded state trailing icon
                onDisplayModeChange = { onDisplayModeChangeCalled = true }, // Should not be called
                onDrawer = {}, // Irrelevant
            )
        }

        // Assert initial state
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_ROOT).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_BACK_BUTTON).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON).assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_CLEAR_BUTTON).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON).assertDoesNotExist()

        // Act: Click clear button
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_CLEAR_BUTTON).performClick()

        // Assert: Text is cleared, clear button gone
        assertEquals(
            "",
            searchTextFieldState.text.toString(),
            "Text should be cleared by clear button",
        )
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_CLEAR_BUTTON).assertDoesNotExist()
        assertFalse(
            onDisplayModeChangeCalled,
            "onDisplayModeChange should not be called by clear button",
        )

        // Arrange again for back button test (text needs to be re-set)
        searchTextFieldState.setTextAndPlaceCursorAtEnd(initialText)
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_CLEAR_BUTTON).assertIsDisplayed()
        // Ensure clear button is back

        // Act: Click back button
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_BACK_BUTTON).performClick()

        // Assert: Text is cleared, SearchBar state should try to collapse, clear button gone
        assertEquals(
            "",
            searchTextFieldState.text.toString(),

            "Text should be cleared by back button",
        )
        // Note: Directly verifying SearchBarState.animateToCollapsed() is complex.
        // We trust that the SearchBar's internal mechanism works when called.
        // After text is cleared by back button, the clear button should also disappear
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_CLEAR_BUTTON).assertDoesNotExist()
        assertFalse(
            onDisplayModeChangeCalled,
            "onDisplayModeChange should not be called by back button",
        )
    }

    @Test
    fun expandedState_noText_displaysBackButton_noClearButton() {
        // Arrange
        val searchTextFieldState = TextFieldState() // No text
        lateinit var searchBarState: SearchBarState

        composeTestRule.setContent {
            searchBarState = rememberSearchBarState(SearchBarValue.Expanded)

            SearchInputField(
                searchBarState = searchBarState,
                searchTextFieldState = searchTextFieldState,
                isGrid = false,
                onDisplayModeChange = {},
                onDrawer = {},
            )
        }

        // Assert
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_ROOT).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_BACK_BUTTON).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_CLEAR_BUTTON).assertDoesNotExist()
        // No text, no clear button
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON).assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON).assertDoesNotExist()
    }

    @Test
    fun searchInputField_displaysTextFromState_whenCollapsed() {
        // Arrange
        val searchTextFieldState = TextFieldState()
        val initialText = "Hello World"
        searchTextFieldState.setTextAndPlaceCursorAtEnd(initialText)

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = rememberSearchBarState(SearchBarValue.Collapsed), // Default is Collapsed
                searchTextFieldState = searchTextFieldState,
                isGrid = false,
                onDisplayModeChange = {},
                onDrawer = {},
            )
        }

        // Assert that the text from the state is displayed.
        composeTestRule.onNodeWithText("Search Synapse").assertDoesNotExist() // Placeholder hidden
        composeTestRule.onNodeWithText(initialText).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON).assertIsDisplayed()
        // Display mode shown when collapsed
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_CLEAR_BUTTON).assertDoesNotExist()
        // Clear button not shown when collapsed
    }

    @Test
    fun collapsedState_nullOnDrawer_noHamburgerMenu_displayModeButtonPresent() {
        // Arrange
        val searchTextFieldState = TextFieldState()
        var onDisplayModeChangeCalled = false

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = rememberSearchBarState(SearchBarValue.Collapsed),
                searchTextFieldState = searchTextFieldState,
                isGrid = false,
                onDisplayModeChange = { onDisplayModeChangeCalled = true },
                onDrawer = null, // Explicitly null
            )
        }

        // Assert
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON).assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON).assertIsDisplayed()

        // Act & Assert
        composeTestRule
            .onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON).performClick()
        assertTrue(onDisplayModeChangeCalled, "onDisplayModeChange should be called")
    }
}
