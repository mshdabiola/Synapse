package com.mshdabiola.main.model

import com.mshdabiola.model.note.Notification

data class SelectState(
    val colorIndex: Int = -1,
    val isAllPin: Boolean = false,
    val setOfSelected: Set<Long> = emptySet(),
    val notificationUiState: Notification? = null,

    )
