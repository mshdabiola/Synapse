package com.mshdabiola.data.repository

import com.mshdabiola.data.model.asEntity
import com.mshdabiola.data.model.asModel
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.model.note.NoteLabelCrossRef
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class RealNoteLabelRepository(
    private val noteLabelDao: NoteLabelDao,
    private val dispatcher: CoroutineDispatcher,
) : NoteLabelRepository {
    override suspend fun upserts(labels: List<NoteLabelCrossRef>): List<Long> {
        return withContext(dispatcher) {
            noteLabelDao.upserts(labels.map { it.asEntity() })
        }
    }

    override suspend fun upsert(label: NoteLabelCrossRef): Long {
        return withContext(dispatcher) {
            noteLabelDao.upsert(label.asEntity())
        }
    }

    override suspend fun deleteByNoteId(id: Long) {
        withContext(dispatcher) {
            noteLabelDao.deleteByNoteId(id)
        }
    }

    override suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long) {
        withContext(dispatcher) {
            noteLabelDao.deleteByNoteIdAndLabelId(noteId, labelId)
        }
    }

    override fun getAll(): Flow<List<NoteLabelCrossRef>> {
        return noteLabelDao.getAll()
            .map { list -> list.map { it.asModel() } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteLabelCrossRef>> {
        return noteLabelDao.getByNoteId(noteId)
            .map { list -> list.map { it.asModel() } }
    }

    override fun getByLabelId(labelId: Long): Flow<List<NoteLabelCrossRef>> {
        return noteLabelDao
            .getByLabelId(labelId = labelId)
            .map { list -> list.map { it.asModel() } }
    }

    override fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabelCrossRef>> {
        return noteLabelDao.getByNoteIds(ids)
            .map { list -> list.map { it.asModel() } }
    }
}
