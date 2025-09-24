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
package com.mshdabiola.data.doubles

import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.model.NoteImageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestNoteImageDao : NoteImageDao {
    private val noteImagesFlow = MutableStateFlow<List<NoteImageEntity>>(emptyList())
    // No nextId needed as NoteImageEntity's id is not auto-generated

    override suspend fun upsert(image: NoteImageEntity): Long {
        val currentImages = noteImagesFlow.value.toMutableList()
        val index = currentImages.indexOfFirst { it.id == image.id }

        if (index != -1) {
            currentImages[index] = image // Update existing
        } else {
            currentImages.add(image) // Add new
        }
        noteImagesFlow.value = currentImages
        return image.id!! // Return the provided ID
    }

    override suspend fun upserts(images: List<NoteImageEntity>): List<Long> {
        val ids = mutableListOf<Long>()
        images.forEach {
            ids.add(upsert(it))
        }
        return ids
    }

    override suspend fun delete(id: Long) {
        val currentImages = noteImagesFlow.value.toMutableList()
        currentImages.removeAll { it.id == id }
        noteImagesFlow.value = currentImages
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        val currentImages = noteImagesFlow.value.toMutableList()
        currentImages.removeAll { it.noteId == noteId }
        noteImagesFlow.value = currentImages
    }

    override fun get(id: Long): Flow<NoteImageEntity?> {
        return noteImagesFlow.asStateFlow().map { images ->
            images.find { it.id == id }
        }
    }

    override fun getAll(): Flow<List<NoteImageEntity>> {
        return noteImagesFlow.asStateFlow()
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteImageEntity>> {
        return noteImagesFlow.asStateFlow().map { images ->
            images.filter { it.noteId == noteId }
        }
    }
}
