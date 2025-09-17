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

import com.mshdabiola.model.note.NotePad
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
        notePad: NotePad,
    ) {

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
