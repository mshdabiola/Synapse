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
package com.mshdabiola.database.di

import com.mshdabiola.database.NotesDatabase
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.dao.NoteNotificationDao
import com.mshdabiola.database.dao.NoteVoiceDao
import org.koin.core.module.Module
import org.koin.dsl.module

expect val databaseModule: Module

val daoModules =
    module {
        single {
            get<NotesDatabase>().getNoteDao()
        }
        single {
            get<NotesDatabase>().getLabelDao()
        }
        single {
            get<NotesDatabase>().getNoteCheckDao()
        }
        single {
            get<NotesDatabase>().getNoteImageDao()
        }
        single {
            get<NotesDatabase>().getNoteLabelDao()
        }
        single {
            get<NotesDatabase>().getNoteVoiceDao()
        }
        single {
            get<NotesDatabase>().getNotification()
        }
        single {
            get<NotesDatabase>().getNoteDrawingDao()
        }
    }
