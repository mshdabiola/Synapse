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
package com.mshdabiola.select

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.mshdabiola.model.testtag.SelectScreenTestTags // Added import
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SelectScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // --- Fake callbacks using simple lambdas and state holders ---
    private var onBackCalled = false
    private var lastCheckedIndex: Int? = null
    private var onCreateLabelCalled = false

    private val fakeOnBack: () -> Unit = { onBackCalled = true }
    private val fakeOnCheckClick: (Int) -> Unit = { index -> lastCheckedIndex = index }
    private val fakeOnCreateLabel: () -> Unit = { onCreateLabelCalled = true }
    // --- End of fake callbacks ---

    private val sampleLabels = listOf(
        LabelUiState(1, "Work", ToggleableState.On),
        LabelUiState(2, "Personal", ToggleableState.Off),
        LabelUiState(3, "Urgent", ToggleableState.Indeterminate),
        LabelUiState(4, "Shopping", ToggleableState.Off),
        LabelUiState(5, "Ideas", ToggleableState.On),
    )

    // Helper to reset callback states before each test if needed, or manage locally
    private fun resetCallbackStates() {
        onBackCalled = false
        lastCheckedIndex = null
        onCreateLabelCalled = false
    }

    @Test
    fun screen_isDisplayed_withBasicElements() {
        resetCallbackStates()
        val uiState = SelectUiState(
            labels = sampleLabels.take(2),
            labelQuery = TextFieldState(""),
            showAddLabel = false,
        )
        composeTestRule.setContent {
            SelectLabelScreen(
                selectUiState = uiState,
                onBack = fakeOnBack,
                onCheckClick = fakeOnCheckClick,
                onCreateLabel = fakeOnCreateLabel,
            )
        }

        composeTestRule.onNodeWithTag(SelectScreenTestTags.SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectScreenTestTags.TOP_APP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectScreenTestTags.BACK_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectScreenTestTags.LABEL_QUERY_TEXT_FIELD).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectScreenTestTags.LABEL_LIST).assertIsDisplayed()
    }

    @Test
    fun backButton_callsOnBack_whenClicked() {
        resetCallbackStates()
        val uiState = SelectUiState(labelQuery = TextFieldState(""))
        composeTestRule.setContent {
            SelectLabelScreen(selectUiState = uiState, onBack = fakeOnBack)
        }

        composeTestRule.onNodeWithTag(SelectScreenTestTags.BACK_BUTTON).performClick()
        assertTrue("onBack callback should have been called", onBackCalled)
    }

    @Test
    fun labelQueryTextField_acceptsInput() {
        resetCallbackStates()
        val initialQuery = ""
        val newQuery = "New Query"
        val textFieldState = TextFieldState(initialQuery)

        val uiState = SelectUiState(
            labels = emptyList(),
            labelQuery = textFieldState,
            showAddLabel = false,
        )
        composeTestRule.setContent {
            SelectLabelScreen(selectUiState = uiState)
        }

        composeTestRule.onNodeWithTag(SelectScreenTestTags.LABEL_QUERY_TEXT_FIELD)
            .performTextInput(newQuery)

        assertEquals(newQuery, textFieldState.text.toString())
    }

    @Test
    fun createLabelButton_isDisplayed_andCallsOnCreateLabel_whenShowAddLabelIsTrue() {
        resetCallbackStates()
        val queryText = "New Label"
        val textFieldState = TextFieldState()
        textFieldState.setTextAndPlaceCursorAtEnd(queryText)

        val uiState = SelectUiState(
            labels = emptyList(),
            labelQuery = textFieldState,
            showAddLabel = true,
        )
        composeTestRule.setContent {
            SelectLabelScreen(
                selectUiState = uiState,
                onCreateLabel = fakeOnCreateLabel,
            )
        }

        composeTestRule.onNodeWithTag(SelectScreenTestTags.CREATE_LABEL_BUTTON)
            .assertIsDisplayed()
            .performClick()

        assertTrue("onCreateLabel callback should have been called", onCreateLabelCalled)
    }

    @Test
    fun labelList_displaysItems_andCheckboxClickCallsOnCheckClick() {
        resetCallbackStates()
        val uiState = SelectUiState(
            labels = sampleLabels,
            labelQuery = TextFieldState(""),
            showAddLabel = false,
        )
        composeTestRule.setContent {
            SelectLabelScreen(
                selectUiState = uiState,
                onCheckClick = fakeOnCheckClick,
            )
        }

        val firstLabel = sampleLabels[0]
        composeTestRule.onNodeWithTag(SelectScreenTestTags.labelItemText(firstLabel.id))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectScreenTestTags.labelItemCheckbox(firstLabel.id))
            .assertIsDisplayed()
            .performClick()

        assertEquals("onCheckClick should have been called with index 0", 0, lastCheckedIndex)

        val lastLabel = sampleLabels.last()
        composeTestRule.onNodeWithTag(SelectScreenTestTags.LABEL_LIST)
            .performScrollToNode(hasTestTag(SelectScreenTestTags.labelItem(lastLabel.id)))

        composeTestRule.onNodeWithTag(SelectScreenTestTags.labelItemText(lastLabel.id))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectScreenTestTags.labelItemCheckbox(lastLabel.id))
            .assertIsDisplayed()
            .performClick()

        assertEquals(
            "onCheckClick should have been called with the last item's index",
            sampleLabels.size - 1,
            lastCheckedIndex,
        )
    }

    @Test
    fun labelItemCheckbox_reflectsToggleableState() {
        resetCallbackStates()
        var clickedIndexForThisTest: Int? = null
        val customFakeOnCheckClick: (Int) -> Unit = { index -> clickedIndexForThisTest = index }

        val labelsWithDifferentStates = listOf(
            LabelUiState(10, "Is On", ToggleableState.On),
            LabelUiState(11, "Is Off", ToggleableState.Off),
            LabelUiState(12, "Is Indeterminate", ToggleableState.Indeterminate),
        )
        val uiState = SelectUiState(
            labels = labelsWithDifferentStates,
            labelQuery = TextFieldState(""),
            showAddLabel = false,
        )
        composeTestRule.setContent {
            SelectLabelScreen(
                selectUiState = uiState,
                onCheckClick = customFakeOnCheckClick,
            )
        }

        composeTestRule.onNodeWithTag(SelectScreenTestTags.labelItemCheckbox(10L))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectScreenTestTags.labelItemCheckbox(11L))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectScreenTestTags.labelItemCheckbox(12L))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(SelectScreenTestTags.labelItemCheckbox(10L)).performClick()
        assertEquals("Clicked checkbox for item at index 0", 0, clickedIndexForThisTest)

        clickedIndexForThisTest = null
        composeTestRule.onNodeWithTag(SelectScreenTestTags.labelItemCheckbox(11L)).performClick()
        assertEquals("Clicked checkbox for item at index 1", 1, clickedIndexForThisTest)
    }
}
