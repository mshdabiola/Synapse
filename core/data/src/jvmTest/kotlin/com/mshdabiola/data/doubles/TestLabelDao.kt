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

import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.model.LabelEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestLabelDao : LabelDao {
    private val labelsFlow = MutableStateFlow<List<LabelEntity>>(emptyList())
    private var nextId = 1L

    override suspend fun upsert(label: LabelEntity): Long {
        val currentLabels = labelsFlow.value.toMutableList()
        val newEntity = if (label.id == null || label.id == 0L) {
            label.copy(id = nextId++)
        } else {
            label
        }

        val index = currentLabels.indexOfFirst { it.id == newEntity.id }
        if (index != -1) {
            currentLabels[index] = newEntity // Update existing
        } else {
            currentLabels.add(newEntity) // Add new
        }
        labelsFlow.value = currentLabels
        return newEntity.id ?: 0L // Should have an ID now
    }

    override suspend fun upserts(labels: List<LabelEntity>): List<Long> {
        val ids = mutableListOf<Long>()
        labels.forEach {
            ids.add(upsert(it))
        }
        return ids
    }

    override suspend fun delete(id: Long) {
        val currentLabels = labelsFlow.value.toMutableList()
        currentLabels.removeAll { it.id == id }
        labelsFlow.value = currentLabels
    }

    override fun get(id: Long): Flow<LabelEntity?> {
        return labelsFlow.asStateFlow().map { labels ->
            labels.find { it.id == id }
        }
    }

    override fun getAll(): Flow<List<LabelEntity>> {
        return labelsFlow.asStateFlow()
    }
}
