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
package com.mshdabiola

import com.mshdabiola.database.NoteDataBase
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.model.NoteItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RealNoteItemDataSource(private val database: NoteDataBase) : NoteCheckDao {

    override suspend fun upsert(check: NoteItemEntity): Long {
        var resultingId: Long? = null
        database.noteItemTable.update { list ->
            val currentList = list ?: emptyList()
            if (check.id == null) { // New item
                val newId = (currentList.maxByOrNull { it.id ?: 0L }?.id ?: 0L) + 1L
                resultingId = newId
                currentList + check.copy(id = newId)
            } else { // Existing item or item with pre-assigned ID
                resultingId = check.id
                val index = currentList.indexOfFirst { it.id == check.id }
                if (index != -1) { // Found, update it
                    currentList.toMutableList().apply { this[index] = check }
                } else { // Not found, add it
                    currentList + check
                }
            }
        }
        return resultingId!!
    }

    override suspend fun upserts(checks: List<NoteItemEntity>): List<Long> {
        val resultingIds = mutableListOf<Long>()
        checks.forEach { check ->
            val id = upsert(check)
            resultingIds.add(id)
        }
        return resultingIds
    }

    override suspend fun delete(id: Long) {
        database.noteItemTable.update { currentList ->
            currentList?.filterNot { it.id == id } ?: emptyList()
        }
    }

    override suspend fun deleteCheckedItems(noteId: Long) {
        database.noteItemTable.update { currentList ->
            currentList?.filterNot { it.noteId == noteId && it.isCheck } ?: emptyList()
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        database.noteItemTable.update { currentList ->
            currentList?.filterNot { it.noteId == noteId } ?: emptyList()
        }
    }

    override fun get(id: Long): Flow<NoteItemEntity?> {
        return database.noteItemTable.updates.map { list ->
            list?.firstOrNull { it.id == id }
        }
    }

    override fun getAll(): Flow<List<NoteItemEntity>> {
        return database.noteItemTable.updates.map { list ->
            list ?: emptyList()
        }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteItemEntity>> {
        return database.noteItemTable.updates.map { list ->
            list?.filter { it.noteId == noteId } ?: emptyList()
        }
    }
}
