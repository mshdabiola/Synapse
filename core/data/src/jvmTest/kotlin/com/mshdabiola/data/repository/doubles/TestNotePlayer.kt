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
package com.mshdabiola.data.repository.doubles

import com.mshdabiola.data.repository.NotePlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

data class PlayMusicInvocation(
    val path: String,
    val position: Int
)

class TestNotePlayer : NotePlayer {
    val playMusicInvocations = mutableListOf<PlayMusicInvocation>()
    var pauseInvocationCount = 0
        private set

    private val currentPlaybackStateFlow = MutableStateFlow<Int?>(null) // e.g., current position or state
    var isPlaying = false
        private set

    // Allows tests to control the flow returned by playMusic
    var mockPlaybackFlow: Flow<Int> = emptyFlow()

    override fun playMusic(path: String, position: Int): Flow<Int> {
        playMusicInvocations.add(PlayMusicInvocation(path, position))
        isPlaying = true
        // You could make this more sophisticated, e.g., emitting a series of values
        // or using the currentPlaybackStateFlow to simulate progress.
        // For simplicity, returning a customizable mock flow.
        return mockPlaybackFlow
    }

    override fun pause() {
        pauseInvocationCount++
        isPlaying = false
        // Potentially update currentPlaybackStateFlow if it represents pause state
    }

    // Helper to simulate playback progress for tests if needed
    fun emitPlaybackState(state: Int) {
        currentPlaybackStateFlow.value = state
    }

    fun getPlaybackStateFlow(): Flow<Int?> = currentPlaybackStateFlow.asStateFlow()


    fun clear() {
        playMusicInvocations.clear()
        pauseInvocationCount = 0
        isPlaying = false
        currentPlaybackStateFlow.value = null
        mockPlaybackFlow = emptyFlow()
    }
}
