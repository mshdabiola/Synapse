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
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.model.note.NoteDrawing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class RealNoteDrawingRepository(
    private val noteDrawingDao: NoteDrawingDao,

    private val dispatcher: CoroutineDispatcher,
) : NoteDrawingRepository {
    override suspend fun upserts(drawings: List<NoteDrawing>): List<Long> {
        return withContext(dispatcher) {
            noteDrawingDao.upserts(drawings.map { it.asEntity() })
        }
    }

    override suspend fun upsert(drawing: NoteDrawing): Long {
        return withContext(dispatcher) {
            noteDrawingDao.upsert(drawing.asEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            noteDrawingDao.delete(id)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        withContext(dispatcher) {
            noteDrawingDao.deleteByNoteId(noteId)
        }
    }

    override fun getAll(): Flow<List<NoteDrawing>> {
        return noteDrawingDao.getAll()
            .map { list -> list.map { it.asModel() } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteDrawing>> {
        return noteDrawingDao.getByNoteId(noteId)
            .map { list -> list.map { it.asModel() } }
    }

    override fun get(id: Long): Flow<NoteDrawing?> {
        return noteDrawingDao.get(id)
            .map { it?.asModel() }
    }
}
