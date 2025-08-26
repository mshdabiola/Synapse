package com.mshdabiola.database.dao


import com.mshdabiola.database.model.NoteVoiceEntity
import kotlinx.coroutines.flow.Flow

interface NoteVoiceDao {

    suspend fun upserts(voices: List<NoteVoiceEntity>): List<Long>

    suspend fun upsert(voice: NoteVoiceEntity): Long

    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun get(id: Long): Flow<NoteVoiceEntity?>

    fun getAll(): Flow<List<NoteVoiceEntity>>

    fun getByNoteId(noteId: Long): Flow<List<NoteVoiceEntity>>
}
