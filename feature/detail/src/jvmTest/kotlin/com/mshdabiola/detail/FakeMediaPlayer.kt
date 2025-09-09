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
package com.mshdabiola.detail

import com.mshdabiola.player.MediaPlayer
import com.mshdabiola.player.MediaPlayerListener
import com.mshdabiola.player.NoteItem

class FakeMediaPlayer : MediaPlayer {
    private var isPlayingTrack: Boolean = false
    private var activeNote: NoteItem? = null
    private var tracklist: List<NoteItem> = emptyList()
    private var currentTrackIndex: Int = -1
    private var listener: MediaPlayerListener? = null
    private var currentTrackPositionMs: Long = 0L
    private var mediaDurationMs: Long = 0L

    var preparedTrackId: String? = null
    var trackListSet: Boolean = false
    var startedCalled: Boolean = false
    var pausedCalled: Boolean = false
    var nextTrackCalled: Boolean = false
    var previousTrackCalled: Boolean = false
    var seekPosition: Long? = null

    override fun prepare(mediaItem: NoteItem, listener: MediaPlayerListener) {
        activeNote = mediaItem
        this@FakeMediaPlayer.listener = listener
        mediaDurationMs = 200000L // Default duration 200s if not specified
        currentTrackPositionMs = 0L
        isPlayingTrack = false
        preparedTrackId = mediaItem.id
    }

    override fun setTrackList(trackList: List<NoteItem>, currentTrackId: String) {
        tracklist = trackList
        currentTrackIndex = tracklist.indexOfFirst { it.id == currentTrackId }
        if (currentTrackIndex != -1) {
            activeNote = tracklist[currentTrackIndex]
            // Optionally call prepare or onReady if the behavior is to auto-prepare
            // For this fake, we assume prepare is called separately or listener is updated if needed
            listener?.onTrackChanged(tracklist[currentTrackIndex].path)
        } else {
            activeNote = null
        }
        trackListSet = true
    }

    override fun playNextTrack(): Boolean {
        nextTrackCalled = true
        if (tracklist.isEmpty() || currentTrackIndex >= tracklist.size - 1) {
            return false
        }
        currentTrackIndex++
        activeNote = tracklist[currentTrackIndex]
        currentTrackPositionMs = 0L
        mediaDurationMs = 200000L
        listener?.onTrackChanged(activeNote!!.path)
        // Simulate auto-play on next track if that's the desired fake behavior
        // start()
        return true
    }

    override fun playPreviousTrack(): Boolean {
        previousTrackCalled = true
        if (tracklist.isEmpty() || currentTrackIndex <= 0) {
            return false
        }
        currentTrackIndex--
        activeNote = tracklist[currentTrackIndex]
        currentTrackPositionMs = 0L
        mediaDurationMs = 200000L
        listener?.onTrackChanged(activeNote!!.path)
        // Simulate auto-play on previous track if that's the desired fake behavior
        // start()
        return true
    }

    override fun start() {
        if (activeNote != null) {
            isPlayingTrack = true
            startedCalled = true
            pausedCalled = false
        }
    }

    override fun pause() {
        isPlayingTrack = false
        pausedCalled = true
        startedCalled = false
    }

    override fun getCurrentPosition(): Long? {
        return if (activeNote != null) currentTrackPositionMs else null
    }

    override fun getDuration(): Long? {
        return if (activeNote != null) mediaDurationMs else null
    }

    override fun seekTo(seconds: Long) {
        seekPosition = seconds
        val newPosition = seconds * 1000 // Assuming seconds to milliseconds
        currentTrackPositionMs = if (activeNote != null) {
            newPosition.coerceIn(0, mediaDurationMs)
        } else {
            0L
        }
    }

    override fun isPlaying(): Boolean {
        return isPlayingTrack
    }

    override fun getCurrentTrack(): NoteItem? {
        return activeNote
    }

    fun reset() {
        isPlayingTrack = false
        activeNote = null
        tracklist = emptyList()
        currentTrackIndex = -1
        listener = null
        currentTrackPositionMs = 0L
        mediaDurationMs = 0L
        preparedTrackId = null
        trackListSet = false
        startedCalled = false
        pausedCalled = false
        nextTrackCalled = false
        previousTrackCalled = false
        seekPosition = null
    }
}
