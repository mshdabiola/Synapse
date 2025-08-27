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

import com.mshdabiola.data.doubles.TestNoteLabelDao
import com.mshdabiola.data.repository.RealNoteLabelRepository
import com.mshdabiola.model.note.NoteLabelCrossRef // Model from com.mshdabiola.model.note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteLabelRepositoryTest {

    private lateinit var noteLabelDao: TestNoteLabelDao
    private lateinit var repository: RealNoteLabelRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        noteLabelDao = TestNoteLabelDao()
        repository = RealNoteLabelRepository(noteLabelDao, testDispatcher)
    }

    private fun createTestCrossRef(noteId: Long, labelId: Long): NoteLabelCrossRef {
        return NoteLabelCrossRef(noteId = noteId, labelId = labelId)
    }

    @Test
    fun `upsert new crossRef adds it`() = runTest(testDispatcher) {
        val newCrossRef = createTestCrossRef(noteId = 1L, labelId = 10L)
        val result = repository.upsert(newCrossRef)

        assertEquals(
            "Upsert should return 1L on success (as per TestNoteLabelDao)",
            1L,
            result,
        )
        val allCrossRefs = repository.getAll().first()
        assertTrue(
            "List should contain the upserted crossRef",
            allCrossRefs.contains(newCrossRef),
        )
        assertEquals(1, allCrossRefs.size)
    }

    @Test
    fun `upsert existing crossRef effectively replaces it`() = runTest(testDispatcher) {
        val initialCrossRef = createTestCrossRef(noteId = 1L, labelId = 10L)
        repository.upsert(initialCrossRef) // First insert

        // Upserting the same crossRef again (TestNoteLabelDao handles replacement)
        val result = repository.upsert(initialCrossRef)
        assertEquals("Upsert should return 1L on success", 1L, result)

        val allCrossRefs = repository.getAll().first()
        assertEquals(
            "List should still contain only one instance of the crossRef",
            1,
            allCrossRefs.size,
        )
        assertTrue(allCrossRefs.contains(initialCrossRef))
    }

    @Test
    fun `upserts_insertsMultipleUniqueCrossRefs`() = runTest(testDispatcher) {
        val crossRef1 = createTestCrossRef(noteId = 1L, labelId = 10L)
        val crossRef2 = createTestCrossRef(noteId = 1L, labelId = 11L)
        val crossRef3 = createTestCrossRef(noteId = 2L, labelId = 10L)
        val toInsert = listOf(crossRef1, crossRef2, crossRef3)

        val results = repository.upserts(toInsert)
        assertEquals(
            "Should return a list of 1L for each successful upsert",
            listOf(1L, 1L, 1L),
            results,
        )

        val allCrossRefs = repository.getAll().first()
        assertEquals("Should have 3 crossRefs in DB", 3, allCrossRefs.size)
        assertTrue(allCrossRefs.containsAll(toInsert))
    }

    @Test
    fun `upserts_handlesDuplicateEntriesInInputListCorrectly`() = runTest(testDispatcher) {
        val crossRef1 = createTestCrossRef(noteId = 1L, labelId = 10L)
        val crossRef2 = createTestCrossRef(noteId = 1L, labelId = 10L) // Duplicate
        val toInsert = listOf(crossRef1, crossRef2)

        val results = repository.upserts(toInsert)
        // TestNoteLabelDao's upsert for a duplicate will still return 1L as it's an "insert or replace"
        assertEquals(listOf(1L, 1L), results)

        val allCrossRefs = repository.getAll().first()
        // Due to the nature of the Set-like behavior for composite keys in the DAO
        assertEquals("Should only have 1 unique crossRef in DB", 1, allCrossRefs.size)
        assertTrue(allCrossRefs.contains(crossRef1))
    }

    @Test
    fun `deleteByNoteId removes all crossRefs for that noteId`() = runTest(testDispatcher) {
        val crossRef1N1 = repository.upsert(createTestCrossRef(noteId = 1L, labelId = 10L))
        val crossRef2N1 = repository.upsert(createTestCrossRef(noteId = 1L, labelId = 11L))
        val crossRef1N2 = repository.upsert(createTestCrossRef(noteId = 2L, labelId = 10L))

        repository.deleteByNoteId(1L) // Parameter name in repo is 'id', but it means noteId

        assertTrue(repository.getByNoteId(1L).first().isEmpty())
        assertEquals(1, repository.getByNoteId(2L).first().size)
        assertEquals(1, repository.getAll().first().size)
    }

    @Test
    fun `deleteByNoteIdAndLabelId removes specific crossRef`() = runTest(testDispatcher) {
        val cr1 = createTestCrossRef(noteId = 1L, labelId = 10L)
        val cr2 = createTestCrossRef(noteId = 1L, labelId = 11L)
        repository.upserts(listOf(cr1, cr2))

        repository.deleteByNoteIdAndLabelId(noteId = 1L, labelId = 10L)

        val remainingForNote1 = repository.getByNoteId(1L).first()
        assertEquals(1, remainingForNote1.size)
        assertTrue(remainingForNote1.contains(cr2))
        assertEquals(1, repository.getAll().first().size)
    }

    @Test
    fun `getAll returns empty list initially`() = runTest(testDispatcher) {
        assertTrue(repository.getAll().first().isEmpty())
    }

    @Test
    fun `getAll returns all inserted crossRefs`() = runTest(testDispatcher) {
        val cr1 = createTestCrossRef(noteId = 1L, labelId = 10L)
        val cr2 = createTestCrossRef(noteId = 2L, labelId = 11L)
        repository.upserts(listOf(cr1, cr2))
        assertEquals(2, repository.getAll().first().size)
    }

    @Test
    fun `getByNoteId returns correct crossRefs`() = runTest(testDispatcher) {
        val note1Label10CrossRef = createTestCrossRef(noteId = 1L, labelId = 10L)
        val note1Label11CrossRef = createTestCrossRef(noteId = 1L, labelId = 11L)
        val note2Label10CrossRef = createTestCrossRef(noteId = 2L, labelId = 10L)
        repository.upserts(listOf(note1Label10CrossRef, note1Label11CrossRef, note2Label10CrossRef))

        val forNote1 = repository.getByNoteId(1L).first()
        assertEquals(2, forNote1.size)
        assertTrue(forNote1.containsAll(listOf(note1Label10CrossRef, note1Label11CrossRef)))

        val forNote2 = repository.getByNoteId(2L).first()
        assertEquals(1, forNote2.size)
        assertTrue(forNote2.contains(note2Label10CrossRef))

        assertTrue(repository.getByNoteId(3L).first().isEmpty())
    }

    @Test
    fun `getByLabelId returns correct crossRefs`() = runTest(testDispatcher) {
        val note1Label10CrossRef = createTestCrossRef(noteId = 1L, labelId = 10L)
        val note1Label11CrossRef = createTestCrossRef(noteId = 1L, labelId = 11L)
        val note2Label10CrossRef = createTestCrossRef(noteId = 2L, labelId = 10L)
        repository.upserts(listOf(note1Label10CrossRef, note1Label11CrossRef, note2Label10CrossRef))

        val forLabel10 = repository.getByLabelId(10L).first()
        assertEquals(2, forLabel10.size)
        assertTrue(forLabel10.containsAll(listOf(note1Label10CrossRef, note2Label10CrossRef)))

        val forLabel11 = repository.getByLabelId(11L).first()
        assertEquals(1, forLabel11.size)
        assertTrue(forLabel11.contains(note1Label11CrossRef))

        assertTrue(repository.getByLabelId(12L).first().isEmpty())
    }

    @Test
    fun `getByNoteIds returns correct crossRefs for multiple note IDs`() = runTest(testDispatcher) {
        val note1Label10CrossRef = createTestCrossRef(noteId = 1L, labelId = 10L)
        val note1Label11CrossRef = createTestCrossRef(noteId = 1L, labelId = 11L)
        val note2Label10CrossRef = createTestCrossRef(noteId = 2L, labelId = 10L)
        val note3Label12CrossRef = createTestCrossRef(noteId = 3L, labelId = 12L)
        repository.upserts(
            listOf(
                note1Label10CrossRef,
                note1Label11CrossRef,
                note2Label10CrossRef,
                note3Label12CrossRef,
            ),
        )

        val forNotes1And2 = repository.getByNoteIds(setOf(1L, 2L)).first()
        assertEquals(3, forNotes1And2.size)
        assertTrue(
            forNotes1And2.containsAll(
                listOf(
                    note1Label10CrossRef,
                    note1Label11CrossRef,
                    note2Label10CrossRef,
                ),
            ),
        )

        val forNotes1And3 = repository.getByNoteIds(setOf(1L, 3L)).first()
        assertEquals(3, forNotes1And3.size)
        assertTrue(
            forNotes1And3.containsAll(
                listOf(
                    note1Label10CrossRef,
                    note1Label11CrossRef,
                    note3Label12CrossRef,
                ),
            ),
        )

        val forNote4 = repository.getByNoteIds(setOf(4L)).first()
        assertTrue(forNote4.isEmpty())

        val forEmptySet = repository.getByNoteIds(emptySet()).first()
        assertTrue(forEmptySet.isEmpty())
    }
}
