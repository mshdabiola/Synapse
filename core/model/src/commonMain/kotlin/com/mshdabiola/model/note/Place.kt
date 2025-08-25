package com.mshdabiola.model.note

sealed class Place {
    data class Edit(val place: String) : Place()
    data object Home : Place()
    data object Work : Place()
    data object School : Place()
}
