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
package com.mshdabiola

import com.mshdabiola.database.NoteDataBase
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.model.LabelEntity
import com.mshdabiola.database.model.NoteDrawingEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteImageEntity
import com.mshdabiola.database.model.NoteItemEntity
import com.mshdabiola.database.model.NoteLabelCrossRef
import com.mshdabiola.database.model.NotePadEntity
import com.mshdabiola.database.model.NoteVoiceEntity
import com.mshdabiola.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

internal class RealNoteDataSource(private val database: NoteDataBase) : NoteDao {
    override suspend fun upsert(noteEntity: NoteEntity): Long {
        var resultingId: Long? = null
        database.noteTable.update { list ->
            val currentList = list ?: emptyList()
            if (noteEntity.id == null) {
                val newId = (currentList.maxByOrNull { it.id ?: 0 }?.id ?: 0) + 1
                resultingId = newId
                currentList + noteEntity.copy(id = newId)
            } else {
                resultingId = noteEntity.id
                val index = currentList.indexOfFirst { it.id == noteEntity.id }
                if (index != -1) {
                    currentList.toMutableList().apply { this[index] = noteEntity }
                } else {
                    currentList + noteEntity
                }
            }
        }
        return resultingId!!
    }

    override suspend fun upserts(noteEntity: List<NoteEntity>): List<Long> {
        val resultingId = mutableListOf<Long>()
        noteEntity.forEach {
            val id = upsert(it)
            resultingId.add(id)
        }
        return resultingId
    }

    override suspend fun delete(id: Long) {
        database.noteTable.update { it?.filter { it.id != id } ?: listOf() }
    }

    override suspend fun deleteIds(ids: Set<Long>) {
        ids.forEach {
            delete(it)
        }
    }

    override suspend fun deleteTrash(noteType: Int) {
        database.noteTable.update { it?.filter { it.noteType == noteType } ?: listOf() }
    }

    override fun getByNoteType(noteType: Int): Flow<List<NotePadEntity>> {
        return database
            .noteTable
            .updates
            .mapNotNull { list ->
                list
                    ?.filter { it.noteType == noteType }
                    ?.map { getNotePad(it) }
            }
    }

    override fun getAll(): Flow<List<NotePadEntity>> {
        return database
            .noteTable
            .updates
            .mapNotNull { list ->
                list
                    ?.map { getNotePad(it) }
            }
    }

    override fun get(noteId: Long): Flow<NotePadEntity?> {
        return database
            .noteTable
            .updates
            .mapNotNull { list ->
                val f = list
                    ?.firstOrNull { it.id == noteId }
                if (f != null) {
                    getNotePad(f)
                } else {
                    null
                }
            }
    }

    override fun getByIds(ids: Set<Long>): Flow<List<NotePadEntity>> {
        return database
            .noteTable
            .updates
            .mapNotNull { list ->
                list
                    ?.filter { ids.contains(it.id) }
                    ?.map { getNotePad(it) }
            }
    }

    override suspend fun updateColorForIds(ids: Set<Long>, color: Int) {
        database.noteTable.update { list ->
            list?.map {
                if (ids.contains(it.id)) {
                    it.copy(color = color)
                } else {
                    it
                }
            }
        }
    }

    override suspend fun updatePinForIds(ids: Set<Long>, isPin: Boolean) {
       database.noteTable.update { list ->
           list?.map {
               if (ids.contains(it.id)) {
                   it.copy(isPin = isPin)
               } else {
                   it
               }
           }
       }
    }

    override suspend fun updateNoteTypeForIds(ids: Set<Long>, noteType: Int) {
        database.noteTable.update { list ->
            list?.map {
                if (ids.contains(it.id)) {
                    it.copy(noteType = noteType)
                } else {
                    it
                }
            }
        }
    }

    suspend fun getNotePad(noteEntity: NoteEntity): NotePadEntity {
        val notification: NotificationEntity? =
            database.notificationTable
                .get()
                ?.firstOrNull { it.noteId == noteEntity.id }
        val images: List<NoteImageEntity> =
            database.noteImageTable
                .get()
                ?.filter { it.noteId == noteEntity.id } ?: listOf()
        val voices: List<NoteVoiceEntity> =
            database.noteVoiceTable
                .get()
                ?.filter { it.noteId == noteEntity.id } ?: listOf()
        val checks: List<NoteItemEntity> =
            database.noteItemTable
                .get()
                ?.filter { it.noteId == noteEntity.id } ?: listOf()
        val drawings: List<NoteDrawingEntity> =
            database.noteDrawingTable
                .get()
                ?.filter { it.noteId == noteEntity.id } ?: listOf()
        val noteLabelCrossRefs: List<NoteLabelCrossRef> =
            database.noteLabelCrossRefTable
                .get()
                ?.filter { it.noteId == noteEntity.id } ?: listOf()
        val allLabels: List<LabelEntity> =
            database.labelTable
                .get()
                ?: listOf()
        val labels: List<LabelEntity> = noteLabelCrossRefs.mapNotNull { crossRef ->
            allLabels.find { it.id == crossRef.labelId }
        }

        return NotePadEntity(
            noteEntity = noteEntity,
            notification = notification,
            images = images,
            voices = voices,
            checks = checks,
            drawings = drawings,
            labels = labels,
        )
    }
}
