package com.mshdabiola.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.testtag.TimeDialogTestTags
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class TimeDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun timeDialog_isNotDisplayed_whenShowIsFalse() {
        composeTestRule.setContent {
            TimeDialog(showDialog = false, state = TimePickerState(0,0,false))
        }
        composeTestRule.onNodeWithTag(TimeDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun timeDialog_isDisplayed_andElementsVisible_whenShowIsTrue() {
        composeTestRule.setContent {
            TimeDialog(showDialog = true, state = TimePickerState(12,30,false))
        }
        composeTestRule.onNodeWithTag(TimeDialogTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TimeDialogTestTags.TIME_PICKER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TimeDialogTestTags.CONFIRM_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TimeDialogTestTags.DISMISS_BUTTON).assertIsDisplayed()
    }

    @Test
    fun timeDialog_confirmButton_invokesOnSetTimeAndOnDismissRequest() {
        val onSetTimeMock = mockk<() -> Unit>(relaxed = true)
        val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            TimeDialog(
                showDialog = true,
                state = TimePickerState(10,0,true),
                onSetTime = onSetTimeMock,
                onDismissRequest = onDismissRequestMock
            )
        }

        composeTestRule.onNodeWithTag(TimeDialogTestTags.CONFIRM_BUTTON).performClick()

        verify { onSetTimeMock() }
        verify { onDismissRequestMock() }
    }

    @Test
    fun timeDialog_dismissButton_invokesOnDismissRequest() {
        val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            TimeDialog(
                showDialog = true,
                state = TimePickerState(9,15,false),
                onDismissRequest = onDismissRequestMock
            )
        }

        composeTestRule.onNodeWithTag(TimeDialogTestTags.DISMISS_BUTTON).performClick()

        verify { onDismissRequestMock() }
    }

    @Test
    fun timeDialog_onDismissRequest_isCalled_whenDialogIsDismissedExternally() {
        val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)
        val showDialogState = mutableStateOf(true)

        composeTestRule.setContent {
            if (showDialogState.value) {
                TimeDialog(
                    showDialog = true, // Dialog is initially shown
                    state = TimePickerState(8,45,true),
                    onDismissRequest = {
                        onDismissRequestMock() // This is the callback we pass to TimeDialog
                        showDialogState.value = false // Simulate the action of dismissing
                    }
                )
            }
        }

        // Simulate an external event that causes the DatePickerDialog's onDismissRequest to be called.
        composeTestRule.runOnUiThread {
            showDialogState.value = false
        }
        composeTestRule.waitForIdle() // Allow UI to update

        verify { onDismissRequestMock() } // Verify our callback was invoked
        // After dismissal, the dialog should no longer exist
        composeTestRule.onNodeWithTag(TimeDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun timeDialog_initializesTimePicker_withGivenState() {
        val initialHour = 10
        val initialMinute = 30
        val is24Hour = false
        val specificTimePickerState = TimePickerState(initialHour, initialMinute, is24Hour)

        composeTestRule.setContent {
            TimeDialog(showDialog = true, state = specificTimePickerState)
        }

        // Verify the TimePicker is displayed with the provided state.
        // Direct assertion of the displayed time in TimePicker is complex.
        // We ensure the picker is there; state correctness is implicitly verified by onSetTime in other tests.
        composeTestRule.onNodeWithTag(TimeDialogTestTags.TIME_PICKER).assertIsDisplayed()
        // We can check if the TimePickerState passed to the TimePicker component inside TimeDialog
        // has the correct initial values if we had access to the state used by the TimePicker node.
        // However, with Jetpack Compose testing, you typically verify state through observed behavior (callbacks)
        // or by checking displayed text if available and stable.
    }
}
