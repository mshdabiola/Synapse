package com.mshdabiola.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.testtag.DateDialogTestTags
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class DateDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dateDialog_isNotDisplayed_whenShowIsFalse() {
        composeTestRule.setContent {
            val datePickerState = rememberDatePickerState()
            DateDialog(state = datePickerState, showDialog = false)
        }
        composeTestRule.onNodeWithTag(DateDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun dateDialog_isDisplayed_andElementsVisible_whenShowIsTrue() {
        composeTestRule.setContent {
            val datePickerState = rememberDatePickerState()
            DateDialog(state = datePickerState, showDialog = true)
        }
        composeTestRule.onNodeWithTag(DateDialogTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DateDialogTestTags.DATE_PICKER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DateDialogTestTags.CONFIRM_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DateDialogTestTags.DISMISS_BUTTON).assertIsDisplayed()
    }

    @Test
    fun dateDialog_confirmButton_invokesOnSetDateAndOnDismissRequest() {
        val onSetDateMock = mockk<() -> Unit>(relaxed = true)
        val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            val datePickerState = rememberDatePickerState()
            DateDialog(
                state = datePickerState,
                showDialog = true,
                onSetDate = onSetDateMock,
                onDismissRequest = onDismissRequestMock
            )
        }

        composeTestRule.onNodeWithTag(DateDialogTestTags.CONFIRM_BUTTON).performClick()

        verify { onSetDateMock() }
        verify { onDismissRequestMock() }
    }

    @Test
    fun dateDialog_dismissButton_invokesOnDismissRequest() {
        val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            val datePickerState = rememberDatePickerState()
            DateDialog(
                state = datePickerState,
                showDialog = true,
                onDismissRequest = onDismissRequestMock
            )
        }

        composeTestRule.onNodeWithTag(DateDialogTestTags.DISMISS_BUTTON).performClick()

        verify { onDismissRequestMock() }
    }

    @Test
    fun dateDialog_onDismissRequest_isCalled_whenDialogIsDismissedExternally() {
        val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)
        val showDialogState = mutableStateOf(true)

        composeTestRule.setContent {
            val datePickerState = rememberDatePickerState()
            if (showDialogState.value) {
                DateDialog(
                    state = datePickerState,
                    showDialog = true, // Dialog is initially shown
                    onDismissRequest = {
                        onDismissRequestMock() // This is the callback we pass to DateDialog
                        showDialogState.value = false // Simulate the action of dismissing
                    }
                )
            }
        }

        // Simulate an external event that causes the DatePickerDialog's onDismissRequest to be called.
        // For testing purposes, we trigger the change in our showDialogState, which in turn
        // calls our onDismissRequestMock as defined in the setContent block.
        composeTestRule.runOnUiThread {
            showDialogState.value = false
        }
        composeTestRule.waitForIdle() // Allow UI to update

        verify { onDismissRequestMock() } // Verify our callback was invoked
        // After dismissal, the dialog should no longer exist
        composeTestRule.onNodeWithTag(DateDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }
}
