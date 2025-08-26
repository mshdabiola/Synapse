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
