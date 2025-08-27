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
import com.mshdabiola.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteNotificationDao {

    @Upsert
    suspend fun upserts(notifications: List<NotificationEntity>): List<Long>

    @Upsert
    suspend fun upsert(notification: NotificationEntity): Long

    @Query("DELETE FROM notification_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM notification_table WHERE note_id = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("SELECT * FROM notification_table WHERE id = :id")
    fun get(id: Long): Flow<NotificationEntity?>

    @Query("SELECT * FROM notification_table")
    fun getAll(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notification_table WHERE note_id = :noteId")
    fun getByNoteId(noteId: Long): Flow<List<NotificationEntity>>
}
