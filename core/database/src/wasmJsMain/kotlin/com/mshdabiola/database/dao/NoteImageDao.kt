package com.mshdabiola.database.dao

import com.mshdabiola.database.model.NoteImageEntity
import kotlinx.coroutines.flow.Flow

interface NoteImageDao {

    suspend fun upserts(images: List<NoteImageEntity>): List<Long>

    suspend fun upsert(image: NoteImageEntity): Long

    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun get(id: Long): Flow<NoteImageEntity?>

    fun getAll(): Flow<List<NoteImageEntity>>

    fun getByNoteId(noteId: Long): Flow<List<NoteImageEntity>>
}
