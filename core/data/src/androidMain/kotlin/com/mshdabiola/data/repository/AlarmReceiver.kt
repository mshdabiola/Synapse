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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteNotificationDao
import org.koin.java.KoinJavaComponent.inject

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val NOTE_ID_EXTRA = "NOTE_ID_EXTRA"
        const val MAIN_ACTIVITY_CLASS_NAME = "com.mshdabiola.synapse.MainActivity"
    }
    private val noteDao: NoteDao by inject(NoteDao::class.java)
    private val notificationDao : NoteNotificationDao by inject(NoteNotificationDao::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_SHORT).show() // Updated Toast message
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "com.mshdabiola.notepad.alarm" // Renamed variable for clarity
        val title = intent.getStringExtra("title")?.ifBlank { "Alarm" } ?: "Alarm"
        val noteId = intent.getLongExtra("id", 0L) // Ensure default is Long
        val content = intent.getStringExtra("content")?.ifBlank { "Alarm notification" }
            ?: "Alarm notification"

        createNotificationChannel(
            channelId,
            "NotePad Alarms",
            "Channel for note alarms",
            notificationManager,
        ) // Updated name/desc
        sendNotification(channelId, title, content, context, noteId, notificationManager)
    }

    private fun createNotificationChannel(
        id: String,
        name: String,
        description: String,
        notificationManager: NotificationManager,
    ) {
        // Channel creation is only on O+
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
            this.description = description
            enableLights(true)
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 100)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendNotification(
        channelId: String, // Renamed from 'id'
        title: String,
        message: String,
        context: Context,
        noteId: Long, // Renamed from 'notiId'
        notificationManager: NotificationManager,
    ) {
        // Intent to open the MainActivity (or your specific note detail activity)
        val resultIntent = Intent().apply {
            setClassName(context, MAIN_ACTIVITY_CLASS_NAME)
            putExtra(NOTE_ID_EXTRA, noteId)
            // Consider adding flags like Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
            // depending on your desired navigation stack behavior.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntentFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val resultPendingIntent: PendingIntent? = PendingIntent.getActivity(
            context,
            noteId.toInt(), // Using noteId as a unique request code for the PendingIntent
            resultIntent,
            pendingIntentFlags,
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_chat) // Replace with your app's icon
            .setContentTitle(title)
            .setContentText(message)
            .setChannelId(channelId) // Ensure channel ID is set for O+
            .setContentIntent(resultPendingIntent) // Set the PendingIntent
            .setAutoCancel(true) // Dismiss notification on tap
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set priority
            .build()

        // Use a unique ID for the notification itself (can be noteId or another system)
        // Using noteId.toInt() for consistency with PendingIntent request code
        notificationManager.notify(noteId.toInt(), notification)
    }
}
