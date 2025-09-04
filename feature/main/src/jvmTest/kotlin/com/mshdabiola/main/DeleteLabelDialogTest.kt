package com.mshdabiola.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.DeleteLabelAlertDialog
import com.mshdabiola.model.testtag.DeleteLabelDialogTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeleteLabelDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun deleteLabelDialog_whenShowIsFalse_isHidden() {
        // Arrange
        composeTestRule.setContent {
            DeleteLabelAlertDialog(
                show = false,
                onDismissRequest = {},
                onDelete = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(DeleteLabelDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun deleteLabelDialog_whenShowIsTrue_displaysCorrectly() {
        // Arrange
        composeTestRule.setContent {
            DeleteLabelAlertDialog(
                show = true,
                onDismissRequest = {},
                onDelete = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(DeleteLabelDialogTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DeleteLabelDialogTestTags.TITLE_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DeleteLabelDialogTestTags.CONTENT_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DeleteLabelDialogTestTags.CONFIRM_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DeleteLabelDialogTestTags.DISMISS_BUTTON).assertIsDisplayed()
    }

    @Test
    fun deleteLabelDialog_confirmButton_invokesOnDeleteAndOnDismiss() {
        // Arrange
        var onDeleteCalled = false
        var onDismissRequestCalled = false
        val showDialog = mutableStateOf(true)

        composeTestRule.setContent {
            DeleteLabelAlertDialog(
                show = showDialog.value,
                onDismissRequest = {
                    onDismissRequestCalled = true
                    showDialog.value = false // Simulate dialog dismissal
                },
                onDelete = { onDeleteCalled = true },
            )
        }

        // Act
        composeTestRule.onNodeWithTag(DeleteLabelDialogTestTags.CONFIRM_BUTTON).performClick()

        // Assert
        assertTrue(onDeleteCalled, "onDelete should be called")
        assertTrue(onDismissRequestCalled, "onDismissRequest should be called")
        assertFalse(showDialog.value, "Dialog should be hidden after confirm")
    }

    @Test
    fun deleteLabelDialog_dismissButton_invokesOnDismissOnly() {
        // Arrange
        var onDeleteCalled = false
        var onDismissRequestCalled = false
        val showDialog = mutableStateOf(true)

        composeTestRule.setContent {
            DeleteLabelAlertDialog(
                show = showDialog.value,
                onDismissRequest = {
                    onDismissRequestCalled = true
                    showDialog.value = false // Simulate dialog dismissal
                },
                onDelete = { onDeleteCalled = true },
            )
        }

        // Act
        composeTestRule.onNodeWithTag(DeleteLabelDialogTestTags.DISMISS_BUTTON).performClick()

        // Assert
        assertFalse(onDeleteCalled, "onDelete should not be called")
        assertTrue(onDismissRequestCalled, "onDismissRequest should be called")
        assertFalse(showDialog.value, "Dialog should be hidden after dismiss")
    }
}
