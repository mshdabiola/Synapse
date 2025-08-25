package com.mshdabiola.data.model

import com.mshdabiola.database.model.LabelEntity
import com.mshdabiola.database.model.NoteDrawingEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteImageEntity
import com.mshdabiola.database.model.NoteItemEntity
import com.mshdabiola.database.model.NoteLabelCrossRef
import com.mshdabiola.database.model.NotePadEntity
import com.mshdabiola.database.model.NoteVoiceEntity
import com.mshdabiola.database.model.NotificationEntity
import com.mshdabiola.model.note.Converter
import com.mshdabiola.model.note.IntervalEnd
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NoteItem
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.NoteVoice
import com.mshdabiola.model.note.Notification
import com.mshdabiola.model.note.Place
import com.mshdabiola.model.note.RepeatSchedule
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun NotePadEntity.asModel() = NotePad(
    noteEntity.id!!,
    noteEntity.title,
    noteEntity.detail,
    noteEntity.editDate,
    noteEntity.isCheck,
    noteEntity.color,
    noteEntity.background,
    noteEntity.isPin,
    NoteCategory.entries[noteEntity.noteType],
    notification = notification?.asModel(),
    images = images.map { it.asModel() },
    voices = voices.map { it.asModel() },
    checks = checks.map { it.asModel() },
    drawings = drawings.map { it.asModel() },
    labels = labels.map { it.asModel() },
)

fun NoteDrawingEntity.asModel(): NoteDrawing {
    return NoteDrawing(
        id = id!!,
        noteId = noteId,
        paths = paths?.let { Converter.toPath(it) } ?: emptyList(),

        )
}

fun NoteDrawing.asEntity(): NoteDrawingEntity {
    return NoteDrawingEntity(
        id = id.check(),
        noteId = noteId,
        paths = if (paths.isEmpty()) {
            null
        } else {
            Converter.pathToString(paths)
        },
    )
}

fun LabelEntity.asModel() = Label(id = id!!, name = name)
fun Label.asEntity() = LabelEntity(id = id.check(), name = name)

fun NoteItemEntity.asModel() = NoteItem(
    id = id!!,
    noteId = noteId,
    content = content,
    isCheck = isCheck,
)

fun NoteItem.asEntity() = NoteItemEntity(
    id = id.check(),
    noteId = noteId,
    content = content,
    isCheck = isCheck
)

fun NotePad.asEntity() = NoteEntity(
    id = id.check(),
    title = title,
    detail = detail,
    editDate = editDate,
    isCheck = isCheck,
    color = color,
    background = background,
    isPin = isPin,
    noteType = noteCategory.ordinal,
)

fun NoteImage.asEntity() = NoteImageEntity(id, noteId)
fun NoteImageEntity.asModel() =
    NoteImage(id = id, noteId = noteId)

fun NoteLabelCrossRef.asModel() = com.mshdabiola.model.note.NoteLabelCrossRef(noteId = noteId, labelId = labelId)
fun com.mshdabiola.model.note.NoteLabelCrossRef.asEntity() = NoteLabelCrossRef(noteId = noteId, labelId = labelId)

fun NoteVoice.asEntity() = NoteVoiceEntity(
    id = id,
    noteId = noteId,
    voiceName = path,
)
fun NoteVoiceEntity.asModel() = NoteVoice(
    id,
    noteId,
    voiceName,
    length = 89,
)

