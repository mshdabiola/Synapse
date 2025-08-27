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
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.model.LabelEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RealLabelDataSource(private val database: NoteDataBase) : LabelDao {

    override suspend fun upsert(label: LabelEntity): Long {
        var resultingId: Long? = null
        database.labelTable.update { list ->
            val currentList = list ?: emptyList()
            if (label.id == null) { // New label
                // Generate new ID (max existing ID + 1, or 1 if list is empty)
                val newId = (currentList.maxByOrNull { it.id ?: 0L }?.id ?: 0L) + 1L
                resultingId = newId
                currentList + label.copy(id = newId)
            } else { // Existing label or label with pre-assigned ID
                resultingId = label.id
                val index = currentList.indexOfFirst { it.id == label.id }
                if (index != -1) { // Found, update it
                    currentList.toMutableList().apply { this[index] = label }
                } else { // Not found, add it
                    currentList + label
                }
            }
        }
        return resultingId!! // Should be non-null as it's set in both branches
    }

    override suspend fun upserts(labels: List<LabelEntity>): List<Long> {
        val resultingIds = mutableListOf<Long>()
        labels.forEach { label ->
            val id = upsert(label)
            resultingIds.add(id)
        }
        return resultingIds
    }

    override suspend fun delete(id: Long) {
        database.labelTable.update { currentList ->
            currentList?.filterNot { it.id == id } ?: emptyList()
        }
    }

    override fun get(id: Long): Flow<LabelEntity?> {
        return database.labelTable.updates.map { list ->
            // list is List<LabelEntity>?
            list?.firstOrNull { it.id == id }
        }
    }

    override fun getAll(): Flow<List<LabelEntity>> {
        return database.labelTable.updates.map { list ->
            // list is List<LabelEntity>?
            list ?: emptyList() // If KStore is null, emit empty list
        }
    }
}
