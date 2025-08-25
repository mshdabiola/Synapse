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
package com.mshdabiola.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.dao.NoteNotificationDao
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.model.LabelEntity
import com.mshdabiola.database.model.NoteDrawingEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteImageEntity
import com.mshdabiola.database.model.NoteItemEntity
import com.mshdabiola.database.model.NoteLabelCrossRef
import com.mshdabiola.database.model.NoteVoiceEntity
import com.mshdabiola.database.model.NotificationEntity

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KmtDatabaseCtor : RoomDatabaseConstructor<NotesDatabase>

@Database(
    entities = [
        NoteEntity::class,
        LabelEntity::class,
        NoteDrawingEntity::class,
        NoteImageEntity::class,
        NoteItemEntity::class,
        NoteLabelCrossRef::class,
        NoteVoiceEntity::class,
        NotificationEntity::class,
    ],
    version = 1,
//    autoMigrations = [
//        //AutoMigration(from = 2, to = 3, spec = DatabaseMigrations.Schema2to3::class),
//
//                     ]
//    ,
    exportSchema = true,
)
@ConstructedBy(KmtDatabaseCtor::class) // NEW
abstract class NotesDatabase : RoomDatabase() {

    abstract fun getLabelDao(): LabelDao

    abstract fun getNoteCheckDao(): NoteCheckDao

    abstract fun getNoteDao(): NoteDao

    abstract fun getNoteImageDao(): NoteImageDao

    abstract fun getNoteLabelDao(): NoteLabelDao

    abstract fun getNoteVoiceDao(): NoteVoiceDao

    abstract fun getNotification(): NoteNotificationDao

    abstract fun getNoteDrawingDao(): NoteDrawingDao

}
