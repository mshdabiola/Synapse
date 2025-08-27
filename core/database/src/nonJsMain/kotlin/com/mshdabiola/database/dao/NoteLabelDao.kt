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

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteLabelCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteLabelDao {

    @Upsert
    suspend fun upserts(labels: List<NoteLabelCrossRef>): List<Long>

    @Upsert
    suspend fun upsert(label: NoteLabelCrossRef): Long

    @Query("DELETE FROM note_label_table WHERE note_id = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("DELETE FROM note_label_table WHERE note_id = :noteId AND labelId = :labelId")
    suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long)

    @Query("SELECT * FROM note_label_table")
    fun getAll(): Flow<List<NoteLabelCrossRef>>

    @Query("SELECT * FROM note_label_table WHERE note_id = :noteId")
    fun getByNoteId(noteId: Long): Flow<List<NoteLabelCrossRef>>

    @Query("SELECT * FROM note_label_table WHERE labelId = :labelId")
    fun getByLabelId(labelId: Long): Flow<List<NoteLabelCrossRef>>

    @Query("SELECT * FROM note_label_table WHERE note_id IN (:ids)")
    fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabelCrossRef>>
}
