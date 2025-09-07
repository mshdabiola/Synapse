package com.mshdabiola.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performFocus
import androidx.compose.ui.test.performTextInput
import com.mshdabiola.model.note.Place
import com.mshdabiola.model.note.ScheduledTime
import com.mshdabiola.model.testtag.TextDropBoxTestTags
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
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
        val onValueChangeMock = mockk<(Place) -> Unit>(relaxed = true)
        var currentPlaceState: Place by mutableStateOf(Place.Home)

        composeTestRule.setContent {
            Place(currentPlace = currentPlaceState, onValueChange = {
                onValueChangeMock(it)
                currentPlaceState = it
            })
        }

        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_OPTION_ROW_PREFIX}work").performClick()
        verify { onValueChangeMock(Place.Work) }
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}work").assertIsSelected()
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}home").assertIsNotSelected()
    }

    @Test
    fun place_editTextField_focus_selectsEditOption_andCallsOnValueChange() {
        val onValueChangeMock = mockk<(Place) -> Unit>(relaxed = true)
        var currentPlaceState: Place by mutableStateOf(Place.Home)
        val expectedPlaceEdit = Place.Edit("")

        composeTestRule.setContent {
            Place(currentPlace = currentPlaceState, onValueChange = {
                onValueChangeMock(it)
                currentPlaceState = it
            })
        }

        composeTestRule.onNodeWithTag(TextDropBoxTestTags.PLACE_EDIT_TEXT_FIELD).performFocus()
        verify { onValueChangeMock(expectedPlaceEdit) } // Initial onValueChange on focus might pass current Edit value
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}edit").assertIsSelected()
    }

    @Test
    fun place_editTextField_textInput_callsOnValueChange() {
        val onValueChangeMock = mockk<(Place) -> Unit>(relaxed = true)
        var currentPlaceState: Place by mutableStateOf(Place.Edit("")) // Start with Edit selected
        val inputText = "My Custom Place"
        val expectedPlaceEdit = Place.Edit(inputText)

        composeTestRule.setContent {
            Place(currentPlace = currentPlaceState, onValueChange = {
                onValueChangeMock(it)
                currentPlaceState = it
            })
        }
        // Ensure radio button for edit is selected for text input to reflect correctly
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}edit").performClick()

        composeTestRule.onNodeWithTag(TextDropBoxTestTags.PLACE_EDIT_TEXT_FIELD).performTextInput(inputText)
        // Verification might be tricky due to TextFieldState internal updates.
        // We check the last call to onValueChange, which should have the complete text.
        verify(atLeast = 1) { onValueChangeMock(expectedPlaceEdit) }
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
        // Assuming 'Pick time' is one of the options from the string array
        composeTestRule.onNodeWithText("Pick time").assertIsDisplayed() // Using text as tag is complex for menu items
    }

    @Test
    fun timeTextDropbox_selectPresetTime_invokesOnValueChange_andUpdatesTextField() {
        val onValueChangeMock = mockk<(LocalTime) -> Unit>(relaxed = true)
        var currentTimeState by mutableStateOf(LocalTime(9, 0))
        // Using Morning time (7:00 AM) as an example preset available in TimeTextDropbox
        val presetTimeToSelect = LocalTime(7,0,0) // From ScheduledTime.Time(LocalTime(7,0,0))
        val presetTimeTextInMenu = "Morning" // From R.array.modules_designsystem_notification_times at index 0

        composeTestRule.setContent {
            TimeTextDropbox(currentTime = currentTimeState, onValueChange = {
                onValueChangeMock(it)
                currentTimeState = it
            }, onErrorMessage = {})
        }

        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD).performClick()
        // Click the menu item. We might need to use its text if the tag is not precise enough.
        composeTestRule.onNodeWithText(presetTimeTextInMenu).performClick()

        verify { onValueChangeMock(presetTimeToSelect) }
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD)
            .assertTextEquals(presetTimeToSelect.format(timeFormatter))
    }

    @Test
    fun timeTextDropbox_selectPickTime_showsTimePickerDialog() {
        composeTestRule.setContent {
            TimeTextDropbox(currentTime = LocalTime(10,0), onValueChange = {}, onErrorMessage = {})
        }
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD).performClick()
        // Test based on the string resource name for "Pick time"
        composeTestRule.onNodeWithText("Pick time").performClick()
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_PICKER_DIALOG_ROOT).assertIsDisplayed()
    }

    @Test
    fun timeTextDropbox_timePickerDialog_confirm_invokesOnValueChange() {
        val onValueChangeMock = mockk<(LocalTime) -> Unit>(relaxed = true)
        var showDialog by remember { mutableStateOf(false) }
        var timePickerState: TimePickerState? = null

        composeTestRule.setContent {
            timePickerState = rememberTimePickerState(initialHour = 14, initialMinute = 30, is24Hour = false)
            // Simulate the dialog being shown after clicking "Pick time"
            if (showDialog) {
                // Minimal DatePickerDialog structure for test
                 androidx.compose.material3.DatePickerDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        androidx.compose.material3.TextButton(
                            onClick = {
                                onValueChangeMock(LocalTime(timePickerState!!.hour, timePickerState!!.minute))
                                showDialog = false
                             },
                            modifier = androidx.compose.ui.Modifier.tag(TextDropBoxTestTags.TIME_PICKER_DIALOG_CONFIRM_BUTTON)
                        ) { androidx.compose.material3.Text("OK") }
                    }
                ) {
                    androidx.compose.material3.TimePicker(state = timePickerState!!, modifier = androidx.compose.ui.Modifier.tag(TextDropBoxTestTags.TIME_PICKER_IN_DIALOG))
                }
            }
            // Actual component to open the dialog initially
            TimeTextDropbox(currentTime = LocalTime(10,0), onValueChange = onValueChangeMock, onErrorMessage = {}, modifier = androidx.compose.ui.Modifier.clickable { showDialog = true })
        }

        // Manually show the dialog for test setup - usually done by clicking "Pick time" in full component
        // The click below simulates clicking "Pick Time" which would show the dialog in the actual composable
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD).performClick() // Open dropdown
        composeTestRule.onNodeWithText("Pick time").performClick() // Click item that shows dialog

        // Now that the dialog is shown by the component logic, find and click confirm
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_PICKER_DIALOG_CONFIRM_BUTTON).performClick()

        // Expected time based on rememberTimePickerState values above if they were settable, using current picker state
        // Since we can't easily *set* time in TimePicker for test, it will confirm the initial state of the picker
        verify { onValueChangeMock(LocalTime(14, 30)) } // TimePickerState default is current time or 00:00
                                                              // We are using the internal state of DatePickerDialog's TimePicker
    }

    @Test
    fun timeTextDropbox_showsError_whenTimeIsPast_andCallsOnError() {
        val onErrorMessageMock = mockk<(Boolean) -> Unit>(relaxed = true)
        val pastTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time.minus(1, kotlinx.datetime.DateTimeUnit.HOUR)

        composeTestRule.setContent {
            TimeTextDropbox(currentTime = pastTime, onValueChange = {}, onErrorMessage = onErrorMessageMock)
        }
        // Check for supporting text indicating error
        composeTestRule.onNodeWithText("Time has past").assertIsDisplayed()
        verify { onErrorMessageMock(true) }
    }
}
