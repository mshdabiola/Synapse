package com.mshdabiola.data.repository

import com.mshdabiola.data.model.asEntity
import com.mshdabiola.data.model.asModel
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.model.note.NoteVoice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class RealNoteVoiceRepository(
    private val noteVoiceDao: NoteVoiceDao,
    private val dispatcher: CoroutineDispatcher,
) : NoteVoiceRepository {
    override suspend fun upserts(voices: List<NoteVoice>): List<Long> {
        return withContext(dispatcher) {
            noteVoiceDao.upserts(voices.map { it.asEntity() })
        }
    }

    override suspend fun upsert(voice: NoteVoice): Long {
        return withContext(dispatcher) {
            noteVoiceDao.upsert(voice.asEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            noteVoiceDao.delete(id)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        withContext(dispatcher) {
            noteVoiceDao.deleteByNoteId(noteId)
        }
    }

    override fun getAll(): Flow<List<NoteVoice>> {
        return noteVoiceDao.getAll()
            .map { list -> list.map { it.asModel() } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteVoice>> {
        return noteVoiceDao.getByNoteId(noteId)
            .map { list -> list.map { it.asModel() } }
    }

    override fun get(id: Long): Flow<NoteVoice?> {
        return noteVoiceDao.get(id)
            .map { it?.asModel() }
    }
}
