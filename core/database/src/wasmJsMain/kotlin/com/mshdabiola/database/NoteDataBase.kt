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
package com.mshdabiola.database

import com.mshdabiola.database.model.LabelEntity
import com.mshdabiola.database.model.NoteDrawingEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteImageEntity
import com.mshdabiola.database.model.NoteItemEntity
import com.mshdabiola.database.model.NoteLabelCrossRef
import com.mshdabiola.database.model.NoteVoiceEntity
import com.mshdabiola.database.model.NotificationEntity
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storage.storeOf

class NoteDataBase {
    val labelTable: KStore<List<LabelEntity>> = storeOf(key = "label_table", default = listOf())
    val noteDrawingTable: KStore<List<NoteDrawingEntity>> = storeOf(key = "note_drawing_table", default = listOf())
    val noteTable: KStore<List<NoteEntity>> = storeOf(key = "note_table", default = listOf())
    val noteImageTable: KStore<List<NoteImageEntity>> = storeOf(key = "note_image_table", default = listOf())
    val noteItemTable: KStore<List<NoteItemEntity>> = storeOf(key = "note_item_table", default = listOf())
    val noteLabelCrossRefTable: KStore<List<NoteLabelCrossRef>> = storeOf(
        key = "note_label_cross_ref_table",
        default = listOf(),
    )
    val noteVoiceTable: KStore<List<NoteVoiceEntity>> = storeOf(key = "note_voice_table", default = listOf())
    val notificationTable: KStore<List<NotificationEntity>> = storeOf(key = "notification_table", default = listOf())
}
