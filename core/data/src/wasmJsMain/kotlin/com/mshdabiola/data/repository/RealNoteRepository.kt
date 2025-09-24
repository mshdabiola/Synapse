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
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NotePad
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class RealNoteRepository(
    private val noteDao: NoteDao,
    private val dispatcher: CoroutineDispatcher,
) : NoteRepository {
    override suspend fun upserts(notes: List<NotePad>): List<Long> {
        return withContext(dispatcher) {
            noteDao.upserts(notes.map { it.asEntity() })
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun upsert(note: NotePad): Long {
        return withContext(dispatcher) {
            val now = Clock.System.now().toEpochMilliseconds()

            noteDao.upsert(note.copy(editDate = now).asEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            noteDao.delete(id)
        }
    }

    override suspend fun deleteIds(ids: Set<Long>) {
        withContext(dispatcher) {
            noteDao.deleteIds(ids)
        }
    }

    override suspend fun deleteTrash() {
        withContext(dispatcher) {
            noteDao.deleteTrash(NoteCategory.TRASH.ordinal)
        }
    }

    override fun getAll(): Flow<List<NotePad>> {
        return noteDao.getAll()
            .map { list -> list.map { it.asModel() } }
    }

    override fun get(id: Long): Flow<NotePad?> {
        return noteDao.get(id)
            .map { it?.asModel() }
    }

    override fun getByNoteType(noteType: NoteCategory): Flow<List<NotePad>> {
        return noteDao.getByNoteType(noteType.ordinal)
            .map { list -> list.map { it.asModel() } }
    }

    override fun getByNoteIds(set: Set<Long>): Flow<List<NotePad>> {
        return noteDao.getByIds(set)
            .map { list -> list.map { it.asModel() } }
    }

    override suspend fun updateColorForIds(ids: Set<Long>, color: Int) {
        withContext(dispatcher) {
            noteDao.updateColorForIds(ids, color)
        }
    }

    override suspend fun updatePinForIds(ids: Set<Long>, isPin: Boolean) {
        withContext(dispatcher) {
            noteDao.updatePinForIds(ids, isPin)
        }
    }

    override suspend fun updateNoteTypeForIds(
        ids: Set<Long>,
        noteType: NoteCategory,
    ) {
        withContext(dispatcher) {
            noteDao.updateNoteTypeForIds(ids, noteType.ordinal)
        }
    }
}
