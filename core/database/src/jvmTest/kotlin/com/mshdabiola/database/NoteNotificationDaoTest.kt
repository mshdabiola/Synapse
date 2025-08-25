package com.mshdabiola.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteNotificationDao
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NotificationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NoteNotificationDaoTest {

    private lateinit var database: NotesDatabase
    private lateinit var noteNotificationDao: NoteNotificationDao
    private lateinit var noteDao: NoteDao

    private var testNoteId1: Long = -1L
    private var testNoteId2: Long = -1L

    // Helper to create a basic NotificationEntity
    private fun createTestNotification(noteId: Long, customPlace: String? = null): NotificationEntity {
        return NotificationEntity(
            noteId = noteId,
            reminderDateTimeStamp = System.currentTimeMillis() + 100000, // Future time
            placeType = 0, // Home
            customPlaceName = customPlace,
            typeIndex = 0, // DoNotRepeat
            intervalValue = "1",
            weeklyDays = null,
            monthlySameDay = null,
            intervalEndTypeIndex = 0, // Forever
            endDateEpochDay = null,
            numberOfTimes = null
        )
    }

    @Before
    fun createDb() = runTest {
        database = Room.inMemoryDatabaseBuilder<NotesDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        noteNotificationDao = database.getNotification()
        noteDao = database.getNoteDao()

        testNoteId1 = noteDao.upsert(NoteEntity(
            id = null,
            title = "Notification Note 1",
            detail = "",
            editDate = 0L,
            isCheck = false,
            color = 0,
            background = 0,
            isPin = false,
            noteType = 0,
        ))
        testNoteId2 = noteDao.upsert(NoteEntity(
            id = null,
            title = "Notification Note 2",
            detail = "",
            editDate = 0L,
            isCheck = false,
            color = 0,
            background = 0,
            isPin = false,
            noteType = 0,
        ))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun upsertAndGetNotification() = runTest {
        val notification = createTestNotification(testNoteId1, "Office")
        val generatedId = noteNotificationDao.upsert(notification)

        val retrievedNotification = noteNotificationDao.get(generatedId).first()
        assertNotNull(retrievedNotification)
        assertEquals(generatedId, retrievedNotification.id)
        assertEquals(testNoteId1, retrievedNotification.noteId)
        assertEquals("Office", retrievedNotification.customPlaceName)
    }

    @Test
    fun upsert_updatesExistingNotification() = runTest {
        val initialNotification = createTestNotification(testNoteId1)
        val id = noteNotificationDao.upsert(initialNotification)

        val updatedNotification = NotificationEntity(
            id = id, // Keep the same ID
            noteId = testNoteId1,
            reminderDateTimeStamp = System.currentTimeMillis() + 200000,
            placeType = 1, // Work
            customPlaceName = "Updated Place",
            typeIndex = 1, // Daily
            intervalValue = "2",
            weeklyDays = null,
            monthlySameDay = null,
            intervalEndTypeIndex = 1, // EndDate
            endDateEpochDay = System.currentTimeMillis() / (24 * 60 * 60 * 1000) + 10, // 10 days from now
            numberOfTimes = null
        )
        noteNotificationDao.upsert(updatedNotification)

        val retrieved = noteNotificationDao.get(id).first()
        assertNotNull(retrieved)
        assertEquals("Updated Place", retrieved.customPlaceName)
        assertEquals(1, retrieved.placeType)
        assertEquals(1, retrieved.typeIndex)
        assertEquals("2", retrieved.intervalValue)
    }

    @Test
    fun upserts_insertsMultipleNotifications() = runTest {
        val notifications = listOf(
            createTestNotification(testNoteId1, "Meeting Room"),
            createTestNotification(testNoteId2, "Client Site")
        )
        val generatedIds = noteNotificationDao.upserts(notifications)
        assertEquals(2, generatedIds.size)
        assertTrue(generatedIds.all { it > 0 })

        val allNotifications = noteNotificationDao.getAll().first()
        assertEquals(2, allNotifications.size)
    }

    @Test
    fun getByNoteId_returnsCorrectNotifications() = runTest {
        // A note should typically have one notification, but DAO supports list
        val notification1 = createTestNotification(testNoteId1)
        noteNotificationDao.upsert(notification1)
        val notification2 = createTestNotification(testNoteId2) // for a different note
        noteNotificationDao.upsert(notification2)


        val notificationsForNote1 = noteNotificationDao.getByNoteId(testNoteId1).first()
        assertEquals(1, notificationsForNote1.size)
        assertEquals(testNoteId1, notificationsForNote1.first().noteId)

        val notificationsForNote2 = noteNotificationDao.getByNoteId(testNoteId2).first()
        assertEquals(1, notificationsForNote2.size)
        assertEquals(testNoteId2, notificationsForNote2.first().noteId)
    }

    @Test
    fun getNotification_nonExistent() = runTest {
        val notification = noteNotificationDao.get(999L).first()
        assertNull(notification)
    }

    @Test
    fun getAll_whenEmpty() = runTest {
        noteNotificationDao.deleteByNoteId(testNoteId1)
        noteNotificationDao.deleteByNoteId(testNoteId2)
        val allNotifications = noteNotificationDao.getAll().first()
        assertTrue(allNotifications.isEmpty())
    }

    @Test
    fun getAll_afterInserts() = runTest {
        noteNotificationDao.upsert(createTestNotification(testNoteId1))
        noteNotificationDao.upsert(createTestNotification(testNoteId2))
        val allNotifications = noteNotificationDao.getAll().first()
        assertEquals(2, allNotifications.size)
    }

    @Test
    fun deleteNotification() = runTest {
        val notification = createTestNotification(testNoteId1)
        val id = noteNotificationDao.upsert(notification)
        assertNotNull(noteNotificationDao.get(id).first())

        noteNotificationDao.delete(id)
        assertNull(noteNotificationDao.get(id).first())
    }

    @Test
    fun deleteByNoteId_deletesAllNotificationsForNote() = runTest {
        val n1 = createTestNotification(testNoteId1, "N1")
        val n2 = createTestNotification(testNoteId2, "N2") // For another note
        noteNotificationDao.upsert(n1)
        noteNotificationDao.upsert(n2)


        noteNotificationDao.deleteByNoteId(testNoteId1)

        assertTrue(noteNotificationDao.getByNoteId(testNoteId1).first().isEmpty())
        assertEquals(1, noteNotificationDao.getByNoteId(testNoteId2).first().size, "Should not delete notifications from other notes")
    }
}

