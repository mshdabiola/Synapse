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
package com.mshdabiola.domain.di
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

import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.DateUseCase
import com.mshdabiola.domain.GetAllNoteUseCase
import com.mshdabiola.domain.GetNoteUseCase
import com.mshdabiola.domain.LinkUriUseCase
import org.koin.dsl.module

val domainModule = module {
    single {
        AddAllNoteUseCase(
            noteRepository = get(),
            noteCheckRepository = get(),
            noteDrawingRepository = get(),
            noteImageRepository = get(),
            noteLabelRepository = get(),
            noteNotificationRepository = get(),
            noteVoiceRepository = get(),
        )
    }

    single {
        GetNoteUseCase(
            noteRepository = get(),
            linkUriUseCase = get(),

        )
    }
    single {
        LinkUriUseCase()
    }
    single {
        GetAllNoteUseCase(
            noteRepository = get(),
            linkUriUseCase = get(),
        )
    }
    single {
        DateUseCase()
    }
}
