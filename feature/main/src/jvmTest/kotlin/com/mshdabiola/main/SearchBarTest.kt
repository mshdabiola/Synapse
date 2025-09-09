package com.mshdabiola.main

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.SearchBar
import com.mshdabiola.main.model.SearchSort
import com.mshdabiola.main.model.SearchState
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.testtag.LabelBoxTestTags
import com.mshdabiola.model.testtag.SearchBarTestTags
import com.mshdabiola.ui.SharedTransitionContainer
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalMaterial3Api::class)
class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val inputFieldTestTag = "searchBarInputField"
    private val inputFieldText = "Test Input"

    @Composable
    private fun TestInputField() {
        Text(text = inputFieldText, modifier = Modifier.testTag(inputFieldTestTag))
    }

    @Test
    fun searchBar_initialDisplay_showsSearchBarAndInputField() {
        val searchTextFieldState = TextFieldState()
        composeTestRule.setContent {
            val searchBarState = rememberSearchBarState(SearchBarValue.Expanded)
            SearchBar(
                searchBarState = searchBarState,
                searchState = SearchState.FilterState(), // Default to filter state
                searchTextFieldState = searchTextFieldState,
                inputField = { TestInputField() }
            )
        }
        composeTestRule.onNodeWithTag(SearchBarTestTags.EXPANDED_SEARCH_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(inputFieldTestTag).assertIsDisplayed()
        composeTestRule.onNodeWithText(inputFieldText).assertIsDisplayed()
    }

    @Test
    fun searchBar_filterState_displaysLabelBoxes() {
        val types = listOf(SearchSort.Type(0), SearchSort.Type(1))
        val labels = listOf(SearchSort.Label("L1", 0,0), SearchSort.Label("L2", 1,1))
        val colors = listOf(SearchSort.Color(0), SearchSort.Color(1))
        val searchTextFieldState = TextFieldState()
        var lastSetSearchSort: SearchSort? = null

        composeTestRule.setContent {
            val searchBarState = rememberSearchBarState(SearchBarValue.Expanded)
            SearchBar(
                searchBarState = searchBarState,
                searchState = SearchState.FilterState(types = types, label = labels, color = colors),
                searchTextFieldState = searchTextFieldState,
                onSetSearch = { lastSetSearchSort = it },
                inputField = { TestInputField() }
            )
        }

        composeTestRule.onNodeWithTag(SearchBarTestTags.SEARCH_FILTER_STATE_COLUMN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchBarTestTags.SEARCH_TYPES_LABEL_BOX).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchBarTestTags.SEARCH_LABELS_LABEL_BOX).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchBarTestTags.SEARCH_COLORS_LABEL_BOX).assertIsDisplayed()

        // Test onSetSearch callback from one of the label boxes (e.g., first type)
        // Need to target the clickable item within the LabelBox.
        // The tag for Type items in LabelBox is LabelBoxTestTags.SEARCH_TYPE_ITEM_PREFIX + typeName + _index
        // This is hard to target without knowing resolved typeName. We click the first available type.
        val typeNodeTag = LabelBoxTestTags.SEARCH_TYPE_ITEM_PREFIX + "Type0_0" // Assuming typeName for index 0 is "Type0"
                                                                         // This needs to align with how typeNames are resolved in LabelBox
                                                                         // For a more robust test, one might need to adjust LabelBoxTest or provide mock resources.
                                                                         // As a simpler approach, find the first clickable node in the types LabelBox if possible.

        // Clicking the first color item as an example of onSetSearch
        val colorItemTag = LabelBoxTestTags.SEARCH_COLOR_ITEM_PREFIX + "0_0"
        composeTestRule.onNodeWithTag(colorItemTag).performClick()
        assertEquals(colors[0], lastSetSearchSort)
    }

    @Test
    fun searchBar_viewState_noResults_displaysNoResultsMessage() {
        val searchTextFieldState = TextFieldState()
        searchTextFieldState.setTextAndPlaceCursorAtEnd("query")

        composeTestRule.setContent {
            val searchBarState = rememberSearchBarState(SearchBarValue.Expanded)
            SearchBar(
                searchBarState = searchBarState,
                searchState = SearchState.ViewState(searches = emptyList(), isGrid = false),
                searchTextFieldState = searchTextFieldState,
                inputField = { TestInputField() }
            )
        }
        composeTestRule.onNodeWithTag(SearchBarTestTags.SEARCH_NO_RESULTS_COLUMN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchBarTestTags.SEARCH_NO_RESULTS_ICON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchBarTestTags.SEARCH_NO_RESULTS_TEXT).assertIsDisplayed()
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun searchBar_viewState_withResults_displaysResultsGrid() {
        val notes = listOf(
            NotePad(id = 1, title = "Note 1", detail = "Content 1",),
            NotePad(id = 2, title = "Note 2", detail = "Content 2",)
        )
        val searchTextFieldState = TextFieldState()
        searchTextFieldState.setTextAndPlaceCursorAtEnd("query")
        var clickedNoteId: Long? = null

        composeTestRule.setContent {
            val searchBarState = rememberSearchBarState(SearchBarValue.Expanded)
            SharedTransitionContainer {
                SearchBar(
                    searchBarState = searchBarState,
                    searchState = SearchState.ViewState(searches = notes, isGrid = false),
                    searchTextFieldState = searchTextFieldState,
                    onNoteClick = { note -> clickedNoteId = note.id },
                    inputField = { TestInputField() }
                )
            }

        }

        composeTestRule.onNodeWithTag(SearchBarTestTags.SEARCH_RESULTS_GRID).assertIsDisplayed()
        val note1Tag = SearchBarTestTags.SEARCH_RESULT_ITEM_PREFIX + notes[0].id
        val note2Tag = SearchBarTestTags.SEARCH_RESULT_ITEM_PREFIX + notes[1].id
        composeTestRule.onNodeWithTag(note1Tag).assertIsDisplayed()
        composeTestRule.onNodeWithTag(note2Tag).assertIsDisplayed()

        // Test onNoteClick
        composeTestRule.onNodeWithTag(note1Tag).performClick()
        assertEquals(notes[0].id, clickedNoteId)
    }
}
