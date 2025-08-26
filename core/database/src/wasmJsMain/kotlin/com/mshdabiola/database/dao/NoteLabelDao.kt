package com.mshdabiola.database.dao

import com.mshdabiola.database.model.NoteLabelCrossRef
import kotlinx.coroutines.flow.Flow

interface NoteLabelDao {

    suspend fun upserts(labels: List<NoteLabelCrossRef>): List<Long>

    suspend fun upsert(label: NoteLabelCrossRef): Long

    suspend fun deleteByNoteId(noteId: Long)

    suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long)

    fun getAll(): Flow<List<NoteLabelCrossRef>>

    fun getByNoteId(noteId: Long): Flow<List<NoteLabelCrossRef>>

    fun getByLabelId(labelId: Long): Flow<List<NoteLabelCrossRef>>

    fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabelCrossRef>>
}
