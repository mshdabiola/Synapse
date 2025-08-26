package com.mshdabiola.database.dao


import com.mshdabiola.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NoteNotificationDao {

    suspend fun upserts(notifications: List<NotificationEntity>): List<Long>

    suspend fun upsert(notification: NotificationEntity): Long

    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun get(id: Long): Flow<NotificationEntity?>

    fun getAll(): Flow<List<NotificationEntity>>

    fun getByNoteId(noteId: Long): Flow<List<NotificationEntity>>
}
