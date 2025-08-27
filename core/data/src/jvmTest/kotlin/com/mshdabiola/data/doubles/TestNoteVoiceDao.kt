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

import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.model.NoteVoiceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestNoteVoiceDao : NoteVoiceDao {
    private val noteVoicesFlow = MutableStateFlow<List<NoteVoiceEntity>>(emptyList())
    // No nextId needed as NoteVoiceEntity's id is not auto-generated

    override suspend fun upsert(voice: NoteVoiceEntity): Long {
        val currentVoices = noteVoicesFlow.value.toMutableList()
        val index = currentVoices.indexOfFirst { it.id == voice.id }

        if (index != -1) {
            currentVoices[index] = voice // Update existing
        } else {
            currentVoices.add(voice) // Add new
        }
        noteVoicesFlow.value = currentVoices
        return voice.id // Return the provided ID
    }

    override suspend fun upserts(voices: List<NoteVoiceEntity>): List<Long> {
        val ids = mutableListOf<Long>()
        voices.forEach {
            ids.add(upsert(it))
        }
        return ids
    }

    override suspend fun delete(id: Long) {
        val currentVoices = noteVoicesFlow.value.toMutableList()
        currentVoices.removeAll { it.id == id }
        noteVoicesFlow.value = currentVoices
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        val currentVoices = noteVoicesFlow.value.toMutableList()
        currentVoices.removeAll { it.noteId == noteId }
        noteVoicesFlow.value = currentVoices
    }

    override fun get(id: Long): Flow<NoteVoiceEntity?> {
        return noteVoicesFlow.asStateFlow().map { voices ->
            voices.find { it.id == id }
        }
    }

    override fun getAll(): Flow<List<NoteVoiceEntity>> {
        return noteVoicesFlow.asStateFlow()
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteVoiceEntity>> {
        return noteVoicesFlow.asStateFlow().map { voices ->
            voices.filter { it.noteId == noteId }
        }
    }
}
