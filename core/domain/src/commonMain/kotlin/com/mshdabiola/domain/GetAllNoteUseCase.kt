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
