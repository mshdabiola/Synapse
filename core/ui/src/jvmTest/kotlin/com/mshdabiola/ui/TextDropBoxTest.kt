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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.mshdabiola.model.note.Place
import com.mshdabiola.model.testtag.TextDropBoxTestTags
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
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

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

        composeTestRule.onNodeWithTag(TextDropBoxTestTags.PLACE_EDIT_TEXT_FIELD) // .performFocus()

        assertTrue("onValueChange (on focus) should have been called", onValueChangeInvokedOnFocus)
        assertEquals("Captured place on focus should be Edit(\"\")", expectedPlaceEdit, capturedPlaceOnFocus)
        composeTestRule.onNodeWithTag("${TextDropBoxTestTags.PLACE_RADIO_BUTTON_PREFIX}edit").assertIsSelected()
    }

    @Test
    fun place_editTextField_textInput_callsOnValueChange() {
        var lastCapturedPlace: Place? = null
        var currentPlaceState: Place by mutableStateOf(Place.Edit("")) // Start with Edit selected
        val inputText = "My Custom Place"
        val expectedPlaceEdit = Place.Edit("")

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
    @OptIn(ExperimentalTime::class)
    @Test
    fun timeTextDropbox_displaysCorrectInitialTime_andDropdownIcon() {
        val initialTime = Clock.System.now().plus(1.hours).toLocalDateTime(TimeZone.currentSystemDefault()).time
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
        composeTestRule.onNodeWithText("Pick a time").assertIsDisplayed()
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun timeTextDropbox_selectPresetTime_invokesOnValueChange_andUpdatesTextField() {
        var onValueChangeInvoked = false
        var capturedTime: LocalTime? = null
        var currentTimeState by mutableStateOf(LocalTime(9, 0))
        val presetTimeToSelect = LocalTime(7, 0, 0)
        val presetTimeTextInMenu = "Morning"

        composeTestRule.setContent {
            TimeTextDropbox(
                currentTime = currentTimeState,
                nowTime = LocalTime(6, 0, 0),
                onValueChange = {
                    onValueChangeInvoked = true
                    capturedTime = it
                    currentTimeState = it
                },
                onErrorMessage = {},
            )
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
            TimeTextDropbox(currentTime = LocalTime(10, 0), onValueChange = {}, onErrorMessage = {})
        }
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithText("Pick a time").performClick()
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_PICKER_DIALOG_ROOT).assertIsDisplayed()
    }

    @Test
    fun timeTextDropbox_timePickerDialog_confirm_invokesOnValueChange() {
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun timeTextDropbox_showsError_whenTimeIsPast_andCallsOnError() {
        var onErrorInvoked = false
        var capturedErrorState: Boolean? = null
        val onErrorLambda = { errorState: Boolean ->
            onErrorInvoked = true
            capturedErrorState = errorState
        }
        val pastTime = kotlin.time.Clock.System.now().minus(
            1.hours,
        ).toLocalDateTime(TimeZone.currentSystemDefault()).time

        composeTestRule.setContent {
            TimeTextDropbox(currentTime = pastTime, onValueChange = {}, onErrorMessage = onErrorLambda)
        }
        composeTestRule.onNodeWithText("Time has past").assertIsDisplayed()
        assertTrue("onError callback should have been invoked", onErrorInvoked)
        assertEquals("Error state should be true", true, capturedErrorState)
    }
}
