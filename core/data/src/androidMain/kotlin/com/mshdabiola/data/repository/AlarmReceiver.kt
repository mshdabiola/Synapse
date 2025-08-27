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
package com.mshdabiola.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val id = "com.mshdabiola.notepad.alarm"
        val title = intent.getStringExtra("title")?.ifBlank { "Alarm" } ?: "Alarm"
        val noteId = intent.getLongExtra("id", 0)
        val content = intent.getStringExtra("content")?.ifBlank { "Alarm notification" }
            ?: "Alarm notification"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(id, "NotePad Notification", "for alarm", notificationManager)
        }
        sendNotification(id, title, content, context, noteId, notificationManager)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(
        id: String,
        name: String,
        description: String,
        notificationManager: NotificationManager,
    ) {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = description

        channel.enableLights(true)
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 100)

        notificationManager.createNotificationChannel(channel)
    }

    private fun sendNotification(
        id: String,
        title: String,
        message: String,
        context: Context,
        notiId: Long,
        notificationManager: NotificationManager,
    ) {
        val notification = NotificationCompat.Builder(context, id)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle(title)
            .setContentText(message)
            .setChannelId(id)
            .build()

        notificationManager.notify(notiId.toInt(), notification)
    }
}
