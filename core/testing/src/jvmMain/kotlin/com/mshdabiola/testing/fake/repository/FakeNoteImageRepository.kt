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

import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.model.note.NoteImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeNoteImageRepository : NoteImageRepository {
    private val imagesFlow = MutableStateFlow<LinkedHashMap<Long, NoteImage>>(linkedMapOf())
    private var nextId = 1L // For auto-incrementing IDs if not using map keys directly

    private fun findNextId(): Long {
        return (imagesFlow.value.keys.maxOrNull() ?: 0L) + 1L
    }

    override suspend fun upserts(images: List<NoteImage>): List<Long> {
        val ids = mutableListOf<Long>()
        imagesFlow.update { currentImages ->
            val newImages = LinkedHashMap(currentImages)
            images.forEach { imageToUpsert ->
                val id: Long
                if (imageToUpsert.id != -1L) {
                    // Update existing
                    id = imageToUpsert.id
                    newImages[id] = imageToUpsert
                } else {
                    // Insert new
                    id = findNextId()
                    newImages[id] = imageToUpsert.copy(id = id)
                    nextId = id + 1 // Ensure nextId is always ahead
                }
                ids.add(id)
            }
            newImages
        }
        return ids
    }

    override suspend fun upsert(image: NoteImage): Long {
        var newId = 0L
        imagesFlow.update { currentImages ->
            val newImages = LinkedHashMap(currentImages)
            if (image.id != -1L && newImages.containsKey(image.id)) {
                // Update existing
                newId = image.id
                newImages[newId] = image
            } else {
                // Insert new
                newId = findNextId()
                newImages[newId] = image.copy(id = newId)
                nextId = newId + 1 // Ensure nextId is always ahead
            }
            newImages
        }
        return newId
    }

    override suspend fun delete(id: Long) {
        imagesFlow.update { currentImages ->
            val newImages = LinkedHashMap(currentImages)
            newImages.remove(id)
            newImages
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        imagesFlow.update { currentImages ->
            val newImages = LinkedHashMap(currentImages)
            val keysToRemove = newImages.filter { it.value.noteId == noteId }.keys
            keysToRemove.forEach { newImages.remove(it) }
            newImages
        }
    }

    override fun getAll(): Flow<List<NoteImage>> {
        return imagesFlow.asStateFlow().map { it.values.toList().sortedBy { image -> image.id } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteImage>> {
        return imagesFlow.asStateFlow().map { imagesMap ->
            imagesMap.values
                .filter { it.noteId == noteId }
                .sortedBy { image -> image.id }
        }
    }

    override fun get(id: Long): Flow<NoteImage?> {
        return imagesFlow.asStateFlow().map { it[id] }
    }

    // Helper function for tests to set initial data or clear
    fun setData(newImages: List<NoteImage>) {
        val imagesMap = LinkedHashMap<Long, NoteImage>()
        var maxId = 0L
        newImages.forEach {
            val id = if (it.id == 0L || it.id == -1L) findNextId() else it.id // Handle default/unset IDs
            imagesMap[id] = it.copy(id = id)
            if (id > maxId) maxId = id
        }
        imagesFlow.value = imagesMap
        nextId = maxId + 1
    }

    fun clearData() {
        imagesFlow.value = linkedMapOf()
        nextId = 1L
    }
}
