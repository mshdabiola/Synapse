package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteLabelCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteLabelDao {

    @Upsert
    suspend fun upserts(labels: List<NoteLabelCrossRef>): List<Long>

    @Upsert
    suspend fun upsert(label: NoteLabelCrossRef): Long

    @Query("DELETE FROM note_label_table WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("DELETE FROM note_label_table WHERE noteId = :noteId AND labelId = :labelId")
    suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long)

    @Query("SELECT * FROM note_label_table")
    fun getAll(): Flow<List<NoteLabelCrossRef>>

    @Query("SELECT * FROM note_label_table WHERE noteId = :noteId")
    fun getByNoteId(noteId: Long): Flow<List<NoteLabelCrossRef>>

    @Query("SELECT * FROM note_label_table WHERE labelId = :labelId")
    fun getByLabelId(labelId: Long): Flow<List<NoteLabelCrossRef>>

    @Query("SELECT * FROM note_label_table WHERE noteId IN (:ids)")
    fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabelCrossRef>>
}
