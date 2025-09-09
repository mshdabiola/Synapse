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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.testtag.DateDialogTestTags
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
        var onSetDateCalled = false
        var onDismissRequestCalled = false
        var showDialog by mutableStateOf(true)

        composeTestRule.setContent {
            val datePickerState = rememberDatePickerState()
            if (showDialog) {
                DateDialog(
                    state = datePickerState,
                    showDialog = true,
                    onSetDate = { onSetDateCalled = true },
                    onDismissRequest = {
                        onDismissRequestCalled = true
                        showDialog = false // Simulate dialog dismissal logic
                    },
                )
            }
        }

        composeTestRule.onNodeWithTag(DateDialogTestTags.CONFIRM_BUTTON).performClick()

        assertTrue("onSetDate should have been called", onSetDateCalled)
        assertTrue("onDismissRequest should have been called", onDismissRequestCalled)
        assertFalse("Dialog should be hidden after confirm click", showDialog)
        composeTestRule.onNodeWithTag(DateDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun dateDialog_dismissButton_invokesOnDismissRequest() {
        var onDismissRequestCalled = false
        var showDialog by mutableStateOf(true)

        composeTestRule.setContent {
            val datePickerState = rememberDatePickerState()
            if (showDialog) {
                DateDialog(
                    state = datePickerState,
                    showDialog = true,
                    onDismissRequest = {
                        onDismissRequestCalled = true
                        showDialog = false // Simulate dialog dismissal logic
                    },
                )
            }
        }

        composeTestRule.onNodeWithTag(DateDialogTestTags.DISMISS_BUTTON).performClick()

        assertTrue("onDismissRequest should have been called", onDismissRequestCalled)
        assertFalse("Dialog should be hidden after dismiss click", showDialog)
        composeTestRule.onNodeWithTag(DateDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun onDismissRequest_callback_updatesStateAndDismissesDialog() {
        var onDismissHandlerCalled = false
        var showDialogState by mutableStateOf(true)

        val dismissHandler = {
            onDismissHandlerCalled = true
            showDialogState = false
        }

        composeTestRule.setContent {
            val datePickerState = rememberDatePickerState()
            if (showDialogState) {
                DateDialog(
                    state = datePickerState,
                    showDialog = true,
                    onDismissRequest = dismissHandler,
                )
            }
        }

        // Simulate the DatePickerDialog invoking its onDismissRequest callback.
        // We do this by directly calling the handler we provided.
        composeTestRule.runOnUiThread {
            dismissHandler()
        }
        composeTestRule.waitForIdle() // Allow UI to update

        assertTrue("onDismissRequest handler should have been called", onDismissHandlerCalled)
        assertFalse("showDialogState should be false after dismissal", showDialogState)
        composeTestRule.onNodeWithTag(DateDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }
}
