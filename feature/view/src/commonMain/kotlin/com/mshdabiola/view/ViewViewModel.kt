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
package com.mshdabiola.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.ContentManager
import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.view.navigation.View
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ViewViewModel(
    val view: View,
    private val noteImageRepository: NoteImageRepository,
    private val noteRepository: NoteRepository,
    private val contentManager: ContentManager
) : ViewModel() {

    val galleryUiState = noteImageRepository
        .getByNoteId(view.id)
        .mapLatest { images ->
            println("images: $images")
            GalleryUiState(
                initIndex = view.index,
                images = images,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = GalleryUiState(
                initIndex = view.index,
                images = List(view.total) {
                    NoteImage(
                        id = it.toLong(),
                        path = view.currentPath,
                    )
                },

                ),
        )

    suspend fun onImage(path: String) {
        try {
            // val image = notePad.images[index]
            val text = try {
                contentManager.imageToText(path)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
            var note = noteRepository.get(view.id).first()!!
            note =
                note.copy( detail = "${note.detail}\n$text")
            noteRepository.upsert(note)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteImage(id: Long) {
        viewModelScope.launch {
            noteImageRepository.delete(id)
        }
    }

}
