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
package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.model.note.NoteDrawing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeNoteDrawingRepository : NoteDrawingRepository {
    private val drawings = mutableListOf<NoteDrawing>()
    private var nextId = 1L

    override suspend fun upserts(drawings: List<NoteDrawing>): List<Long> {
        println("list $drawings")
        val ids = mutableListOf<Long>()
        drawings.forEach { drawing ->
            ids.add(upsert(drawing))
        }
        return ids
    }

    override suspend fun upsert(drawing: NoteDrawing): Long {
        return if (drawing.id == -1L) {
            val newDrawing = drawing.copy(id = nextId++)
            drawings.add(newDrawing)
            newDrawing.id
        } else {
            val indexedValue = drawings.indexOfFirst { it.id == drawing.id }
            if (indexedValue == -1) {
                drawings.add(drawing)
            } else {
                drawings.add(indexedValue, drawing)
            }
            drawing.id
        }
    }

    override suspend fun delete(id: Long) {
        drawings.removeIf { it.id == id }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        drawings.removeIf { it.noteId == noteId }
    }

    override fun getAll(): Flow<List<NoteDrawing>> {
        return flowOf(drawings.toList())
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteDrawing>> {
        return flowOf(drawings.filter { it.noteId == noteId }.toList())
    }

    override fun get(id: Long): Flow<NoteDrawing?> {
        return flowOf(drawings.find { it.id == id })
    }
}
