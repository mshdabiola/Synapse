package com.mshdabiola.database.model

data class NotePadEntity(
    val noteEntity: NoteEntity,
    val notification: NotificationEntity?,
    val images: List<NoteImageEntity>,
    val voices: List<NoteVoiceEntity>,
    val checks: List<NoteItemEntity>,
    val drawings: List<NoteDrawingEntity>,
    val labels: List<LabelEntity>,
)
