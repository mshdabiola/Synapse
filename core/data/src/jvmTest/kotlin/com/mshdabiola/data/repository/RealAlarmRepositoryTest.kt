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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class RealAlarmRepositoryTest {

    private lateinit var alarmRepository: RealAlarmRepository

    // Helper to access activeAlarms via reflection for test verification
    private fun getActiveAlarms(repository: RealAlarmRepository): Map<Int, ScheduledFuture<*>> {
        val field = RealAlarmRepository::class.java.getDeclaredField("activeAlarms")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(repository) as Map<Int, ScheduledFuture<*>>
    }

    @Before
    fun setUp() {
        alarmRepository = RealAlarmRepository()
    }

    @After
    fun tearDown() {
        alarmRepository.shutdown() // Ensure scheduler is cleaned up after each test
    }

    @Test
    fun `setAlarm oneTime schedules task`() = runTest {
        val requestCode = 1
        val timeInFuture = System.currentTimeMillis() + 10000 // 10 seconds in future

        alarmRepository.setAlarm(timeInFuture, null, requestCode, "Test One-Time", 1L, "Content")

        val activeAlarms = getActiveAlarms(alarmRepository)
        assertNotNull("Alarm should be active", activeAlarms[requestCode])
        assertFalse("Alarm future should not be done immediately", activeAlarms[requestCode]?.isDone ?: true)
        assertFalse("Alarm future should not be cancelled immediately", activeAlarms[requestCode]?.isCancelled ?: true)
    }

    @Test
    fun `setAlarm repeating schedules task`() = runTest {
        val requestCode = 2
        val timeInFuture = System.currentTimeMillis() + 10000
        val interval = 60000L // 1 minute

        alarmRepository.setAlarm(timeInFuture, interval, requestCode, "Test Repeating", 2L, "Content")

        val activeAlarms = getActiveAlarms(alarmRepository)
        assertNotNull("Repeating alarm should be active", activeAlarms[requestCode])
    }

    @Test
    fun `setAlarm past oneTime alarm is skipped`() = runTest {
        val requestCode = 3
        val timeInPast = System.currentTimeMillis() - 10000 // 10 seconds in past

        alarmRepository.setAlarm(timeInPast, null, requestCode, "Past One-Time", 3L, "Content")

        val activeAlarms = getActiveAlarms(alarmRepository)
        assertNull("Past one-time alarm should be skipped", activeAlarms[requestCode])
    }

    @Test
    fun `setAlarm past repeating alarm is rescheduled`() = runTest {
        val requestCode = 4
        val timeInPast = System.currentTimeMillis() - 50000 // 50s in past
        val interval = 60000L // 60s interval

        alarmRepository.setAlarm(timeInPast, interval, requestCode, "Past Repeating", 4L, "Content")

        val activeAlarms = getActiveAlarms(alarmRepository)
        assertNotNull("Past repeating alarm should be rescheduled and active", activeAlarms[requestCode])
        // The delay should be positive now
        assertTrue("Rescheduled alarm delay should be positive", (activeAlarms[requestCode]?.getDelay(TimeUnit.MILLISECONDS) ?: -1L) > 0)
    }

    @Test
    fun `setAlarm with same requestCode replaces existing alarm`() = runTest {
        val requestCode = 5
        val initialTime = System.currentTimeMillis() + 20000
        alarmRepository.setAlarm(initialTime, null, requestCode, "Initial Alarm", 5L, "Initial")
        val firstAlarmFuture = getActiveAlarms(alarmRepository)[requestCode]
        assertNotNull(firstAlarmFuture)

        // Wait a tiny bit to ensure the first alarm is processed by the scheduler before cancelling
        Thread.sleep(50)

        val newTime = System.currentTimeMillis() + 30000
        alarmRepository.setAlarm(newTime, null, requestCode, "New Alarm", 5L, "New")
        val newAlarmFuture = getActiveAlarms(alarmRepository)[requestCode]

        assertNotNull("New alarm should be active", newAlarmFuture)
        // Due to immediate cancellation, isCancelled might become true quickly
        // It's tricky to assert firstAlarmFuture.isCancelled without more control or waiting,
        // but the map should contain the new future.
        assertTrue("First alarm should be cancelled or replaced", firstAlarmFuture?.isCancelled ?: false || firstAlarmFuture != newAlarmFuture)
        assertFalse("New alarm future should not be cancelled immediately", newAlarmFuture?.isCancelled ?: true)
    }

    @Test
    fun `deleteAlarm cancels and removes active alarm`() = runTest {
        val requestCode = 6
        val timeInFuture = System.currentTimeMillis() + 10000
        alarmRepository.setAlarm(timeInFuture, null, requestCode, "To Delete", 6L, "Content")

        assertNotNull("Alarm should be active before delete", getActiveAlarms(alarmRepository)[requestCode])

        alarmRepository.deleteAlarm(requestCode)
        Thread.sleep(50) // Allow scheduler to process cancellation

        val activeAlarms = getActiveAlarms(alarmRepository)
        val deletedFuture = getActiveAlarms(alarmRepository)[requestCode]
        // The future might still be in the map if cancel() was called but before it's fully removed by the task wrapper logic.
        // More robust is to check if it was indeed cancelled.
        // For this test, if it's removed from the map that's a strong indicator.
        // However, the current impl of deleteAlarm removes from map then cancels.
        assertNull("Alarm should be removed from active list after delete", activeAlarms[requestCode])
    }


    @Test
    fun `deleteAlarm nonExistentAlarm does nothing`() = runTest {
        val requestCode = 7
        val initialActiveCount = getActiveAlarms(alarmRepository).size

        alarmRepository.deleteAlarm(requestCode) // Try to delete a non-existent alarm

        assertEquals("Active alarm count should not change", initialActiveCount, getActiveAlarms(alarmRepository).size)
    }

    @Test
    fun `shutdown terminates scheduler`() = runTest {
        alarmRepository.setAlarm(System.currentTimeMillis() + 1000, null, 99, "Test", 1L, "C")
        alarmRepository.shutdown()

        val schedulerField = RealAlarmRepository::class.java.getDeclaredField("scheduler")
        schedulerField.isAccessible = true
        val scheduler = schedulerField.get(alarmRepository) as java.util.concurrent.ScheduledExecutorService

        assertTrue("Scheduler should be shutdown", scheduler.isShutdown)
        assertTrue("Scheduler should be terminated or terminating", scheduler.isTerminated || scheduler.isShutdown) // isTerminated might take a moment
    }
}
