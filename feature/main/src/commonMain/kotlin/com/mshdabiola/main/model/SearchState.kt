package com.mshdabiola.main.model

import com.mshdabiola.model.note.NotePad

sealed class SearchState {

    data class Select(

        val types: List<SearchSort.Type> = emptyList(),
        val color: List<SearchSort.Color> = emptyList(),
        val label: List<SearchSort.Label> = emptyList(),
    ) : SearchState()

    data class Success(
        val searches: List<NotePad> = emptyList(),
        val isGrid: Boolean = false,
        val searchSort: SearchSort? = null,

        ) : SearchState()
}
