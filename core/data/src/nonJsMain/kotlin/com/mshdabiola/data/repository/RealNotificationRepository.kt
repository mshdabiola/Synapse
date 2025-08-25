package com.mshdabiola.data.repository

import com.mshdabiola.data.model.asEntity
import com.mshdabiola.data.model.asModel
import com.mshdabiola.database.dao.NoteNotificationDao
import com.mshdabiola.model.note.Notification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class RealNotificationRepository(
    private val notificationDao: NoteNotificationDao,
    private val dispatcher: CoroutineDispatcher,
) : NoteNotificationRepository {
    override suspend fun upserts(notifications: List<Notification>): List<Long> {
        return withContext(dispatcher) {
            notificationDao.upserts(notifications.map { it.asEntity() })
        }
    }

    override suspend fun upsert(notification: Notification): Long {
        return withContext(dispatcher) {
            notificationDao.upsert(notification.asEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            notificationDao.delete(id)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        withContext(dispatcher) {
            notificationDao.deleteByNoteId(noteId)
        }
    }

    override fun getAll(): Flow<List<Notification>> {
        return notificationDao.getAll().map {
            it.map { it.asModel() }
        }
    }

    override fun getByNoteId(noteId: Long): Flow<List<Notification>> {
        return notificationDao.getByNoteId(noteId).map {
            it.map { it.asModel() }
        }
    }

    override fun get(id: Long): Flow<Notification?> {
        return notificationDao.get(id).map {
            it?.asModel()
        }
    }
}
