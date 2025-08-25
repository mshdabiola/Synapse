package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteCheckDao {

    @Upsert
    suspend fun upserts(checks: List<NoteItemEntity>): List<Long>

    @Upsert
    suspend fun upsert(check: NoteItemEntity): Long

    @Query("DELETE FROM note_check_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM note_check_table WHERE isCheck = 1 AND note_id = :noteId")
    suspend fun deleteCheckedItems(noteId: Long)

    @Query("DELETE FROM note_check_table WHERE note_id = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("SELECT * FROM note_check_table WHERE id = :id")
    fun get(id: Long): Flow<NoteItemEntity?>

    @Query("SELECT * FROM note_check_table")
    fun getAll(): Flow<List<NoteItemEntity>>

    @Query("SELECT * FROM note_check_table WHERE note_id = :noteId")
    fun getByNoteId(noteId: Long): Flow<List<NoteItemEntity>>
}
