package com.mshdabiola.data.repository

import com.mshdabiola.data.model.asEntity
import com.mshdabiola.data.model.asModel
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.model.note.Label
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class RealLabelRepository(
    private val labelDao: LabelDao,
    private val dispatcher: CoroutineDispatcher,
) : LabelRepository {
    override suspend fun upserts(labels: List<Label>): List<Long> {
        return withContext(dispatcher) {
            labelDao.upserts(labels.map { it.asEntity() })
        }
    }

    override suspend fun upsert(label: Label): Long {
        return withContext(dispatcher) {
            labelDao.upsert(label.asEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            labelDao.delete(id)
        }
    }

    override fun getAll(): Flow<List<Label>> {
        return labelDao.getAll()
            .map { it.map { it.asModel() } }
    }

    override fun get(id: Long): Flow<Label?> {
        return labelDao.get(id)
            .map { it?.asModel() }
    }
}
