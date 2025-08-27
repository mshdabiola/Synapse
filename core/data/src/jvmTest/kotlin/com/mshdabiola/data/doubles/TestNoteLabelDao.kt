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

import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.model.NoteLabelCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestNoteLabelDao : NoteLabelDao {
    private val noteLabelCrossRefsFlow = MutableStateFlow<List<NoteLabelCrossRef>>(emptyList())
    // No nextId needed as NoteLabelCrossRef relies on composite keys

    override suspend fun upsert(label: NoteLabelCrossRef): Long {
        val currentCrossRefs = noteLabelCrossRefsFlow.value.toMutableList()
        // Remove existing entry with the same composite key, then add the new one
        currentCrossRefs.removeAll { it.noteId == label.noteId && it.labelId == label.labelId }
        currentCrossRefs.add(label)
        noteLabelCrossRefsFlow.value = currentCrossRefs
        return 1L // Signify one record was processed/affected
    }

    override suspend fun upserts(labels: List<NoteLabelCrossRef>): List<Long> {
        val ids = mutableListOf<Long>()
        labels.forEach {
            ids.add(upsert(it)) // Each upsert returns 1L
        }
        return ids
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        val currentCrossRefs = noteLabelCrossRefsFlow.value.toMutableList()
        currentCrossRefs.removeAll { it.noteId == noteId }
        noteLabelCrossRefsFlow.value = currentCrossRefs
    }

    override suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long) {
        val currentCrossRefs = noteLabelCrossRefsFlow.value.toMutableList()
        currentCrossRefs.removeAll { it.noteId == noteId && it.labelId == labelId }
        noteLabelCrossRefsFlow.value = currentCrossRefs
    }

    override fun getAll(): Flow<List<NoteLabelCrossRef>> {
        return noteLabelCrossRefsFlow.asStateFlow()
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteLabelCrossRef>> {
        return noteLabelCrossRefsFlow.asStateFlow().map { crossRefs ->
            crossRefs.filter { it.noteId == noteId }
        }
    }

    override fun getByLabelId(labelId: Long): Flow<List<NoteLabelCrossRef>> {
        return noteLabelCrossRefsFlow.asStateFlow().map { crossRefs ->
            crossRefs.filter { it.labelId == labelId }
        }
    }

    override fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabelCrossRef>> {
        return noteLabelCrossRefsFlow.asStateFlow().map { crossRefs ->
            crossRefs.filter { it.noteId in ids }
        }
    }
}
