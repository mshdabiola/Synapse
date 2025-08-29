package com.mshdabiola.domain

import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.model.note.NotePad
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetNoteUseCase (
    private val noteRepository: NoteRepository,
    private val linkUriUseCase: LinkUriUseCase,

) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: Long): Flow<NotePad?> {
        return noteRepository.get(id)
            .mapLatest {
                it?.copy(

                    uris = linkUriUseCase(it.detail, 10),
                )
            }
    }
}
