package com.mshdabiola.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.note.IntervalEnd
import com.mshdabiola.model.note.RepeatSchedule
import com.mshdabiola.model.testtag.NotificationDialogIntervalTestTags
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class NotificationDialogIntervalTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val today: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val sampleIntervals = listOf(
        RepeatSchedule.DoNotRepeat,
        RepeatSchedule.Daily(intervalEnd = IntervalEnd.Forever),
        RepeatSchedule.Weekly(intervalEnd = IntervalEnd.Forever, days = setOf(today.dayOfWeek.ordinal)),
        RepeatSchedule.Monthly(intervalEnd = IntervalEnd.Forever, sameDay = true),
        RepeatSchedule.Yearly(intervalEnd = IntervalEnd.Forever),
        RepeatSchedule.Custom
    )
    private val intervalNames = listOf("Do not repeat", "Daily", "Weekly", "Monthly", "Yearly", "Custom")

    @Test
    fun dialog_isDisplayed_withDefaultInterval_andActionButtons() {
        val initInterval = RepeatSchedule.DoNotRepeat
        composeTestRule.setContent {
            NotificationDialogInterval(
                initInterval = initInterval,
                todayDate = today,
                intervals = sampleIntervals,
                onValueChange = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.INTERVAL_TYPE_DROPDOWN_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.INTERVAL_TYPE_TEXT_FIELD)
            .assertTextContains(intervalNames[initInterval.index], substring = true) // "Do not repeat"

        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.ACTIONS_ROW).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.CLOSE_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.SET_REPEAT_BUTTON).assertIsDisplayed()
    }

    @Test
    fun intervalTypeDropdown_opensAndAllowsSelection_Daily() {
        val initInterval = RepeatSchedule.DoNotRepeat
        var selectedInterval: RepeatSchedule? = null

        composeTestRule.setContent {
            NotificationDialogInterval(
                initInterval = initInterval,
                todayDate = today,
                intervals = sampleIntervals,
                onValueChange = { selectedInterval = it },
                onDismiss = {}
            )
        }

        // Open the dropdown
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.INTERVAL_TYPE_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.INTERVAL_TYPE_MENU).assertIsDisplayed()

        // Select "Daily" - constructing tag based on common pattern
        val dailyMenuItemTag = "${NotificationDialogIntervalTestTags.INTERVAL_TYPE_MENU_ITEM_PREFIX}_${intervalNames[1].lowercase().replace(" ", "_")}" // daily
        composeTestRule.onNodeWithTag(dailyMenuItemTag).performClick()

        // Verify dropdown text updated
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.INTERVAL_TYPE_TEXT_FIELD)
            .assertTextContains(intervalNames[1], substring = true) // "Daily"

        // Verify Daily specific UI is shown
        composeTestRule.onNodeWithTag("${NotificationDialogIntervalTestTags.INTERVAL_TF_ROOT_PREFIX}_daily").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${NotificationDialogIntervalTestTags.REPEAT_END_ROOT_ROW_PREFIX}_daily").assertIsDisplayed()
    }

    @Test
    fun dailyInterval_options_areDisplayed_whenSelected() {
        val initInterval = RepeatSchedule.Daily(interval = "1", intervalEnd = IntervalEnd.Forever)
        composeTestRule.setContent {
            NotificationDialogInterval(
                initInterval = initInterval,
                todayDate = today,
                intervals = sampleIntervals, // Ensure Daily is in this list and at index 1 for consistency
                onValueChange = {},
                onDismiss = {}
            )
        }
        // TextField for "Every X day(s)"
        composeTestRule.onNodeWithTag("${NotificationDialogIntervalTestTags.INTERVAL_TF_ROOT_PREFIX}_daily").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${NotificationDialogIntervalTestTags.INTERVAL_TF_TEXT_FIELD_PREFIX}_daily").assertIsDisplayed()
        // "Repeat end" section
        composeTestRule.onNodeWithTag("${NotificationDialogIntervalTestTags.REPEAT_END_ROOT_ROW_PREFIX}_daily").assertIsDisplayed()
         // Check IntervalRepeatEnd specific dropdown is there
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.REPEAT_END_TYPE_DROPDOWN_ROOT, useUnmergedTree = true).assertIsDisplayed()
    }


    @Test
    fun closeButton_invokesOnDismiss_andDismissesDialog() {
        var showDialog by mutableStateOf(true)
        var onDismissInvoked = false

        composeTestRule.setContent {
            if (showDialog) {
                NotificationDialogInterval(
                    initInterval = RepeatSchedule.DoNotRepeat,
                    todayDate = today,
                    intervals = sampleIntervals,
                    onValueChange = {},
                    onDismiss = {
                        onDismissInvoked = true
                        showDialog = false
                    }
                )
            }
        }
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.CLOSE_BUTTON).performClick()

        assertTrue("onDismiss callback should have been invoked", onDismissInvoked)
        assertFalse("showDialog state should be false after close button click", showDialog)
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun setRepeatButton_invokesOnValueChangeAndOnDismiss_andDismissesDialog() {
        var showDialog by mutableStateOf(true)
        var receivedInterval: RepeatSchedule? = null
        var onValueChangeInvoked = false
        var onDismissInvoked = false

        val initialInterval = RepeatSchedule.DoNotRepeat
        val expectedIntervalAfterSelection = sampleIntervals[1] // Daily

        composeTestRule.setContent {
            if (showDialog) {
                NotificationDialogInterval(
                    initInterval = initialInterval,
                    todayDate = today,
                    intervals = sampleIntervals,
                    onValueChange = { interval ->
                        receivedInterval = interval
                        onValueChangeInvoked = true
                    },
                    onDismiss = {
                        onDismissInvoked = true
                        showDialog = false
                    }
                )
            }
        }

        // Change to Daily
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.INTERVAL_TYPE_TEXT_FIELD).performClick()
        val dailyMenuItemTag = "${NotificationDialogIntervalTestTags.INTERVAL_TYPE_MENU_ITEM_PREFIX}_${intervalNames[1].lowercase().replace(" ", "_")}"
        composeTestRule.onNodeWithTag(dailyMenuItemTag).performClick()

        // Click Set Repeat
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.SET_REPEAT_BUTTON).performClick()

        assertTrue("onValueChange callback should have been invoked", onValueChangeInvoked)
        assertEquals("The correct interval should be passed to onValueChange", expectedIntervalAfterSelection, receivedInterval)
        assertTrue("onDismiss callback should have been invoked after Set Repeat", onDismissInvoked)
        assertFalse("showDialog state should be false after Set Repeat button click", showDialog)
        composeTestRule.onNodeWithTag(NotificationDialogIntervalTestTags.DIALOG_ROOT).assertDoesNotExist()
    }
}
