package com.mshdabiola.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.note.Notification
import com.mshdabiola.model.testtag.NotificationDialogTestTags
import com.mshdabiola.model.testtag.TextDropBoxTestTags
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Rule
import org.junit.Test

class NotificationDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val dummyNotification = Notification(
        currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
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
    fun notificationDialog_saveButton_callsOnSetAlarmAndDismisses() {
        val onSetAlarmMock = mockk<(Notification) -> Unit>(relaxed = true)
        val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            NotificationDialog(
                showDialog = true,
                initState = dummyNotification,
                onSetAlarm = onSetAlarmMock,
                onDismissRequest = onDismissRequestMock
            )
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.SAVE_BUTTON).performClick()
        verify { onSetAlarmMock(any()) } // any() because the state can change internally
        verify { onDismissRequestMock() }
    }

    @Test
    fun notificationDialog_cancelButton_callsOnDismissRequest() {
        val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)
        composeTestRule.setContent {
            NotificationDialog(
                showDialog = true,
                initState = dummyNotification,
                onDismissRequest = onDismissRequestMock
            )
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.CANCEL_BUTTON).performClick()
        verify { onDismissRequestMock() }
    }

    @Test
    fun notificationDialog_deleteButton_callsOnDeleteAlarmAndDismisses_whenIsEditIsTrue() {
        val onDeleteAlarmMock = mockk<() -> Unit>(relaxed = true)
        val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            NotificationDialog(
                showDialog = true,
                isEdit = true,
                initState = dummyNotification,
                onDeleteAlarm = onDeleteAlarmMock,
                onDismissRequest = onDismissRequestMock
            )
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DELETE_BUTTON).performClick()
        verify { onDeleteAlarmMock() }
        verify { onDismissRequestMock() }
    }

    @Test
    fun notificationDialog_contentOfTimeTab_isDisplayed() {
        composeTestRule.setContent {
            NotificationDialog(showDialog = true, initState = dummyNotification)
        }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.TIME_TAB).performClick() // Ensure selected
        // Check for a distinctive element from the Time tab, e.g., TimeTextDropbox
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
        // Check for a distinctive element from the Place tab, e.g., Place composable root
        composeTestRule.onNodeWithTag(TextDropBoxTestTags.PLACE_ROOT_COLUMN).assertIsDisplayed()
    }

    @Test
    fun notificationDialog_dismisses_whenOnDismissRequestIsCalled() {
        val onDismissMock = mockk<() -> Unit>(relaxed = true)
        val showDialogState = mutableStateOf(true)

        composeTestRule.setContent {
            if (showDialogState.value) {
                NotificationDialog(
                    showDialog = true,
                    initState = dummyNotification,
                    onDismissRequest = {
                        onDismissMock()
                        showDialogState.value = false // Simulate dismiss behavior
                    }
                )
            }
        }

        // Simulate the dialog being dismissed (e.g., by system back press or clicking outside)
        // This directly calls the lambda that would be passed to AlertDialog's onDismissRequest.
        composeTestRule.runOnUiThread {
            // In a real scenario, this would be an external trigger.
            // Here, we simulate our onDismissRequest being called.
            showDialogState.value = false
        }
        composeTestRule.waitForIdle() // Wait for recomposition

        verify { onDismissMock() }
        composeTestRule.onNodeWithTag(NotificationDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }
}
