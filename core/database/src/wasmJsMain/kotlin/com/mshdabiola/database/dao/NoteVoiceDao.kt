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
package com.mshdabiola.database.dao

import com.mshdabiola.database.model.NoteVoiceEntity
import kotlinx.coroutines.flow.Flow

interface NoteVoiceDao {

    suspend fun upserts(voices: List<NoteVoiceEntity>): List<Long>

    suspend fun upsert(voice: NoteVoiceEntity): Long

    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun get(id: Long): Flow<NoteVoiceEntity?>

    fun getAll(): Flow<List<NoteVoiceEntity>>

    fun getByNoteId(noteId: Long): Flow<List<NoteVoiceEntity>>
}