@OptIn(ExperimentalTime::class)
fun Notification.asEntity(): NotificationEntity {
    val reminderTimestamp =
        this.currentDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

    val placeType: Int
    val customPlaceName: String?
    when (this.currentPlace) {
        Place.Home -> {
            placeType = 0
            customPlaceName = null
        }

        Place.Work -> {
            placeType = 1
            customPlaceName = null
        }

        Place.School -> {
            placeType = 2
            customPlaceName = null
        }

        is Place.Edit -> {
            placeType = 3
            customPlaceName = (this.currentPlace as Place.Edit).place
        }

        null -> { // Handle cases where place might not be set, map to a default or error
            placeType = -1 // Or some other indicator for "no place" if needed
            customPlaceName = null
        }
    }

    var typeIndexValue = 0
    var intervalValueStr = "1"
    var weeklyDaysStr: String? = null
    var monthlySameDayBool: Boolean? = null
    var intervalEndTypeIndexValue = 0
    var endDateEpochDayValue: Long? = null
    var numberOfTimesValue: Int? = null

    when (val interval = this.currentInterval) {
        is RepeatSchedule.DoNotRepeat -> {
            typeIndexValue = 0
        }

        is RepeatSchedule.Daily -> {
            typeIndexValue = 1
            intervalValueStr = interval.interval
            when (val end = interval.intervalEnd) {
                IntervalEnd.Forever -> intervalEndTypeIndexValue = 0
                is IntervalEnd.EndDate -> {
                    intervalEndTypeIndexValue = 1
                    endDateEpochDayValue = end.date.toEpochDays().toLong()
                }

                is IntervalEnd.NumberOfTimes -> {
                    intervalEndTypeIndexValue = 2
                    numberOfTimesValue = end.times
                }
            }
        }

        is RepeatSchedule.Weekly -> {
            typeIndexValue = 2
            intervalValueStr = interval.interval
            weeklyDaysStr = interval.days.joinToString(",")
            when (val end = interval.intervalEnd) {
                IntervalEnd.Forever -> intervalEndTypeIndexValue = 0
                is IntervalEnd.EndDate -> {
                    intervalEndTypeIndexValue = 1
                    endDateEpochDayValue = end.date.toEpochDays().toLong()
                }

                is IntervalEnd.NumberOfTimes -> {
                    intervalEndTypeIndexValue = 2
                    numberOfTimesValue = end.times
                }
            }
        }

        is RepeatSchedule.Monthly -> {
            typeIndexValue = 3
            intervalValueStr = interval.interval
            monthlySameDayBool = interval.sameDay
            when (val end = interval.intervalEnd) {
                IntervalEnd.Forever -> intervalEndTypeIndexValue = 0
                is IntervalEnd.EndDate -> {
                    intervalEndTypeIndexValue = 1
                    endDateEpochDayValue = end.date.toEpochDays().toLong()
                }

                is IntervalEnd.NumberOfTimes -> {
                    intervalEndTypeIndexValue = 2
                    numberOfTimesValue = end.times
                }
            }
        }

        is RepeatSchedule.Yearly -> {
            typeIndexValue = 4
            intervalValueStr = interval.interval
            when (val end = interval.intervalEnd) {
                IntervalEnd.Forever -> intervalEndTypeIndexValue = 0
                is IntervalEnd.EndDate -> {
                    intervalEndTypeIndexValue = 1
                    endDateEpochDayValue = end.date.toEpochDays().toLong()
                }

                is IntervalEnd.NumberOfTimes -> {
                    intervalEndTypeIndexValue = 2
                    numberOfTimesValue = end.times
                }
            }
        }

        is RepeatSchedule.Custom -> { // Ensure your NotificationInterval.Custom has necessary fields
            typeIndexValue = 5
            // Populate fields based on NotificationInterval.Custom structure
        }
    }

    return NotificationEntity(
        id = noteId, // Use 0 for new, or pass existing ID for updates
        noteId = noteId,
        reminderDateTimeStamp = reminderTimestamp,
        placeType = placeType,
        customPlaceName = customPlaceName,
        typeIndex = typeIndexValue,
        intervalValue = intervalValueStr,
        weeklyDays = weeklyDaysStr,
        monthlySameDay = monthlySameDayBool,
        intervalEndTypeIndex = intervalEndTypeIndexValue,
        endDateEpochDay = endDateEpochDayValue,
        numberOfTimes = numberOfTimesValue,
    )
}

// --- Mapper from NotificationEntity to NotificationUiState ---
@OptIn(ExperimentalTime::class)
fun NotificationEntity.asModel(): Notification{
    val currentDateTime = Instant.fromEpochMilliseconds(this.reminderDateTimeStamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val currentPlace: Place? = when (this.placeType) {
        0 -> Place.Home
        1 -> Place.Work
        2 -> Place.School
        3 -> Place.Edit(this.customPlaceName ?: "")
        else -> null // Or handle error/default for unknown placeType
    }

    val intervalEnd = when (this.intervalEndTypeIndex) {
        0 -> IntervalEnd.Forever
        1 -> IntervalEnd.EndDate(LocalDate.fromEpochDays(this.endDateEpochDay!!.toInt())) // Ensure not null
        2 -> IntervalEnd.NumberOfTimes(this.numberOfTimes!!) // Ensure not null
        else -> IntervalEnd.Forever // Default or error handling
    }

    val currentInterval: RepeatSchedule = when (this.typeIndex) {
        0 -> RepeatSchedule.DoNotRepeat
        1 -> RepeatSchedule.Daily(
            interval = this.intervalValue,
            intervalEnd = intervalEnd,
        )

        2 -> RepeatSchedule.Weekly(
            interval = this.intervalValue,
            days = this.weeklyDays?.split(',')?.mapNotNull { it.toIntOrNull() }?.toSet()
                ?: emptySet(),
            intervalEnd = intervalEnd,
        )

        3 -> RepeatSchedule.Monthly(
            interval = this.intervalValue,
            sameDay = this.monthlySameDay ?: false, // Provide default if null
            intervalEnd = intervalEnd,
        )

        4 -> RepeatSchedule.Yearly(
            interval = this.intervalValue,
            intervalEnd = intervalEnd,
        )

        5 -> RepeatSchedule.Custom // Ensure your NotificationInterval.Custom can be reconstructed
        else -> RepeatSchedule.DoNotRepeat // Default or error handling
    }

    return Notification(
        currentDateTime = currentDateTime,
        currentInterval = currentInterval,
        currentPlace = currentPlace,
    )
}

fun Long.check() = if (this == -1L) null else this
