package com.mshdabiola.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_image_table",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class NoteImageEntity(
    @PrimaryKey()
    val id: Long,
    @ColumnInfo(name = "note_id", index = true)
    val noteId: Long,
)
