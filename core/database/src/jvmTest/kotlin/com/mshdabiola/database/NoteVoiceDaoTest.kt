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
package com.mshdabiola.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteVoiceEntity
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

class NoteVoiceDaoTest {

    private lateinit var database: NotesDatabase
    private lateinit var noteVoiceDao: NoteVoiceDao
    private lateinit var noteDao: NoteDao

    private var testNoteId1: Long = -1L
    private var testNoteId2: Long = -1L

    // Since NoteVoiceEntity.id is not auto-generated, we manage test IDs
    private val testVoiceId1: Long = 2001L
    private val testVoiceId2: Long = 2002L
    private val testVoiceId3: Long = 2003L

    @Before
    fun createDb() = runTest {
        database = Room.inMemoryDatabaseBuilder<NotesDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        noteVoiceDao = database.getNoteVoiceDao()
        noteDao = database.getNoteDao()

        testNoteId1 = noteDao.upsert(
            NoteEntity(
                id = null,
                title = "Voice Note 1",
                detail = "",
                editDate = 0L,
                isCheck = false,
                color = 0,
                background = 0,
                isPin = false,
                noteType = 0,
            ),
        )
        testNoteId2 = noteDao.upsert(
            NoteEntity(
                id = null,
                title = "Voice Note 2",
                detail = "",
                editDate = 0L,
                isCheck = false,
                color = 0,
                background = 0,
                isPin = false,
                noteType = 0,
            ),
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun upsertAndGetVoice() = runTest {
        val voice = NoteVoiceEntity(id = testVoiceId1, noteId = testNoteId1, path = "meeting_recording.mp3")
        noteVoiceDao.upsert(voice)

        val retrievedVoice = noteVoiceDao.get(testVoiceId1).first()
        assertNotNull(retrievedVoice)
        assertEquals(testVoiceId1, retrievedVoice.id)
        assertEquals(testNoteId1, retrievedVoice.noteId)
        assertEquals("meeting_recording.mp3", retrievedVoice.path)
    }

    @Test
    fun upsert_updatesExistingVoice() = runTest {
        val initialVoice = NoteVoiceEntity(id = testVoiceId1, noteId = testNoteId1, path = "old_name.aac")
        noteVoiceDao.upsert(initialVoice)

        val updatedVoice = NoteVoiceEntity(id = testVoiceId1, noteId = testNoteId1, path = "new_name_final.aac")
        noteVoiceDao.upsert(updatedVoice)

        val retrieved = noteVoiceDao.get(testVoiceId1).first()
        assertNotNull(retrieved)
        assertEquals("new_name_final.aac", retrieved.path)
        assertEquals(testNoteId1, retrieved.noteId) // Ensure noteId isn't accidentally changed
    }

    @Test
    fun upserts_insertsMultipleVoices() = runTest {
        val voices = listOf(
            NoteVoiceEntity(id = testVoiceId1, noteId = testNoteId1, path = "voice_a.ogg"),
            NoteVoiceEntity(id = testVoiceId2, noteId = testNoteId1, path = "voice_b.ogg"),
        )
        val rowIds = noteVoiceDao.upserts(voices)
        assertEquals(2, rowIds.size)
        assertTrue(rowIds.all { it > 0 })

        val retrievedVoices = noteVoiceDao.getByNoteId(testNoteId1).first()
        assertEquals(2, retrievedVoices.size)
        assertTrue(retrievedVoices.any { it.id == testVoiceId1 })
        assertTrue(retrievedVoices.any { it.id == testVoiceId2 })
    }

    @Test
    fun getByNoteId_returnsCorrectVoices() = runTest {
        noteVoiceDao.upsert(NoteVoiceEntity(id = testVoiceId1, noteId = testNoteId1, path = "v1_n1.wav"))
        noteVoiceDao.upsert(NoteVoiceEntity(id = testVoiceId2, noteId = testNoteId2, path = "v1_n2.wav"))
        noteVoiceDao.upsert(NoteVoiceEntity(id = testVoiceId3, noteId = testNoteId1, path = "v2_n1.wav"))

        val voicesForNote1 = noteVoiceDao.getByNoteId(testNoteId1).first()
        assertEquals(2, voicesForNote1.size)
        assertTrue(voicesForNote1.all { it.noteId == testNoteId1 })

        val voicesForNote2 = noteVoiceDao.getByNoteId(testNoteId2).first()
        assertEquals(1, voicesForNote2.size)
        assertEquals(testNoteId2, voicesForNote2.first().noteId)
        assertEquals(testVoiceId2, voicesForNote2.first().id)
    }

    @Test
    fun getVoice_nonExistent() = runTest {
        val voice = noteVoiceDao.get(9999L).first()
        assertNull(voice)
    }

    @Test
    fun getAll_whenEmpty() = runTest {
        noteVoiceDao.deleteByNoteId(testNoteId1)
        noteVoiceDao.deleteByNoteId(testNoteId2)
        val allVoices = noteVoiceDao.getAll().first()
        assertTrue(allVoices.isEmpty())
    }

    @Test
    fun getAll_afterInserts() = runTest {
        noteVoiceDao.upsert(NoteVoiceEntity(id = testVoiceId1, noteId = testNoteId1, path = "all_v1.m4a"))
        noteVoiceDao.upsert(NoteVoiceEntity(id = testVoiceId2, noteId = testNoteId2, path = "all_v2.m4a"))
        val allVoices = noteVoiceDao.getAll().first()
        assertEquals(2, allVoices.size)
    }

    @Test
    fun deleteVoice() = runTest {
        val voice = NoteVoiceEntity(id = testVoiceId1, noteId = testNoteId1, path = "delete_me.mp3")
        noteVoiceDao.upsert(voice)
        assertNotNull(noteVoiceDao.get(testVoiceId1).first())

        noteVoiceDao.delete(testVoiceId1) // Delete by NoteVoiceEntity.id
        assertNull(noteVoiceDao.get(testVoiceId1).first())
    }

    @Test
    fun deleteByNoteId_deletesAllVoicesForNote() = runTest {
        noteVoiceDao.upsert(NoteVoiceEntity(id = testVoiceId1, noteId = testNoteId1, path = "audio_1.flac"))
        noteVoiceDao.upsert(NoteVoiceEntity(id = testVoiceId2, noteId = testNoteId1, path = "audio_2.flac"))
        noteVoiceDao.upsert(NoteVoiceEntity(id = testVoiceId3, noteId = testNoteId2, path = "audio_3.flac"))

        noteVoiceDao.deleteByNoteId(testNoteId1)

        assertTrue(noteVoiceDao.getByNoteId(testNoteId1).first().isEmpty())
        assertEquals(1, noteVoiceDao.getByNoteId(testNoteId2).first().size, "Should not delete voices from other notes")
        assertEquals(testVoiceId3, noteVoiceDao.getByNoteId(testNoteId2).first().firstOrNull()?.id)
    }
}
