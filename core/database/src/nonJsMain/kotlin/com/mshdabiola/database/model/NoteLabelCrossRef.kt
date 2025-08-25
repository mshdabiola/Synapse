package com.mshdabiola.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "note_label_table",
    primaryKeys = ["note_id", "labelId"],
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = LabelEntity::class,
            parentColumns = ["id"],
            childColumns = ["labelId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class NoteLabelCrossRef(
    @ColumnInfo(name = "note_id", index = true)
    val noteId: Long,
    @ColumnInfo(index = true)
    val labelId: Long,
)
