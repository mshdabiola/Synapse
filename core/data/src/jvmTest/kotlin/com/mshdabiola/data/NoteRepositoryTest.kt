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

import com.mshdabiola.data.doubles.TestNoteDao
import com.mshdabiola.data.repository.RealNoteRepository
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NotePad
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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NoteRepositoryTest {

    private lateinit var noteDao: TestNoteDao
    private lateinit var repository: RealNoteRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        noteDao = TestNoteDao()
        repository = RealNoteRepository(noteDao, testDispatcher)
    }

    // Helper to create a NotePad with default values for testing
    private fun createTestNotePad(
        id: Long = 0L, // TestNoteDao will assign ID if 0 or null
        title: String = "Test Title",
        detail: String = "Test Detail",
        noteCategory: NoteCategory = NoteCategory.NOTE,
        color: Int = 0,
        isPin: Boolean = false,
    ): NotePad {
        return NotePad(
            id = if (id == 0L) -1 else id,
            title = title,
            detail = detail,
            noteCategory = noteCategory,
            editDate = System.currentTimeMillis(),
            color = color,
            isPin = isPin,
            // Add other essential NotePad fields if their defaults aren't suitable for most tests
        )
    }

    @Test
    fun `upsert new notepad returns valid id and adds notepad`() = runTest(testDispatcher) {
        val newNotePad = createTestNotePad(title = "New NotePad", detail = "Content")
        val id = repository.upsert(newNotePad)

        assertTrue("Generated ID should be positive", id > 0L)
        val insertedNotePad = repository.get(id).first()
        assertNotNull("Inserted NotePad should not be null", insertedNotePad)
        assertEquals("New NotePad", insertedNotePad?.title)
        assertEquals("Content", insertedNotePad?.detail)
    }

    @Test
    fun `upsert existing notepad updates it`() = runTest(testDispatcher) {
        val initialNotePad = createTestNotePad(title = "Initial NotePad", detail = "Initial Content")
        val id = repository.upsert(initialNotePad) // First insert

        val updatedNotePad = createTestNotePad(id = id, title = "Updated NotePad", detail = "Updated Content")
        val updatedId = repository.upsert(updatedNotePad) // Update

        assertEquals("Updated ID should match original ID", id, updatedId)

        val fetchedNotePad = repository.get(id).first()
        assertNotNull("Fetched NotePad should not be null after update", fetchedNotePad)
        assertEquals("Updated NotePad", fetchedNotePad?.title)
        assertEquals("Updated Content", fetchedNotePad?.detail)
    }

    @Test
    fun `getAll returns empty list initially`() = runTest(testDispatcher) {
        val notePads = repository.getAll().first()
        assertTrue("Initially, getAll should return an empty list", notePads.isEmpty())
    }

    @Test
    fun `getAll returns inserted notepads`() = runTest(testDispatcher) {
        val notePad1 = createTestNotePad(title = "NotePad 1", detail = "Content 1")
        val notePad2 = createTestNotePad(title = "NotePad 2", detail = "Content 2")
        repository.upsert(notePad1)
        repository.upsert(notePad2)

        val notePads = repository.getAll().first()
        assertEquals("getAll should return 2 notepads", 2, notePads.size)
    }

    @Test
    fun `get returns null for non-existent id`() = runTest(testDispatcher) {
        val notePad = repository.get(999L).first() // An ID that surely doesn't exist
        assertNull("get should return null for a non-existent ID", notePad)
    }

    @Test
    fun `get returns correct notepad`() = runTest(testDispatcher) {
        val notePad = createTestNotePad(title = "Test NotePad", detail = "Test Content")
        val id = repository.upsert(notePad)

        val fetchedNotePad = repository.get(id).first()
        assertNotNull("Fetched NotePad should not be null", fetchedNotePad)
        assertEquals("ID should match", id, fetchedNotePad?.id)
        assertEquals("Test NotePad", fetchedNotePad?.title)
    }

    @Test
    fun `delete removes notepad`() = runTest(testDispatcher) {
        val notePad = createTestNotePad(title = "To Delete", detail = "Delete Content")
        val id = repository.upsert(notePad)

        var fetchedNotePad = repository.get(id).first()
        assertNotNull("NotePad should exist before delete", fetchedNotePad)

        repository.delete(id)
        fetchedNotePad = repository.get(id).first()
        assertNull("NotePad should be null after delete", fetchedNotePad)
    }

    @Test
    fun `upserts_insertsMultipleNotes_andReturnsTheirIds`() = runTest(testDispatcher) {
        val notePad1 = createTestNotePad(title = "Bulk 1")
        val notePad2 = createTestNotePad(title = "Bulk 2")
        val notesToInsert = listOf(notePad1, notePad2)

        val generatedIds = repository.upserts(notesToInsert)
        assertEquals("Should return 2 generated IDs", 2, generatedIds.size)
        assertTrue("All generated IDs should be positive", generatedIds.all { it > 0L })

        val allNotes = repository.getAll().first()
        assertEquals("Should have 2 notes in DB after bulk upsert", 2, allNotes.size)
    }

    @Test
    fun `deleteIds_removesSpecifiedNotes`() = runTest(testDispatcher) {
        val id1 = repository.upsert(createTestNotePad(title = "Delete ID 1"))
        val id2 = repository.upsert(createTestNotePad(title = "Keep Me"))
        val id3 = repository.upsert(createTestNotePad(title = "Delete ID 3"))

        repository.deleteIds(setOf(id1, id3))

        assertNull("NotePad with id1 should be deleted", repository.get(id1).first())
        assertNotNull("NotePad with id2 should still exist", repository.get(id2).first())
        assertNull("NotePad with id3 should be deleted", repository.get(id3).first())
        assertEquals("Only 1 note should remain", 1, repository.getAll().first().size)
    }

    @Test
    fun `deleteTrash_removesNotesInCategoryTrash`() = runTest(testDispatcher) {
        repository.upsert(createTestNotePad(title = "Trash Note 1", noteCategory = NoteCategory.TRASH))

        repository.upsert(createTestNotePad(title = "Normal Note 1", noteCategory = NoteCategory.NOTE))
        repository.upsert(createTestNotePad(title = "Trash Note 2", noteCategory = NoteCategory.TRASH))

        repository.deleteTrash()

        val remainingNotes = repository.getAll().first()
        assertEquals(
            "Only 1 note should remain after deleteTrash",
            1,
            remainingNotes.size,
        )
        assertEquals(
            "Remaining note should be of type NOTE",
            NoteCategory.NOTE,
            remainingNotes.first().noteCategory,
        )
        assertTrue(
            "No TRASH notes should remain",
            remainingNotes.none { it.noteCategory == NoteCategory.TRASH },
        )
    }

    @Test
    fun `getByNoteType_returnsCorrectNotes`() = runTest(testDispatcher) {
        repository.upsert(createTestNotePad(title = "Note Type NOTE 1", noteCategory = NoteCategory.NOTE))
        repository.upsert(createTestNotePad(title = "Note Type ARCHIVE 1", noteCategory = NoteCategory.ARCHIVE))
        repository.upsert(createTestNotePad(title = "Note Type NOTE 2", noteCategory = NoteCategory.NOTE))

        val notesOfTypeNote = repository.getByNoteType(NoteCategory.NOTE).first()
        assertEquals("Should be 2 notes of type NOTE", 2, notesOfTypeNote.size)
        assertTrue(
            "All fetched notes should be of type NOTE",
            notesOfTypeNote.all { it.noteCategory == NoteCategory.NOTE },
        )

        val notesOfTypeArchive = repository.getByNoteType(NoteCategory.ARCHIVE).first()
        assertEquals("Should be 1 note of type ARCHIVE", 1, notesOfTypeArchive.size)
        assertEquals(
            "Fetched note should be of type ARCHIVE",
            NoteCategory.ARCHIVE,
            notesOfTypeArchive.first().noteCategory,
        )

        val notesOfTypeTrash = repository.getByNoteType(NoteCategory.TRASH).first()
        assertTrue("Should be 0 notes of type TRASH", notesOfTypeTrash.isEmpty())
    }

    @Test
    fun `getByNoteIds_returnsSpecifiedNotes`() = runTest(testDispatcher) {
        val id1 = repository.upsert(createTestNotePad(title = "Get ID 1"))
        repository.upsert(createTestNotePad(title = "Ignore Me")) // id2
        val id3 = repository.upsert(createTestNotePad(title = "Get ID 3"))

        val retrievedNotes = repository.getByNoteIds(setOf(id1, id3)).first()
        assertEquals("Should retrieve 2 notes by IDs", 2, retrievedNotes.size)
        assertTrue(
            "Retrieved notes should contain NotePad with id1",
            retrievedNotes.any { it.id == id1 && it.title == "Get ID 1" },
        )
        assertTrue(
            "Retrieved notes should contain NotePad with id3",
            retrievedNotes.any { it.id == id3 && it.title == "Get ID 3" },
        )
        assertTrue(
            "Retrieved notes should not contain the ignored note",
            retrievedNotes.none { it.title == "Ignore Me" },
        )
    }

    @Test
    fun `updateColorForIds_updatesColorForSpecifiedNotes`() = runTest(testDispatcher) {
        val initialColor = 0
        val updatedColor = 5
        val id1 = repository.upsert(createTestNotePad(title = "Color Change 1", color = initialColor))
        val id2 = repository.upsert(createTestNotePad(title = "Color Keep", color = initialColor))
        val id3 = repository.upsert(createTestNotePad(title = "Color Change 2", color = initialColor))

        repository.updateColorForIds(setOf(id1, id3), updatedColor)

        val note1 = repository.get(id1).first()
        val note2 = repository.get(id2).first()
        val note3 = repository.get(id3).first()

        assertNotNull(note1)
        assertEquals(updatedColor, note1?.color, "Note 1 color should be updated")
        assertNotNull(note2)
        assertEquals(initialColor, note2?.color, "Note 2 color should not be updated")
        assertNotNull(note3)
        assertEquals(updatedColor, note3?.color, "Note 3 color should be updated")
    }

    @Test
    fun `updatePinForIds_updatesPinStatusForSpecifiedNotes`() = runTest(testDispatcher) {
        val id1 = repository.upsert(createTestNotePad(title = "Pin Me 1", isPin = false))
        val id2 = repository.upsert(createTestNotePad(title = "Keep Unpinned", isPin = false))
        val id3 = repository.upsert(createTestNotePad(title = "Pin Me 2", isPin = false))

        repository.updatePinForIds(setOf(id1, id3), true)

        val note1 = repository.get(id1).first()
        val note2 = repository.get(id2).first()
        val note3 = repository.get(id3).first()

        assertNotNull(note1)
        assertTrue(note1!!.isPin, "Note 1 should be pinned")
        assertNotNull(note2)
        assertFalse(note2!!.isPin, "Note 2 should remain unpinned")
        assertNotNull(note3)
        assertTrue(note3!!.isPin, "Note 3 should be pinned")

        // Test unpinning
        repository.updatePinForIds(setOf(id1), false)
        val unpinnedNote1 = repository.get(id1).first()
        assertNotNull(unpinnedNote1)
        assertFalse(unpinnedNote1!!.isPin, "Note 1 should be unpinned")
    }

    @Test
    fun `updateNoteTypeForIds_updatesNoteTypeForSpecifiedNotes`() = runTest(testDispatcher) {
        val initialCategory = NoteCategory.NOTE
        val archiveCategory = NoteCategory.ARCHIVE
        val trashCategory = NoteCategory.TRASH

        val id1 = repository.upsert(createTestNotePad(title = "Archive Me", noteCategory = initialCategory))
        val id2 = repository.upsert(createTestNotePad(title = "Keep Normal", noteCategory = initialCategory))
        val id3 = repository.upsert(createTestNotePad(title = "Trash Me", noteCategory = initialCategory))

        // Archive id1
        repository.updateNoteTypeForIds(setOf(id1), archiveCategory)
        val note1Archived = repository.get(id1).first()
        assertNotNull(note1Archived)
        assertEquals(archiveCategory, note1Archived?.noteCategory, "Note 1 should be archived")

        // Trash id3
        repository.updateNoteTypeForIds(setOf(id3), trashCategory)
        val note3Trashed = repository.get(id3).first()
        assertNotNull(note3Trashed)
        assertEquals(trashCategory, note3Trashed?.noteCategory, "Note 3 should be trashed")

        // Check id2 remained unchanged
        val note2Unchanged = repository.get(id2).first()
        assertNotNull(note2Unchanged)
        assertEquals(initialCategory, note2Unchanged?.noteCategory, "Note 2 category should remain normal")
    }
}
