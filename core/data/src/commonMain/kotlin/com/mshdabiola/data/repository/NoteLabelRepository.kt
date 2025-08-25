package com.mshdabiola.data.repository

import com.mshdabiola.model.note.NoteLabelCrossRef
import kotlinx.coroutines.flow.Flow

interface NoteLabelRepository {
    suspend fun upserts(labels: List<NoteLabelCrossRef>): List<Long>

    suspend fun upsert(label: NoteLabelCrossRef): Long
    suspend fun deleteByNoteId(id: Long)

    suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long)

    fun getAll(): Flow<List<NoteLabelCrossRef>>
    fun getByNoteId(noteId: Long): Flow<List<NoteLabelCrossRef>>

    fun getByLabelId(labelId: Long): Flow<List<NoteLabelCrossRef>>
    fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabelCrossRef>>
}
