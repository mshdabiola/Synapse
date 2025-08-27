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
import com.mshdabiola.database.dao.NoteNotificationDao
import com.mshdabiola.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RealNotificationDataSource(private val database: NoteDataBase) : NoteNotificationDao {
    override suspend fun upsert(notification: NotificationEntity): Long {
        var resultingId: Long = notification.id
        database.notificationTable.update { list ->
            val currentList = list ?: emptyList()
            if (notification.id == 0L) { // New notification (ID is 0 by default for new entities)
                val newId = (currentList.maxByOrNull { it.id }?.id ?: 0L) + 1L
                resultingId = newId
                currentList + notification.copy(id = newId)
            } else { // Existing notification
                val index = currentList.indexOfFirst { it.id == notification.id }
                if (index != -1) { // Found, update it
                    currentList.toMutableList().apply { this[index] = notification }
                } else { // Not found (e.g. ID was set but not in list), add it
                    // This case might indicate an issue if an ID is provided for a non-existent item.
                    // For KStore, simply adding it might be the desired behavior to mimic upsert.
                    currentList + notification
                }
            }
        }
        return resultingId
    }

    override suspend fun upserts(notifications: List<NotificationEntity>): List<Long> {
        val resultingIds = mutableListOf<Long>()
        // Optimize by performing a single KStore update if possible,
        // but for now, iterate for simplicity, similar to other DAOs.
        notifications.forEach { notification ->
            val id = upsert(notification)
            resultingIds.add(id)
        }
        return resultingIds
    }

    override suspend fun delete(id: Long) {
        database.notificationTable.update { currentList ->
            currentList?.filterNot { it.id == id } ?: emptyList()
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        database.notificationTable.update { currentList ->
            currentList?.filterNot { it.noteId == noteId } ?: emptyList()
        }
    }

    override fun get(id: Long): Flow<NotificationEntity?> {
        return database.notificationTable.updates.map { list ->
            list?.firstOrNull { it.id == id }
        }
    }

    override fun getAll(): Flow<List<NotificationEntity>> {
        return database.notificationTable.updates.map { list ->
            list ?: emptyList()
        }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NotificationEntity>> {
        return database.notificationTable.updates.map { list ->
            list?.filter { it.noteId == noteId } ?: emptyList()
        }
    }
}
