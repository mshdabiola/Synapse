package com.mshdabiola.main.model

import com.mshdabiola.model.note.NotePad

sealed class SearchState {

    data class FilterState(

        val types: List<SearchSort.Type> = emptyList(),
        val color: List<SearchSort.Color> = emptyList(),
        val label: List<SearchSort.Label> = emptyList(),
    ) : com.mshdabiola.main.model.SearchState()

    data class SearchState(
        val searches: List<NotePad> = emptyList(),
        val isGrid: Boolean = false,
        val searchSort: SearchSort? = null,

        ) : com.mshdabiola.main.model.SearchState()
}
