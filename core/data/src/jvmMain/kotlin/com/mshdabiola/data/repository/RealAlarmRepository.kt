package com.mshdabiola.data.repository

class RealAlarmRepository : AlarmManager  {
    override fun setAlarm(
        timeInMil: Long,
        interval: Long?,
        requestCode: Int,
        title: String,
        noteId: Long,
        content: String,
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteAlarm(requestCode: Int) {
        TODO("Not yet implemented")
    }
}
