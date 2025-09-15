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

import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.data.repository.NoteItemRepository
import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.data.repository.NoteNotificationRepository
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.data.repository.NoteVoiceRepository
import com.mshdabiola.model.note.NoteLabelCrossRef
import com.mshdabiola.model.note.NotePad
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AddAllNoteUseCase(
    private val noteRepository: NoteRepository,
    private val noteCheckRepository: NoteItemRepository,
    private val noteDrawingRepository: NoteDrawingRepository,
    private val noteImageRepository: NoteImageRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val noteNotificationRepository: NoteNotificationRepository,
    private val noteVoiceRepository: NoteVoiceRepository,

) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(notePad: NotePad): Long {
//        check(!notePad.isEmpty())


        var id = noteRepository.upsert(notePad)

        if (id == -1L) {
            id = notePad.id
        }
        if (notePad.voices.isNotEmpty()) {
            noteVoiceRepository.upserts(
                notePad.voices.map { it.copy(noteId = id) },
            )
        }

        if (notePad.drawings.isNotEmpty()) {
            noteDrawingRepository.upserts(
                notePad.drawings.map { it.copy(noteId = id) },
            )
        }
        if (notePad.images.isNotEmpty()) {
            noteImageRepository.upserts(
                notePad.images.map { it.copy(noteId = id) },
            )
        }
        if (notePad.checks.isNotEmpty()) {
            noteCheckRepository.upserts(
                notePad.checks.map { it.copy(noteId = id) },
            )
        }
        if (notePad.labels.isNotEmpty()) {
            noteLabelRepository.upserts(
                notePad.labels.map { NoteLabelCrossRef(noteId = id, labelId = it.id) },
            )
        }
        notePad.notification?.let {
            noteNotificationRepository.upsert(
                it.copy(noteId = id),
            )
        }

        return id
    }
}
