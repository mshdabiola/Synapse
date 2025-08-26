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
package com.mshdabiola.data

import com.mshdabiola.data.doubles.TestNoteNotificationDao
import com.mshdabiola.data.repository.RealNotificationRepository
import com.mshdabiola.model.note.Notification
import com.mshdabiola.model.note.Place
import com.mshdabiola.model.note.RepeatSchedule
import com.mshdabiola.model.note.IntervalEnd
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteNotificationRepositoryTest {

    private lateinit var noteNotificationDao: TestNoteNotificationDao
    private lateinit var repository: RealNotificationRepository
    private val testDispatcher = StandardTestDispatcher()

    private val testNowLocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    private val testFutureDate = LocalDate(testNowLocalDateTime.year + 1, 1, 1)


    @Before
    fun setUp() {
        noteNotificationDao = TestNoteNotificationDao()
        repository = RealNotificationRepository(noteNotificationDao, testDispatcher)
    }

    private fun createTestNotification(
        noteId: Long, // Must be provided and should be realistic (e.g., > 0)
        dateTime: kotlinx.datetime.LocalDateTime = testNowLocalDateTime,
        interval: RepeatSchedule = RepeatSchedule.DoNotRepeat,
        place: Place? = null
    ): Notification {
        return Notification(
            noteId = noteId,
            currentDateTime = dateTime,
            currentInterval = interval,
            currentPlace = place
        )
    }

    @Test
    fun `upsert new notification returns noteId and adds notification`() = runTest(testDispatcher) {
        val testNoteId = 1L
        val newNotification = createTestNotification(noteId = testNoteId)
        // The ID returned by upsert should be the noteId (if noteId was not 0)
        // due to Notification.asEntity() setting NotificationEntity.id = Notification.noteId
        // and TestNoteNotificationDao using that id.
        val returnedId = repository.upsert(newNotification)

        assertEquals("Returned ID should be the noteId", testNoteId, returnedId)

        // Fetching by this returnedId (which is the noteId and also the NotificationEntity PK in the test DAO)
        val insertedNotificationModel = repository.get(returnedId).first()
        assertNotNull("Inserted Notification should not be null", insertedNotificationModel)
        // The model itself doesn't have an 'id' field, only 'noteId'
        // assertEquals(testNoteId, insertedNotificationModel?.noteId) // This check is implicitly done by get(returnedId)
        assertEquals(testNowLocalDateTime.date, insertedNotificationModel?.currentDateTime?.date)
        assertEquals(testNowLocalDateTime.hour, insertedNotificationModel?.currentDateTime?.hour)
        assertEquals(testNowLocalDateTime.minute, insertedNotificationModel?.currentDateTime?.minute)


        assertEquals(RepeatSchedule.DoNotRepeat, insertedNotificationModel?.currentInterval)
        assertNull(insertedNotificationModel?.currentPlace)
    }

    @Test
    fun `upsert existing notification (by noteId) updates it`() = runTest(testDispatcher) {
        val testNoteId = 2L
        val initialNotification = createTestNotification(
            noteId = testNoteId,
            interval = RepeatSchedule.DoNotRepeat
        )
        val initialReturnedId = repository.upsert(initialNotification)
        assertEquals(testNoteId, initialReturnedId)

        val updatedDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            .plusHours(1)
        val updatedInterval = RepeatSchedule.Daily(
            interval = "2",
            intervalEnd = IntervalEnd.NumberOfTimes(5)
        )
        val updatedNotification = createTestNotification(
            noteId = testNoteId, // Same noteId, so it's an update
            dateTime = updatedDateTime,
            interval = updatedInterval,
            place = Place.Home
        )
        val updatedReturnedId = repository.upsert(updatedNotification)
        assertEquals("Returned ID should still be the noteId", testNoteId, updatedReturnedId)

        val fetchedNotificationModel = repository.get(testNoteId).first()
        assertNotNull(fetchedNotificationModel)
        assertEquals(updatedDateTime.date, fetchedNotificationModel?.currentDateTime?.date)
        assertEquals(updatedDateTime.hour, fetchedNotificationModel?.currentDateTime?.hour)
        assertEquals(updatedDateTime.minute, fetchedNotificationModel?.currentDateTime?.minute)
        assertEquals(updatedInterval, fetchedNotificationModel?.currentInterval)
        assertEquals(Place.Home, fetchedNotificationModel?.currentPlace)
    }

    @Test
    fun `upserts_insertsMultipleNotifications_andReturnsTheirNoteIds`() = runTest(testDispatcher) {
        val notification1 = createTestNotification(noteId = 10L)
        val notification2 = createTestNotification(noteId = 11L, place = Place.Work)
        val notificationsToInsert = listOf(notification1, notification2)

        val returnedIds = repository.upserts(notificationsToInsert)
        assertEquals("Should return 2 IDs", 2, returnedIds.size)
        assertTrue("Returned IDs should match noteIds", returnedIds.containsAll(listOf(10L, 11L)))

        val allNotifications = repository.getAll().first()
        assertEquals("Should have 2 notifications in DB", 2, allNotifications.size)
        // Verify by checking if notifications for these noteIds exist
        assertNotNull(repository.get(10L).first())
        assertNotNull(repository.get(11L).first())
    }


    @Test
    fun `delete removes notification by its primary key (noteId in this test setup)`() = runTest(testDispatcher) {
        val testNoteId = 3L
        val notification = createTestNotification(noteId = testNoteId)
        val pk = repository.upsert(notification) // pk will be testNoteId
        assertEquals(testNoteId, pk)

        assertNotNull(repository.get(pk).first())
        repository.delete(pk) // Deleting using the PK
        assertNull(repository.get(pk).first())
    }

    @Test
    fun `deleteByNoteId removes all notifications for that specific noteId`() = runTest(testDispatcher) {
        val noteIdToDelete = 4L
        val otherNoteId = 5L
        repository.upsert(createTestNotification(noteId = noteIdToDelete, place = Place.Home))
        // Test DAO would create another notification with noteIdToDelete as its PK
        // If we add another with same noteId, it will overwrite in Test DAO
        repository.upsert(createTestNotification(noteId = otherNoteId, place = Place.Work))


        assertNotNull("Notification for noteIdToDelete should exist before delete", repository.getByNoteId(noteIdToDelete).first().firstOrNull())
        assertNotNull("Notification for otherNoteId should exist before delete", repository.getByNoteId(otherNoteId).first().firstOrNull())


        repository.deleteByNoteId(noteIdToDelete)

        assertTrue("Notifications for noteIdToDelete should be empty after delete", repository.getByNoteId(noteIdToDelete).first().isEmpty())
        assertNotNull("Notification for otherNoteId should still exist", repository.getByNoteId(otherNoteId).first().firstOrNull())
        assertEquals("Only 1 notification should remain in total", 1, repository.getAll().first().size)
    }


    @Test
    fun `getAll returns empty list initially`() = runTest(testDispatcher) {
        val notifications = repository.getAll().first()
        assertTrue("Initially, getAll should return an empty list", notifications.isEmpty())
    }

    @Test
    fun `getAll returns inserted notifications`() = runTest(testDispatcher) {
        repository.upsert(createTestNotification(noteId = 6L))
        repository.upsert(createTestNotification(noteId = 7L))
        assertEquals(2, repository.getAll().first().size)
    }

    @Test
    fun `get returns null for non-existent id (noteId)`() = runTest(testDispatcher) {
        assertNull(repository.get(999L).first())
    }

    @Test
    fun `get returns correct notification by its primary key (noteId)`() = runTest(testDispatcher) {
        val testNoteId = 8L
        val notification = createTestNotification(noteId = testNoteId, place = Place.School)
        val pk = repository.upsert(notification)
        assertEquals(testNoteId, pk)

        val fetched = repository.get(pk).first()
        assertNotNull(fetched)
        // fetched.noteId is not directly available on the model, but it's used as the key.
        assertEquals(Place.School, fetched?.currentPlace)
    }

    @Test
    fun `getByNoteId returns correct notifications`() = runTest(testDispatcher) {
        val noteId1 = 20L
        val noteId2 = 21L
        val notification1 = createTestNotification(noteId = noteId1, place = Place.Home)
        // TestNoteNotificationDao will overwrite if the same noteId (which is the PK) is used.
        // So, for getByNoteId to return multiple, they must be for different notes mapped to different PKs
        // This test as-is for getByNoteId will only ever find one or zero for a given noteId
        // because the PK in the test DAO becomes the noteId.
        repository.upsert(notification1)
        repository.upsert(createTestNotification(noteId = noteId2, place = Place.Work))


        val forNote1 = repository.getByNoteId(noteId1).first()
        assertEquals("Should be 1 notification for noteId1", 1, forNote1.size)
        assertEquals(Place.Home, forNote1.first().currentPlace)

        val forNote2 = repository.getByNoteId(noteId2).first()
        assertEquals("Should be 1 notification for noteId2", 1, forNote2.size)
        assertEquals(Place.Work, forNote2.first().currentPlace)

        assertTrue("Should be 0 notifications for noteId3", repository.getByNoteId(30L).first().isEmpty())
    }

     @Test
    fun `complex RepeatSchedule mapping is correct`() = runTest(testDispatcher) {
        val testNoteId = 99L
        val weeklySchedule = RepeatSchedule.Weekly(
            interval = "1",
            days = setOf(1, 3, 5), // Mon, Wed, Fri
            intervalEnd = IntervalEnd.EndDate(testFutureDate)
        )
        val notification = createTestNotification(
            noteId = testNoteId,
            interval = weeklySchedule,
            place = Place.Edit("Custom Place")
        )
        val pk = repository.upsert(notification)
        assertEquals(testNoteId, pk)

        val fetched = repository.get(pk).first()
        assertNotNull(fetched)
        assertEquals(Place.Edit("Custom Place"), fetched?.currentPlace)
        assertEquals(weeklySchedule, fetched?.currentInterval)

        val fetchedWeeklyInterval = fetched?.currentInterval as RepeatSchedule.Weekly
        assertEquals(setOf(1,3,5), fetchedWeeklyInterval.days)
        assertEquals("1", fetchedWeeklyInterval.interval)
        val fetchedEndDate = fetchedWeeklyInterval.intervalEnd as IntervalEnd.EndDate
        assertEquals(testFutureDate, fetchedEndDate.date)
    }
}
