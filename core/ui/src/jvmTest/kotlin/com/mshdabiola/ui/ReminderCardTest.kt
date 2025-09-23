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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.note.IntervalEnd
import com.mshdabiola.model.note.Notification
import com.mshdabiola.model.note.Place
import com.mshdabiola.model.note.RepeatSchedule
import com.mshdabiola.model.testtag.ReminderCardTestTags
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ReminderCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testColor = Color.White
    private val nowLdt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    // Helper to create a Notification object for date/time based reminders
    private fun createDateTimeNotification(
        dateTime: LocalDateTime,
        interval: RepeatSchedule = RepeatSchedule.DoNotRepeat,
    ): Notification {
        return Notification(
            currentDateTime = dateTime,
            currentInterval = interval,
            currentPlace = null,
        )
    }

    // Helper to create a Notification object for place based reminders
    private fun createPlaceNotification(
        place: Place = Place.Home,
        interval: RepeatSchedule = RepeatSchedule.DoNotRepeat,
    ): Notification {
        return Notification(
            currentDateTime = nowLdt, // DateTime is always present, but place should take precedence in UI
            currentInterval = interval,
            currentPlace = place,
        )
    }

    @Test
    fun reminderCard_nonRepeating_dateTime_displaysCorrectly_today() {
        val notification = createDateTimeNotification(nowLdt)
        val expectedText = nowLdt.myFormat()

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor,contentColor = Color.Black)
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_ICON_ROW,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_ALARM_ICON,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_REPEAT_ICON,
            useUnmergedTree = true,
        ).assertDoesNotExist()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT,
            useUnmergedTree = true,
        ).assertTextEquals(expectedText)
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT).assertDoesNotExist()
    }

    @Test
    fun reminderCard_nonRepeating_dateTime_displaysCorrectly_tomorrow() {
        val tomorrow = nowLdt.date.plus(1, DateTimeUnit.DAY)
        val time = LocalDateTime(tomorrow, nowLdt.time)
        val notification = createDateTimeNotification(time)
        val expectedText = time.myFormat()

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor, contentColor = Color.Black)
        }
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT,
            useUnmergedTree = true,
        ).assertTextEquals(expectedText)
    }

    @Test
    fun reminderCard_nonRepeating_dateTime_displaysCorrectly_yesterday() {
        val yesterday = nowLdt.date.minus(1, DateTimeUnit.DAY)
        val time = LocalDateTime(yesterday, nowLdt.time)
        val notification = createDateTimeNotification(time)
        val expectedText = time.myFormat()

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor, contentColor = Color.Black)
        }
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT,
            useUnmergedTree = true,
        ).assertTextEquals(expectedText)
    }

    @Test
    fun reminderCard_nonRepeating_dateTime_displaysCorrectly_otherDate() {
        val otherDate = nowLdt.date.plus(5, DateTimeUnit.DAY)
        val time = LocalDateTime(otherDate, nowLdt.time)

        val notification = createDateTimeNotification(time)
        val expectedText = time.myFormat() // Adjusted to use dateFormatter

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor, contentColor = Color.Black)
        }
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT,
            useUnmergedTree = true,
        ).assertTextEquals(expectedText)
    }

    @Test
    fun reminderCard_repeating_dateTime_displaysCorrectly() {
        val notification = createDateTimeNotification(
            nowLdt,
            RepeatSchedule.Daily(intervalEnd = IntervalEnd.Forever),
        )
        val expectedText = nowLdt.myFormat()

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor, contentColor = Color.Black)
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_ICON_ROW,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_REPEAT_ICON,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_ALARM_ICON,
            useUnmergedTree = true,
        ).assertDoesNotExist()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT,
            useUnmergedTree = true,
        ).assertTextEquals(expectedText)
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT,
            useUnmergedTree = true,
        ).assertDoesNotExist()
    }

    @Test
    fun reminderCard_place_displaysCorrectly_withAlarmIcon() {
        val notification = createPlaceNotification(interval = RepeatSchedule.DoNotRepeat)

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor, contentColor = Color.Black)
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_ICON_ROW,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_ALARM_ICON,
            useUnmergedTree = true,
        ).assertIsDisplayed() // Because DoNotRepeat
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_REPEAT_ICON,
            useUnmergedTree = true,
        ).assertDoesNotExist()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT,
            useUnmergedTree = true,
        ).assertTextEquals("Home")
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT,
            useUnmergedTree = true,
        ).assertDoesNotExist()
    }

    @Test
    fun reminderCard_place_displaysCorrectly_withRepeatIcon() {
        val notification = createPlaceNotification(interval = RepeatSchedule.Weekly(intervalEnd = IntervalEnd.Forever))

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor, contentColor = Color.Black)
        }
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_REPEAT_ICON,
            useUnmergedTree = true,
        ).assertIsDisplayed() // Because Weekly
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_ALARM_ICON,
            useUnmergedTree = true,
        ).assertDoesNotExist()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT,
            useUnmergedTree = true,
        ).assertTextEquals("Home")
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT,
            useUnmergedTree = true,
        ).assertDoesNotExist()
    }

    @Test
    fun reminderCard_onClick_invoked() {
        var onClickCalled = false
        val notification = createDateTimeNotification(nowLdt)

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor, contentColor = Color.Black, onClick = { onClickCalled = true })
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ROOT).performClick()
        assertTrue("onClick callback should have been invoked", onClickCalled)
    }

    // Tests for LabelCard
    @Test
    fun labelCard_displaysNameCorrectly() {
        val labelName = "My Label"
        composeTestRule.setContent {
            LabelCard(name = labelName, color = testColor, contentColor = Color.Black)
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.LABEL_CARD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.LABEL_CARD_NAME_TEXT,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(
            ReminderCardTestTags.LABEL_CARD_NAME_TEXT,
            useUnmergedTree = true,
        ).assertTextEquals(labelName)
    }

    @Test
    fun labelCard_onClick_invoked() {
        var onClickCalled = false
        val labelName = "Clickable Label"

        composeTestRule.setContent {
            LabelCard(name = labelName, color = testColor, contentColor = Color.Black, onClick = { onClickCalled = true })
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.LABEL_CARD_ROOT).performClick()
        assertTrue("onClick callback for LabelCard should have been invoked", onClickCalled)
    }
}
