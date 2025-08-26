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

import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.database.model.NoteDrawingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestNoteDrawingDao : NoteDrawingDao {
    private val noteDrawingsFlow = MutableStateFlow<List<NoteDrawingEntity>>(emptyList())
    private var nextId = 1L

    override suspend fun upsert(drawing: NoteDrawingEntity): Long {
        val currentDrawings = noteDrawingsFlow.value.toMutableList()
        // Updated condition to handle -1L from the model's default ID
        val newEntity = if (drawing.id == null || drawing.id == 0L || drawing.id == -1L) {
            drawing.copy(id = nextId++)
        } else {
            drawing
        }

        val index = currentDrawings.indexOfFirst { it.id == newEntity.id }
        if (index != -1) {
            currentDrawings[index] = newEntity // Update existing
        } else {
            currentDrawings.add(newEntity) // Add new
        }
        noteDrawingsFlow.value = currentDrawings
        return newEntity.id ?: 0L // Should have an ID now
    }

    override suspend fun upserts(drawings: List<NoteDrawingEntity>): List<Long> {
        val ids = mutableListOf<Long>()
        drawings.forEach {
            ids.add(upsert(it))
        }
        return ids
    }

    override suspend fun delete(id: Long) {
        val currentDrawings = noteDrawingsFlow.value.toMutableList()
        currentDrawings.removeAll { it.id == id }
        noteDrawingsFlow.value = currentDrawings
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        val currentDrawings = noteDrawingsFlow.value.toMutableList()
        currentDrawings.removeAll { it.noteId == noteId }
        noteDrawingsFlow.value = currentDrawings
    }

    override fun get(id: Long): Flow<NoteDrawingEntity?> {
        return noteDrawingsFlow.asStateFlow().map { drawings ->
            drawings.find { it.id == id }
        }
    }

    override fun getAll(): Flow<List<NoteDrawingEntity>> {
        return noteDrawingsFlow.asStateFlow()
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteDrawingEntity>> {
        return noteDrawingsFlow.asStateFlow().map { drawings ->
            drawings.filter { it.noteId == noteId }
        }
    }
}
