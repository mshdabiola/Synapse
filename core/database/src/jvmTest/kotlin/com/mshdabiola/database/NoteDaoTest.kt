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
import com.mshdabiola.database.model.NoteEntity
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
import kotlin.test.assertFalse

class NoteDaoTest {

    private lateinit var database: NotesDatabase
    private lateinit var noteDao: NoteDao

    // Helper to create a NoteEntity with default values
    private fun createTestNote(
        id: Long? = null,
        title: String = "Test Title",
        detail: String = "Test Detail",
        editDate: Long = System.currentTimeMillis(),
        isCheck: Boolean = false,
        color: Int = 0,
        background: Int = 0,
        isPin: Boolean = false,
        noteType: Int = 0, // Default note type (e.g., normal note)
    ): NoteEntity {
        return NoteEntity(id, title, detail, editDate, isCheck, color, background, isPin, noteType)
    }

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder<NotesDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        noteDao = database.getNoteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun upsert_insertsNewNote_andGetReturnsIt() = runTest {
        val note = createTestNote(title = "Insert Test", detail = "Detail for insert")
        val generatedId = noteDao.upsert(note)
        assertTrue(generatedId > 0, "Upsert should return a positive ID for new inserts")

        val retrievedNotePad = noteDao.get(generatedId).first()
        assertNotNull(retrievedNotePad)
        val retrievedNote = retrievedNotePad.noteEntity
        assertEquals(generatedId, retrievedNote.id)
        assertEquals("Insert Test", retrievedNote.title)
        assertEquals("Detail for insert", retrievedNote.detail)
    }

    @Test
    fun upsert_updatesExistingNote() = runTest {
        val initialNote = createTestNote(title = "Original Title", detail = "Original Detail")
        val id = noteDao.upsert(initialNote)

        val updatedNoteEntity = createTestNote(id = id, title = "Updated Title", detail = "Updated Detail")
        noteDao.upsert(updatedNoteEntity)

        val retrievedNotePad = noteDao.get(id).first()
        assertNotNull(retrievedNotePad)
        val retrievedNote = retrievedNotePad.noteEntity
        assertEquals(id, retrievedNote.id)
        assertEquals("Updated Title", retrievedNote.title)
        assertEquals("Updated Detail", retrievedNote.detail)
    }

    @Test
    fun upserts_insertsMultipleNotes() = runTest {
        val notes = listOf(
            createTestNote(title = "Bulk 1"),
            createTestNote(title = "Bulk 2"),
        )
        val generatedIds = noteDao.upserts(notes)
        assertEquals(2, generatedIds.size)
        assertTrue(generatedIds.all { it > 0 })

        val allNotes = noteDao.getAll().first()
        assertEquals(2, allNotes.size)
    }

    @Test
    fun get_nonExistentNote_returnsNull() = runTest {
        val notePad = noteDao.get(999L).first() // ID that doesn't exist
        assertNull(notePad, "Should return null for a non-existent ID")
    }

    @Test
    fun getAll_whenEmpty_returnsEmptyList() = runTest {
        val allNotes = noteDao.getAll().first()
        assertTrue(allNotes.isEmpty(), "Database should be empty initially")
    }

    @Test
    fun getAll_afterInsertingMultiple_returnsAllNotes() = runTest {
        noteDao.upsert(createTestNote(title = "Note A"))
        noteDao.upsert(createTestNote(title = "Note B"))

        val allNotes = noteDao.getAll().first()
        assertEquals(2, allNotes.size)
        assertTrue(allNotes.any { it.noteEntity.title == "Note A" })
        assertTrue(allNotes.any { it.noteEntity.title == "Note B" })
    }

    @Test
    fun delete_removesNote() = runTest {
        val note = createTestNote(title = "To Delete")
        val id = noteDao.upsert(note)

        assertNotNull(noteDao.get(id).first(), "Note should exist before delete")
        noteDao.delete(id)
        assertNull(noteDao.get(id).first(), "Note should be null after delete")
    }

    @Test
    fun deleteIds_removesSpecifiedNotes() = runTest {
        val id1 = noteDao.upsert(createTestNote(title = "Delete ID 1"))
        val id2 = noteDao.upsert(createTestNote(title = "Keep Me"))
        val id3 = noteDao.upsert(createTestNote(title = "Delete ID 3"))

        noteDao.deleteIds(setOf(id1, id3))

        assertNull(noteDao.get(id1).first())
        assertNotNull(noteDao.get(id2).first())
        assertNull(noteDao.get(id3).first())
        assertEquals(1, noteDao.getAll().first().size)
    }

    @Test
    fun deleteTrash_removesNotesOfSpecifiedType() = runTest {
        val trashNoteType = 2 // Assuming 2 is a designated trash type
        val normalNoteType = 0

        noteDao.upsert(createTestNote(title = "Trash Note 1", noteType = trashNoteType))
        noteDao.upsert(createTestNote(title = "Normal Note 1", noteType = normalNoteType))
        noteDao.upsert(createTestNote(title = "Trash Note 2", noteType = trashNoteType))

        noteDao.deleteTrash(trashNoteType)

        val remainingNotes = noteDao.getAll().first()
        assertEquals(1, remainingNotes.size)
        assertEquals(normalNoteType, remainingNotes.first().noteEntity.noteType)
        assertTrue(remainingNotes.none { it.noteEntity.noteType == trashNoteType })
    }

