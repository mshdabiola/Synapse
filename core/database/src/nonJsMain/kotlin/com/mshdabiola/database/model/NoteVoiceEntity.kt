package com.mshdabiola.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_voice_table",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class NoteVoiceEntity(
    @PrimaryKey()
    val id: Long,
    @ColumnInfo(name = "note_id", index = true)
    val noteId: Long,
    val path: String,
)
