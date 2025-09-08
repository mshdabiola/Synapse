package com.mshdabiola.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.testtag.NotificationOptionsTestTags
import org.junit.Rule
import org.junit.Test

class NotificationOptionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun notificationOptions_whenShown_displaysAllExpectedItems() {
        composeTestRule.setContent {
            NotificationOptions(
                show = true,
                currentColor = -1,
                currentImage = -1,
                onDismissRequest = {}
            )
        }

        composeTestRule.onNodeWithTag(NotificationOptionsTestTags.LATER_TODAY_TOMORROW_MORNING).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationOptionsTestTags.TOMORROW_MORNING_TOMORROW_EVENING).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationOptionsTestTags.NEXT_WEEK_MORNING).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NotificationOptionsTestTags.PICK_DATE_TIME).assertIsDisplayed()
    }

    @Test
    fun notificationOptions_itemClicks_invokeCorrectCallbacksAndDismiss() {
        var alarmCalled = false
        var showDialogCalled = false
        var dismissed = false

        composeTestRule.setContent {
            NotificationOptions(
                show = true,
                currentColor = -1,
                currentImage = -1,
                onAlarm = { _, _ -> alarmCalled = true },
                showDialog = { showDialogCalled = true },
                onDismissRequest = { dismissed = true }
            )
        }

        composeTestRule.onNodeWithTag(NotificationOptionsTestTags.LATER_TODAY_TOMORROW_MORNING).performClick()
        assert(alarmCalled)
        assert(dismissed)
        alarmCalled = false // Reset for next check
        dismissed = false

        composeTestRule.onNodeWithTag(NotificationOptionsTestTags.TOMORROW_MORNING_TOMORROW_EVENING).performClick()
        assert(alarmCalled)
        assert(dismissed)
        alarmCalled = false
        dismissed = false

        composeTestRule.onNodeWithTag(NotificationOptionsTestTags.NEXT_WEEK_MORNING).performClick()
        assert(alarmCalled)
        assert(dismissed)
        dismissed = false

        composeTestRule.onNodeWithTag(NotificationOptionsTestTags.PICK_DATE_TIME).performClick()
        assert(showDialogCalled)
        assert(dismissed)
    }

    @Test
    fun notificationOptions_whenNotShown_doesNotExist() {
        composeTestRule.setContent {
            NotificationOptions(
                show = false, // Set to false
                currentColor = -1,
                currentImage = -1,
                onDismissRequest = {}
            )
        }

        composeTestRule.onNodeWithTag(NotificationOptionsTestTags.LATER_TODAY_TOMORROW_MORNING).assertDoesNotExist()
    }
}
