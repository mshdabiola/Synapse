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
package com.mshdabiola.data.repository.doubles

import com.mshdabiola.data.repository.AlarmManager
import com.mshdabiola.model.note.NotePad

data class AlarmDetails(
    val timeInMil: Long,
    val interval: Long?,
    val requestCode: Int,
    val title: String,
    val noteId: Long,
    val content: String,
)

class TestAlarmManager : AlarmManager {
    val setAlarms = mutableListOf<AlarmDetails>()
    val deletedRequestCodes = mutableListOf<Int>()

    override fun setAlarm(
        notePad: NotePad,
    ) {
    }

    override fun deleteAlarm(requestCode: Int) {
        setAlarms.removeAll { it.requestCode == requestCode }
        if (!deletedRequestCodes.contains(requestCode)) {
            deletedRequestCodes.add(requestCode)
        }
    }

    fun clear() {
        setAlarms.clear()
        deletedRequestCodes.clear()
    }

    fun getAlarmByRequestCode(requestCode: Int): AlarmDetails? {
        return setAlarms.find { it.requestCode == requestCode }
    }
}
