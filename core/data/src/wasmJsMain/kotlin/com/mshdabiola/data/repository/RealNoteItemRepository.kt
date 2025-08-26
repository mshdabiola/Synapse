package com.mshdabiola.data.repository

import com.mshdabiola.data.model.asEntity
import com.mshdabiola.data.model.asModel
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.model.note.NoteItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class RealNoteItemRepository(
    private val noteCheckDao: NoteCheckDao,
    private val dispatcher: CoroutineDispatcher,
) : NoteItemRepository{
    override suspend fun upserts(checks: List<NoteItem>): List<Long> {
        return withContext(dispatcher) {
            noteCheckDao.upserts(checks.map { it.asEntity() })
        }
    }

    override suspend fun upsert(check: NoteItem): Long {
        return withContext(dispatcher) {
            noteCheckDao.upsert(check.asEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            noteCheckDao.delete(id)
        }
    }
    override suspend fun deleteCheckedItems(noteId: Long) {
        withContext(dispatcher) {
            noteCheckDao.deleteCheckedItems(noteId)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        withContext(dispatcher) {
            noteCheckDao.deleteByNoteId(noteId)
        }
    }

    override fun getAll(): Flow<List<NoteItem>> {
        return noteCheckDao.getAll()
            .map { list -> list.map { it.asModel() } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteItem>> {
        return noteCheckDao.getByNoteId(noteId)
            .map { list -> list.map { it.asModel() } }
    }

    override fun get(id: Long): Flow<NoteItem?> {
        return noteCheckDao.get(id)
            .map { it?.asModel() }
    }
}
