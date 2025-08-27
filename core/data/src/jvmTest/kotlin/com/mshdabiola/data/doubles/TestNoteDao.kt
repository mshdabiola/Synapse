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

import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NotePadEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestNoteDao : NoteDao {
    private val notesFlow = MutableStateFlow<List<NoteEntity>>(emptyList())
    private var nextId = 1L

    // Helper to create a NotePadEntity from NoteEntity for test purposes
    private fun NoteEntity.toNotePadEntity(): NotePadEntity {
        return NotePadEntity(
            noteEntity = this,
            notification = null,
            images = emptyList(),
            voices = emptyList(),
            checks = emptyList(),
            drawings = emptyList(),
            labels = emptyList(),
        )
    }

    override suspend fun upsert(noteEntity: NoteEntity): Long {
        val currentNotes = notesFlow.value.toMutableList()
        val newEntity = if (noteEntity.id == null || noteEntity.id == 0L) { // Consider 0L as new too
            noteEntity.copy(id = nextId++)
        } else {
            noteEntity
        }

        val index = currentNotes.indexOfFirst { it.id == newEntity.id }
        if (index != -1) {
            currentNotes[index] = newEntity // Update existing
        } else {
            currentNotes.add(newEntity) // Add new
        }
        notesFlow.value = currentNotes
        return newEntity.id ?: 0L // Should have an ID now
    }

    override suspend fun upserts(noteEntities: List<NoteEntity>): List<Long> {
        val ids = mutableListOf<Long>()
        noteEntities.forEach {
            ids.add(upsert(it))
        }
        return ids
    }

    override suspend fun delete(id: Long) {
        val currentNotes = notesFlow.value.toMutableList()
        currentNotes.removeAll { it.id == id }
        notesFlow.value = currentNotes
    }

    override suspend fun deleteIds(ids: Set<Long>) {
        val currentNotes = notesFlow.value.toMutableList()
        currentNotes.removeAll { it.id in ids }
        notesFlow.value = currentNotes
    }

    override suspend fun deleteTrash(noteType: Int) {
        val currentNotes = notesFlow.value.toMutableList()
        currentNotes.removeAll { it.noteType == noteType }
        notesFlow.value = currentNotes
    }

    override fun getByNoteType(noteType: Int): Flow<List<NotePadEntity>> {
        return notesFlow.asStateFlow().map { notes ->
            notes.filter { it.noteType == noteType }.map { it.toNotePadEntity() }
        }
    }

    override fun getAll(): Flow<List<NotePadEntity>> {
        return notesFlow.asStateFlow().map { notes ->
            notes.map { it.toNotePadEntity() }
        }
    }

    override fun get(noteId: Long): Flow<NotePadEntity?> {
        return notesFlow.asStateFlow().map { notes ->
            notes.find { it.id == noteId }?.toNotePadEntity()
        }
    }

    override fun getByIds(ids: Set<Long>): Flow<List<NotePadEntity>> {
        return notesFlow.asStateFlow().map { notes ->
            notes.filter { it.id in ids }.map { it.toNotePadEntity() }
        }
    }
}
