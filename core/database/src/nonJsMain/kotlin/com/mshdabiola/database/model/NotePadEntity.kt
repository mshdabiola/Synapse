package com.mshdabiola.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NotePadEntity(
    @Embedded
    val noteEntity: NoteEntity,
    @Relation(parentColumn = "id", entityColumn = "note_id")
    val notification: NotificationEntity?,
    @Relation(parentColumn = "id", entityColumn = "note_id")
    val images: List<NoteImageEntity>,
    @Relation(parentColumn = "id", entityColumn = "note_id")
    val voices: List<NoteVoiceEntity>,
    @Relation(parentColumn = "id", entityColumn = "note_id")
    val checks: List<NoteItemEntity>,
    @Relation(parentColumn = "id", entityColumn = "note_id")
    val drawings: List<NoteDrawingEntity>,

    @Relation(
        parentColumn = "id", // This 'id' is from the embedded NoteEntity (PrimaryKey of NoteEntity)
        entity = LabelEntity::class, // Explicitly state the target entity (good practice)
        entityColumn = "id", // This 'id' is from LabelEntity (PrimaryKey of LabelEntity)
        associateBy = Junction(
            value = NoteLabelCrossRef::class,
            parentColumn = "note_id", // Column in NoteLabelCrossRef that references NoteEntity's PK
            entityColumn = "labelId", // Column in NoteLabelCrossRef that references LabelEntity's PK
        ),
    )
    val labels: List<LabelEntity>,
)
