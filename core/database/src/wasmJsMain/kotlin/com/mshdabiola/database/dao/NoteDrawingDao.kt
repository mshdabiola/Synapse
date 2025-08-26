package com.mshdabiola.database.dao


import com.mshdabiola.database.model.NoteDrawingEntity
import kotlinx.coroutines.flow.Flow

interface NoteDrawingDao {

    suspend fun upserts(drawings: List<NoteDrawingEntity>): List<Long>

    suspend fun upsert(drawing: NoteDrawingEntity): Long

    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun get(id: Long): Flow<NoteDrawingEntity?>

    fun getAll(): Flow<List<NoteDrawingEntity>>

    fun getByNoteId(noteId: Long): Flow<List<NoteDrawingEntity>>
}