    @Test
    fun getByNoteType_returnsCorrectNotes() = runTest {
        val typeA = 1
        val typeB = 0

        noteDao.upsert(createTestNote(title = "Note Type A1", noteType = typeA))
        noteDao.upsert(createTestNote(title = "Note Type B1", noteType = typeB))
        noteDao.upsert(createTestNote(title = "Note Type A2", noteType = typeA))

        val notesOfTypeA = noteDao.getByNoteType(typeA).first()
        assertEquals(2, notesOfTypeA.size)
        assertTrue(notesOfTypeA.all { it.noteEntity.noteType == typeA })

        val notesOfTypeB = noteDao.getByNoteType(typeB).first()
        assertEquals(1, notesOfTypeB.size)
        assertEquals(typeB, notesOfTypeB.first().noteEntity.noteType)
    }

    @Test
    fun getByIds_returnsSpecifiedNotes() = runTest {
        val id1 = noteDao.upsert(createTestNote(title = "Get ID 1"))
        noteDao.upsert(createTestNote(title = "Ignore Me")) // id2
        val id3 = noteDao.upsert(createTestNote(title = "Get ID 3"))

        val retrievedNotes = noteDao.getByIds(setOf(id1, id3)).first()
        assertEquals(2, retrievedNotes.size)
        assertTrue(retrievedNotes.any { it.noteEntity.id == id1 && it.noteEntity.title == "Get ID 1" })
        assertTrue(retrievedNotes.any { it.noteEntity.id == id3 && it.noteEntity.title == "Get ID 3" })
        assertTrue(retrievedNotes.none { it.noteEntity.title == "Ignore Me" })
    }

    @Test
    fun updateColorForIds_updatesColorForSpecifiedNotes() = runTest {
        val initialColor = 0
        val updatedColor = 5
        val id1 = noteDao.upsert(createTestNote(title = "Color Change 1", color = initialColor))
        val id2 = noteDao.upsert(createTestNote(title = "Color Keep", color = initialColor))
        val id3 = noteDao.upsert(createTestNote(title = "Color Change 2", color = initialColor))

        noteDao.updateColorForIds(setOf(id1, id3), updatedColor)

        val note1 = noteDao.get(id1).first()?.noteEntity
        val note2 = noteDao.get(id2).first()?.noteEntity
        val note3 = noteDao.get(id3).first()?.noteEntity

        assertNotNull(note1)
        assertEquals(updatedColor, note1.color, "Note 1 color should be updated")
        assertNotNull(note2)
        assertEquals(initialColor, note2.color, "Note 2 color should not be updated")
        assertNotNull(note3)
        assertEquals(updatedColor, note3.color, "Note 3 color should be updated")
    }

    @Test
    fun updatePinForIds_updatesPinStatusForSpecifiedNotes() = runTest {
        val id1 = noteDao.upsert(createTestNote(title = "Pin Me 1", isPin = false))
        val id2 = noteDao.upsert(createTestNote(title = "Keep Unpinned", isPin = false))
        val id3 = noteDao.upsert(createTestNote(title = "Pin Me 2", isPin = false))

        noteDao.updatePinForIds(setOf(id1, id3), true)

        val note1 = noteDao.get(id1).first()?.noteEntity
        val note2 = noteDao.get(id2).first()?.noteEntity
        val note3 = noteDao.get(id3).first()?.noteEntity

        assertNotNull(note1)
        assertTrue(note1.isPin, "Note 1 should be pinned")
        assertNotNull(note2)
        assertFalse(note2.isPin, "Note 2 should remain unpinned")
        assertNotNull(note3)
        assertTrue(note3.isPin, "Note 3 should be pinned")

        // Test unpinning
        noteDao.updatePinForIds(setOf(id1), false)
        val unpinnedNote1 = noteDao.get(id1).first()?.noteEntity
        assertNotNull(unpinnedNote1)
        assertFalse(unpinnedNote1.isPin, "Note 1 should be unpinned")
    }

    @Test
    fun updateNoteTypeForIds_updatesNoteTypeForSpecifiedNotes() = runTest {
        val initialType = 0 // e.g., Normal
        val archiveType = 1   // e.g., Archive
        val trashType = 2     // e.g., Trash

        val id1 = noteDao.upsert(createTestNote(title = "Archive Me", noteType = initialType))
        val id2 = noteDao.upsert(createTestNote(title = "Keep Normal", noteType = initialType))
        val id3 = noteDao.upsert(createTestNote(title = "Trash Me", noteType = initialType))

        // Archive id1
        noteDao.updateNoteTypeForIds(setOf(id1), archiveType)
        val note1Archived = noteDao.get(id1).first()?.noteEntity
        assertNotNull(note1Archived)
        assertEquals(archiveType, note1Archived.noteType, "Note 1 should be archived")

        // Trash id3
        noteDao.updateNoteTypeForIds(setOf(id3), trashType)
        val note3Trashed = noteDao.get(id3).first()?.noteEntity
        assertNotNull(note3Trashed)
        assertEquals(trashType, note3Trashed.noteType, "Note 3 should be trashed")

        // Check id2 remained unchanged
        val note2Unchanged = noteDao.get(id2).first()?.noteEntity
        assertNotNull(note2Unchanged)
        assertEquals(initialType, note2Unchanged.noteType, "Note 2 type should remain normal")
    }
}
