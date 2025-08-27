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

import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NotePadEntity
import kotlinx.coroutines.flow.Flow

interface NoteDao {

    suspend fun upsert(noteEntity: NoteEntity): Long

    suspend fun upserts(noteEntity: List<NoteEntity>): List<Long>

    suspend fun delete(id: Long)

    suspend fun deleteIds(ids: Set<Long>)

    suspend fun deleteTrash(noteType: Int)

    fun getByNoteType(noteType: Int): Flow<List<NotePadEntity>>

//    @Transaction
//    @Query("SELECT * FROM note_table WHERE reminder > 0 ORDER BY id DESC")
//    fun getListOfNotePadByReminder(): Flow<List<NotePadEntity>>

    fun getAll(): Flow<List<NotePadEntity>>

    fun get(noteId: Long): Flow<NotePadEntity?>

    fun getByIds(ids: Set<Long>): Flow<List<NotePadEntity>> // Define a return type
}
