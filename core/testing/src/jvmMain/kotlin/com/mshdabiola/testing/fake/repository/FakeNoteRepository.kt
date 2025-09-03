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
package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NotePad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// Simulate a delay as if accessing a database
private const val DELAY_MILLIS = 50L

class FakeNoteRepository : NoteRepository {

    private val notesFlow = MutableStateFlow<LinkedHashMap<Long, NotePad>>(linkedMapOf())
    private var nextId = 1L // For auto-incrementing IDs

    private fun findNextId(): Long {
        return (notesFlow.value.keys.maxOrNull() ?: 0L) + 1L
    }

    override suspend fun upserts(notes: List<NotePad>): List<Long> {
        kotlinx.coroutines.delay(DELAY_MILLIS)
        val ids = mutableListOf<Long>()
        notesFlow.update { currentNotes ->
            val newNotes = LinkedHashMap(currentNotes)
            notes.forEach { notePadToUpsert ->
                val id: Long
                if (notePadToUpsert.id != -1L) {
                    // Update existing
                    id = notePadToUpsert.id
                    newNotes[id] = notePadToUpsert
                } else {
                    // Insert new
                    id = findNextId()
                    newNotes[id] = notePadToUpsert.copy(id = id)
                }
                ids.add(id)
            }
            newNotes
        }
        return ids
    }

    override suspend fun upsert(note: NotePad): Long {
        kotlinx.coroutines.delay(DELAY_MILLIS)
        var newId = 0L
        notesFlow.update { currentNotes ->
            val newNotes = LinkedHashMap(currentNotes)
            if (note.id != -1L) {
                // Update existing
                newId = note.id
                newNotes[newId] = note
            } else {
                // Insert new
                newId = findNextId()
                newNotes[newId] = note.copy(id = newId)
            }
            newNotes
        }
        return newId
    }

    override suspend fun delete(id: Long) {
        kotlinx.coroutines.delay(DELAY_MILLIS)
        notesFlow.update { currentNotes ->
            val newNotes = LinkedHashMap(currentNotes)
            newNotes.remove(id)
            newNotes
        }
    }

    override suspend fun deleteIds(ids: Set<Long>) {
        kotlinx.coroutines.delay(DELAY_MILLIS)
        notesFlow.update { currentNotes ->
            val newNotes = LinkedHashMap(currentNotes)
            ids.forEach { idToRemove ->
                newNotes.remove(idToRemove)
            }
            newNotes
        }
    }

    override suspend fun deleteTrash() {
        kotlinx.coroutines.delay(DELAY_MILLIS)
        notesFlow.update { currentNotes ->
            val newNotes = LinkedHashMap(currentNotes)
            val keysToRemove = newNotes.filter { it.value.noteCategory == NoteCategory.TRASH }.keys
            keysToRemove.forEach { newNotes.remove(it) }
            newNotes
        }
    }

    override fun getAll(): Flow<List<NotePad>> {
        return notesFlow.asStateFlow().map { it.values.toList().sortedByDescending { notePad -> notePad.editDate } }
    }

    override fun get(id: Long): Flow<NotePad?> {
        return notesFlow.asStateFlow().map { it[id] }
    }

    override fun getByNoteType(noteType: NoteCategory): Flow<List<NotePad>> {
        return notesFlow.asStateFlow().map { notesMap ->
            notesMap.values
                .filter { it.noteCategory == noteType }
                .sortedByDescending { it.editDate }
        }
    }

    override fun getByNoteIds(set: Set<Long>): Flow<List<NotePad>> {
        return notesFlow.asStateFlow().map { notesMap ->
            notesMap.values
                .filter { it.id in set }
                .sortedByDescending { it.editDate }
        }
    }

    override suspend fun updateColorForIds(ids: Set<Long>, color: Int) {
        notesFlow.update { currentNotes ->
            val newNotes = LinkedHashMap(currentNotes)
            ids.forEach { id ->
                newNotes[id] = newNotes[id]?.copy(color = color) ?: return@forEach
            }
            newNotes
        }
    }

    override suspend fun updatePinForIds(ids: Set<Long>, isPin: Boolean) {
        notesFlow.update { currentNotes ->
            val newNotes = LinkedHashMap(currentNotes)
            ids.forEach { id ->
                newNotes[id] = newNotes[id]?.copy(isPin = isPin) ?: return@forEach
            }
            newNotes
        }
    }

    override suspend fun updateNoteTypeForIds(
        ids: Set<Long>,
        noteType: NoteCategory,
    ) {
        notesFlow.update { currentNotes ->
            val newNotes = LinkedHashMap(currentNotes)
            ids.forEach { id ->
                newNotes[id] = newNotes[id]?.copy(noteCategory = noteType) ?: return@forEach
            }
            newNotes
        }
    }

    // Helper function for tests to set initial data or clear
    fun setData(newNotes: List<NotePad>) {
        val notesMap = LinkedHashMap<Long, NotePad>()
        var maxId = 0L
        newNotes.forEach {
            val id = if (it.id == 0L) findNextId() else it.id
            notesMap[id] = it.copy(id = id)
            if (id > maxId) maxId = id
        }
        notesFlow.value = notesMap
        nextId = maxId + 1
    }

    fun clearData() {
        notesFlow.value = linkedMapOf()
        nextId = 1L
    }
}
