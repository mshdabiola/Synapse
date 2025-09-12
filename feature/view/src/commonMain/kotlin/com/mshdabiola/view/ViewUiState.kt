package com.mshdabiola.view

import com.mshdabiola.model.note.NoteImage


data class ViewUiState(
    val images: List<NoteImage> = emptyList(),
    val initIndex: Int = 0,
)
