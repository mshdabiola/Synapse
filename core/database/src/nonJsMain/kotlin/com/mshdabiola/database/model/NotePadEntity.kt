/*
 * Designed and developed by 2024 mshdabiola (lawal abiola)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
