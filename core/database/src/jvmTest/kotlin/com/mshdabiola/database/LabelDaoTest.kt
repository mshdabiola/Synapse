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
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.model.LabelEntity
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

class LabelDaoTest {

    private lateinit var database: NotesDatabase
    private lateinit var labelDao: LabelDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder<NotesDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO) // Use IO dispatcher for test queries
            .build()
        labelDao = database.getLabelDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetLabel() = runTest {
        val label = LabelEntity(id = null, name = "Shopping")
        val generatedId = labelDao.upsert(label)

        val retrievedLabelFlow = labelDao.get(generatedId)
        val retrievedLabel = retrievedLabelFlow.first()

        assertNotNull(retrievedLabel)
        assertEquals(generatedId, retrievedLabel.id)
        assertEquals("Shopping", retrievedLabel.name)
    }

    @Test
    fun getAllLabels_whenEmpty() = runTest {
        val allLabels = labelDao.getAll().first()
        assertTrue(allLabels.isEmpty(), "Database should be empty initially")
    }

    @Test
    fun getAllLabels_afterInsertingMultiple() = runTest {
        val label1 = LabelEntity(id = null, name = "Work")
        val label2 = LabelEntity(id = null, name = "Personal")
        labelDao.upsert(label1)
        labelDao.upsert(label2)

        val allLabels = labelDao.getAll().first()
        assertEquals(2, allLabels.size)
    }

    @Test
    fun getLabel_nonExistent() = runTest {
        val label = labelDao.get(999L).first() // ID that doesn't exist
        assertNull(label, "Should return null for a non-existent ID")
    }

    @Test
    fun upsert_insertsNewLabel() = runTest {
        val newLabel = LabelEntity(id = null, name = "Urgent")
        val id = labelDao.upsert(newLabel)
        assertTrue(id > 0, "Upsert should return a positive ID for new inserts")

        val retrieved = labelDao.get(id).first()
        assertNotNull(retrieved)
        assertEquals("Urgent", retrieved.name)
    }

    @Test
    fun upsert_updatesExistingLabel() = runTest {
        val initialLabel = LabelEntity(id = null, name = "Old Name")
        val id = labelDao.upsert(initialLabel) // Insert first

        val updatedLabel = LabelEntity(id = id, name = "New Name")
        labelDao.upsert(updatedLabel) // Update

        val retrieved = labelDao.get(id).first()
        assertNotNull(retrieved)
        assertEquals(id, retrieved.id)
        assertEquals("New Name", retrieved.name)
    }

    @Test
    fun upserts_insertsMultipleLabels() = runTest {
        val labels = listOf(
            LabelEntity(id = null, name = "Home"),
            LabelEntity(id = null, name = "Errands"),
        )
        val generatedIds = labelDao.upserts(labels)
        assertEquals(2, generatedIds.size)
        assertTrue(generatedIds.all { it > 0 })

        val allLabels = labelDao.getAll().first()
        assertEquals(2, allLabels.size)
        assertTrue(allLabels.any { it.name == "Home" })
        assertTrue(allLabels.any { it.name == "Errands" })
    }

    @Test
    fun deleteLabel() = runTest {
        val label = LabelEntity(id = null, name = "To Delete")
        val id = labelDao.upsert(label)

        assertNotNull(labelDao.get(id).first(), "Label should exist before delete")

        labelDao.delete(id)
        assertNull(labelDao.get(id).first(), "Label should be null after delete")
    }
}
