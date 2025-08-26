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
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.database.model.NoteDrawingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RealNoteDrawingDataSource(private val database: NoteDataBase) : NoteDrawingDao {

    override suspend fun upsert(drawing: NoteDrawingEntity): Long {
        var resultingId: Long? = null
        database.noteDrawingTable.update { list ->
            val currentList = list ?: emptyList()
            if (drawing.id == null) { // New drawing
                val newId = (currentList.maxByOrNull { it.id ?: 0L }?.id ?: 0L) + 1L
                resultingId = newId
                currentList + drawing.copy(id = newId)
            } else { // Existing drawing or drawing with pre-assigned ID
                resultingId = drawing.id
                val index = currentList.indexOfFirst { it.id == drawing.id }
                if (index != -1) { // Found, update it
                    currentList.toMutableList().apply { this[index] = drawing }
                } else { // Not found, add it
                    currentList + drawing
                }
            }
        }
        return resultingId!!
    }

    override suspend fun upserts(drawings: List<NoteDrawingEntity>): List<Long> {
        val resultingIds = mutableListOf<Long>()
        drawings.forEach { drawing ->
            val id = upsert(drawing)
            resultingIds.add(id)
        }
        return resultingIds
    }

    override suspend fun delete(id: Long) {
        database.noteDrawingTable.update { currentList ->
            currentList?.filterNot { it.id == id } ?: emptyList()
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        database.noteDrawingTable.update { currentList ->
            currentList?.filterNot { it.noteId == noteId } ?: emptyList()
        }
    }

    override fun get(id: Long): Flow<NoteDrawingEntity?> {
        return database.noteDrawingTable.updates.map { list ->
            list?.firstOrNull { it.id == id }
        }
    }

    override fun getAll(): Flow<List<NoteDrawingEntity>> {
        return database.noteDrawingTable.updates.map { list ->
            list ?: emptyList()
        }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteDrawingEntity>> {
        return database.noteDrawingTable.updates.map { list ->
            list?.filter { it.noteId == noteId } ?: emptyList()
        }
    }
}
