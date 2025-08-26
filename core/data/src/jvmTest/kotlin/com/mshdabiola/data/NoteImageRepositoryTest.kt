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

import com.mshdabiola.data.doubles.TestNoteImageDao
import com.mshdabiola.data.repository.RealNoteImageRepository
import com.mshdabiola.model.note.NoteImage
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
class NoteImageRepositoryTest {

    private lateinit var noteImageDao: TestNoteImageDao
    private lateinit var repository: RealNoteImageRepository
    private val testDispatcher = StandardTestDispatcher()
    private var nextImageIdCounter = 1L // To ensure unique IDs for new images

    @Before
    fun setUp() {
        noteImageDao = TestNoteImageDao()
        repository = RealNoteImageRepository(noteImageDao, testDispatcher)
        nextImageIdCounter = 1L // Reset for each test
    }

    private fun createTestNoteImage(
        id: Long? = null, // Null means generate a new unique ID
        noteId: Long,
        path: String = "test/path/image.jpg"
    ): NoteImage {
        val imageId = id ?: nextImageIdCounter++
        return NoteImage(id = imageId, noteId = noteId, path = path)
    }

    @Test
    fun `upsert new image returns provided id and adds image`() = runTest(testDispatcher) {
        val newImage = createTestNoteImage(noteId = 1L) // ID will be 1L
        val returnedId = repository.upsert(newImage)

        assertEquals("Returned ID should match provided ID", newImage.id, returnedId)
        val insertedImage = repository.get(newImage.id).first()
        assertNotNull("Inserted Image should not be null", insertedImage)
        assertEquals(newImage.id, insertedImage?.id)
        assertEquals(1L, insertedImage?.noteId)
        assertEquals("test/path/image.jpg", insertedImage?.path)
    }

    @Test
    fun `upsert existing image updates it`() = runTest(testDispatcher) {
        val initialImage = createTestNoteImage(noteId = 1L) // ID will be 1L
        repository.upsert(initialImage)

        val updatedImage = createTestNoteImage(id = initialImage.id, noteId = 1L, path = "updated/path/image.png")
        val returnedId = repository.upsert(updatedImage)

        assertEquals("Returned ID should match original ID", initialImage.id, returnedId)

        val fetchedImage = repository.get(initialImage.id).first()
        assertNotNull(fetchedImage)
        assertEquals("updated/path/image.png", fetchedImage?.path)
    }

    @Test
    fun `upserts_insertsMultipleImages_andReturnsTheirIds`() = runTest(testDispatcher) {
        val image1 = createTestNoteImage(noteId = 1L) // ID 1L
        val image2 = createTestNoteImage(noteId = 2L) // ID 2L
        val imagesToInsert = listOf(image1, image2)

        val returnedIds = repository.upserts(imagesToInsert)
        assertEquals("Should return 2 IDs", 2, returnedIds.size)
        assertEquals(image1.id, returnedIds[0])
        assertEquals(image2.id, returnedIds[1])

        val allImages = repository.getAll().first()
        assertEquals("Should have 2 images in DB after bulk upsert", 2, allImages.size)
    }

    @Test
    fun `delete removes image`() = runTest(testDispatcher) {
        val image = createTestNoteImage(noteId = 1L)
        repository.upsert(image)
        assertNotNull(repository.get(image.id).first())

        repository.delete(image.id)
        assertNull(repository.get(image.id).first())
    }

    @Test
    fun `deleteByNoteId removes images for that note`() = runTest(testDispatcher) {
        val image1Note1 = createTestNoteImage(noteId = 1L) // ID 1
        val image2Note1 = createTestNoteImage(noteId = 1L) // ID 2
        val image1Note2 = createTestNoteImage(noteId = 2L) // ID 3
        repository.upserts(listOf(image1Note1, image2Note1, image1Note2))

        repository.deleteByNoteId(1L)

        assertNull(repository.get(image1Note1.id).first())
        assertNull(repository.get(image2Note1.id).first())
        assertNotNull(repository.get(image1Note2.id).first())
        assertEquals(1, repository.getAll().first().size)
    }

    @Test
    fun `getAll returns empty list initially`() = runTest(testDispatcher) {
        val images = repository.getAll().first()
        assertTrue("Initially, getAll should return an empty list", images.isEmpty())
    }

    @Test
    fun `getAll returns inserted images`() = runTest(testDispatcher) {
        repository.upsert(createTestNoteImage(noteId = 1L))
        repository.upsert(createTestNoteImage(noteId = 2L))
        assertEquals(2, repository.getAll().first().size)
    }

    @Test
    fun `get returns null for non-existent id`() = runTest(testDispatcher) {
        assertNull(repository.get(999L).first())
    }

    @Test
    fun `get returns correct image`() = runTest(testDispatcher) {
        val image = createTestNoteImage(noteId = 1L)
        repository.upsert(image)
        val fetched = repository.get(image.id).first()
        assertNotNull(fetched)
        assertEquals(image.id, fetched?.id)
        assertEquals(1L, fetched?.noteId)
    }

    @Test
    fun `getByNoteId returns correct images for specific note`() = runTest(testDispatcher) {
        val image1 = createTestNoteImage(noteId = 1L, path = "path1")
        val image2 = createTestNoteImage(noteId = 1L, path = "path2")
        val image3 = createTestNoteImage(noteId = 2L, path = "path3")
        repository.upserts(listOf(image1, image2, image3))

        val imagesForNote1 = repository.getByNoteId(1L).first()
        assertEquals(2, imagesForNote1.size)
        assertTrue(imagesForNote1.all { it.noteId == 1L })
        assertTrue(imagesForNote1.any { it.path == "path1" })
        assertTrue(imagesForNote1.any { it.path == "path2" })


        val imagesForNote2 = repository.getByNoteId(2L).first()
        assertEquals(1, imagesForNote2.size)
        assertTrue(imagesForNote2.all { it.noteId == 2L })
        assertEquals("path3", imagesForNote2.first().path)

        val imagesForNote3 = repository.getByNoteId(3L).first()
        assertTrue(imagesForNote3.isEmpty())
    }
}
