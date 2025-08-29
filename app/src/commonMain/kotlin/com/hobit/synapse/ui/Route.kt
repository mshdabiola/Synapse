package com.hobit.synapse.ui

import com.mshdabiola.model.note.NoteDisplayCategory

sealed class Route(val path: Any) {
    data class Main(
       val noteDisplayCategory: NoteDisplayCategory
    ) : Route(com.mshdabiola.main.navigation.Main)
    object Setting : Route(com.mshdabiola.setting.navigation.Setting)
}
