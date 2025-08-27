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

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.time.ExperimentalTime

class RealAlarmRepository : AlarmManager {
    private val logger = Logger.getLogger(RealAlarmRepository::class.java.name)
    private val scheduler = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "JvmAlarmScheduler").apply { isDaemon = true }
    }
    private val activeAlarms = ConcurrentHashMap<Int, ScheduledFuture<*>>()

    init {
        logger.info("JvmRealAlarmRepository initialized.")
    }

    @OptIn(ExperimentalTime::class)
    override fun setAlarm(
        timeInMil: Long,
        interval: Long?,
        requestCode: Int,
        title: String,
        noteId: Long,
        content: String,
    ) {
        deleteAlarm(requestCode) // Cancel any existing alarm with the same request code

        val now = System.currentTimeMillis()
        var delay = timeInMil - now
        if (delay < 0) {
            // If the time is in the past, log a warning.
            // Depending on requirements, you might want to trigger it immediately or skip.
            // For repeating alarms, you might adjust the first trigger time to be in the future.
            logger.warning(
                "Alarm time $timeInMil is in the past (current time $now). " +
                    "Request Code: $requestCode. Title: $title. " +
                    if (interval != null) "This is a repeating alarm." else "This is a one-time alarm.",
            )
            if (interval == null) {
                logger.info("Skipping past one-time alarm: $requestCode")
                return // Do not schedule past one-time alarms
            } else {
                // For repeating alarms, advance to the next valid interval from now
                val periodsMissed = (-delay + interval - 1) / interval // Number of intervals missed
                delay += periodsMissed * interval
                logger.info(
                    "Adjusted past repeating alarm " +
                        "$requestCode to next future slot. New initial delay: $delay ms",
                )
            }
        }

        val alarmTask = Runnable {
            val currentDateTime = kotlin.time.Instant
                .fromEpochMilliseconds(System.currentTimeMillis())
                .toLocalDateTime(TimeZone.currentSystemDefault())
            logger.info(
                "ALARM TRIGGERED! Request Code: $requestCode\n" +
                    "  Scheduled Time (approx): ${kotlin.time.Instant
                        .fromEpochMilliseconds(timeInMil)
                        .toLocalDateTime(TimeZone.currentSystemDefault())}\n" +
                    "  Actual Trigger Time: $currentDateTime\n" +
                    "  Title: $title\n" +
                    "  Note ID: $noteId\n" +
                    "  Content: $content\n" +
                    "  Repeating: ${interval?.let { "every $it ms" } ?: "false"}",
            )
            // Here you would typically trigger some application-specific logic,
            // e.g., show a notification (if UI app), run a background task, etc.

            if (interval == null) { // If it's a one-time alarm, remove it after execution
                activeAlarms.remove(requestCode)
                logger.info("One-time alarm $requestCode completed and removed.")
            }
        }

        val future: ScheduledFuture<*>
        if (interval != null && interval > 0) {
            future = scheduler.scheduleAtFixedRate(
                alarmTask,
                delay,
                interval,
                TimeUnit.MILLISECONDS,
            )
            logger.info(
                "Scheduled repeating alarm " +
                    "$requestCode: delay=$delay ms, interval=$interval ms. Title: $title",
            )
        } else {
            future = scheduler.schedule(alarmTask, delay, TimeUnit.MILLISECONDS)
            logger.info("Scheduled one-time alarm $requestCode: delay=$delay ms. Title: $title")
        }
        activeAlarms[requestCode] = future
    }

    override fun deleteAlarm(requestCode: Int) {
        activeAlarms.remove(requestCode)?.let { future ->
            if (!future.isDone) {
                future.cancel(false) // false: don't interrupt if already running
                logger.info("Cancelled active alarm with Request Code: $requestCode")
            } else {
                logger.info("Alarm with Request Code: $requestCode was already done. Removed.")
            }
        } ?: logger.info("No active alarm found to delete with Request Code: $requestCode")
    }

    // Optional: Method to shut down the scheduler when the application is closing
    fun shutdown() {
        logger.info("Shutting down JvmAlarmRepository scheduler.")
        scheduler.shutdownNow()
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.severe("Scheduler did not terminate in time.")
            }
        } catch (ie: InterruptedException) {
            logger.warning("Scheduler shutdown interrupted.")
            Thread.currentThread().interrupt()
        }
        logger.info("JvmAlarmRepository scheduler shutdown complete.")
    }
}
