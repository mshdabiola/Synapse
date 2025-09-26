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
package com.mshdabiola.label

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.mshdabiola.model.testtag.LabelScreenTestTags
import org.jetbrains.compose.resources.stringResource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import synapse.feature.label.generated.resources.Res
import synapse.feature.label.generated.resources.modules_designsystem_edit_label

class LabelScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Helper to allow tests to remember TextFieldState if needed, though often passed via LabelUiState
    @Composable
    private fun rememberTestTextFieldState(initialText: String = ""): TextFieldState {
        return remember { TextFieldState(initialText) }
    }

    private fun setupLabelScreen(
        initialUiState: LabelUiState,
        onBack: () -> Unit = {},
        onDelete: (Long) -> Unit = {},
        onAdd: (Int) -> Unit = {}, // Int is the index, -1 for new label
    ) {
        composeTestRule.setContent {
            LabelScreen(
                labelUiState = initialUiState,
                onBack = onBack,
                onDelete = onDelete,
                onAdd = onAdd,
            )
        }
    }

    @Test
    fun initialDisplay_showsAppBarAndNewLabelInput() {
        var titleText = ""
        composeTestRule.setContent {
            titleText = stringResource(Res.string.modules_designsystem_edit_label)
        }
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(-1, TextFieldState("")),
                labels = emptyList(),
                isEditMode = true, // To ensure focusRequester logic is covered if applicable
            ),
        )

        composeTestRule.onNodeWithTag(LabelScreenTestTags.BACK_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithText(titleText).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).assertIsDisplayed()
        // Done button (trailing icon in EditLabelTextField) is hidden when text is empty
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON).assertDoesNotExist()
    }

    @Test
    fun backButton_invokesOnBackCallback() {
        var onBackCalled = false
        setupLabelScreen(
            initialUiState = LabelUiState(newLabel = LabelState(-1, TextFieldState(""))),
            onBack = { onBackCalled = true },
        )

        composeTestRule.onNodeWithTag(LabelScreenTestTags.BACK_BUTTON).performClick()
        assertTrue(onBackCalled)
    }

    @Test
    fun newLabelInput_iconChangesWithText_andClearButtonWorks_whenFocused() {
        val newLabelState = LabelState(-1, TextFieldState())
        setupLabelScreen(
            initialUiState = LabelUiState(newLabel = newLabelState, labels = emptyList(), isEditMode = true),
        )
        composeTestRule.waitForIdle() // Allow LaunchedEffect for focus if isEditMode = true

        // Initial: newLabel is focused (due to isEditMode or currentFocus = -1), text is empty.
        // Leading icon should be Add icon.
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON).assertDoesNotExist()

        // Type something
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performTextInput("Test")
        // Text is not blank, still focused. Leading icon becomes Clear, Trailing becomes Done.
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.NEW_LABEL_DONE_BUTTON,
            useUnmergedTree = true,
        ).assertIsDisplayed().assertIsEnabled()

        // Click clear
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON,
            useUnmergedTree = true,
        ).performClick()
        assertEquals("", newLabelState.label.text.toString()) // Text should be cleared
        // Text is blank, still focused. Leading icon reverts to Add.
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON).assertDoesNotExist()
        // Done button hides
    }

    @Test
    fun newLabelInput_typeAndClickDone_invokesOnAdd() {
        var onAddCalledWithIndex: Int? = null
        val newLabelState = LabelState(-1, TextFieldState())
        setupLabelScreen(
            initialUiState = LabelUiState(newLabel = newLabelState, isEditMode = false),
            onAdd = { index -> onAddCalledWithIndex = index },
        )

        // Click to focus explicitly, as isEditMode=false won't auto-focus newLabel initially.
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performClick()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT)
            .performTextInput("New Label")
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON, useUnmergedTree = true)
            .assertIsDisplayed().performClick()

        assertEquals(-1, onAddCalledWithIndex) // -1 for new label
    }

    @Test
    fun newLabelInput_imeActionDone_invokesOnAdd() {
        var onAddCalledWithIndex: Int? = null
        val newLabelState = LabelState(-1, TextFieldState())
        setupLabelScreen(
            initialUiState = LabelUiState(newLabel = newLabelState, isEditMode = false),
            onAdd = { index -> onAddCalledWithIndex = index },
        )

        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT)
            .performClick() // Ensure focus
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT)
            .performTextInput("New Label via IME")
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT)
            .performImeAction()

        assertEquals(-1, onAddCalledWithIndex)
    }

    @Test
    fun existingLabel_clickEdit_focusesAndChangesIconsCorrectly() {
        val labelId = 1L
        val existingLabel = LabelState(labelId, TextFieldState("Existing"))
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(0, TextFieldState("")),
                // newLabel id is not -1 to avoid initial focus on it
                labels = listOf(existingLabel),
                isEditMode = false,
            ),
        )

        // Initial state (not focused): Label icon (leading), Edit button (trailing)
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.itemLabelIconIndicator(labelId),
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.itemEditButton(labelId),
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDeleteButton(labelId)).assertDoesNotExist()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDoneButton(labelId)).assertDoesNotExist()

        // Click edit button on existing label item to focus it
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.itemEditButton(labelId),
            useUnmergedTree = true,
        ).performClick()
        composeTestRule.waitForIdle() // Allow focus change and recomposition

        // Focused state: Delete icon (leading), Done button (trailing, if text not blank)
        composeTestRule
            .onNodeWithTag(
                LabelScreenTestTags.itemDeleteButton(labelId),
                useUnmergedTree = true,
            ).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(
                LabelScreenTestTags.itemDoneButton(labelId),
                useUnmergedTree = true,
            ).assertIsDisplayed().assertIsEnabled()
        composeTestRule
            .onNodeWithTag(LabelScreenTestTags.itemLabelIconIndicator(labelId)).assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(LabelScreenTestTags.itemEditButton(labelId)).assertDoesNotExist()
    }

    @Test
    fun existingLabel_editTextAndClickDone_invokesOnAdd() {
        var onAddCalledWithIndex: Int? = null
        val labelId = 1L
        val itemIndex = 0 // The index of the item in the `labels` list
        val existingLabel = LabelState(labelId, TextFieldState("Old Text"))
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(0, TextFieldState("")),
                // Avoid initial focus on newLabel
                labels = listOf(existingLabel),
                isEditMode = false,
            ),
            onAdd = { index -> onAddCalledWithIndex = index },
        )

        // Click Edit to focus
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.itemEditButton(labelId),
            useUnmergedTree = true,
        ).performClick()
        composeTestRule.waitForIdle()

        // Edit text
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.itemLabelInput(labelId),
            useUnmergedTree = true,
        ).performTextInput(" New Text") // Appends

        // Click Done
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.itemDoneButton(labelId),
            useUnmergedTree = true,
        ).performClick()

        assertEquals(itemIndex, onAddCalledWithIndex)
    }

    @Test
    fun existingLabel_editTextAndImeActionDone_invokesOnAdd() {
        var onAddCalledWithIndex: Int? = null
        val labelId = 1L
        val itemIndex = 0
        val existingLabel = LabelState(labelId, TextFieldState("Old Text"))
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(0, TextFieldState("")), // Avoid initial focus on newLabel
                labels = listOf(existingLabel),
                isEditMode = false,
            ),
            onAdd = { index -> onAddCalledWithIndex = index },
        )

        // Click Edit to focus
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.itemEditButton(labelId),
            useUnmergedTree = true,
        ).performClick()
        composeTestRule.waitForIdle()

        // Edit text
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.itemLabelInput(labelId),
            useUnmergedTree = true,
        ).performTextInput(" More Text")

        // Perform IME action
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.itemLabelInput(labelId),
            useUnmergedTree = true,
        ).performImeAction()

        assertEquals(itemIndex, onAddCalledWithIndex)
    }

    @Test
    fun existingLabel_clickDelete_invokesOnDelete() {
        var onDeleteCalledWithId: Long? = null
        val labelIdToDelete = 1L
        val existingLabel = LabelState(labelIdToDelete, TextFieldState("To Delete"))
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(0, TextFieldState("")), // Avoid initial focus
                labels = listOf(existingLabel),
                isEditMode = false,
            ),
            onDelete = { id -> onDeleteCalledWithId = id },
        )

        // Click Edit button to focus the item, which makes the Delete icon visible
        composeTestRule.onNodeWithTag(
            LabelScreenTestTags.itemEditButton(labelIdToDelete),
            useUnmergedTree = true,
        ).performClick()
        composeTestRule.waitForIdle() // Wait for recomposition

        // Now the Delete icon (leading) should be visible
        composeTestRule
            .onNodeWithTag(
                LabelScreenTestTags.itemDeleteButton(labelIdToDelete),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(LabelScreenTestTags.itemDeleteButton(labelIdToDelete), useUnmergedTree = true)
            .performClick()

        assertEquals(labelIdToDelete, onDeleteCalledWithId)
    }

    @Test
    fun screen_whenIsEditModeTrue_newLabelInputIsFocused_showsAddIconInitially() {
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(-1, TextFieldState("")), // newLabel id is -1
                labels = emptyList(),
                isEditMode = true, // isEditMode = true will trigger LaunchedEffect
            ),
        )
        composeTestRule.waitForIdle() // Allow LaunchedEffect, focus, and state updates

        // Due to isEditMode=true and newLabel.id=-1, EditLabelTextField should be focused.
        // Since text is empty, leading icon is Add.
        composeTestRule
            .onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertDoesNotExist()
    }

    @Test
    fun screen_whenNoExistingLabels_onlyNewLabelInputIsShownInListContent() {
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(-1, TextFieldState("")),
                labels = emptyList(),
                isEditMode = false,
            ),
        )

        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).assertIsDisplayed()
        // Verify no items by checking for a non-existent item tag
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelInput(0L)).assertDoesNotExist()
    }

    @Test
    fun newLabelInput_whenTextCausesError_doneButtonIsDisabled() {
        val existingLabelName = "Existing Label"
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(-1, TextFieldState("")),
                labels = listOf(LabelState(1L, TextFieldState(existingLabelName))),
                isEditMode = false,
            ),
        )
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performClick() // Focus
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT)
            .performTextInput(existingLabelName)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON, useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun existingLabel_whenTextCausesError_doneButtonIsDisabled() {
        val label1Id = 1L
        val label2Id = 2L
        val label1Name = "Unique Label 1"
        val label2Name = "Unique Label 2"

        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(0, TextFieldState("")),
                labels = listOf(
                    LabelState(label1Id, TextFieldState(label1Name)),
                    LabelState(label2Id, TextFieldState(label2Name)),
                ),
                isEditMode = false,
            ),
        )

        // Focus the first item for editing
        composeTestRule
            .onNodeWithTag(LabelScreenTestTags.itemLabelInput(label1Id), useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()

        // Change text of first item to be same as second item
        // First clear existing text, then type the new one.
        // Assuming TextFieldState.edit or similar would be used by user, which replaces.
        // For test, performTextInput might append if not careful or if state isn't cleared first.
        // Let's assume the TextFieldState is directly manipulated or clearText is available if needed.
        // For simplicity, let's assume performTextInput replaces if the field is selected (which it should be).
        // To be absolutely sure, one might need to clear it first if the component doesn't auto-clear on edit.
        // However, EditLabelTextField's state is directly from labelState.label
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelInput(label1Id), useUnmergedTree = true)
            .performTextClearance()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelInput(label1Id), useUnmergedTree = true)
            .performTextInput(label2Name) // This should replace, not append.
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDoneButton(label1Id), useUnmergedTree = true)
            .assertDoesNotExist()
    }
}
