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
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.model.NoteVoiceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RealNoteVoiceDataSource(private val database: NoteDataBase) : NoteVoiceDao {

    override suspend fun upsert(voice: NoteVoiceEntity): Long {
        // Similar to RealNoteImageDataSource, as id is not auto-generated.
        // Return the voice.id as a signal of success.
        database.noteVoiceTable.update { list ->
            val currentList = list ?: emptyList()
            val index = currentList.indexOfFirst { it.id == voice.id }
            if (index != -1) { // Found, update it
                currentList.toMutableList().apply { this[index] = voice }
            } else { // Not found, add it
                currentList + voice
            }
        }
        return voice.id // Return the provided ID as confirmation
    }

    override suspend fun upserts(voices: List<NoteVoiceEntity>): List<Long> {
        val resultingIds = mutableListOf<Long>()
        voices.forEach { voice ->
            val id = upsert(voice)
            resultingIds.add(id)
        }
        return resultingIds
    }

    override suspend fun delete(id: Long) {
        database.noteVoiceTable.update { currentList ->
            currentList?.filterNot { it.id == id } ?: emptyList()
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        database.noteVoiceTable.update { currentList ->
            currentList?.filterNot { it.noteId == noteId } ?: emptyList()
        }
    }

    override fun get(id: Long): Flow<NoteVoiceEntity?> {
        return database.noteVoiceTable.updates.map { list ->
            list?.firstOrNull { it.id == id }
        }
    }

    override fun getAll(): Flow<List<NoteVoiceEntity>> {
        return database.noteVoiceTable.updates.map { list ->
            list ?: emptyList()
        }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteVoiceEntity>> {
        return database.noteVoiceTable.updates.map { list ->
            list?.filter { it.noteId == noteId } ?: emptyList()
        }
    }
}
