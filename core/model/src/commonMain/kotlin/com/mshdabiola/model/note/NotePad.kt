package com.mshdabiola.model.note

data class NotePad(
    val id: Long = -1,
    val title: String = "",
    val detail: String = "",
    val editDate: Long = 0,
    val isCheck: Boolean = false,
    val color: Int = -1,
    val background: Int = -1,
    val isPin: Boolean = false,
    val noteCategory: NoteCategory = NoteCategory.NOTE,
    val notification: Notification? = null,
    val drawings: List<NoteDrawing> = emptyList(),
    val images: List<NoteImage> = emptyList(),
    val voices: List<NoteVoice> = emptyList(),
    val checks: List<NoteItem> = emptyList(),
    val labels: List<Label> = emptyList(),
    val uris: List<NoteLink> = emptyList(),
) {
    fun getVisuals(): List<NoteVisual> {
        return (drawings + images)
            .sortedBy { it.key }
    }

    override fun toString(): String {
        return """
            $title
            $detail
            ${checks.joinToString(separator = " ")}
        """.trimIndent()
    }

    fun isEmpty(): Boolean {
        val titleIsBlank = title.isBlank()
        val detailIsBlank = detail.isBlank()
        val emptyImage = getVisuals().isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && emptyImage && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }

    fun isImageOnly(): Boolean {
        val titleIsBlank = title.isBlank()
        val detailIsBlank = detail.isBlank()
        val emptyImage = images.isEmpty()
        val emptyDrawing = drawings.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && !emptyImage && emptyDrawing && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }

    fun isDrawingOnly(): Boolean {
        val titleIsBlank = title.isBlank()
        val detailIsBlank = detail.isBlank()
        val emptyImage = images.isEmpty()
        val emptyDrawing = drawings.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && emptyImage && !emptyDrawing && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }
}
