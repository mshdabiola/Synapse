package com.mshdabiola.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.DeleteForeverDialog
import com.mshdabiola.model.testtag.DeleteForeverDialogTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class DeleteForeverDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun deleteForeverDialog_whenShowIsFalse_isHidden() {
        // Arrange
        composeTestRule.setContent {
            DeleteForeverDialog(
                show = false,
                onDismissRequest = {},
                onDelete = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(DeleteForeverDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun deleteForeverDialog_whenShowIsTrue_displaysCorrectly() {
        // Arrange
        composeTestRule.setContent {
            DeleteForeverDialog(
                show = true,
                onDismissRequest = {},
                onDelete = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(DeleteForeverDialogTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DeleteForeverDialogTestTags.TITLE_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DeleteForeverDialogTestTags.CONTENT_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DeleteForeverDialogTestTags.CONFIRM_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DeleteForeverDialogTestTags.DISMISS_BUTTON).assertIsDisplayed()
    }

    @Test
    fun deleteForeverDialog_confirmButton_invokesOnDeleteAndOnDismiss() {
        // Arrange
        var onDeleteCalled = false
        var onDismissRequestCalled = false
        val showDialog = mutableStateOf(true)

        composeTestRule.setContent {
            DeleteForeverDialog(
                show = showDialog.value,
                onDismissRequest = {
                    onDismissRequestCalled = true
                    showDialog.value = false // Simulate dialog dismissal
                },
                onDelete = { onDeleteCalled = true },
            )
        }

        // Act
        composeTestRule.onNodeWithTag(DeleteForeverDialogTestTags.CONFIRM_BUTTON).performClick()

        // Assert
        assertTrue(onDeleteCalled, "onDelete should be called")
        assertTrue(onDismissRequestCalled, "onDismissRequest should be called")
        assertFalse(showDialog.value, "Dialog should be hidden after confirm")
    }

    @Test
    fun deleteForeverDialog_dismissButton_invokesOnDismissOnly() {
        // Arrange
        var onDeleteCalled = false
        var onDismissRequestCalled = false
        val showDialog = mutableStateOf(true)

        composeTestRule.setContent {
            DeleteForeverDialog(
                show = showDialog.value,
                onDismissRequest = {
                    onDismissRequestCalled = true
                    showDialog.value = false // Simulate dialog dismissal
                },
                onDelete = { onDeleteCalled = true },
            )
        }

        // Act
        composeTestRule.onNodeWithTag(DeleteForeverDialogTestTags.DISMISS_BUTTON).performClick()

        // Assert
        assertFalse(onDeleteCalled, "onDelete should not be called")
        assertTrue(onDismissRequestCalled, "onDismissRequest should be called")
        assertFalse(showDialog.value, "Dialog should be hidden after dismiss")
    }
}
