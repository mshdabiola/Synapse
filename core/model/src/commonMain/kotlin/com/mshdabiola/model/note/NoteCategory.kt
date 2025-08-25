package com.mshdabiola.model.note

enum class NoteCategory(val index: Long = 0) {
    NOTE(-1),
    ARCHIVE(-2),
    TRASH(-3),
    LABEL,
    REMINDER(-4),
}
