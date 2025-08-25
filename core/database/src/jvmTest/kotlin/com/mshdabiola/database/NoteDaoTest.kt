package com.mshdabiola.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.model.NoteEntity
// import com.mshdabiola.database.model.NotePadEntity // Not strictly needed for assertions if only checking NoteEntity part
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
        noteType: Int = 0 // Default noteType (e.g., normal note)
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
            createTestNote(title = "Bulk 2")
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
}
