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

import com.mshdabiola.data.doubles.TestNoteVoiceDao
import com.mshdabiola.data.repository.RealNoteVoiceRepository
import com.mshdabiola.model.note.NoteVoice
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteVoiceRepositoryTest {

    private lateinit var noteVoiceDao: TestNoteVoiceDao
    private lateinit var repository: RealNoteVoiceRepository
    private val testDispatcher = StandardTestDispatcher()
    private var nextVoiceIdCounter = 1L // To ensure unique IDs for new voices

    @Before
    fun setUp() {
        noteVoiceDao = TestNoteVoiceDao()
        repository = RealNoteVoiceRepository(noteVoiceDao, testDispatcher)
        nextVoiceIdCounter = 1L // Reset for each test
    }

    private fun createTestNoteVoice(
        id: Long? = null, // If null, a new unique ID is generated
        noteId: Long,
        path: String = "test/path/voice.mp3",
        length: Long = 120000L // e.g., 2 minutes
    ): NoteVoice {
        val voiceId = id ?: nextVoiceIdCounter++
        return NoteVoice(id = voiceId, noteId = noteId, path = path, length = length)
    }

    @Test
    fun `upsert new voice returns provided id and adds voice`() = runTest(testDispatcher) {
        val newVoice = createTestNoteVoice(noteId = 1L) // ID will be 1L
        val returnedId = repository.upsert(newVoice)

        assertEquals("Returned ID should match provided ID", newVoice.id, returnedId)
        val insertedVoice = repository.get(newVoice.id).first()
        assertNotNull("Inserted Voice should not be null", insertedVoice)
        assertEquals(newVoice.id, insertedVoice?.id)
        assertEquals(1L, insertedVoice?.noteId)
        assertEquals("test/path/voice.mp3", insertedVoice?.path)
        assertEquals(120000L, insertedVoice?.length)
    }

    @Test
    fun `upsert existing voice updates it`() = runTest(testDispatcher) {
        val initialVoice = createTestNoteVoice(noteId = 1L) // ID will be 1L
        repository.upsert(initialVoice)

        val updatedVoice = createTestNoteVoice(id = initialVoice.id, noteId = 1L, path = "updated/path/voice.ogg", length = 60000L)
        val returnedId = repository.upsert(updatedVoice)

        assertEquals("Returned ID should match original ID", initialVoice.id, returnedId)
        val fetchedVoice = repository.get(initialVoice.id).first()
        assertNotNull(fetchedVoice)
        assertEquals("updated/path/voice.ogg", fetchedVoice?.path)
        assertEquals(60000L, fetchedVoice?.length)
    }

    @Test
    fun `upserts_insertsMultipleVoices_andReturnsTheirIds`() = runTest(testDispatcher) {
        val voice1 = createTestNoteVoice(noteId = 1L) // ID 1L
        val voice2 = createTestNoteVoice(noteId = 2L) // ID 2L
        val voicesToInsert = listOf(voice1, voice2)

        val returnedIds = repository.upserts(voicesToInsert)
        assertEquals("Should return 2 IDs", 2, returnedIds.size)
        assertEquals(voice1.id, returnedIds[0])
        assertEquals(voice2.id, returnedIds[1])

        val allVoices = repository.getAll().first()
        assertEquals("Should have 2 voices in DB after bulk upsert", 2, allVoices.size)
    }

    @Test
    fun `delete removes voice`() = runTest(testDispatcher) {
        val voice = createTestNoteVoice(noteId = 1L)
        repository.upsert(voice)
        assertNotNull(repository.get(voice.id).first())

        repository.delete(voice.id)
        assertNull(repository.get(voice.id).first())
    }

    @Test
    fun `deleteByNoteId removes voices for that note`() = runTest(testDispatcher) {
        val voice1Note1 = createTestNoteVoice(noteId = 1L) // ID 1
        val voice2Note1 = createTestNoteVoice(noteId = 1L) // ID 2
        val voice1Note2 = createTestNoteVoice(noteId = 2L) // ID 3
        repository.upserts(listOf(voice1Note1, voice2Note1, voice1Note2))

        repository.deleteByNoteId(1L)

        assertNull(repository.get(voice1Note1.id).first())
        assertNull(repository.get(voice2Note1.id).first())
        assertNotNull(repository.get(voice1Note2.id).first())
        assertEquals(1, repository.getAll().first().size)
    }

    @Test
    fun `getAll returns empty list initially`() = runTest(testDispatcher) {
        val voices = repository.getAll().first()
        assertTrue("Initially, getAll should return an empty list", voices.isEmpty())
    }

    @Test
    fun `getAll returns inserted voices`() = runTest(testDispatcher) {
        repository.upsert(createTestNoteVoice(noteId = 1L))
        repository.upsert(createTestNoteVoice(noteId = 2L))
        assertEquals(2, repository.getAll().first().size)
    }

    @Test
    fun `get returns null for non-existent id`() = runTest(testDispatcher) {
        assertNull(repository.get(999L).first())
    }

    @Test
    fun `get returns correct voice`() = runTest(testDispatcher) {
        val voice = createTestNoteVoice(noteId = 1L)
        repository.upsert(voice)
        val fetched = repository.get(voice.id).first()
        assertNotNull(fetched)
        assertEquals(voice.id, fetched?.id)
        assertEquals(1L, fetched?.noteId)
    }

    @Test
    fun `getByNoteId returns correct voices for specific note`() = runTest(testDispatcher) {
        val voice1 = createTestNoteVoice(noteId = 1L, path = "path1")
        val voice2 = createTestNoteVoice(noteId = 1L, path = "path2")
        val voice3 = createTestNoteVoice(noteId = 2L, path = "path3")
        repository.upserts(listOf(voice1, voice2, voice3))

        val voicesForNote1 = repository.getByNoteId(1L).first()
        assertEquals(2, voicesForNote1.size)
        assertTrue(voicesForNote1.all { it.noteId == 1L })
        assertTrue(voicesForNote1.any { it.path == "path1" })
        assertTrue(voicesForNote1.any { it.path == "path2" })

        val voicesForNote2 = repository.getByNoteId(2L).first()
        assertEquals(1, voicesForNote2.size)
        assertTrue(voicesForNote2.all { it.noteId == 2L })
        assertEquals("path3", voicesForNote2.first().path)

        val voicesForNote3 = repository.getByNoteId(3L).first()
        assertTrue(voicesForNote3.isEmpty())
    }
}
