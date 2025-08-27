package com.mshdabiola.data.repository

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toStdlibInstant
import kotlin.time.ExperimentalTime

class RealAlarmRepository : AlarmManager {



    @OptIn(ExperimentalTime::class)
    override fun setAlarm(
        timeInMil: Long,
        interval: Long?,
        requestCode: Int,
        title: String,
        noteId: Long,
        content: String,
    ) {
        val dateTime = kotlin.time.Instant.fromEpochMilliseconds(timeInMil)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        println(
            "WasmJS RealAlarmRepository: Would set alarm with:\n" +
                "  Request Code: $requestCode\n" +
                "  Time: $dateTime (Epoch: $timeInMil)\n" +
                "  Interval: ${interval?.let { "$it ms" } ?: "One-time"}\n" +
                "  Title: $title\n" +
                "  Note ID: $noteId\n" +
                "  Content: $content"
        )
        // In a real scenario, you might use kotlinx.coroutines.delay for an in-page effect,
        // or JS interop for browser notifications or setTimeout/setInterval.
    }

    override fun deleteAlarm(requestCode: Int) {
        println("WasmJS RealAlarmRepository: Would delete alarm with Request Code: $requestCode")
        // If using something like a map of active setTimeout IDs, you'd clear it here.
    }
}
