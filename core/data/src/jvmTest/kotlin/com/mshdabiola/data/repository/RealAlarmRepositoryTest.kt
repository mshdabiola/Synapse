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
import org.junit.After
import org.junit.Before
import java.util.concurrent.ScheduledFuture

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
}
