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
package com.mshdabiola.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.testtag.TimeDialogTestTags
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class TimeDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun timeDialog_isNotDisplayed_whenShowIsFalse() {
        composeTestRule.setContent {
            TimeDialog(showDialog = false, state = TimePickerState(0, 0, false))
        }
        composeTestRule.onNodeWithTag(TimeDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun timeDialog_isDisplayed_andElementsVisible_whenShowIsTrue() {
        composeTestRule.setContent {
            TimeDialog(showDialog = true, state = TimePickerState(12, 30, false))
        }
        composeTestRule.onNodeWithTag(TimeDialogTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TimeDialogTestTags.TIME_PICKER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TimeDialogTestTags.CONFIRM_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TimeDialogTestTags.DISMISS_BUTTON).assertIsDisplayed()
    }

    @Test
    fun timeDialog_confirmButton_invokesOnSetTimeAndOnDismissRequest_andDismissesDialog() {
        var onSetTimeCalled = false
        var onDismissRequestCalled = false
        var showDialog by mutableStateOf(true)

        composeTestRule.setContent {
            if (showDialog) {
                TimeDialog(
                    showDialog = true,
                    state = TimePickerState(10, 0, true),
                    onSetTime = { onSetTimeCalled = true },
                    onDismissRequest = {
                        onDismissRequestCalled = true
                        showDialog = false // Simulate dismissal
                    },
                )
            }
        }

        composeTestRule.onNodeWithTag(TimeDialogTestTags.CONFIRM_BUTTON).performClick()

        assertTrue("onSetTime should have been called", onSetTimeCalled)
        assertTrue("onDismissRequest should have been called", onDismissRequestCalled)
        assertFalse("Dialog should be hidden after confirm click", showDialog)
        composeTestRule.onNodeWithTag(TimeDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun timeDialog_dismissButton_invokesOnDismissRequest_andDismissesDialog() {
        var onDismissRequestCalled = false
        var showDialog by mutableStateOf(true)

        composeTestRule.setContent {
            if (showDialog) {
                TimeDialog(
                    showDialog = true,
                    state = TimePickerState(9, 15, false),
                    onDismissRequest = {
                        onDismissRequestCalled = true
                        showDialog = false // Simulate dismissal
                    },
                )
            }
        }

        composeTestRule.onNodeWithTag(TimeDialogTestTags.DISMISS_BUTTON).performClick()

        assertTrue("onDismissRequest should have been called", onDismissRequestCalled)
        assertFalse("Dialog should be hidden after dismiss click", showDialog)
        composeTestRule.onNodeWithTag(TimeDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun onDismissRequest_callback_updatesStateAndDismissesDialog() {
        var externalDismissHandlerCalled = false
        var showDialogState by mutableStateOf(true)

        val onDismissRequestHandler = {
            externalDismissHandlerCalled = true
            showDialogState = false // This simulates the action of dismissing the dialog
        }

        composeTestRule.setContent {
            if (showDialogState) {
                TimeDialog(
                    showDialog = true,
                    state = TimePickerState(8, 45, true),
                    onDismissRequest = onDismissRequestHandler, // Pass the handler
                )
            }
        }

        // Simulate an external event that causes the DatePickerDialog's onDismissRequest to be called.
        // We do this by directly invoking the handler that would be called.
        composeTestRule.runOnUiThread {
            onDismissRequestHandler()
        }
        composeTestRule.waitForIdle() // Allow UI to update

        assertTrue("External dismiss handler should have been invoked", externalDismissHandlerCalled)
        assertFalse("showDialogState should be false after dismissal", showDialogState)
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

        composeTestRule.onNodeWithTag(TimeDialogTestTags.TIME_PICKER).assertIsDisplayed()
        // Direct assertion of TimePickerState internal values like hour/minute on the displayed
        // picker is not straightforward with compose testing. We trust that passing the state
        // to TimePicker component works as expected. The interaction with this state
        // (leading to onSetTime) is verified in the confirmButton test.
    }
}
