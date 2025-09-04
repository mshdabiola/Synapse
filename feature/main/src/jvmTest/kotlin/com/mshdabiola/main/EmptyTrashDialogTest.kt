package com.mshdabiola.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.EmptyTrashDialog
import com.mshdabiola.model.testtag.EmptyTrashDialogTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmptyTrashDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyTrashDialog_whenShowIsFalse_isHidden() {
        // Arrange
        composeTestRule.setContent {
            EmptyTrashDialog(
                show = false,
                onDismissRequest = {},
                onDelete = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(EmptyTrashDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun emptyTrashDialog_whenShowIsTrue_displaysCorrectly() {
        // Arrange
        composeTestRule.setContent {
            EmptyTrashDialog(
                show = true,
                onDismissRequest = {},
                onDelete = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(EmptyTrashDialogTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(EmptyTrashDialogTestTags.TITLE_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(EmptyTrashDialogTestTags.CONTENT_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(EmptyTrashDialogTestTags.CONFIRM_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(EmptyTrashDialogTestTags.DISMISS_BUTTON).assertIsDisplayed()
    }

    @Test
    fun emptyTrashDialog_confirmButton_invokesOnDeleteAndOnDismiss() {
        // Arrange
        var onDeleteCalled = false
        var onDismissRequestCalled = false
        val showDialog = mutableStateOf(true)

        composeTestRule.setContent {
            EmptyTrashDialog(
                show = showDialog.value,
                onDismissRequest = {
                    onDismissRequestCalled = true
                    showDialog.value = false // Simulate dialog dismissal
                },
                onDelete = { onDeleteCalled = true },
            )
        }

        // Act
        composeTestRule.onNodeWithTag(EmptyTrashDialogTestTags.CONFIRM_BUTTON).performClick()

        // Assert
        assertTrue(onDeleteCalled, "onDelete should be called")
        assertTrue(onDismissRequestCalled, "onDismissRequest should be called")
        assertFalse(showDialog.value, "Dialog should be hidden after confirm")
    }

    @Test
    fun emptyTrashDialog_dismissButton_invokesOnDismissOnly() {
        // Arrange
        var onDeleteCalled = false
        var onDismissRequestCalled = false
        val showDialog = mutableStateOf(true)

        composeTestRule.setContent {
            EmptyTrashDialog(
                show = showDialog.value,
                onDismissRequest = {
                    onDismissRequestCalled = true
                    showDialog.value = false // Simulate dialog dismissal
                },
                onDelete = { onDeleteCalled = true },
            )
        }

        // Act
        composeTestRule.onNodeWithTag(EmptyTrashDialogTestTags.DISMISS_BUTTON).performClick()

        // Assert
        assertFalse(onDeleteCalled, "onDelete should not be called")
        assertTrue(onDismissRequestCalled, "onDismissRequest should be called")
        assertFalse(showDialog.value, "Dialog should be hidden after dismiss")
    }
}
