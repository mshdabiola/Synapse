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
