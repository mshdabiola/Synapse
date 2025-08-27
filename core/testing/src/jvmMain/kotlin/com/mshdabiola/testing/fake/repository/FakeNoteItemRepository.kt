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

import com.mshdabiola.data.repository.NoteItemRepository
import com.mshdabiola.model.note.NoteItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeNoteItemRepository : NoteItemRepository {
    private val noteChecks = mutableListOf<NoteItem>()
    private var nextId = 1L

    override suspend fun upserts(checks: List<NoteItem>): List<Long> {
        val ids = mutableListOf<Long>()
        checks.forEach { check ->
            ids.add(upsert(check))
        }
        return ids
    }

    override suspend fun upsert(check: NoteItem): Long {
        return if (check.id == -1L) {
            val newCheck = check.copy(id = nextId++)
            noteChecks.add(newCheck)
            newCheck.id
        } else {
            val index = noteChecks.indexOfFirst { it.id == check.id }
            if (index != -1) {
                noteChecks[index] = check
                check.id
            } else {
                // If ID is non-zero but not found, treat as a new insert with a new ID
                val newCheck = check.copy(id = nextId++)
                noteChecks.add(newCheck)
                newCheck.id
            }
        }
    }

    override suspend fun delete(id: Long) {
        noteChecks.removeIf { it.id == id }
    }

    override suspend fun deleteCheckedItems(noteId: Long) {
        noteChecks.removeIf { it.noteId == noteId && it.isCheck }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        noteChecks.removeIf { it.noteId == noteId }
    }

    override fun getAll(): Flow<List<NoteItem>> {
        return flowOf(noteChecks.toList())
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteItem>> {
        return flowOf(noteChecks.filter { it.noteId == noteId }.toList())
    }

    override fun get(id: Long): Flow<NoteItem?> {
        return flowOf(noteChecks.find { it.id == id })
    }
}
