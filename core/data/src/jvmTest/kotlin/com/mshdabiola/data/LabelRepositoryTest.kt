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

import com.mshdabiola.data.doubles.TestLabelDao
import com.mshdabiola.data.repository.RealLabelRepository
import com.mshdabiola.model.note.Label
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
class LabelRepositoryTest {

    private lateinit var labelDao: TestLabelDao
    private lateinit var repository: RealLabelRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        labelDao = TestLabelDao()
        repository = RealLabelRepository(labelDao, testDispatcher)
    }

    private fun createTestLabel(id: Long = -1L, name: String = "Test Label"): Label {
        return Label(id = id, name = name)
    }

    @Test
    fun `upsert new label returns valid id and adds label`() = runTest(testDispatcher) {
        val newLabel = createTestLabel(name = "New Label")
        val id = repository.upsert(newLabel)

        assertTrue("Generated ID should be positive", id > 0L)
        val insertedLabel = repository.get(id).first()
        assertNotNull("Inserted Label should not be null", insertedLabel)
        assertEquals(id, insertedLabel?.id)
        assertEquals("New Label", insertedLabel?.name)
    }

    @Test
    fun `upsert existing label updates it`() = runTest(testDispatcher) {
        val initialLabel = createTestLabel(name = "Initial Label")
        val id = repository.upsert(initialLabel) // First insert

        val updatedLabel = createTestLabel(id = id, name = "Updated Label")
        val updatedId = repository.upsert(updatedLabel) // Update

        assertEquals("Updated ID should match original ID", id, updatedId)

        val fetchedLabel = repository.get(id).first()
        assertNotNull("Fetched Label should not be null after update", fetchedLabel)
        assertEquals("Updated Label", fetchedLabel?.name)
    }

    @Test
    fun `upserts_insertsMultipleLabels_andReturnsTheirIds`() = runTest(testDispatcher) {
        val label1 = createTestLabel(name = "Bulk Label 1")
        val label2 = createTestLabel(name = "Bulk Label 2")
        val labelsToInsert = listOf(label1, label2)

        val generatedIds = repository.upserts(labelsToInsert)
        assertEquals("Should return 2 generated IDs", 2, generatedIds.size)
        assertTrue("All generated IDs should be positive", generatedIds.all { it > 0L })

        val allLabels = repository.getAll().first()
        assertEquals("Should have 2 labels in DB after bulk upsert", 2, allLabels.size)
        assertTrue("Contains Bulk Label 1", allLabels.any { it.name == "Bulk Label 1" && it.id == generatedIds[0] })
        assertTrue("Contains Bulk Label 2", allLabels.any { it.name == "Bulk Label 2" && it.id == generatedIds[1] })
    }

    @Test
    fun `delete removes label`() = runTest(testDispatcher) {
        val label = createTestLabel(name = "To Delete")
        val id = repository.upsert(label)

        var fetchedLabel = repository.get(id).first()
        assertNotNull("Label should exist before delete", fetchedLabel)

        repository.delete(id)
        fetchedLabel = repository.get(id).first()
        assertNull("Label should be null after delete", fetchedLabel)
    }

    @Test
    fun `getAll returns empty list initially`() = runTest(testDispatcher) {
        val labels = repository.getAll().first()
        assertTrue("Initially, getAll should return an empty list", labels.isEmpty())
    }

    @Test
    fun `getAll returns inserted labels`() = runTest(testDispatcher) {
        val label1 = createTestLabel(name = "Label 1")
        val label2 = createTestLabel(name = "Label 2")
        repository.upsert(label1)
        repository.upsert(label2)

        val labels = repository.getAll().first()
        assertEquals("getAll should return 2 labels", 2, labels.size)
    }

    @Test
    fun `get returns null for non-existent id`() = runTest(testDispatcher) {
        val label = repository.get(999L).first() // An ID that surely doesn't exist
        assertNull("get should return null for a non-existent ID", label)
    }

    @Test
    fun `get returns correct label`() = runTest(testDispatcher) {
        val label = createTestLabel(name = "Specific Label")
        val id = repository.upsert(label)

        val fetchedLabel = repository.get(id).first()
        assertNotNull("Fetched Label should not be null", fetchedLabel)
        assertEquals("ID should match", id, fetchedLabel?.id)
        assertEquals("Specific Label", fetchedLabel?.name)
    }
}
