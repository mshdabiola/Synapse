package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.IAlarmManager

class FakeAlarmManager : IAlarmManager {
    override fun setAlarm(
        timeInMil: Long,
        interval: Long?,
        requestCode: Int,
        title: String,
        noteId: Long,
        content: String,
    ) {
    }

    override fun deleteAlarm(requestCode: Int) {
    }
}
