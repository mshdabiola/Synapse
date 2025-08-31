/*
 * Designed and developed by 2024 mshdabiola (lawal abiola)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mshdabiola.main

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.GetAllNoteUseCase
import com.mshdabiola.main.model.MainState
import com.mshdabiola.main.model.SearchSort
import com.mshdabiola.main.model.SearchState
import com.mshdabiola.main.model.SelectState
import com.mshdabiola.model.UserSettings
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.Notification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
internal class MainViewModel(
    private val noteRepository: NoteRepository,
    private val userDataRepository: UserDataRepository,
    private val labelRepository: LabelRepository,
    private val getAllNoteUseCase: GetAllNoteUseCase,
    private val addAllNoteUseCase: AddAllNoteUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val isSearchState = MutableStateFlow<Boolean>(false)
    private var isInitSearchState = false

    val searchQuery = TextFieldState()
    private val searchSort = MutableStateFlow<SearchSort?>(null)
    private var isTextAfterSearchSort = false


    private val selectedNotesState = MutableStateFlow<SelectState?>(null)
    private val currentNotepads = userDataRepository
        .userSettings
        .mapLatest { it.noteCategory }
        .flatMapLatest {
            getAllNoteUseCase.invoke(it)
        }

    private val label = userDataRepository
        .userSettings
        .mapLatest { it.noteCategory }
        .flatMapLatest {
            labelRepository.get(it.labelId)
        }

    val mainState = combine(
        currentNotepads,
        label,
        selectedNotesState,
        userDataRepository.userSettings,
        isSearchState,
        snapshotFlow { searchQuery.text }
            .debounce(200),
        searchSort,
    ) { arrays ->

        //) { notepad, label, selectState, userSettings, isSearch,query, searchSorts,->
        val notepad = arrays[0] as List<NotePad>
        val label = arrays[1] as Label?
        val selectState = arrays[2] as SelectState?
        val userSettings = arrays[3] as UserSettings
        val isSearch = arrays[4] as Boolean
        val query = arrays[5] as CharSequence
        val searchSorts = arrays[6] as SearchSort?

        when {
            isSearch && isInitSearchState -> {

                isInitSearchState = false
                onBlankSearch(emptyList())
            }

            isSearch -> {
                if (query.isBlank() && searchSorts == null) {
                    onBlankSearch(notepad)
                } else {
                    val list = onSearch(query.toString(), searchSorts, notepad)
                    MainState.SearchState(
                        searches = list,
                        isGrid = userSettings.isGrid,
                        searchSort = searchSorts,
                    )
                }
            }

            else -> {
                val pinNote = notepad.filter { it.isPin }
                val unPinNote = notepad.filter { !it.isPin }
                MainState.ViewState(
                    labelName = label?.name,
                    pinNotePads = pinNote,
                    unPinNotePads = unPinNote,
                    noteDisplayCategory = userSettings.noteCategory,
                    selectState = selectState,
                    isGrid = userSettings.isGrid,
                )
            }
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MainState.Loading,
    )

    private fun getSelectState(): SelectState {
        return selectedNotesState.value ?: SelectState()
    }

    private fun getAllNotePad(): List<NotePad> {
        return getSuccess().unPinNotePads + getSuccess().pinNotePads
    }

    fun handleCardSelection(id: Long) {
        val state = getSelectState()

        if (state.setOfSelected.contains(id) && state.setOfSelected.size == 1) {
            deselectNotes()
            return
        }

        val setOfSelected = if (state.setOfSelected.contains(id)) {
            state.setOfSelected - id
        } else {
            state.setOfSelected + id
        }
        var notificationUiState: Notification? = null
        var colorIndex = -1
        if (setOfSelected.size == 1) {
            val notepad = getAllNotePad().single { it.id == setOfSelected.first() }
            colorIndex = notepad.color
            notificationUiState = notepad.notification
        }

        val isAllPin = getAllNotePad()
            .filter { setOfSelected.contains(it.id) }
            .all { it.isPin }

        selectedNotesState.value = state.copy(
            setOfSelected = setOfSelected,
            isAllPin = isAllPin,
            colorIndex = colorIndex,
            notificationUiState = notificationUiState,
        )
    }

    fun deselectNotes() {
        selectedNotesState.value = null
    }

    //Todo(Functions like pinOrUnpinNotes, setAllColor, and others iterate over selected notes and call addAllNoteUseCase for each item. This can cause performance issues (N+1 problem) when many items are selected. Consider adding bulk update methods to your repository and use case to handle these operations in a single database transaction.)
    fun pinOrUnpinNotes() {
        val selected = getSelectState().setOfSelected
        val selectedNotepad =
            getAllNotePad().filter { selected.contains(it.id) }

        deselectNotes()

        if (selectedNotepad.any { !it.isPin }) {
            val pinNotepad = selectedNotepad.map {
                it.copy(isPin = true)
            }

            viewModelScope.launch {
                for (note in pinNotepad) {
                    addAllNoteUseCase(note)
                }
            }
        } else {
            val unPinNote = selectedNotepad.map { it.copy(isPin = false) }

            viewModelScope.launch {
                for (note in unPinNote) {
                    addAllNoteUseCase(note)
                }
            }
        }
    }

    fun setAllColor(colorId: Int) {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.id) }

        deselectNotes()
        val notepads = selectedNotes.map { it.copy(color = colorId) }

        viewModelScope.launch {
            for (note in notepads) {
                addAllNoteUseCase(note)
            }
        }
    }

    fun onArchiveNote() {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.id) }

        deselectNotes()
        val notepads = selectedNotes.map {
            val notepadType = if (it.noteCategory == NoteCategory.ARCHIVE) NoteCategory.NOTE else NoteCategory.ARCHIVE
            it.copy(noteCategory = notepadType)
        }

        viewModelScope.launch {
            for (note in notepads) {
                addAllNoteUseCase(note)
            }
        }
    }

    fun onDeleteNote() {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.id) }

        deselectNotes()
        val notepads = selectedNotes.map { it.copy(noteCategory = NoteCategory.TRASH, isPin = false) }

        viewModelScope.launch {
            for (note in notepads) {
                addAllNoteUseCase(note)
            }
        }
    }

    fun onDeleteForever() {
        val selected = getSelectState().setOfSelected

        deselectNotes()

        viewModelScope.launch {
            noteRepository.deleteIds(selected)
        }
    }

    fun onRestore() {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.id) }
                .map { it.copy(noteCategory = NoteCategory.NOTE) }

        deselectNotes()

        viewModelScope.launch {
            for (note in selectedNotes) {
                addAllNoteUseCase(note)
            }
        }
    }

    fun onCopyNote() {
        viewModelScope.launch(ioDispatcher) {
            val id = getSelectState().setOfSelected.first()
            val notepads = getAllNotePad().find { it.id == id }

            deselectNotes()

            if (notepads != null) {
                val copy = notepads.copy(id = -1)

                addAllNoteUseCase(copy)
            }
        }
    }

    fun deleteLabel() {
        val labelId = getSuccess().noteDisplayCategory.labelId

        viewModelScope.launch {
            userDataRepository.setNoteCategory(NoteDisplayCategory(0, NoteCategory.NOTE))
            labelRepository.delete(labelId)
        }
    }

    fun renameLabel(name: String) {
        val labelId = getSuccess().noteDisplayCategory.labelId
//
        viewModelScope.launch {
            labelRepository.upserts(listOf(Label(labelId, name)))
        }
    }

    fun onDeleteAllTrash() {
        viewModelScope.launch {
            noteRepository.deleteTrash()
        }
    }

    // Todo("deleteByNoteId empty notepad")
//    fun deleteEmptyNote() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val emptyList = notepadpadRepository.getNotePads().first()
//                .filter { it.note.isEmpty() }
//
//            if (emptyList.isNotEmpty()) {
//                notepadpadRepository.deleteNotePad(emptyList)
//            }
//        }
//    }

    fun onDisplayModeChange() {
        viewModelScope.launch {
            val isGrid = getSuccess().isGrid
            userDataRepository.setGrid(!isGrid)
        }
    }

    fun setAlarm(notificationUiState: Notification) {
//        val time = timeListDefault[dateTimeState.value.currentTime]
//        val date = when (dateTimeState.value.currentDate) {
//            0 -> today.date
//            1 -> today.date.plus(1, DateTimeUnit.note.DAY)
//            else -> currentLocalDate
//        }
//        val interval = when (dateTimeState.value.currentInterval) {
//            0 -> null
//            1 -> DateTimeUnit.note.HOUR.times(24).duration.toLong(DurationUnit.note.MILLISECONDS)
//
//            2 -> DateTimeUnit.note.HOUR.times(24 * 7).duration.toLong(DurationUnit.note.MILLISECONDS)
//
//            3 -> DateTimeUnit.note.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.note.MILLISECONDS)
//
//            else -> DateTimeUnit.note.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.note.MILLISECONDS)
//        }
//
//        val setime = LocalDateTime(date, time)
//        if (setime > today) {
//            setAlarm(
//                setime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
//                interval,
//            )
//            Log.e("editv", "Set Alarm")
//        } else {
//            Log.e("editv", "Alarm not set $today time $time date$date")
//        }
    }

    private fun setAlarm(time: Long, interval: Long?) {
    }

    fun onDeleteAlarm() {
    }

    private fun getSuccess() = mainState.value as MainState.ViewState
    fun onSendNote(): NotePad {
        val notepad = getAllNotePad().first { it.id == getSelectState().setOfSelected.first() }
        deselectNotes()
        return notepad
    }

//Search Section

    fun onSetSearch(searchSort: SearchSort?) {
        this.searchSort.value = searchSort
    }

    private fun onBlankSearch(notepads: List<NotePad>): MainState.FilterState {
        val type = listOf(
            SearchSort.Type(0),
            SearchSort.Type(1),
            SearchSort.Type(2),
            SearchSort.Type(3),
            SearchSort.Type(4),
            SearchSort.Type(5),
        )

        val labels = notepads
            .flatMap { it.labels }
            .distinctBy { it.id }
            .map { SearchSort.Label(it.name, 6, it.id) }

        val backgrounds = notepads
            .map {
                it.color
            }
            .distinct()
            .sorted()
            .map { SearchSort.Color(it) }

        return MainState.FilterState(
            types = type,
            label = labels,
            color = backgrounds,
        )
    }

    private fun onSearch(
        query: String,
        searchSort: SearchSort?,
        notepads: List<NotePad>,
    ): List<NotePad> {
        return when {
            searchSort != null -> {
                var list = when (searchSort) {
                    is SearchSort.Color -> {
                        notepads.filter { it.color == searchSort.colorIndex }
                    }

                    is SearchSort.Label -> {
                        notepads.filter { it.labels.any { it.id == searchSort.id } }
                    }

                    is SearchSort.Type -> {
                        when (searchSort.index) {
                            0 -> notepads.filter { it.notification != null }
                            1 -> notepads.filter { it.isCheck }
                            2 -> notepads.filter { it.images.isNotEmpty() }
                            3 -> notepads.filter { it.voices.isNotEmpty() }
                            4 -> notepads.filter { it.drawings.isNotEmpty() }
                            5 -> notepads.filter { it.uris.isNotEmpty() }
                            else -> notepads
                        }
                    }
                }

                if (query.isNotBlank()) {
                    isTextAfterSearchSort = true

                    list = list.filter {
                        it.toString().contains(
                            query,
                            true,
                        )
                    }
                }

                if (isTextAfterSearchSort && query.isBlank()) {
                    isTextAfterSearchSort = false
                    onSetSearch(null)
                }

                list
            }

            query.isNotBlank() -> {
                val list = notepads.filter {
                    it.toString().contains(query, true)
                }

                list
            }

            else -> emptyList()
        }
    }
}
