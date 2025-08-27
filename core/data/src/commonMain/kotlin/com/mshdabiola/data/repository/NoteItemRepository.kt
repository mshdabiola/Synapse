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
package com.mshdabiola.data.repository

import com.mshdabiola.model.note.NoteItem
import kotlinx.coroutines.flow.Flow

interface NoteItemRepository {

    suspend fun upserts(checks: List<NoteItem>): List<Long>

    suspend fun upsert(check: NoteItem): Long
    suspend fun delete(id: Long)

    suspend fun deleteCheckedItems(noteId: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun getAll(): Flow<List<NoteItem>>
    fun getByNoteId(noteId: Long): Flow<List<NoteItem>>

    fun get(id: Long): Flow<NoteItem?>
}
