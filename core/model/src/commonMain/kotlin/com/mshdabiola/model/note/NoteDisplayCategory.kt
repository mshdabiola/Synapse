package com.mshdabiola.model.note


data class NoteDisplayCategory(
    val labelId: Long = 1,
    val noteCategory: NoteCategory = NoteCategory.NOTE,
)
