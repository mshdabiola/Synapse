package com.mshdabiola.database.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationEntity(
    val id: Long = 0,

    val noteId: Long,

    val reminderDateTimeStamp: Long,
    val placeType: Int,

    val customPlaceName: String? = null,

    val typeIndex: Int,
    val intervalValue: String = "1",
    val weeklyDays: String? = null,
    val monthlySameDay: Boolean? = null,
    val intervalEndTypeIndex: Int,
    val endDateEpochDay: Long? = null,
    val numberOfTimes: Int? = null,

)
