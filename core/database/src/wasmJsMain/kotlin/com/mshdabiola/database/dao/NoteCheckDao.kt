package com.mshdabiola.database.dao

import com.mshdabiola.database.model.NoteItemEntity
import kotlinx.coroutines.flow.Flow


interface NoteCheckDao {

    suspend fun upserts(checks: List<NoteItemEntity>): List<Long>

    suspend fun upsert(check: NoteItemEntity): Long

    suspend fun delete(id: Long)

    suspend fun deleteCheckedItems(noteId: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun get(id: Long): Flow<NoteItemEntity?>

    fun getAll(): Flow<List<NoteItemEntity>>

    fun getByNoteId(noteId: Long): Flow<List<NoteItemEntity>>
}
