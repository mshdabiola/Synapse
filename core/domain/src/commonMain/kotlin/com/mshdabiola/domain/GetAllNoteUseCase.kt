package com.mshdabiola.domain

import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.note.NotePad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllNoteUseCase(
    private val noteRepository: NoteRepository,
    private val linkUriUseCase: LinkUriUseCase,

) {
    operator fun invoke(noteDisplayCategory: NoteDisplayCategory): Flow<List<NotePad>> {
        val notes = when (noteDisplayCategory.noteCategory) {
            NoteCategory.LABEL ->
                noteRepository
                    .getAll()
                    .map { notes ->
                        notes.filter { note ->
                            note.labels.any { it.id == noteDisplayCategory.labelId }
                        }
                    }

            NoteCategory.REMINDER ->
                noteRepository
                    .getAll()
                    .map { notes ->
                        notes.filter { note ->
                            note.notification != null
                        }
                    }

            else -> noteRepository.getByNoteType(noteDisplayCategory.noteCategory)
        }

        return notes
            .map { notes ->
                notes.map { note ->
                    note.copy(
                        uris = linkUriUseCase(
                            note.detail,
                            10,
                        ),
                    )
                }
            }
    }
}
