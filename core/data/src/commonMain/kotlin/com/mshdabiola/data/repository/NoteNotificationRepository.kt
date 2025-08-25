package com.mshdabiola.data.repository


import com.mshdabiola.model.note.Notification
import kotlinx.coroutines.flow.Flow

interface NoteNotificationRepository {
    suspend fun upserts(notifications: List<Notification>): List<Long>

    suspend fun upsert(notification: Notification): Long
    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun getAll(): Flow<List<Notification>>
    fun getByNoteId(noteId: Long): Flow<List<Notification>>

    fun get(id: Long): Flow<Notification?>
}
