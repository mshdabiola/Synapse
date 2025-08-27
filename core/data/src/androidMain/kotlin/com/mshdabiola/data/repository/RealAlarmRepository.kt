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
package com.mshdabiola.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class RealAlarmRepository(
    private val context: Context,
) : AlarmManager {

    override fun setAlarm(
        timeInMil: Long,
        interval: Long?,
        requestCode: Int,
        title: String,
        noteId: Long,
        content: String,
    ) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            intent.putExtra("title", title)
            intent.putExtra("content", content)
            intent.putExtra("id", noteId)
            PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        }

// 20 minutes.
        if (interval == null) {
            alarmMgr.setExact(
                /* type = */
                android.app.AlarmManager.RTC_WAKEUP,
                /* triggerAtMillis = */
                timeInMil,
                /* operation = */
                alarmIntent,
            )
        } else {
            alarmMgr.setInexactRepeating(
                /* type = */
                android.app.AlarmManager.RTC_WAKEUP,
                /* triggerAtMillis = */
                timeInMil,
                /* intervalMillis = 1000 * 60 * 20*/
                interval,
                /* operation = */
                alarmIntent,
            )
        }
    }

    override fun deleteAlarm(requestCode: Int) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        }

        alarmMgr.cancel(alarmIntent)
    }
}
