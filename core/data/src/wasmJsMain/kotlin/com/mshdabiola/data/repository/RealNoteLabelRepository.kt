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
