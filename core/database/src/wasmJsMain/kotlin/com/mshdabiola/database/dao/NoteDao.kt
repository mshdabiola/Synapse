package com.mshdabiola.database.dao


import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NotePadEntity
import kotlinx.coroutines.flow.Flow

interface NoteDao {

    suspend fun upsert(noteEntity: NoteEntity): Long

    suspend fun upserts(noteEntity: List<NoteEntity>): List<Long>

    suspend fun delete(id: Long)

    suspend fun deleteIds(ids: Set<Long>)

    suspend fun deleteTrash(noteType: Int)


    fun getByNoteType(noteType: Int): Flow<List<NotePadEntity>>

//    @Transaction
//    @Query("SELECT * FROM note_table WHERE reminder > 0 ORDER BY id DESC")
//    fun getListOfNotePadByReminder(): Flow<List<NotePadEntity>>


    fun getAll(): Flow<List<NotePadEntity>>


    fun get(noteId: Long): Flow<NotePadEntity?>

    fun getByIds(ids: Set<Long>): Flow<List<NotePadEntity>> // Define a return type
}
