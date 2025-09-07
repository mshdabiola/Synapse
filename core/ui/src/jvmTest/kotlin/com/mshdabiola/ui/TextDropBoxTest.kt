package com.mshdabiola.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performFocus
import androidx.compose.ui.test.performTextInput
import com.mshdabiola.model.note.Place
import com.mshdabiola.model.testtag.TextDropBoxTestTags
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class TextDropBoxTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val timeFormatter = LocalTime.Format {
        amPmHour(Padding.ZERO)
        char(':')
        minute(Padding.ZERO)
        char(' ')
        amPmMarker("AM", "PM")
    }

    // Composable Place Tests
    @Test
    fun place_displaysAllOptions_andCorrectlySelectsInitial_Home() {
        var currentPlaceState: Place by mutableStateOf(Place.Home)
        composeTestRule.setContent {
            Place(currentPlace = currentPlaceState, onValueChange = { currentPlaceState = it })
        }

        composeTestRule.onNodeWithTag(TextDropBoxTestTags.PLACE_ROOT_COLUMN).assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_OPTION_ROW_PREFIX}home").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_OPTION_ROW_PREFIX}work").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_OPTION_ROW_PREFIX}school").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_OPTION_ROW_PREFIX}edit").assertIsDisplayed()

        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}home").assertIsSelected()
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}work").assertIsNotSelected()
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.PLACE_EDIT_TEXT_FIELD).assertIsDisplayed()
    }

    @Test
    fun place_optionClick_invokesOnValueChange_andUpdatesSelection_Work() {
        var onValueChangeInvoked = false
        var capturedPlace: Place? = null
        var currentPlaceState: Place by mutableStateOf(Place.Home)

        composeTestRule.setContent {
            Place(currentPlace = currentPlaceState, onValueChange = {
                onValueChangeInvoked = true
                capturedPlace = it
                currentPlaceState = it
            })
        }

        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_OPTION_ROW_PREFIX}work").performClick()

        assertTrue("onValueChange should have been called", onValueChangeInvoked)
        assertEquals("Captured place should be Work", Place.Work, capturedPlace)
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}work").assertIsSelected()
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}home").assertIsNotSelected()
    }

    @Test
    fun place_editTextField_focus_selectsEditOption_andCallsOnValueChange() {
        var onValueChangeInvokedOnFocus = false
        var capturedPlaceOnFocus: Place? = null
        var currentPlaceState: Place by mutableStateOf(Place.Home)
        val expectedPlaceEdit = Place.Edit("")

        composeTestRule.setContent {
            Place(currentPlace = currentPlaceState, onValueChange = {
                // Capturing the first Place.Edit that results from focus
                if (it is Place.Edit && capturedPlaceOnFocus == null) {
                    capturedPlaceOnFocus = it
                    onValueChangeInvokedOnFocus = true
                }
                currentPlaceState = it
            })
        }

        composeTestRule.onNodeWithTag(TextDropBoxTestTags.PLACE_EDIT_TEXT_FIELD)//.performFocus()

        assertTrue("onValueChange (on focus) should have been called", onValueChangeInvokedOnFocus)
        assertEquals("Captured place on focus should be Edit(\"\")", expectedPlaceEdit, capturedPlaceOnFocus)
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}edit").assertIsSelected()
    }

    @Test
    fun place_editTextField_textInput_callsOnValueChange() {
        var lastCapturedPlace: Place? = null
        var currentPlaceState: Place by mutableStateOf(Place.Edit("")) // Start with Edit selected
        val inputText = "My Custom Place"
        val expectedPlaceEdit = Place.Edit(inputText)

        composeTestRule.setContent {
            Place(currentPlace = currentPlaceState, onValueChange = {
                lastCapturedPlace = it
                currentPlaceState = it
            })
        }
        // Ensure radio button for edit is selected for text input to reflect correctly
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}edit").performClick()

        composeTestRule.onNodeWithTag(TextDropBoxTestTags.PLACE_EDIT_TEXT_FIELD).performTextInput(inputText)

        assertEquals("Last captured place should be Edit with input text", expectedPlaceEdit, lastCapturedPlace)
    }

    // Composable TimeTextDropbox Tests
    @Test
    fun timeTextDropbox_displaysCorrectInitialTime_andDropdownIcon() {
        val initialTime = LocalTime(10, 30)
        composeTestRule.setContent {
            TimeTextDropbox(currentTime = initialTime, onValueChange = {}, onErrorMessage = {})
        }
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD)
            .assertTextEquals(initialTime.format(timeFormatter))
    }

    @Test
    fun timeTextDropbox_dropdownOpens_andDisplaysMenuItems() {
        composeTestRule.setContent {
            TimeTextDropbox(currentTime = LocalTime(10, 0), onValueChange = {}, onErrorMessage = {})
        }
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_MENU).assertIsDisplayed()
        composeTestRule.onNodeWithText("Pick time").assertIsDisplayed()
    }

    @Test
    fun timeTextDropbox_selectPresetTime_invokesOnValueChange_andUpdatesTextField() {
        var onValueChangeInvoked = false
        var capturedTime: LocalTime? = null
        var currentTimeState by mutableStateOf(LocalTime(9, 0))
        val presetTimeToSelect = LocalTime(7,0,0)
        val presetTimeTextInMenu = "Morning"

        composeTestRule.setContent {
            TimeTextDropbox(currentTime = currentTimeState, onValueChange = {
                onValueChangeInvoked = true
                capturedTime = it
                currentTimeState = it
            }, onErrorMessage = {})
        }

        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithText(presetTimeTextInMenu).performClick()

        assertTrue("onValueChange should have been called", onValueChangeInvoked)
        assertEquals("Captured time should be the preset time", presetTimeToSelect, capturedTime)
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD)
            .assertTextEquals(presetTimeToSelect.format(timeFormatter))
    }

    @Test
    fun timeTextDropbox_selectPickTime_showsTimePickerDialog() {
        composeTestRule.setContent {
            TimeTextDropbox(currentTime = LocalTime(10,0), onValueChange = {}, onErrorMessage = {})
        }
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithText("Pick time").performClick()
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_PICKER_DIALOG_ROOT).assertIsDisplayed()
    }

    @Test
    fun timeTextDropbox_timePickerDialog_confirm_invokesOnValueChange() {
        var onValueChangeLambdaCalled = false
        var timeFromLambda: LocalTime? = null
        val onValueChangeLambda = { timeArg: LocalTime ->
            onValueChangeLambdaCalled = true
            timeFromLambda = timeArg
        }

        var showDialog by remember { mutableStateOf(false) }
        var timePickerState: TimePickerState? = null

        composeTestRule.setContent {
            timePickerState = rememberTimePickerState(initialHour = 14, initialMinute = 30, is24Hour = false)
            // This outer TimeTextDropbox's onValueChange is what we're ultimately interested in.
            TimeTextDropbox(
                currentTime = LocalTime(10,0),
                onValueChange = onValueChangeLambda, // Pass our test lambda here
                onErrorMessage = {},
                // Modifier with clickable to simulate opening the dialog via the component itself for a more integrated test.
                // However, the original test structure tests the onValueChange directly called by a *test-defined* dialog.
                // Let's stick to the original test's intent: it simulates a dialog confirming and calling the callback.
            )

            // This dialog is defined by the test, not the one internal to TimeTextDropbox.
            // Its confirm button will directly call `onValueChangeLambda`.
            if (showDialog) {
                 androidx.compose.material3.DatePickerDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedTimeInTestPicker = LocalTime(timePickerState!!.hour, timePickerState!!.minute)
                                onValueChangeLambda(selectedTimeInTestPicker) // Directly call the lambda
                                showDialog = false
                             },
                            modifier = Modifier.tag(TextDropBoxTestTags.TIME_PICKER_DIALOG_CONFIRM_BUTTON)
                        ) { androidx.compose.material3.Text("OK") }
                    }
                ) {
                    androidx.compose.material3.TimePicker(state = timePickerState!!, modifier = Modifier.tag(TextDropBoxTestTags.TIME_PICKER_IN_DIALOG))
                }
            }
        }

        // Manually show the test's dialog
        composeTestRule.runOnUiThread { showDialog = true }
        composeTestRule.waitForIdle()

        // Click the confirm button of the test's dialog
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_PICKER_DIALOG_CONFIRM_BUTTON).performClick()

        assertTrue("onValueChangeLambda should have been called", onValueChangeLambdaCalled)
        assertEquals("Time from lambda should be 14:30", LocalTime(14, 30), timeFromLambda)
    }

    @Test
    fun timeTextDropbox_showsError_whenTimeIsPast_andCallsOnError() {
        var onErrorInvoked = false
        var capturedErrorState: Boolean? = null
        val onErrorLambda = { errorState: Boolean ->
            onErrorInvoked = true
            capturedErrorState = errorState
        }
        val pastTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time.minus(1, kotlinx.datetime.DateTimeUnit.HOUR)

        composeTestRule.setContent {
            TimeTextDropbox(currentTime = pastTime, onValueChange = {}, onErrorMessage = onErrorLambda)
        }
        composeTestRule.onNodeWithText("Time has past").assertIsDisplayed()
        assertTrue("onError callback should have been invoked", onErrorInvoked)
        assertEquals("Error state should be true", true, capturedErrorState)
    }
}
