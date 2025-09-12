package com.mshdabiola.label

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.mshdabiola.model.testtag.LabelScreenTestTags // Added import
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
                isEditMode = true,
            ),
        )

        composeTestRule.onNodeWithTag(LabelScreenTestTags.BACK_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithText(titleText).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON)
            .assertDoesNotExist() // Done button initially hidden
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
    fun newLabelInput_focusChangesIcons_andClearButtonWorks() {
        val newLabelState = LabelState(-1, TextFieldState())
        setupLabelScreen(initialUiState = LabelUiState(newLabel = newLabelState, isEditMode = true))

        // Initial state when isEditMode=true and input is focused by LaunchedEffect:
        // clear button should be displayed, add icon indicator should not.
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR).assertDoesNotExist()

        // Type something
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performTextInput("Test")
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON).assertIsDisplayed()
            .assertIsEnabled()

        // Click clear
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).performClick()
        assertEquals("", newLabelState.label.text.toString()) // Text should be cleared
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON)
            .assertDoesNotExist() // Done button hides when text is blank
        // Focus should remain, so clear button still visible, add icon indicator not visible
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR).assertDoesNotExist()
    }

    @Test
    fun newLabelInput_typeAndClickDone_invokesOnAdd() {
        var onAddCalledWithIndex: Int? = null
        val newLabelState = LabelState(-1, TextFieldState())
        setupLabelScreen(
            initialUiState = LabelUiState(newLabel = newLabelState, isEditMode = false), // isEditMode = false for this test flow
            onAdd = { index -> onAddCalledWithIndex = index },
        )
        // When not in edit mode initially, add icon indicator is shown
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertDoesNotExist()

        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performClick() // Click to focus
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performTextInput("New Label")
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON).assertIsDisplayed()
            .performClick()
        assertEquals(-1, onAddCalledWithIndex)
    }

    @Test
    fun newLabelInput_imeActionDone_invokesOnAdd() {
        var onAddCalledWithIndex: Int? = null
        val newLabelState = LabelState(-1, TextFieldState())
        setupLabelScreen(
            initialUiState = LabelUiState(newLabel = newLabelState, isEditMode = false),
            onAdd = { index -> onAddCalledWithIndex = index },
        )

        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performClick() // Click to focus
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performTextInput("New Label via IME")
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performImeAction()
        assertEquals(-1, onAddCalledWithIndex)
    }

    @Test
    fun existingLabel_clickEdit_focusesAndChangesIcons() {
        val labelId = 1L
        val existingLabel = LabelState(labelId, TextFieldState("Existing"))
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(0, TextFieldState("")), // newLabel id is not -1 to avoid focus
                labels = listOf(existingLabel),
                isEditMode = false, // Set to false, focus is manual via edit button
            ),
        )

        // Initial state: label icon, edit button
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelIconIndicator(labelId))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelId)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDeleteButton(labelId)).assertDoesNotExist()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDoneButton(labelId)).assertDoesNotExist()

        // Click edit button
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelId)).performClick()

        // Focused state: delete button, done button (if text not blank)
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelIconIndicator(labelId))
            .assertDoesNotExist()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelId)).assertDoesNotExist()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDeleteButton(labelId)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDoneButton(labelId)).assertIsDisplayed()
    }

    @Test
    fun existingLabel_editTextAndClickDone_invokesOnAdd() {
        var onAddCalledWithIndex: Int? = null
        val labelId = 1L
        val itemIndex = 0
        val existingLabel = LabelState(labelId, TextFieldState("Old Text"))
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(-1, TextFieldState("")),
                labels = listOf(existingLabel),
                 isEditMode = false,
            ),
            onAdd = { index -> onAddCalledWithIndex = index },
        )

        // Focus and edit
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelId)).performClick()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelInput(labelId))
            .performTextInput(" New Text") // Appends
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDoneButton(labelId)).performClick()

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
                newLabel = LabelState(-1, TextFieldState("")),
                labels = listOf(existingLabel),
                 isEditMode = false,
            ),
            onAdd = { index -> onAddCalledWithIndex = index },
        )
        // Focus and edit
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelId)).performClick()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelInput(labelId))
            .performTextInput(" More Text")
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelInput(labelId)).performImeAction()

        assertEquals(itemIndex, onAddCalledWithIndex)
    }

    @Test
    fun existingLabel_clickDelete_invokesOnDelete() {
        var onDeleteCalledWithId: Long? = null
        val labelIdToDelete = 1L
        val existingLabel = LabelState(labelIdToDelete, TextFieldState("To Delete"))
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(-1, TextFieldState("")),
                labels = listOf(existingLabel),
                 isEditMode = false,
            ),
            onDelete = { id -> onDeleteCalledWithId = id },
        )

        // Focus item then click delete
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelIdToDelete)).performClick()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDeleteButton(labelIdToDelete)).performClick()

        assertEquals(labelIdToDelete, onDeleteCalledWithId)
    }

    @Test
    fun screen_whenIsEditModeTrue_newLabelInputIsFocusedInitially() {
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(-1, TextFieldState("")),
                labels = emptyList(),
                isEditMode = true, // Key for this test
            ),
        )
        composeTestRule.waitForIdle() // Allow LaunchedEffect to run

        // When new label input is focused programmatically, the clear button should appear.
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR).assertDoesNotExist()
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
        // Verify a non-existent item is not found, ensuring no actual items are rendered
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelInput(999L)).assertDoesNotExist()
    }
}
