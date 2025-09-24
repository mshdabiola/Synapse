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
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.RepeatSchedule
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

class RealAlarmRepository(
    private val context: Context,
) : AlarmManager {

    @OptIn(ExperimentalTime::class)
    override fun setAlarm(
        notePad: NotePad,
    ) {
        val notification = notePad.notification

        if (notification == null) {
            return
        }

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->

            intent.putExtra(AlarmReceiver.NOTE_ID_EXTRA, notePad.id)
            PendingIntent.getBroadcast(
                /* context = */
                context,
                /* requestCode = */
                notePad.id.toInt(),
                /* intent = */
                intent,
                /* flags = */
                PendingIntent.FLAG_IMMUTABLE,
            )
        }

// 20 minutes.
        if (notification.currentInterval == RepeatSchedule.DoNotRepeat) {
            alarmMgr.setExact(
                /* type = */
                android.app.AlarmManager.RTC_WAKEUP,
                /* triggerAtMillis = */
                notification.currentDateTime
                    .toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds(),
                /* operation = */
                alarmIntent,
            )
        } else {
            alarmMgr.setInexactRepeating(
                /* type = */
                android.app.AlarmManager.RTC_WAKEUP,
                /* triggerAtMillis = */
                notification.currentDateTime
                    .toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds(),
                /* intervalMillis = 1000 * 60 * 20*/
                notification.currentInterval.toApproximateIntervalMillis(),
                /* operation = */
                alarmIntent,
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    fun RepeatSchedule.toApproximateIntervalMillis(): Long {
        val intervalValue = when (this) {
            is RepeatSchedule.Daily -> this.interval.toLongOrNull() ?: 1L
            is RepeatSchedule.Weekly -> this.interval.toLongOrNull() ?: 1L
            is RepeatSchedule.Monthly -> this.interval.toLongOrNull() ?: 1L
            is RepeatSchedule.Yearly -> this.interval.toLongOrNull() ?: 1L
            else -> 0L
        }

        val baseDuration: Duration = when (this) {
            is RepeatSchedule.Daily -> {
                intervalValue.toInt().days
            }
            is RepeatSchedule.Weekly -> (intervalValue * 7).toInt().days // Basic, ignores specific days
            is RepeatSchedule.Monthly -> (intervalValue * 30).toInt().days + if (sameDay)0.days else 21.days
            // Approximation
            is RepeatSchedule.Yearly -> (intervalValue * 365).toInt().days // Approximation
            is RepeatSchedule.DoNotRepeat -> Duration.ZERO
            is RepeatSchedule.Custom -> Duration.ZERO // Cannot determine
        }
        return baseDuration.inWholeMilliseconds
    }

    // Helper, if not already available in your kotlinx-datetime version or stdlib
    val Int.days: Duration
        get() = this.toDuration(DurationUnit.DAYS)

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
