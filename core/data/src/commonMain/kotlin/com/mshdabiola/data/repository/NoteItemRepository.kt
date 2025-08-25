package com.mshdabiola.data.repository

import com.mshdabiola.model.note.NoteItem
import kotlinx.coroutines.flow.Flow

interface NoteItemRepository {

    suspend fun upserts(checks: List<NoteItem>): List<Long>

    suspend fun upsert(check: NoteItem): Long
    suspend fun delete(id: Long)

    suspend fun deleteCheckedItems(noteId: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun getAll(): Flow<List<NoteItem>>
    fun getByNoteId(noteId: Long): Flow<List<NoteItem>>

    fun get(id: Long): Flow<NoteItem?>
}
