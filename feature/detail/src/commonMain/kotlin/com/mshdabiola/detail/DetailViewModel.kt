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
package com.mshdabiola.detail

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mshdabiola.data.repository.ContentManager
import com.mshdabiola.data.repository.NoteItemRepository
import com.mshdabiola.data.repository.NoteVoiceRepository
import com.mshdabiola.detail.navigation.Detail
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.DateUseCase
import com.mshdabiola.domain.GetNoteUseCase
import com.mshdabiola.model.note.IntervalEnd
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NoteItem
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.NoteVoice
import com.mshdabiola.model.note.Notification
import com.mshdabiola.model.note.Place
import com.mshdabiola.model.note.RepeatSchedule
import com.mshdabiola.player.MediaPlayer
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

@OptIn(FlowPreview::class)
class DetailViewModel(
    val detailArg: Detail,
    private val voicePlayer: MediaPlayer,
    private val getNoteUseCase: GetNoteUseCase,
    private val addAllNoteUseCase: AddAllNoteUseCase,
    private val contentManager: ContentManager,
    private val dateUseCase: DateUseCase,
    private val noteCheckRepository: NoteItemRepository,
    private val noteVoiceRepository: NoteVoiceRepository,
    private val logger: Logger,
) : ViewModel() {

    val notificationUiState = Notification(
        currentDateTime = LocalDateTime(2026, 6, 16, 22, 1),
        currentInterval = RepeatSchedule.Daily(
            intervalEnd = IntervalEnd.Forever,
        ),
        currentPlace = Place.Home,

    )
    private val currentNoteId = MutableStateFlow(detailArg.id)

    private val currentNote = currentNoteId
        .flatMapLatest { ll ->
            getNoteUseCase(ll)
        }
    private var initState = DetailState(
        notePad = NotePad(

            id = detailArg.id,
            color = detailArg.color,
            background = detailArg.background,
            title = detailArg.title,
            detail = detailArg.detail,
            isCheck = detailArg.isCheck,
            checks = if (detailArg.isCheck) listOf(NoteItem()) else emptyList(),
            images = detailArg.images.map { NoteImage(path = it, noteId = detailArg.id) },
            voices = detailArg.voices.map { NoteVoice(id = -1, path = it, noteId = detailArg.id) },

        ),
        title = TextFieldState(detailArg.title),
        detail = TextFieldState(detailArg.detail),

    )
    private val titleFlow = snapshotFlow { initState.title.text }
        .debounce(300L)
        .distinctUntilChanged()

    private val detailFlow = snapshotFlow { initState.detail.text }
        .debounce(300L)
        .distinctUntilChanged()

    private val checkListFlow = snapshotFlow { initState.checks.toList() } // First, react to list changes (add/remove)
        .flatMapLatest { currentChecksList ->
            // If the list is empty, emit an empty list of texts immediately
            if (currentChecksList.isEmpty()) {
                return@flatMapLatest flowOf(emptyList<String>())
            }
            // For each item, create a flow of its text, then combine them
            val flowsOfText = currentChecksList.map { checkItem ->
                snapshotFlow { checkItem.content.text }
            }
            combine(flowsOfText) { arrayOfTexts ->
                arrayOfTexts.toList() // Convert array to list
            }
        }
        .debounce(300L) // Debounce the list of texts
        .distinctUntilChanged()

    private val unCheckListFlow = snapshotFlow { initState.unChecks.toList() }
        .flatMapLatest { currentUnChecksList ->
            if (currentUnChecksList.isEmpty()) {
                return@flatMapLatest kotlinx.coroutines.flow.flowOf(emptyList<String>())
            }
            val flowsOfText = currentUnChecksList.map { unCheckItem ->
                snapshotFlow { unCheckItem.content.text }
            }
            combine(flowsOfText) { arrayOfTexts ->
                arrayOfTexts.toList()
            }
        }
        .debounce(300L)
        .distinctUntilChanged()

    val checksFlow = combine(unCheckListFlow, checkListFlow) { unChecks, checks ->
        unChecks + checks
    }

    private val playerState = MutableStateFlow<PlayerState?>(null)

    private var initTitle = false
    val detailState = combine(
        flow = titleFlow,
        flow2 = detailFlow,
        flow3 = checksFlow,
        flow4 = currentNote,
        flow5 = playerState,

    ) { title, content, checks, notepad, playerState ->

        when {
            notepad == null -> {
                logger.d { "notepad is null" }
                val id = addAllNoteUseCase(initState.notePad)
                currentNoteId.update {
                    id
                }
                logger.d { "id is $id" }
                initState = initState.copy(notePad = initState.notePad.copy(id = id))
                initState
            }
            !initTitle -> {
                logger.d { "initTitle is false" }
                initState.title.clearText()
                initState.detail.clearText()
                initState.title.edit {
                    append(notepad.title)
                }
                initState.detail.edit {
                    append(notepad.detail)
                }
                val list = notepad.checks.partition { it.isCheck }
                logger.d { "list noteitem $list" }

                initState.checks.addAll(list.first.map { it.toNoteCheckUiState() })
                initState.unChecks.addAll(list.second.map { it.toNoteCheckUiState() })

                initTitle = true
                initState.copy(
                    notePad = notepad,
                    updateAt = dateUseCase(notepad.editDate),
                )
            }
            else -> {
                logger.d { "notepad is not null" }
                val newNote = notepad.copy(
                    title = title.toString(),
                    detail = content.toString(),
                    checks =
                    if (notepad.isCheck) {
                        (initState.checks + initState.unChecks)
                            .map { it.toNoteCheck() }
                            .sortedBy { it.id }
                    } else {
                        emptyList()
                    },

                )

                val id = if (newNote != notepad) {
                    addAllNoteUseCase(newNote)
                } else {
                    notepad.id
                }
                initState.copy(
                    notePad = notepad.copy(id = id),
                    updateAt = dateUseCase(notepad.editDate),
                    playerState = playerState,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = initState,
    )

//    fun savNewNote() {
//        viewModelScope.launch {
//            val id = notePadRepository.upsert(NotePad(id = -1))
//
//        }
//    }

    private fun getNotePad(): NotePad {
        return detailState.value.notePad
    }

    fun addCheck() {
        viewModelScope.launch {
            val noteCheck = NoteItem(
                isCheck = false,
                noteId = currentNoteId.value,
            )
            val id = noteCheckRepository.upsert(noteCheck)

            val noteCheckUiState = NoteCheckUiState(
                id = id,
                noteId = currentNoteId.value,
                focus = true,
            )

            initState.unChecks.add(noteCheckUiState)
        }
    }

    fun onCheckDelete(index: Int, isCheck: Boolean) {
        logger.d { "onCheckDelete index $index ischeck $isCheck" }

        viewModelScope.launch {
            val value = if (isCheck) {
                detailState.value.checks.removeAt(index)
            } else {
                detailState.value.unChecks.removeAt(index)
            }
            noteCheckRepository.delete(value.id)
        }
    }

    fun onCheckChange(index: Int, isCheck: Boolean) {
        val state = detailState.value
        if (isCheck) {
            val value = state.checks.removeAt(index)
            state.unChecks.add(value.copy(isCheck = false))
            state.unChecks.sortBy { it.id }
        } else {
            val value = state.unChecks.removeAt(index)
            state.checks.add(value.copy(isCheck = true))
            state.checks.sortBy { it.id }
        }
    }

    fun changeToCheckBoxes() {
        viewModelScope.launch {
            val newChecks = initState
                .detail
                .text.split("\n")
                .map {
                    NoteItem(
                        content = it,
                        noteId = currentNoteId.value,
                        isCheck = false,
                    )
                }
            val notepad = getNotePad()

            addAllNoteUseCase(
                notepad.copy(

                    detail = "",
                    isCheck = true,

                ),
            )
            val ids = noteCheckRepository.upserts(newChecks)
            val noteChecks = newChecks.mapIndexed { index, noteCheck ->
                noteCheck.copy(id = ids[index])
            }
            initState.detail.clearText()

            initState.unChecks.addAll(noteChecks.map { it.toNoteCheckUiState() })
        }
    }

    fun deleteCheckedItems() {
        viewModelScope.launch {
            noteCheckRepository.deleteCheckedItems(currentNoteId.value)
            initState.checks.clear()
        }
    }

    fun hideCheckBoxes() {
        viewModelScope.launch {
            val notepad = getNotePad()

            val noteCheck =
                (initState.checks + initState.unChecks)
                    .joinToString(separator = "\n") { it.content.text }

            noteCheckRepository.deleteByNoteId(currentNoteId.value)

            addAllNoteUseCase(
                notepad.copy(
                    isCheck = false,
                    checks = emptyList(),

                ),
            )
            initState.checks.clear()
            initState.unChecks.clear()

            initState.detail.edit {
                append(noteCheck)
            }
        }
    }

    fun pinNote() {
        viewModelScope.launch {
            val notepad = getNotePad()

            addAllNoteUseCase(notepad.copy(isPin = !notepad.isPin))
        }
    }

    fun onColorChange(index: Int) {
        viewModelScope.launch {
            val notepad = getNotePad()
            addAllNoteUseCase(notepad.copy(color = index))
        }
    }

    fun onImageChange(index: Int) {
        viewModelScope.launch {
            val notepad = getNotePad()
            addAllNoteUseCase(notepad.copy(background = index))
        }
    }

    fun onArchive() {
        viewModelScope.launch {
            val notepad = getNotePad()

            val newNote = if (notepad.noteCategory == NoteCategory.ARCHIVE) {
                notepad.copy(noteCategory = NoteCategory.NOTE)
            } else {
                notepad.copy(noteCategory = NoteCategory.ARCHIVE)
            }

            addAllNoteUseCase(newNote)
        }
    }

    fun onTrash() {
        viewModelScope.launch {
            val notepad = getNotePad()
            addAllNoteUseCase(notepad.copy(noteCategory = NoteCategory.TRASH))
        }
    }

    fun copyNote() {
        viewModelScope.launch {
            val note2 = getNotePad()

            val newNotePad = note2.copy(
                id = -1,
            )

            addAllNoteUseCase(newNotePad)
        }
    }

    fun deleteVoiceNote(index: Int) {
        viewModelScope.launch {
            val notepad = getNotePad()

            val voices = notepad.voices.toMutableList()
            val voice = voices.removeAt(index)

            noteVoiceRepository.delete(voice.id)

            addAllNoteUseCase(notepad.copy(voices = voices))
        }
    }

    fun setAlarm() {
    }

    fun deleteAlarm() {
    }

    fun setAlarm(time: Long, interval: Long?) {
    }

    fun saveImage(uri: String) {
        viewModelScope.launch {
            val id = contentManager.saveImage(uri)

            val image = NoteImage(
                path = id,
            )

            val notepad = getNotePad()

            addAllNoteUseCase(notepad.copy(images = notepad.images + image))
        }
    }

    fun saveVoice(uri: String, text: String) {
        viewModelScope.launch {
            val id = contentManager.saveVoice(uri)

            val voice = NoteVoice(
                id = -1,
                path = id,
            )
            initState.detail.edit {
                append(text)
            }

            val notepad = getNotePad()
            addAllNoteUseCase(notepad.copy(voices = notepad.voices + voice))
        }
    }

    fun getPhotoUri(): String {
        return contentManager.pictureUri()
    }

    private var playJob: Job? = null
    fun playMusic(index: Int) {
        playJob?.cancel()
        val notepad = getNotePad()

        var voices = notepad.voices.toMutableList()

        val voiceUiState = voices[index]

        val state = when {
            playerState.value == null -> {
                playerState.updateAndGet {
                    PlayerState(
                        indexPlaying = index,
                        isPlaying = true,
                        currentPosition = 0,
                    )
                }
            }
            playerState.value!!.indexPlaying != index -> {
                playerState.updateAndGet {
                    PlayerState(
                        indexPlaying = index,
                        isPlaying = true,
                        currentPosition = 0,
                    )
                }
            }
            else -> {
                playerState.value
            }
        }

        playJob = viewModelScope.launch {
            playerState.update {
                null
            }
        }
    }

    fun pause() {
        playerState.update {
            it!!.copy(isPlaying = false)
        }
        playJob?.cancel()
        voicePlayer.pause()
    }
}
