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
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.model.note.NoteImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class RealNoteImageRepository(
    private val noteImageDao: NoteImageDao,
    private val dispatcher: CoroutineDispatcher,
) : NoteImageRepository {
    override suspend fun upserts(images: List<NoteImage>): List<Long> {
        return withContext(dispatcher) {
            noteImageDao.upserts(images.map { it.asEntity() })
        }
    }

    override suspend fun upsert(image: NoteImage): Long {
        return withContext(dispatcher) {
            noteImageDao.upsert(image.asEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            noteImageDao.delete(id)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        withContext(dispatcher) {
            noteImageDao.deleteByNoteId(noteId)
        }
    }

    override fun getAll(): Flow<List<NoteImage>> {
        return noteImageDao.getAll()
            .map { list -> list.map { it.asModel() } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteImage>> {
        return noteImageDao.getByNoteId(noteId)
            .map { list -> list.map { it.asModel() } }
    }

    override fun get(id: Long): Flow<NoteImage?> {
        return noteImageDao.get(id)
            .map { it?.asModel() }
    }
}
