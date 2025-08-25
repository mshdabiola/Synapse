package com.mshdabiola.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.database.model.NoteDrawingEntity
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

class NoteDrawingDaoTest {

    private lateinit var database: NotesDatabase
    private lateinit var noteDrawingDao: NoteDrawingDao
    private lateinit var noteDao: NoteDao
    private var testNoteId1: Long = -1L
    private var testNoteId2: Long = -1L

    @Before
    fun createDb() = runTest {
        database = Room.inMemoryDatabaseBuilder<NotesDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        noteDrawingDao = database.getNoteDrawingDao()
        noteDao = database.getNoteDao()

        testNoteId1 = noteDao.upsert(NoteEntity(
            id = null,
            title = "Drawing Note 1",
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
            title = "Drawing Note 2",
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
    fun upsertAndGetDrawing() = runTest {
        val drawing = NoteDrawingEntity(noteId = testNoteId1, paths = "path1;path2")
        val generatedId = noteDrawingDao.upsert(drawing)

        val retrievedDrawing = noteDrawingDao.get(generatedId).first()
        assertNotNull(retrievedDrawing)
        assertEquals(generatedId, retrievedDrawing.id)
        assertEquals(testNoteId1, retrievedDrawing.noteId)
        assertEquals("path1;path2", retrievedDrawing.paths)
    }

    @Test
    fun upsert_updatesExistingDrawing() = runTest {
        val initialDrawing = NoteDrawingEntity(noteId = testNoteId1, paths = "original_path")
        val id = noteDrawingDao.upsert(initialDrawing)

        val updatedDrawing = NoteDrawingEntity(id = id, noteId = testNoteId1, paths = "updated_path")
        noteDrawingDao.upsert(updatedDrawing)

        val retrieved = noteDrawingDao.get(id).first()
        assertNotNull(retrieved)
        assertEquals("updated_path", retrieved.paths)
    }

    @Test
    fun upserts_insertsMultipleDrawings() = runTest {
        val drawings = listOf(
            NoteDrawingEntity(noteId = testNoteId1, paths = "drawingA"),
            NoteDrawingEntity(noteId = testNoteId1, paths = "drawingB")
        )
        val generatedIds = noteDrawingDao.upserts(drawings)
        assertEquals(2, generatedIds.size)
        assertTrue(generatedIds.all { it > 0 })

        val retrievedDrawings = noteDrawingDao.getByNoteId(testNoteId1).first()
        assertEquals(2, retrievedDrawings.size)
    }

    @Test
    fun getByNoteId_returnsCorrectDrawings() = runTest {
        noteDrawingDao.upsert(NoteDrawingEntity(noteId = testNoteId1, paths = "d1_n1"))
        noteDrawingDao.upsert(NoteDrawingEntity(noteId = testNoteId2, paths = "d1_n2"))
        noteDrawingDao.upsert(NoteDrawingEntity(noteId = testNoteId1, paths = "d2_n1"))

        val drawingsForNote1 = noteDrawingDao.getByNoteId(testNoteId1).first()
        assertEquals(2, drawingsForNote1.size)
        assertTrue(drawingsForNote1.all { it.noteId == testNoteId1 })

        val drawingsForNote2 = noteDrawingDao.getByNoteId(testNoteId2).first()
        assertEquals(1, drawingsForNote2.size)
        assertEquals(testNoteId2, drawingsForNote2.first().noteId)
    }

    @Test
    fun getDrawing_nonExistent() = runTest {
        val drawing = noteDrawingDao.get(999L).first()
        assertNull(drawing)
    }

    @Test
    fun getAll_whenEmpty() = runTest {
        noteDrawingDao.deleteByNoteId(testNoteId1) // Clear any potential inserts from other tests for this noteId
        noteDrawingDao.deleteByNoteId(testNoteId2)
        val allDrawings = noteDrawingDao.getAll().first()
        assertTrue(allDrawings.isEmpty())
    }

    @Test
    fun getAll_afterInserts() = runTest {
        noteDrawingDao.upsert(NoteDrawingEntity(noteId = testNoteId1, paths = "pathX"))
        noteDrawingDao.upsert(NoteDrawingEntity(noteId = testNoteId2, paths = "pathY"))
        val allDrawings = noteDrawingDao.getAll().first()
        assertEquals(2, allDrawings.size)
    }

    @Test
    fun deleteDrawing() = runTest {
        val drawing = NoteDrawingEntity(noteId = testNoteId1, paths = "to_delete")
        val id = noteDrawingDao.upsert(drawing)
        assertNotNull(noteDrawingDao.get(id).first())

        noteDrawingDao.delete(id)
        assertNull(noteDrawingDao.get(id).first())
    }

    @Test
    fun deleteByNoteId_deletesAllDrawingsForNote() = runTest {
        noteDrawingDao.upsert(NoteDrawingEntity(noteId = testNoteId1, paths = "drawing_1a"))
        noteDrawingDao.upsert(NoteDrawingEntity(noteId = testNoteId1, paths = "drawing_1b"))
        noteDrawingDao.upsert(NoteDrawingEntity(noteId = testNoteId2, paths = "drawing_2a"))

        noteDrawingDao.deleteByNoteId(testNoteId1)

        assertTrue(noteDrawingDao.getByNoteId(testNoteId1).first().isEmpty())
        assertEquals(1, noteDrawingDao.getByNoteId(testNoteId2).first().size, "Should not delete drawings from other notes")
    }
}
