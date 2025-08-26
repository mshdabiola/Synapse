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
package com.mshdabiola.data.doubles

import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.model.NoteItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestNoteCheckDao : NoteCheckDao {
    private val noteItemsFlow = MutableStateFlow<List<NoteItemEntity>>(emptyList())
    private var nextId = 1L

    override suspend fun upsert(check: NoteItemEntity): Long {
        val currentItems = noteItemsFlow.value.toMutableList()
        // Updated condition to handle -1L from the model's default ID
        val newEntity = if (check.id == null || check.id == 0L || check.id == -1L) {
            check.copy(id = nextId++)
        } else {
            check
        }

        val index = currentItems.indexOfFirst { it.id == newEntity.id }
        if (index != -1) {
            currentItems[index] = newEntity // Update existing
        } else {
            currentItems.add(newEntity) // Add new
        }
        noteItemsFlow.value = currentItems
        return newEntity.id ?: 0L // Should have an ID now
    }

    override suspend fun upserts(checks: List<NoteItemEntity>): List<Long> {
        val ids = mutableListOf<Long>()
        checks.forEach {
            ids.add(upsert(it))
        }
        return ids
    }

    override suspend fun delete(id: Long) {
        val currentItems = noteItemsFlow.value.toMutableList()
        currentItems.removeAll { it.id == id }
        noteItemsFlow.value = currentItems
    }

    override suspend fun deleteCheckedItems(noteId: Long) {
        val currentItems = noteItemsFlow.value.toMutableList()
        currentItems.removeAll { it.noteId == noteId && it.isCheck }
        noteItemsFlow.value = currentItems
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        val currentItems = noteItemsFlow.value.toMutableList()
        currentItems.removeAll { it.noteId == noteId }
        noteItemsFlow.value = currentItems
    }

    override fun get(id: Long): Flow<NoteItemEntity?> {
        return noteItemsFlow.asStateFlow().map { items ->
            items.find { it.id == id }
        }
    }

    override fun getAll(): Flow<List<NoteItemEntity>> {
        return noteItemsFlow.asStateFlow()
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteItemEntity>> {
        return noteItemsFlow.asStateFlow().map { items ->
            items.filter { it.noteId == noteId }
        }
    }
}
