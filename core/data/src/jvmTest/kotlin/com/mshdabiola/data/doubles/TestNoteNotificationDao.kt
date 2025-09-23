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
package com.mshdabiola.data.doubles

import com.mshdabiola.database.dao.NoteNotificationDao
import com.mshdabiola.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestNoteNotificationDao : NoteNotificationDao {
    private val notificationsFlow = MutableStateFlow<List<NotificationEntity>>(emptyList())
    private var nextId = 1L

    override suspend fun upsert(notification: NotificationEntity): Long {
        val currentNotifications = notificationsFlow.value.toMutableList()
        val newEntity = if (notification.id == 0L) { // Default value for new entities
            notification.copy(id = nextId++)
        } else {
            notification
        }

        val index = currentNotifications.indexOfFirst { it.id == newEntity.id }
        if (index != -1) {
            currentNotifications[index] = newEntity // Update existing
        } else {
            currentNotifications.add(newEntity) // Add new or existing with non-zero ID not found
        }
        notificationsFlow.value = currentNotifications
        return newEntity.id
    }

    override suspend fun upserts(notifications: List<NotificationEntity>): List<Long> {
        val ids = mutableListOf<Long>()
        notifications.forEach {
            ids.add(upsert(it))
        }
        return ids
    }

    override suspend fun delete(id: Long) {
        val currentNotifications = notificationsFlow.value.toMutableList()
        currentNotifications.removeAll { it.id == id }
        notificationsFlow.value = currentNotifications
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        val currentNotifications = notificationsFlow.value.toMutableList()
        currentNotifications.removeAll { it.noteId == noteId }
        notificationsFlow.value = currentNotifications
    }

    override fun get(id: Long): Flow<NotificationEntity?> {
        return notificationsFlow.asStateFlow().map { notifications ->
            notifications.find { it.id == id }
        }
    }

    override fun getAll(): Flow<List<NotificationEntity>> {
        return notificationsFlow.asStateFlow()
    }

    override fun getByNoteId(noteId: Long): Flow<List<NotificationEntity>> {
        return notificationsFlow.asStateFlow().map { notifications ->
            notifications.filter { it.noteId == noteId }
        }
    }

    override suspend fun updateAlarmCount(noteId: Long, i: Int) {
        TODO("Not yet implemented")
    }
}
