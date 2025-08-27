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
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.model.NoteImageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RealNoteImageDataSource(private val database: NoteDataBase) : NoteImageDao {

    override suspend fun upsert(image: NoteImageEntity): Long {
        // Since id is not auto-generated, KStore's update will handle insert/replace.
        // The returned Long for upsert in Room usually signifies the rowId.
        // For KStore, we can't directly return a SQLite rowId.
        // We'll mimic the behavior: if it's an insert or update, it "succeeds".
        // Let's return the image.id as a signal of success, as it's the PK.
        database.noteImageTable.update { list ->
            val currentList = list ?: emptyList()
            val index = currentList.indexOfFirst { it.id == image.id }
            if (index != -1) { // Found, update it
                currentList.toMutableList().apply { this[index] = image }
            } else { // Not found, add it
                currentList + image
            }
        }
        return image.id // Return the provided ID as confirmation
    }

    override suspend fun upserts(images: List<NoteImageEntity>): List<Long> {
        val resultingIds = mutableListOf<Long>()
        images.forEach { image ->
            val id = upsert(image)
            resultingIds.add(id)
        }
        return resultingIds
    }

    override suspend fun delete(id: Long) {
        database.noteImageTable.update { currentList ->
            currentList?.filterNot { it.id == id } ?: emptyList()
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        database.noteImageTable.update { currentList ->
            currentList?.filterNot { it.noteId == noteId } ?: emptyList()
        }
    }

    override fun get(id: Long): Flow<NoteImageEntity?> {
        return database.noteImageTable.updates.map { list ->
            list?.firstOrNull { it.id == id }
        }
    }

    override fun getAll(): Flow<List<NoteImageEntity>> {
        return database.noteImageTable.updates.map { list ->
            list ?: emptyList()
        }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteImageEntity>> {
        return database.noteImageTable.updates.map { list ->
            list?.filter { it.noteId == noteId } ?: emptyList()
        }
    }
}
