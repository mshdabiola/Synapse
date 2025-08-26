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

internal class RealNoteRepository(
    private val noteDao: NoteDao,
    private val dispatcher: CoroutineDispatcher,
) : NoteRepository {
    override suspend fun upserts(notes: List<NotePad>): List<Long> {
        return withContext(dispatcher) {
            noteDao.upserts(notes.map { it.asEntity() })
        }
    }

    override suspend fun upsert(note: NotePad): Long {
        return withContext(dispatcher) {
            noteDao.upsert(note.asEntity())
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
}
