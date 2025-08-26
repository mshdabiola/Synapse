package com.mshdabiola.data.repository

import com.mshdabiola.data.model.asEntity
import com.mshdabiola.data.model.asModel
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.model.note.NoteDrawing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class RealNoteDrawingRepository (
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
