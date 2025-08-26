package com.mshdabiola.database.dao

import com.mshdabiola.database.model.LabelEntity
import kotlinx.coroutines.flow.Flow

interface LabelDao {

    suspend fun upserts(labels: List<LabelEntity>): List<Long>

    suspend fun upsert(label: LabelEntity): Long

    suspend fun delete(id: Long)

    fun get(id: Long): Flow<LabelEntity?>

    fun getAll(): Flow<List<LabelEntity>>
}
