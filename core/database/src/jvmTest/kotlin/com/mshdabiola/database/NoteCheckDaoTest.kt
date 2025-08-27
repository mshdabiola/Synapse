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
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteItemEntity
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

class NoteCheckDaoTest {

    private lateinit var database: NotesDatabase
    private lateinit var noteCheckDao: NoteCheckDao
    private lateinit var noteDao: NoteDao // For inserting parent notes

    private var testNoteId1: Long = -1L
    private var testNoteId2: Long = -1L

    @Before
    fun createDb() = runTest {
        // Made suspend to allow note insertion
        database = Room.inMemoryDatabaseBuilder<NotesDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        noteCheckDao = database.getNoteCheckDao()
        noteDao = database.getNoteDao() // Initialize NoteDao

        // Insert some parent notes for FK constraints
        testNoteId1 = noteDao.upsert(
            NoteEntity(
                id = null,
                title = "Test Note 1",
                detail = "",
                editDate = 0L,
                isCheck = true,
                color = 0,
                background = 0,
                isPin = false,
                noteType = 0,
            ),
        )
        testNoteId2 = noteDao.upsert(
            NoteEntity(
                id = null,
                title = "Test Note 2",
                detail = "",
                editDate = 0L,
                isCheck = true,
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
    fun upsertAndGetCheckItem() = runTest {
        val item = NoteItemEntity(id = null, noteId = testNoteId1, content = "Milk", isCheck = false)
        val generatedId = noteCheckDao.upsert(item)

        val retrievedItem = noteCheckDao.get(generatedId).first()
        assertNotNull(retrievedItem)
        assertEquals(generatedId, retrievedItem.id)
        assertEquals(testNoteId1, retrievedItem.noteId)
        assertEquals("Milk", retrievedItem.content)
        assertEquals(false, retrievedItem.isCheck)
    }

    @Test
    fun upserts_insertsMultipleItems() = runTest {
        val items = listOf(
            NoteItemEntity(id = null, noteId = testNoteId1, content = "Bread", isCheck = true),
            NoteItemEntity(id = null, noteId = testNoteId1, content = "Butter", isCheck = false),
        )
        val generatedIds = noteCheckDao.upserts(items)
        assertEquals(2, generatedIds.size)
        assertTrue(generatedIds.all { it > 0 })

        val retrievedItems = noteCheckDao.getByNoteId(testNoteId1).first()
        assertEquals(2, retrievedItems.size)
    }

    @Test
    fun upsert_updatesExistingItem() = runTest {
        val initialItem = NoteItemEntity(id = null, noteId = testNoteId1, content = "Eggs", isCheck = false)
        val id = noteCheckDao.upsert(initialItem)

        val updatedItem = NoteItemEntity(id = id, noteId = testNoteId1, content = "Organic Eggs", isCheck = true)
        noteCheckDao.upsert(updatedItem)

        val retrieved = noteCheckDao.get(id).first()
        assertNotNull(retrieved)
        assertEquals("Organic Eggs", retrieved.content)
        assertTrue(retrieved.isCheck)
    }

    @Test
    fun getByNoteId_returnsCorrectItems() = runTest {
        noteCheckDao.upsert(
            NoteItemEntity(id = null, noteId = testNoteId1, content = "Item 1 for Note 1", isCheck = false),
        )
        noteCheckDao.upsert(
            NoteItemEntity(id = null, noteId = testNoteId2, content = "Item 1 for Note 2", isCheck = false),
        )
        noteCheckDao.upsert(
            NoteItemEntity(id = null, noteId = testNoteId1, content = "Item 2 for Note 1", isCheck = true),
        )

        val itemsForNote1 = noteCheckDao.getByNoteId(testNoteId1).first()
        assertEquals(2, itemsForNote1.size)
        assertTrue(itemsForNote1.all { it.noteId == testNoteId1 })

        val itemsForNote2 = noteCheckDao.getByNoteId(testNoteId2).first()
        assertEquals(1, itemsForNote2.size)
        assertEquals(testNoteId2, itemsForNote2.first().noteId)
    }

    @Test
    fun getAll_whenEmpty() = runTest {
        // Clear any items inserted by other tests if run in a shared context (though @Before should handle this for each test)
        noteCheckDao.deleteByNoteId(testNoteId1)
        noteCheckDao.deleteByNoteId(testNoteId2)
        val allItems = noteCheckDao.getAll().first()
        assertTrue(allItems.isEmpty())
    }

    @Test
    fun getAll_afterInserts() = runTest {
        noteCheckDao.upsert(NoteItemEntity(id = null, noteId = testNoteId1, content = "All Item 1", isCheck = false))
        noteCheckDao.upsert(NoteItemEntity(id = null, noteId = testNoteId2, content = "All Item 2", isCheck = false))
        val allItems = noteCheckDao.getAll().first()
        assertEquals(2, allItems.size)
    }

    @Test
    fun deleteItem() = runTest {
        val item = NoteItemEntity(id = null, noteId = testNoteId1, content = "To Delete", isCheck = false)
        val id = noteCheckDao.upsert(item)
        assertNotNull(noteCheckDao.get(id).first())

        noteCheckDao.delete(id)
        assertNull(noteCheckDao.get(id).first())
    }

    @Test
    fun deleteCheckedItems_deletesOnlyChecked() = runTest {
        noteCheckDao.upsert(NoteItemEntity(id = null, noteId = testNoteId1, content = "Checked Item 1", isCheck = true))
        noteCheckDao.upsert(
            NoteItemEntity(id = null, noteId = testNoteId1, content = "Unchecked Item 1", isCheck = false),
        )
        noteCheckDao.upsert(NoteItemEntity(id = null, noteId = testNoteId1, content = "Checked Item 2", isCheck = true))

        noteCheckDao.upsert(
            NoteItemEntity(id = null, noteId = testNoteId2, content = "Checked Item Note 2", isCheck = true),
        ) // Different noteId

        noteCheckDao.deleteCheckedItems(testNoteId1)

        val remainingItemsNote1 = noteCheckDao.getByNoteId(testNoteId1).first()
        assertEquals(1, remainingItemsNote1.size)
        assertEquals("Unchecked Item 1", remainingItemsNote1.first().content)

        val itemsNote2 = noteCheckDao.getByNoteId(testNoteId2).first()
        assertEquals(1, itemsNote2.size, "Should not delete checked items from other notes")
    }

    @Test
    fun deleteByNoteId_deletesAllForNote() = runTest {
        noteCheckDao.upsert(NoteItemEntity(id = null, noteId = testNoteId1, content = "Item A", isCheck = false))
        noteCheckDao.upsert(NoteItemEntity(id = null, noteId = testNoteId1, content = "Item B", isCheck = true))
        noteCheckDao.upsert(
            NoteItemEntity(id = null, noteId = testNoteId2, content = "Item C (other note)", isCheck = false),
        )

        noteCheckDao.deleteByNoteId(testNoteId1)

        assertTrue(noteCheckDao.getByNoteId(testNoteId1).first().isEmpty())
        assertEquals(1, noteCheckDao.getByNoteId(testNoteId2).first().size, "Should not delete items from other notes")
    }
}
