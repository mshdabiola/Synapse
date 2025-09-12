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
package com.mshdabiola.draw

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.draw.navigation.Draw
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.ui.DrawingController
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class)
class DrawViewModel(
    val draw: Draw,
    private val drawingRepository: NoteDrawingRepository,
    private val noteRepository: NoteRepository

    ) : ViewModel() {
    private val detailArgs = MutableStateFlow(draw)

    val controller = DrawingController()

    private var isInit = false

    @OptIn(FlowPreview::class)
    val drawingState = combine(
        snapshotFlow { controller.drawingPaths.toList() }
            .debounce(500)
            .distinctUntilChanged(),
        detailArgs,
    ) { drawingPaths, drawArg ->

        val state = when {
            !isInit && drawArg.id != null -> {
                val path = drawingRepository.get(drawArg.id)
                    .first()
                    ?.paths
                val drawingPathsMutableList = controller.drawingPaths.toMutableList()
                drawingPathsMutableList.addAll(path!!)
                controller.drawingPaths.addAll(drawingPathsMutableList)

                isInit = true
                DrawUiState(
                    drawingId = drawArg.id,
                    drawings = path,
                )
            }
            !isInit && drawArg.id == null -> {
                val noteId= if (detailArgs.value.noteId!=null)
                    detailArgs.value.noteId!!
                else
                    noteRepository.upsert(NotePad())
                val id = drawingRepository.upsert(
                    NoteDrawing(
                        id = -1,
                        paths = drawingPaths,
                        noteId = noteId,
                    ),
                )
                detailArgs.update {
                    it.copy(id = id,noteId = noteId)
                }

                isInit = true
                DrawUiState(
                    noteId = noteId,
                    drawingId = id,
                    drawings = emptyList(),
                )
            }
            else -> {
                drawingRepository.upsert(
                    NoteDrawing(
                        id = detailArgs.value.id!!,
                        paths = drawingPaths,
                        noteId = detailArgs.value.noteId!!,
                    ),
                )

                DrawUiState(
                    drawingId = detailArgs.value.id,
                    noteId = detailArgs.value.noteId,
                    drawings = drawingPaths,
                )
            }
        }

        state
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = DrawUiState(),
    )

//    fun saveImage2(paths: ImmutablePath): Deferred<String?> {
//        return viewModelScope.async {
//            try {
//                val pathsMap = changeToDrawPath(paths)
//
//                // deleteByNoteId exist drawing from db
//                drawingPathRepository.deleteByNoteId(imageID)
//                if (pathsMap.isEmpty()) {
//                    // deleteByNoteId image too
//                    File(contentManager.getImagePath(imageID)).deleteOnExit()
//                    null
//                } else {
//                    val width = drawingArgs.width
//                    val height = drawingArgs.height
//                    val density = drawingArgs.density
//
//                    val bitmap = getBitMap(
//                        changeToPathAndData(paths),
//                        width,
//                        height,
//                        density,
//                    )
//                    val path = contentManager.getImagePath(imageID)
//                    contentManager.saveBitmap(path, bitmap)
//
//                    drawingPathRepository.insert(pathsMap)
//                    path
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }
//    }

    suspend fun deleteDrawing() {
        drawingRepository.delete(detailArgs.value.id!!)
    }

}
