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

import com.mshdabiola.data.doubles.TestNoteCheckDao
import com.mshdabiola.data.repository.RealNoteItemRepository
import com.mshdabiola.model.note.NoteItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteItemRepositoryTest {

    private lateinit var noteCheckDao: TestNoteCheckDao
    private lateinit var repository: RealNoteItemRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        noteCheckDao = TestNoteCheckDao()
        repository = RealNoteItemRepository(noteCheckDao, testDispatcher)
    }

    private fun createTestNoteItem(
        id: Long = -1L, // Will be handled by TestNoteCheckDao to assign new ID if -1L
        noteId: Long,
        content: String = "Test item content",
        isCheck: Boolean = false
    ): NoteItem {
        return NoteItem(id = id, noteId = noteId, content = content, isCheck = isCheck)
    }

    @Test
    fun `upsert new item returns valid id and adds item`() = runTest(testDispatcher) {
        val newItem = createTestNoteItem(noteId = 1L)
        val id = repository.upsert(newItem)

        assertTrue("Generated ID should be positive", id > 0L)
        val insertedItem = repository.get(id).first()
        assertNotNull("Inserted item should not be null", insertedItem)
        assertEquals(id, insertedItem?.id)
        assertEquals(1L, insertedItem?.noteId)
        assertEquals("Test item content", insertedItem?.content)
        assertFalse(insertedItem?.isCheck ?: true)
    }

    @Test
    fun `upsert existing item updates it`() = runTest(testDispatcher) {
        val initialItem = createTestNoteItem(noteId = 1L, content = "Initial content")
        val id = repository.upsert(initialItem)

        val updatedItem = createTestNoteItem(id = id, noteId = 1L, content = "Updated content", isCheck = true)
        val updatedId = repository.upsert(updatedItem)

        assertEquals("Updated ID should match original ID", id, updatedId)
        val fetchedItem = repository.get(id).first()
        assertNotNull(fetchedItem)
        assertEquals("Updated content", fetchedItem?.content)
        assertTrue(fetchedItem?.isCheck ?: false)
    }

    @Test
    fun `upserts_insertsMultipleItems_andReturnsTheirIds`() = runTest(testDispatcher) {
        val item1 = createTestNoteItem(noteId = 1L, content = "Item 1")
        val item2 = createTestNoteItem(noteId = 1L, content = "Item 2")
        val itemsToInsert = listOf(item1, item2)

        val generatedIds = repository.upserts(itemsToInsert)
        assertEquals("Should return 2 generated IDs", 2, generatedIds.size)
        assertTrue("All generated IDs should be positive", generatedIds.all { it > 0L })

        val allItems = repository.getByNoteId(1L).first()
        assertEquals("Should have 2 items for noteId 1", 2, allItems.size)
    }

    @Test
    fun `delete removes item`() = runTest(testDispatcher) {
        val item = createTestNoteItem(noteId = 1L)
        val id = repository.upsert(item)
        assertNotNull(repository.get(id).first())

        repository.delete(id)
        assertNull(repository.get(id).first())
    }

    @Test
    fun `deleteCheckedItems removes only checked items for a noteId`() = runTest(testDispatcher) {
        val noteId = 1L
        val checkedItem1 = repository.upsert(createTestNoteItem(noteId = noteId, content = "Checked 1", isCheck = true))
        val uncheckedItem = repository.upsert(createTestNoteItem(noteId = noteId, content = "Unchecked", isCheck = false))
        val checkedItem2 = repository.upsert(createTestNoteItem(noteId = noteId, content = "Checked 2", isCheck = true))
        repository.upsert(createTestNoteItem(noteId = 2L, content = "Other note", isCheck = true)) // Different noteId

        repository.deleteCheckedItems(noteId)

        assertNull(repository.get(checkedItem1).first())
        assertNotNull(repository.get(uncheckedItem).first())
        assertNull(repository.get(checkedItem2).first())

        val remainingItemsForNote1 = repository.getByNoteId(noteId).first()
        assertEquals("Only 1 (unchecked) item should remain for noteId 1", 1, remainingItemsForNote1.size)
        assertFalse(remainingItemsForNote1.first().isCheck)

        assertNotNull(repository.getByNoteId(2L).first().first { it.content == "Other note" }) // Ensure other note's item is not deleted
    }

    @Test
    fun `deleteByNoteId removes all items for that noteId`() = runTest(testDispatcher) {
        val noteId1 = 1L
        val noteId2 = 2L
        repository.upsert(createTestNoteItem(noteId = noteId1, content = "Item 1 Note 1"))
        repository.upsert(createTestNoteItem(noteId = noteId1, content = "Item 2 Note 1"))
        repository.upsert(createTestNoteItem(noteId = noteId2, content = "Item 1 Note 2"))

        repository.deleteByNoteId(noteId1)

        assertTrue(repository.getByNoteId(noteId1).first().isEmpty())
        assertFalse(repository.getByNoteId(noteId2).first().isEmpty())
        assertEquals(1, repository.getAll().first().size)
    }

    @Test
    fun `getAll returns empty list initially`() = runTest(testDispatcher) {
        val items = repository.getAll().first()
        assertTrue("Initially, getAll should return an empty list", items.isEmpty())
    }

    @Test
    fun `getAll returns all inserted items`() = runTest(testDispatcher) {
        repository.upsert(createTestNoteItem(noteId = 1L))
        repository.upsert(createTestNoteItem(noteId = 2L))
        assertEquals(2, repository.getAll().first().size)
    }

    @Test
    fun `get returns null for non-existent id`() = runTest(testDispatcher) {
        assertNull(repository.get(999L).first())
    }

    @Test
    fun `get returns correct item`() = runTest(testDispatcher) {
        val item = createTestNoteItem(noteId = 1L)
        val id = repository.upsert(item)
        val fetched = repository.get(id).first()
        assertNotNull(fetched)
        assertEquals(id, fetched?.id)
        assertEquals(1L, fetched?.noteId)
    }

    @Test
    fun `getByNoteId returns correct items for specific note`() = runTest(testDispatcher) {
        repository.upsert(createTestNoteItem(noteId = 1L, content = "A"))
        repository.upsert(createTestNoteItem(noteId = 1L, content = "B"))
        repository.upsert(createTestNoteItem(noteId = 2L, content = "C"))

        val itemsForNote1 = repository.getByNoteId(1L).first()
        assertEquals(2, itemsForNote1.size)
        assertTrue(itemsForNote1.all { it.noteId == 1L })

        val itemsForNote2 = repository.getByNoteId(2L).first()
        assertEquals(1, itemsForNote2.size)
        assertEquals("C", itemsForNote2.first().content)

        assertTrue(repository.getByNoteId(3L).first().isEmpty())
    }
}
