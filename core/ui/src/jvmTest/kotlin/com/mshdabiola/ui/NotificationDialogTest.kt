package com.mshdabiola.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.note.Notification
import com.mshdabiola.model.note.RepeatSchedule
import com.mshdabiola.model.testtag.NotificationDialogTestTags
import com.mshdabiola.model.testtag.TextDropBoxTestTags
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.hours

class NotificationDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val dummyNotification = Notification(
        currentDateTime = Clock.System.now().plus(1.hours).toLocalDateTime(TimeZone.currentSystemDefault()),
        currentPlace = null,
        currentInterval = RepeatSchedule.DoNotRepeat
    )

    @Test
    fun notificationDialog_isNotDisplayed_whenShowIsFalse() {
        composeTestRule.setContent {
            NotificationDialog(showDialog = false)
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun notificationDialog_isDisplayed_whenShowIsTrue() {
        composeTestRule.setContent {
            NotificationDialog(showDialog = true, initState = dummyNotification)
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.TIME_TAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.PLACE_TAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.SAVE_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.CANCEL_BUTTON).assertIsDisplayed()
    }

    @Test
    fun notificationDialog_deleteButton_isVisible_whenIsEditIsTrue() {
        composeTestRule.setContent {
            NotificationDialog(showDialog = true, isEdit = true, initState = dummyNotification)
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DELETE_BUTTON).assertIsDisplayed()
    }

    @Test
    fun notificationDialog_deleteButton_isNotVisible_whenIsEditIsFalse() {
        composeTestRule.setContent {
            NotificationDialog(showDialog = true, isEdit = false, initState = dummyNotification)
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DELETE_BUTTON).assertDoesNotExist()
    }

    @Test
    fun notificationDialog_timeTab_isSelectedByDefault() {
        composeTestRule.setContent {
            NotificationDialog(showDialog = true, initState = dummyNotification)
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.TIME_TAB).assertIsSelected()
    }

    @Test
    fun notificationDialog_placeTab_canBeSelected() {
        composeTestRule.setContent {
            NotificationDialog(showDialog = true, initState = dummyNotification)
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.PLACE_TAB).performClick()
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.PLACE_TAB).assertIsSelected()
    }

    @Test
    fun notificationDialog_saveButton_invokesOnSetAlarmAndOnDismissRequest_andDismissesDialog() {
        var onSetAlarmCalled = false
        var capturedNotification: Notification? = null
        var onDismissRequestCalled = false
        var showDialog by mutableStateOf(true)

        composeTestRule.setContent {
            if (showDialog) {
                NotificationDialog(
                    showDialog = true,
                    initState = dummyNotification,
                    onSetAlarm = { notification ->
                        println("set alarm called with $notification")
                        onSetAlarmCalled = true
                        capturedNotification = notification
                    },
                    onDismissRequest = {
                        onDismissRequestCalled = true
                        showDialog = false
                    }
                )
            }
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.SAVE_BUTTON).performClick()

        assertTrue("onSetAlarm should have been called", onSetAlarmCalled)
        assertNotNull("Captured notification should not be null", capturedNotification)
        // We can check if the passed notification is similar to dummyNotification,
        // though internal state might have changed it slightly.
        assertEquals(dummyNotification.currentDateTime.date, capturedNotification?.currentDateTime?.date)

        assertTrue("onDismissRequest should have been called", onDismissRequestCalled)
        assertFalse("Dialog should be hidden after save", showDialog)
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun notificationDialog_cancelButton_invokesOnDismissRequest_andDismissesDialog() {
        var onDismissRequestCalled = false
        var showDialog by mutableStateOf(true)

        composeTestRule.setContent {
            if (showDialog) {
                NotificationDialog(
                    showDialog = true,
                    initState = dummyNotification,
                    onDismissRequest = {
                        onDismissRequestCalled = true
                        showDialog = false
                    }
                )
            }
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.CANCEL_BUTTON).performClick()

        assertTrue("onDismissRequest should have been called", onDismissRequestCalled)
        assertFalse("Dialog should be hidden after cancel", showDialog)
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun notificationDialog_deleteButton_invokesOnDeleteAlarmAndOnDismissRequest_andDismissesDialog() {
        var onDeleteAlarmCalled = false
        var onDismissRequestCalled = false
        var showDialog by mutableStateOf(true)

        composeTestRule.setContent {
            if (showDialog) {
                NotificationDialog(
                    showDialog = true,
                    isEdit = true,
                    initState = dummyNotification,
                    onDeleteAlarm = { onDeleteAlarmCalled = true },
                    onDismissRequest = {
                        onDismissRequestCalled = true
                        showDialog = false
                    }
                )
            }
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DELETE_BUTTON).performClick()

        assertTrue("onDeleteAlarm should have been called", onDeleteAlarmCalled)
        assertTrue("onDismissRequest should have been called", onDismissRequestCalled)
        assertFalse("Dialog should be hidden after delete", showDialog)
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun notificationDialog_contentOfTimeTab_isDisplayed() {
        composeTestRule.setContent {
            NotificationDialog(showDialog = true, initState = dummyNotification)
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.TIME_TAB).performClick() // Ensure selected
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.TIME_DROPBOX_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.DATE_DROPBOX_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.INTERVAL_DROPBOX_ROOT).assertIsDisplayed()
    }

    @Test
    fun notificationDialog_contentOfPlaceTab_isDisplayed() {
        composeTestRule.setContent {
            NotificationDialog(showDialog = true, initState = dummyNotification)
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.PLACE_TAB).performClick()
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.PLACE_ROOT_COLUMN).assertIsDisplayed()
    }

    @Test
    fun onDismissRequest_callback_updatesStateAndDismissesDialog() {
        var onDismissHandlerInvoked = false
        var showDialogState by mutableStateOf(true)

        val dismissHandler = {
            onDismissHandlerInvoked = true
            showDialogState = false
        }

        composeTestRule.setContent {
            if (showDialogState) {
                NotificationDialog(
                    showDialog = true,
                    initState = dummyNotification,
                    onDismissRequest = dismissHandler
                )
            }
        }

        // Simulate the AlertDialog triggering its onDismissRequest callback
        composeTestRule.runOnUiThread {
            dismissHandler() // Directly invoke the handler
        }
        composeTestRule.waitForIdle() // Allow UI to update

        assertTrue("onDismissRequest handler lambda should have been invoked", onDismissHandlerInvoked)
        assertFalse("showDialogState should be false after dismiss handler execution", showDialogState)
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }
}
