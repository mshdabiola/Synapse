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
import kotlin.test.assertTrue

@OptIn(ExperimentalMaterial3Api::class)
class SearchInputFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchInputField_collapsedState_noText_displaysCorrectlyAndHandlesDrawerClick() {
        // Arrange
        val searchTextFieldState = TextFieldState()
        var onDrawerCalled = false

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = rememberSearchBarState(SearchBarValue.Collapsed),
                searchTextFieldState = searchTextFieldState,
                onDrawer = { onDrawerCalled = true },
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_ROOT)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Search Synapse").assertIsDisplayed() // Placeholder
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_ClEAR_BUTTON)
            .assertDoesNotExist() // No text, so clear button should not be there
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_BACK_BUTTON)
            .assertDoesNotExist() // Collapsed state, no back button

        // Act & Assert Clicks
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON)
            .performClick()
        assertTrue(onDrawerCalled, "onDrawer should be called")
    }

    @Test
    fun searchInputField_collapsedState_withText_displaysClearButtonAndHandlesClick() {
        // Arrange
        val searchTextFieldState = TextFieldState()
        val initialText = "Test Query"
        searchTextFieldState.setTextAndPlaceCursorAtEnd(initialText)
        var onDrawerCalled = false // To ensure it's not called by clear button

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = rememberSearchBarState(SearchBarValue.Collapsed),
                searchTextFieldState = searchTextFieldState,
                onDrawer = { onDrawerCalled = true },
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_ROOT)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Search Synapse").assertDoesNotExist() // Placeholder not shown
        composeTestRule.onNodeWithText(initialText).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_ClEAR_BUTTON)
            .assertIsDisplayed() // Text is present, clear button shown

        // Act: Click clear button
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_ClEAR_BUTTON)
            .performClick()

        // Assert: Text is cleared
        assertEquals("", searchTextFieldState.text.toString())
        assertTrue(!onDrawerCalled, "onDrawer should not be called by clear button")
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_ClEAR_BUTTON)
            .assertDoesNotExist() // Text cleared, button gone
    }


    @Test
    fun searchInputField_expandedState_displaysBackButtonAndHandlesClick() {
        // Arrange
        val searchTextFieldState = TextFieldState()
        val initialText = "Some Text"
        searchTextFieldState.setTextAndPlaceCursorAtEnd(initialText)
        val searchBarState = SearchBarValue.Expanded // Initial state for the test

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = rememberSearchBarState(searchBarState),
                searchTextFieldState = searchTextFieldState,
                onDrawer = {}, // onDrawer is irrelevant in expanded state for back button
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_ROOT)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_BACK_BUTTON)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON)
            .assertDoesNotExist()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_ClEAR_BUTTON)
            .assertIsDisplayed() // Text is present

        // Act: Click back button
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_BACK_BUTTON)
            .performClick()

        // Assert: Text is cleared
        // Note: Directly verifying SearchBarState.animateToCollapsed() is complex in unit tests.
        // We trust that the SearchBar's internal mechanism works when called.
        assertEquals("", searchTextFieldState.text.toString())
        // After text is cleared, the clear button should also disappear
         composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_ClEAR_BUTTON)
            .assertDoesNotExist()
    }

    @Test
    fun searchInputField_displaysTextFromState() {
        // Arrange
        val searchTextFieldState = TextFieldState()
        val initialText = "Hello World"
        searchTextFieldState.setTextAndPlaceCursorAtEnd(initialText)

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = rememberSearchBarState(), // Default is Collapsed
                searchTextFieldState = searchTextFieldState,
                onDrawer = {},
            )
        }

        // Assert that the text from the state is displayed.
        composeTestRule.onNodeWithText("Search Synapse").assertDoesNotExist() // Placeholder hidden
        composeTestRule.onNodeWithText(initialText).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_ClEAR_BUTTON)
            .assertIsDisplayed() // Text is present
    }

    @Test
    fun searchInputField_collapsedState_nullOnDrawer_noHamburgerMenu() {
        // Arrange
        val searchTextFieldState = TextFieldState()

        composeTestRule.setContent {
            SearchInputField(
                searchBarState = rememberSearchBarState(SearchBarValue.Collapsed),
                searchTextFieldState = searchTextFieldState,
                onDrawer = null, // Explicitly null
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON)
            .assertDoesNotExist()
    }
}
