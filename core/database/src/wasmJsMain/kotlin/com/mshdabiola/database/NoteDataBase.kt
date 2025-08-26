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

class NoteDataBase{
     val labelTable: KStore<List<LabelEntity>> = storeOf(key = "label_table", default = listOf())
    val noteDrawingTable: KStore<List<NoteDrawingEntity>> = storeOf(key = "note_drawing_table", default = listOf())
    val noteTable: KStore<List<NoteEntity>> = storeOf(key = "note_table", default = listOf())
    val noteImageTable: KStore<List<NoteImageEntity>> = storeOf(key = "note_image_table", default = listOf())
    val noteItemTable: KStore<List<NoteItemEntity>> = storeOf(key = "note_item_table", default = listOf())
    val noteLabelCrossRefTable: KStore<List<NoteLabelCrossRef>> = storeOf(key = "note_label_cross_ref_table",
        default = listOf())
    val noteVoiceTable: KStore<List<NoteVoiceEntity>> = storeOf(key = "note_voice_table", default = listOf())
    val notificationTable: KStore<List<NotificationEntity>> = storeOf(key = "notification_table", default = listOf())

}
