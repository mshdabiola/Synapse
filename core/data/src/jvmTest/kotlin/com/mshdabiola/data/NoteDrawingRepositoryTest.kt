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

import com.mshdabiola.data.doubles.TestNoteDrawingDao
import com.mshdabiola.data.repository.RealNoteDrawingRepository
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.Path
import com.mshdabiola.model.note.PenProperties
import com.mshdabiola.model.note.Point
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
class NoteDrawingRepositoryTest {

    private lateinit var noteDrawingDao: TestNoteDrawingDao
    private lateinit var repository: RealNoteDrawingRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        noteDrawingDao = TestNoteDrawingDao()
        repository = RealNoteDrawingRepository(noteDrawingDao, testDispatcher)
    }

    private fun createTestPath(pointCount: Int = 2): Path {
        val points = List(pointCount) { Point(it.toFloat(), it.toFloat() * 2) }
        return Path(points = points, penProperties = PenProperties())
    }

    private fun createTestNoteDrawing(
        id: Long = -1L,
        noteId: Long,
        pathList: List<Path> = listOf(createTestPath()),
    ): NoteDrawing {
        return NoteDrawing(id = id, noteId = noteId, paths = pathList)
    }

    @Test
    fun `upsert new drawing returns valid id and adds drawing`() = runTest(testDispatcher) {
        val newDrawing = createTestNoteDrawing(noteId = 1L)
        val id = repository.upsert(newDrawing)

        assertTrue("Generated ID should be positive", id > 0L)
        val insertedDrawing = repository.get(id).first()
        assertNotNull("Inserted Drawing should not be null", insertedDrawing)
        assertEquals(id, insertedDrawing?.id)
        assertEquals(1L, insertedDrawing?.noteId)
        assertEquals(1, insertedDrawing?.paths?.size) // Assuming default one path
        assertEquals(2, insertedDrawing?.paths?.first()?.points?.size) // Assuming default 2 points
    }

    @Test
    fun `upsert existing drawing updates it`() = runTest(testDispatcher) {
        val initialDrawing = createTestNoteDrawing(noteId = 1L)
        val id = repository.upsert(initialDrawing)

        val updatedPath = createTestPath(pointCount = 3)
        val updatedDrawing = createTestNoteDrawing(id = id, noteId = 1L, pathList = listOf(updatedPath))
        val updatedId = repository.upsert(updatedDrawing)

        assertEquals("Updated ID should match original ID", id, updatedId)
        val fetchedDrawing = repository.get(id).first()
        assertNotNull(fetchedDrawing)
        assertEquals(1, fetchedDrawing?.paths?.size)
        assertEquals(3, fetchedDrawing?.paths?.first()?.points?.size)
    }

    @Test
    fun `upserts_insertsMultipleDrawings_andReturnsTheirIds`() = runTest(testDispatcher) {
        val drawing1 = createTestNoteDrawing(noteId = 1L)
        val drawing2 = createTestNoteDrawing(noteId = 2L)
        val drawingsToInsert = listOf(drawing1, drawing2)

        val generatedIds = repository.upserts(drawingsToInsert)
        assertEquals("Should return 2 generated IDs", 2, generatedIds.size)
        assertTrue("All generated IDs should be positive", generatedIds.all { it > 0L })

        val allDrawings = repository.getAll().first()
        assertEquals("Should have 2 drawings in DB after bulk upsert", 2, allDrawings.size)
    }

    @Test
    fun `delete removes drawing`() = runTest(testDispatcher) {
        val drawing = createTestNoteDrawing(noteId = 1L)
        val id = repository.upsert(drawing)
        assertNotNull(repository.get(id).first())

        repository.delete(id)
        assertNull(repository.get(id).first())
    }

    @Test
    fun `deleteByNoteId removes drawings for that note`() = runTest(testDispatcher) {
        val drawing1Note1 = repository.upsert(createTestNoteDrawing(noteId = 1L))
        val drawing2Note1 = repository.upsert(createTestNoteDrawing(noteId = 1L))
        val drawing1Note2 = repository.upsert(createTestNoteDrawing(noteId = 2L))

        repository.deleteByNoteId(1L)

        assertNull(repository.get(drawing1Note1).first())
        assertNull(repository.get(drawing2Note1).first())
        assertNotNull(repository.get(drawing1Note2).first())
        assertEquals(1, repository.getAll().first().size)
    }

    @Test
    fun `getAll returns empty list initially`() = runTest(testDispatcher) {
        val drawings = repository.getAll().first()
        assertTrue("Initially, getAll should return an empty list", drawings.isEmpty())
    }

    @Test
    fun `getAll returns inserted drawings`() = runTest(testDispatcher) {
        repository.upsert(createTestNoteDrawing(noteId = 1L))
        repository.upsert(createTestNoteDrawing(noteId = 2L))
        assertEquals(2, repository.getAll().first().size)
    }

    @Test
    fun `get returns null for non-existent id`() = runTest(testDispatcher) {
        assertNull(repository.get(999L).first())
    }

    @Test
    fun `get returns correct drawing`() = runTest(testDispatcher) {
        val drawing = createTestNoteDrawing(noteId = 1L)
        val id = repository.upsert(drawing)
        val fetched = repository.get(id).first()
        assertNotNull(fetched)
        assertEquals(id, fetched?.id)
        assertEquals(1L, fetched?.noteId)
    }

    @Test
    fun `getByNoteId returns correct drawings for specific note`() = runTest(testDispatcher) {
        repository.upsert(createTestNoteDrawing(noteId = 1L, pathList = listOf(createTestPath(1))))
        repository.upsert(createTestNoteDrawing(noteId = 1L, pathList = listOf(createTestPath(2))))
        repository.upsert(createTestNoteDrawing(noteId = 2L, pathList = listOf(createTestPath(3))))

        val drawingsForNote1 = repository.getByNoteId(1L).first()
        assertEquals(2, drawingsForNote1.size)
        assertTrue(drawingsForNote1.all { it.noteId == 1L })

        val drawingsForNote2 = repository.getByNoteId(2L).first()
        assertEquals(1, drawingsForNote2.size)
        assertTrue(drawingsForNote2.all { it.noteId == 2L })

        val drawingsForNote3 = repository.getByNoteId(3L).first()
        assertTrue(drawingsForNote3.isEmpty())
    }
}
