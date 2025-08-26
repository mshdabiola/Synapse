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
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.model.NoteLabelCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RealNoteLabelDataSource(private val database: NoteDataBase) : NoteLabelDao {

    override suspend fun upsert(label: NoteLabelCrossRef): Long {
        database.noteLabelCrossRefTable.update { list ->
            val currentList = list ?: emptyList()
            // Remove existing entry with the same composite key, then add the new one
            // This mimics "INSERT OR REPLACE" behavior of Room's @Upsert
            val filteredList = currentList.filterNot { it.noteId == label.noteId && it.labelId == label.labelId }
            filteredList + label
        }
        return 1L // Signify one record was processed/affected
    }

    override suspend fun upserts(labels: List<NoteLabelCrossRef>): List<Long> {
        val resultingIds = mutableListOf<Long>()
        // A more KStore-idiomatic batch upsert could be done in a single update block:
        database.noteLabelCrossRefTable.update { list ->
            var currentListMutable = list?.toMutableList() ?: mutableListOf()
            labels.forEach { newLabel ->
                // Remove old if exists
                currentListMutable.removeAll { it.noteId == newLabel.noteId && it.labelId == newLabel.labelId }
                // Add new
                currentListMutable.add(newLabel)
                resultingIds.add(1L) // Add 1L for each processed label
            }
            currentListMutable
        }
        return resultingIds
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        database.noteLabelCrossRefTable.update { currentList ->
            currentList?.filterNot { it.noteId == noteId } ?: emptyList()
        }
    }

    override suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long) {
        database.noteLabelCrossRefTable.update { currentList ->
            currentList?.filterNot { it.noteId == noteId && it.labelId == labelId } ?: emptyList()
        }
    }

    override fun getAll(): Flow<List<NoteLabelCrossRef>> {
        return database.noteLabelCrossRefTable.updates.map { list ->
            list ?: emptyList()
        }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteLabelCrossRef>> {
        return database.noteLabelCrossRefTable.updates.map { list ->
            list?.filter { it.noteId == noteId } ?: emptyList()
        }
    }

    override fun getByLabelId(labelId: Long): Flow<List<NoteLabelCrossRef>> {
        return database.noteLabelCrossRefTable.updates.map { list ->
            list?.filter { it.labelId == labelId } ?: emptyList()
        }
    }

    override fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabelCrossRef>> {
        return database.noteLabelCrossRefTable.updates.map { list ->
            list?.filter { it.noteId in ids } ?: emptyList()
        }
    }
}
