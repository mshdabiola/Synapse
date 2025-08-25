package com.mshdabiola.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.model.LabelEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteLabelCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoteLabelDaoTest {

    private lateinit var database: NotesDatabase
    private lateinit var noteLabelDao: NoteLabelDao
    private lateinit var noteDao: NoteDao
    private lateinit var labelDao: LabelDao

    private var testNoteId1: Long = -1L
    private var testNoteId2: Long = -1L
    private var testLabelId1: Long = -1L
    private var testLabelId2: Long = -1L
    private var testLabelId3: Long = -1L

    @Before
    fun createDb() = runTest {
        database = Room.inMemoryDatabaseBuilder<NotesDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        noteLabelDao = database.getNoteLabelDao()
        noteDao = database.getNoteDao()
        labelDao = database.getLabelDao()

        // Insert parent notes and labels
        testNoteId1 = noteDao.upsert(NoteEntity(
            id = null,
            title = "NL Note 1",
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
            title = "NL Note 2",
            detail = "",
            editDate = 0L,
            isCheck = false,
            color = 0,
            background = 0,
            isPin = false,
            noteType = 0,
        ))

        testLabelId1 = labelDao.upsert(LabelEntity(id = null,name = "NL Label 1"))
        testLabelId2 = labelDao.upsert(LabelEntity(id = null,name = "NL Label 2"))
        testLabelId3 = labelDao.upsert(LabelEntity(id = null,name = "NL Label 3"))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun upsertAndGetCrossRef() = runTest {
        val crossRef = NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId1)
        val rowId = noteLabelDao.upsert(crossRef)
        assertTrue(rowId > 0)

        val retrievedByNoteId = noteLabelDao.getByNoteId(testNoteId1).first()
        assertEquals(1, retrievedByNoteId.size)
        assertEquals(testLabelId1, retrievedByNoteId.first().labelId)

        val retrievedByLabelId = noteLabelDao.getByLabelId(testLabelId1).first()
        assertEquals(1, retrievedByLabelId.size)
        assertEquals(testNoteId1, retrievedByLabelId.first().noteId)
    }

    @Test
    fun upsert_existingCrossRef_doesNothingExtraIfNoConflictStrategy() = runTest {
        // For cross-ref, an upsert on an existing pair usually just re-asserts.
        // Room's default for @Upsert is to behave like INSERT OR REPLACE on conflict
        // which for a join table means the existing row is replaced by an identical one.
        val crossRef = NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId1)
        noteLabelDao.upsert(crossRef)
        var count = noteLabelDao.getAll().first().size
        assertEquals(1, count)

        noteLabelDao.upsert(crossRef) // Upsert again
        count = noteLabelDao.getAll().first().size
        assertEquals(1, count, "Upserting an existing cross-ref should not duplicate it")
    }


    @Test
    fun upserts_insertsMultipleCrossRefs() = runTest {
        val crossRefs = listOf(
            NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId1),
            NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId2),
            NoteLabelCrossRef(noteId = testNoteId2, labelId = testLabelId1)
        )
        val rowIds = noteLabelDao.upserts(crossRefs)
        assertEquals(3, rowIds.size)
        assertTrue(rowIds.all { it > 0 })

        val allCrossRefs = noteLabelDao.getAll().first()
        assertEquals(3, allCrossRefs.size)
    }

    @Test
    fun getByNoteId_returnsCorrectRefs() = runTest {
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId1))
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId2, labelId = testLabelId2))
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId3))

        val refsForNote1 = noteLabelDao.getByNoteId(testNoteId1).first()
        assertEquals(2, refsForNote1.size)
        assertTrue(refsForNote1.any { it.labelId == testLabelId1 })
        assertTrue(refsForNote1.any { it.labelId == testLabelId3 })

        val refsForNote2 = noteLabelDao.getByNoteId(testNoteId2).first()
        assertEquals(1, refsForNote2.size)
        assertEquals(testLabelId2, refsForNote2.first().labelId)
    }

    @Test
    fun getByLabelId_returnsCorrectRefs() = runTest {
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId1))
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId2, labelId = testLabelId1))
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId2))

        val refsForLabel1 = noteLabelDao.getByLabelId(testLabelId1).first()
        assertEquals(2, refsForLabel1.size)
        assertTrue(refsForLabel1.any { it.noteId == testNoteId1 })
        assertTrue(refsForLabel1.any { it.noteId == testNoteId2 })

        val refsForLabel2 = noteLabelDao.getByLabelId(testLabelId2).first()
        assertEquals(1, refsForLabel2.size)
        assertEquals(testNoteId1, refsForLabel2.first().noteId)
    }

    @Test
    fun getByNoteIds_returnsCorrectRefs() = runTest {
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId1))
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId2, labelId = testLabelId2))
        // Create a third note and label for this test
        val testNoteId3 = noteDao.upsert(NoteEntity(
            id = null,
            title = "NL Note 3",
            detail = "",
            editDate = 0L,
            isCheck = false,
            color = 0,
            background = 0,
            isPin = false,
            noteType = 0,
        ))
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId3, labelId = testLabelId3))


        val refsForNotes1and2 = noteLabelDao.getByNoteIds(setOf(testNoteId1, testNoteId2)).first()
        assertEquals(2, refsForNotes1and2.size)
        assertTrue(refsForNotes1and2.any { it.noteId == testNoteId1 && it.labelId == testLabelId1 })
        assertTrue(refsForNotes1and2.any { it.noteId == testNoteId2 && it.labelId == testLabelId2 })

        val refsForNote1Only = noteLabelDao.getByNoteIds(setOf(testNoteId1)).first()
        assertEquals(1, refsForNote1Only.size)
        assertEquals(testNoteId1, refsForNote1Only.first().noteId)

    }


    @Test
    fun getAll_whenEmpty() = runTest {
        val allRefs = noteLabelDao.getAll().first()
        assertTrue(allRefs.isEmpty())
    }

    @Test
    fun getAll_afterInserts() = runTest {
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId1))
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId2, labelId = testLabelId2))
        val allRefs = noteLabelDao.getAll().first()
        assertEquals(2, allRefs.size)
    }

    @Test
    fun deleteByNoteIdAndLabelId() = runTest {
        val ref1 = NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId1)
        val ref2 = NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId2)
        noteLabelDao.upserts(listOf(ref1, ref2))
        assertEquals(2, noteLabelDao.getByNoteId(testNoteId1).first().size)

        noteLabelDao.deleteByNoteIdAndLabelId(testNoteId1, testLabelId1)
        val remainingRefs = noteLabelDao.getByNoteId(testNoteId1).first()
        assertEquals(1, remainingRefs.size)
        assertEquals(testLabelId2, remainingRefs.first().labelId)
    }

    @Test
    fun deleteByNoteId_deletesAllRefsForNote() = runTest {
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId1))
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId1, labelId = testLabelId2))
        noteLabelDao.upsert(NoteLabelCrossRef(noteId = testNoteId2, labelId = testLabelId1)) // Different note

        noteLabelDao.deleteByNoteId(testNoteId1)

        assertTrue(noteLabelDao.getByNoteId(testNoteId1).first().isEmpty(), "All refs for testNoteId1 should be deleted")
        assertEquals(1, noteLabelDao.getByNoteId(testNoteId2).first().size, "Refs for testNoteId2 should remain")
        assertEquals(1, noteLabelDao.getByLabelId(testLabelId1).first().size, "Ref for testNoteId2 / testLabelId1 should remain")

    }
}

