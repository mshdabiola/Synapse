package com.mshdabiola.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.note.Notification
import com.mshdabiola.model.note.Place
import com.mshdabiola.model.note.RepeatSchedule
import com.mshdabiola.model.testtag.ReminderCardTestTags
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.junit.Rule
import org.junit.Test

class ReminderCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testColor = Color.White
    private val nowLdt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    // Helper to create a Notification object for date/time based reminders
    private fun createDateTimeNotification(dateTime: LocalDateTime, interval: RepeatSchedule = RepeatSchedule.DoNotRepeat): Notification {
        return Notification(
            currentDateTime = dateTime,
            currentInterval = interval,
            currentPlace = null
        )
    }

    // Helper to create a Notification object for place based reminders
    private fun createPlaceNotification(place: Place = Place.Home, interval: RepeatSchedule = RepeatSchedule.DoNotRepeat): Notification {
        return Notification(
            currentDateTime = nowLdt, // DateTime is always present, but place should take precedence in UI
            currentInterval = interval,
            currentPlace = place
        )
    }

    @Test
    fun reminderCard_nonRepeating_dateTime_displaysCorrectly_today() {
        val notification = createDateTimeNotification(nowLdt)
        val expectedText = "Today, ${nowLdt.time.format(timeFormater)}"

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor)
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ICON_ROW).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ALARM_ICON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_REPEAT_ICON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT).assertTextEquals(expectedText)
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT).assertDoesNotExist()
    }

    @Test
    fun reminderCard_nonRepeating_dateTime_displaysCorrectly_tomorrow() {
        val tomorrow = nowLdt.date.plus(1, DateTimeUnit.DAY)
        val notification = createDateTimeNotification(LocalDateTime(tomorrow, nowLdt.time))
        val expectedText = "Tomorrow, ${nowLdt.time.format(timeFormater)}"

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor)
        }
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT).assertTextEquals(expectedText)
    }

    @Test
    fun reminderCard_nonRepeating_dateTime_displaysCorrectly_yesterday() {
        val yesterday = nowLdt.date.minus(1, DateTimeUnit.DAY)
        val notification = createDateTimeNotification(LocalDateTime(yesterday, nowLdt.time))
        val expectedText = "Yesterday, ${nowLdt.time.format(timeFormater)}"

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor)
        }
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT).assertTextEquals(expectedText)
    }

    @Test
    fun reminderCard_nonRepeating_dateTime_displaysCorrectly_otherDate() {
        val otherDate = nowLdt.date.plus(5, DateTimeUnit.DAY)
        val notification = createDateTimeNotification(LocalDateTime(otherDate, nowLdt.time))
        val expectedText = "${otherDate}, ${nowLdt.time.format(timeFormater)}"

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor)
        }
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT).assertTextEquals(expectedText)
    }


    @Test
    fun reminderCard_repeating_dateTime_displaysCorrectly() {
        val notification = createDateTimeNotification(nowLdt, RepeatSchedule.Daily())
        val expectedText = "Today, ${nowLdt.time.format(timeFormater)}" // Format depends on myFormat()

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor)
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ICON_ROW).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_REPEAT_ICON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ALARM_ICON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT).assertTextEquals(expectedText)
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT).assertDoesNotExist()
    }

    @Test
    fun reminderCard_place_displaysCorrectly_withAlarmIcon() {
        val notification = createPlaceNotification(interval = RepeatSchedule.DoNotRepeat)

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor)
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ICON_ROW).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ALARM_ICON).assertIsDisplayed() // Because DoNotRepeat
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_REPEAT_ICON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT).assertTextEquals("Place")
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT).assertDoesNotExist()
    }

    @Test
    fun reminderCard_place_displaysCorrectly_withRepeatIcon() {
        val notification = createPlaceNotification(interval = RepeatSchedule.Weekly())

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor)
        }
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_REPEAT_ICON).assertIsDisplayed() // Because Weekly
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ALARM_ICON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_PLACE_TEXT).assertTextEquals("Place")
        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_DATETIME_TEXT).assertDoesNotExist()
    }

    @Test
    fun reminderCard_onClick_invoked() {
        val onClickMock = mockk<() -> Unit>(relaxed = true)
        val notification = createDateTimeNotification(nowLdt)

        composeTestRule.setContent {
            ReminderCard(notification = notification, color = testColor, onClick = onClickMock)
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.REMINDER_CARD_ROOT).performClick()
        verify { onClickMock() }
    }

    // Tests for LabelCard
    @Test
    fun labelCard_displaysNameCorrectly() {
        val labelName = "My Label"
        composeTestRule.setContent {
            LabelCard(name = labelName, color = testColor)
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.LABEL_CARD_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.LABEL_CARD_NAME_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReminderCardTestTags.LABEL_CARD_NAME_TEXT).assertTextEquals(labelName)
    }

    @Test
    fun labelCard_onClick_invoked() {
        val onClickMock = mockk<() -> Unit>(relaxed = true)
        val labelName = "Clickable Label"

        composeTestRule.setContent {
            LabelCard(name = labelName, color = testColor, onClick = onClickMock)
        }

        composeTestRule.onNodeWithTag(ReminderCardTestTags.LABEL_CARD_ROOT).performClick()
        verify { onClickMock() }
    }
}
