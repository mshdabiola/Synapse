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
import androidx.compose.ui.test.printToLog
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
    fun newLabelInput_iconChangesWithText_andClearButtonWorks_whenFocused() {
        val newLabelState = LabelState(-1, TextFieldState())
        // isEditMode = true causes initial focus on newLabel via LaunchedEffect.
        // LabelScreen.currentFocus is initialized to -1, so EditLabelTextField's isCurrentFocus = true.
        // The onFocused callback in EditLabelTextField will also set LabelScreen.currentFocus = -1.
        setupLabelScreen(initialUiState = LabelUiState(newLabel = newLabelState, labels = emptyList(), isEditMode = true))
        composeTestRule.waitForIdle() // Allow LaunchedEffect and state updates

        // Initial state: newLabel is focused (isCurrentFocus=true in EditLabelTextField due to currentFocus=-1), text is empty.
        // -> Add icon should be displayed.
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR,useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertDoesNotExist()

        // Type something
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performTextInput("Test")
        // Now text is not blank, still focused (isCurrentFocus=true).
        // -> Clear icon should be displayed.
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON).assertIsDisplayed().assertIsEnabled()

        // Click clear
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).performClick()
        assertEquals("", newLabelState.label.text.toString()) // Text should be cleared
        // Text is now blank, still focused (isCurrentFocus=true).
        // -> Add icon should be displayed again.
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR,useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON).assertDoesNotExist() // Done button hides
    }

    @Test
    fun newLabelInput_typeAndClickDone_invokesOnAdd() {
        var onAddCalledWithIndex: Int? = null
        val newLabelState = LabelState(-1, TextFieldState())
        setupLabelScreen(
            initialUiState = LabelUiState(newLabel = newLabelState, isEditMode = false), // isEditMode = false
            onAdd = { index -> onAddCalledWithIndex = index },
        )
        // LabelScreen.currentFocus is -1, so EditLabelTextField's isCurrentFocus = true. Text is empty.
        // -> Add icon should be displayed.
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR,useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON).assertDoesNotExist()

        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performClick() // Click to focus. onFocused -> currentFocus = -1.
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_INPUT).performTextInput("New Label")
        // After typing, text is not blank, isCurrentFocus = true -> Clear icon should appear (not asserted here).
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
                newLabel = LabelState(-1, TextFieldState("")), // newLabel id is not -1 to avoid initial focus on it
                labels = listOf(existingLabel),
                isEditMode = false,
            ),
        )
        // Initial LabelScreen.currentFocus is -1, so for existingLabel, isCurrentFocus = false.
        // -> Label icon, Edit button
        composeTestRule.onNodeWithTag(LabelScreenTestTags.LIST).printToLog("LIST")
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelIconIndicator(labelId))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelId)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDeleteButton(labelId)).assertDoesNotExist()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDoneButton(labelId)).assertDoesNotExist()

        // Click edit button on existing label item
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelId)).performClick()
        // Now onFocused callback sets LabelScreen.currentFocus = index for this item.
        // So for this existingLabel, isCurrentFocus = true.
        // -> Delete button, Done button
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
                newLabel = LabelState(-1, TextFieldState("")), // currentFocus = -1 initially
                labels = listOf(existingLabel),
                 isEditMode = false,
            ),
            onAdd = { index -> onAddCalledWithIndex = index },
        )

        // Focus and edit
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelId)).performClick() // Sets currentFocus to itemIndex
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
                newLabel = LabelState(-1, TextFieldState("")), // currentFocus = -1 initially
                labels = listOf(existingLabel),
                 isEditMode = false,
            ),
            onAdd = { index -> onAddCalledWithIndex = index },
        )
        // Focus and edit
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelId)).performClick() // Sets currentFocus to itemIndex
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
                newLabel = LabelState(-1, TextFieldState("")), // currentFocus = -1 initially
                labels = listOf(existingLabel),
                 isEditMode = false,
            ),
            onDelete = { id -> onDeleteCalledWithId = id },
        )

        // Focus item then click delete
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemEditButton(labelIdToDelete)).performClick() // Sets currentFocus to itemIndex
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemDeleteButton(labelIdToDelete)).performClick()

        assertEquals(labelIdToDelete, onDeleteCalledWithId)
    }

    @Test
    fun screen_whenIsEditModeTrue_newLabelInputIsFocused_showsAddIconInitially() {
        setupLabelScreen(
            initialUiState = LabelUiState(
                newLabel = LabelState(-1, TextFieldState("")), // newLabel id is -1
                labels = emptyList(),
                isEditMode = true, // isEditMode = true will trigger LaunchedEffect in EditLabelTextField
            ),
        )
        composeTestRule.waitForIdle() // Allow LaunchedEffect, focus, and state updates

        // LabelScreen.currentFocus is -1 (initial) or set to -1 by onFocused after LaunchedEffect.
        // EditLabelTextField gets isCurrentFocus = true. Text is empty.
        // -> Add icon should appear.
        composeTestRule.onNodeWithTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR,useUnmergedTree = true).assertIsDisplayed()
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
        // Check no other items by trying to find a generic item tag that would exist if list was populated
        // This is a bit indirect; a more robust way might be to check the count if LazyColumn supports it directly in test.
        // For now, ensuring no specific existing label items appear is sufficient.
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelInput(0L)).assertDoesNotExist()
        composeTestRule.onNodeWithTag(LabelScreenTestTags.itemLabelInput(1L)).assertDoesNotExist()
    }
}
