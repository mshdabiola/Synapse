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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.mshdabiola.data.R
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteNotificationDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel // Import cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val NOTE_ID_EXTRA = "NOTE_ID_EXTRA"
        const val MAIN_ACTIVITY_CLASS_NAME = "com.hobit.synapse.MainActivity"
        const val ORIGINAL_REQUEST_CODE_KEY = "original_request_code"
    }

    private val noteDao: NoteDao by inject()
    private val notificationDao: NoteNotificationDao by inject()


    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) {
            Log.e("AlarmReceiver", "Received null intent")
            return
        }

        val noteId = intent.getLongExtra(NOTE_ID_EXTRA, -1L) // Use your defined constant


        if (noteId == -1L) {
            Log.e("AlarmReceiver", "Invalid Note ID received.")
            return
        }

        Toast.makeText(context, "Alarm Triggered for Note ID: $noteId", Toast.LENGTH_SHORT).show()
        Log.d("AlarmReceiver", "Alarm triggered for Note ID: $noteId")

        // Crucial for performing asynchronous work in a BroadcastReceiver
        val pendingResult: PendingResult = goAsync()

        // 2. Use the receiverScope to launch coroutines
        receiverScope.launch {
            try {
                // --- Your Background Work Here ---
                Log.d("AlarmReceiver", "Coroutine started on thread: ${Thread.currentThread().name}")

                val noteEntity = noteDao.get(noteId).first()

                if (noteEntity != null) {
                    Log.d("AlarmReceiver", "Fetched note: ${noteEntity.noteEntity.title}") // Assuming NoteEntity has a title

                    val notificationTitle = noteEntity.noteEntity.title
                    val notificationContent = noteEntity.noteEntity.detail.take(100)


                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                    val channelId = "com.hobit.synapse.alarm"

                    // Pass applicationContext if these methods are long-lived or store context
                    createNotificationChannel(
                        channelId,
                        "NotePad Alarms",
                        "Channel for note alarms",
                        notificationManager,
                    )
                    sendNotification(channelId, notificationTitle, notificationContent, context, noteId, notificationManager)

                    Log.d("AlarmReceiver", "Notification process completed for Note ID: $noteId")

                    // Example: Further database update
                    // noteDao.updateLastTriggered(noteId, System.currentTimeMillis())
                    // Log.d("AlarmReceiver", "Database updated for Note ID: $noteId")

                } else {
                    Log.w("AlarmReceiver", "Note with ID $noteId not found.")
                }

            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Error processing alarm for Note ID $noteId", e)
                // Handle exceptions (e.g., database errors, network errors)
            } finally {
                // 3. IMPORTANT: Finish the pending result when all background work is done
                pendingResult.finish()
                Log.d("AlarmReceiver", "PendingResult finished for Note ID: $noteId")
            }
        }
        Log.d("AlarmReceiver", "onReceive finished for Note ID: $noteId. Coroutine launched.")
    }

    // Your existing createNotificationChannel and sendNotification methods
    private fun createNotificationChannel(
        id: String,
        name: String,
        description: String,
        notificationManager: android.app.NotificationManager,
    ) {
        val channel = android.app.NotificationChannel(id, name,
            android.app.NotificationManager.IMPORTANCE_DEFAULT).apply {
            this.description = description
            enableLights(true)
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 100)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendNotification(
        channelId: String,
        title: String,
        message: String,
        context: Context,
        noteId: Long,
        notificationManager: android.app.NotificationManager,
    ) {
        val resultIntent = Intent().apply {
            setClassName(context, MAIN_ACTIVITY_CLASS_NAME)
            putExtra(NOTE_ID_EXTRA, noteId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntentFlags =
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE

        val resultPendingIntent: android.app.PendingIntent? = android.app.PendingIntent.getActivity(
            context,
            noteId.toInt(),
            resultIntent,
            pendingIntentFlags,
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_name) // Replace with your app's icon
            .setContentTitle(title)
            .setContentText(message)
            .setChannelId(channelId)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(noteId.toInt(), notification)
    }


    // Optional: Cancel the scope if the receiver is somehow long-lived
    // or if you want to ensure all coroutines are stopped when no longer needed.
    // For a typical BroadcastReceiver, this isn't strictly necessary as the
    // instance itself is short-lived. However, it's good practice for cleanup.
    fun cancelScope() {
        receiverScope.cancel() // Cancels the SupervisorJob and all its children
        Log.d("AlarmReceiver", "ReceiverScope cancelled.")
    }
}
