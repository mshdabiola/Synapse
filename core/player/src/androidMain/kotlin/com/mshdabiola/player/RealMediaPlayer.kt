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
package com.mshdabiola.player

import java.io.IOException
import android.media.MediaPlayer as AndroidMediaPlayer // Alias to avoid confusion

internal class RealMediaPlayer : MediaPlayer {
    private val androidMediaPlayer: AndroidMediaPlayer = AndroidMediaPlayer()
    private var listener: MediaPlayerListener? = null
    private var trackList: List<PlayerItem> = emptyList()
    private var currentTrackIndex: Int = -1
    private var currentTrackInternal: PlayerItem? = null
    private var isPrepared: Boolean = false
    private var playWhenReady: Boolean = false

    override fun prepare(
        mediaItem: PlayerItem,
        listener: MediaPlayerListener,
    ) {
        this.listener = listener
        this.currentTrackInternal = mediaItem
        this.isPrepared = false

        androidMediaPlayer.reset()
        try {
            androidMediaPlayer.setDataSource(mediaItem.path)
        } catch (e: IOException) {
            this.listener?.onError()
            return
        } catch (e: IllegalArgumentException) {
            this.listener?.onError()
            return
        } catch (e: SecurityException) {
            this.listener?.onError()
            return
        } catch (e: IllegalStateException) {
            this.listener?.onError()
            return
        }

        androidMediaPlayer.setOnPreparedListener {
            isPrepared = true
            this.listener?.onReady()
            if (playWhenReady) {
                // playWhenReady = false // Keep true if start() is called again before pause
                startInternal()
            }
        }

        androidMediaPlayer.setOnCompletionListener {
            // isPrepared = false // Keep prepared to allow seeking or restarting
            this.listener?.onAudioCompleted()
            // Optional: Auto-play next or handle via listener
        }

        androidMediaPlayer.setOnErrorListener { _, _, _ ->
            isPrepared = false
            playWhenReady = false
            this.listener?.onError()
            true // Error handled
        }

        androidMediaPlayer.setOnInfoListener { _, what, _ ->
            when (what) {
                AndroidMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    this.listener?.onBufferingStateChanged(true)
                }
                AndroidMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    this.listener?.onBufferingStateChanged(false)
                }
            }
            true
        }
        androidMediaPlayer.prepareAsync()
    }

    override fun setTrackList(trackList: List<PlayerItem>, currentTrackId: Long) {
        this.trackList = trackList
        val newIndex = trackList.indexOfFirst { it.id == currentTrackId }
        if (newIndex != -1) {
            currentTrackIndex = newIndex
            // currentTrackInternal = trackList[currentTrackIndex] // Set by prepare
            // listener?.onTrackChanged(currentTrackInternal!!.id) // Also by prepare
        } else {
            currentTrackIndex = -1
            currentTrackInternal = null
        }
    }

    private fun playTrackAtIndex(index: Int): Boolean {
        if (index < 0 || index >= trackList.size) {
            return false
        }
        currentTrackIndex = index
        val trackToPlay = trackList[currentTrackIndex]

        val wasPlaying = isPlaying() || playWhenReady
        playWhenReady = false // Reset before preparing new track

        currentTrackInternal = trackToPlay // Update internal reference immediately
        this.listener?.onTrackChanged(trackToPlay.id) // Notify listener

        this.listener?.let {
            if (wasPlaying) {
                playWhenReady = true
            }
            prepare(trackToPlay, it)
        }
        return true
    }

    override fun playNextTrack(): Boolean {
        if (trackList.isEmpty()) return false
        val nextIndex = if (currentTrackIndex >= trackList.size - 1) 0 else currentTrackIndex + 1 // Loop for now
        return playTrackAtIndex(nextIndex)
    }

    override fun playPreviousTrack(): Boolean {
        if (trackList.isEmpty()) return false
        val prevIndex = if (currentTrackIndex <= 0) trackList.size - 1 else currentTrackIndex - 1 // Loop for now
        return playTrackAtIndex(prevIndex)
    }

    private fun startInternal() {
        if (isPrepared && currentTrackInternal != null) {
            androidMediaPlayer.start()
            listener?.onPlaybackStateChanged(true)
        }
    }

    override fun start() {
        if (currentTrackInternal == null && trackList.isNotEmpty() && currentTrackIndex != -1) {
            // If no current track but list is available, attempt to load and play
            currentTrackInternal = trackList.getOrNull(currentTrackIndex)
        }

        if (currentTrackInternal != null) {
            playWhenReady = true // Set intent to play
            if (isPrepared) {
                startInternal()
            } else {
                // Not prepared, prepare will pick up playWhenReady
                this.listener?.let { prepare(currentTrackInternal!!, it) }
            }
        }
    }

    override fun pause() {
        playWhenReady = false // Clear intent to play
        if (isPrepared && androidMediaPlayer.isPlaying) {
            androidMediaPlayer.pause()
            listener?.onPlaybackStateChanged(false)
        }
    }

    override fun getCurrentPosition(): Long {
        return if (isPrepared && currentTrackInternal != null) {
            try {
                androidMediaPlayer.currentPosition.toLong()
            } catch (e: IllegalStateException) {
                0 // Can happen if player is not in a valid state
            }
        } else {
            0
        }
    }

    override fun getDuration(): Long {
        return if (isPrepared && currentTrackInternal != null) {
            try {
                androidMediaPlayer.duration.toLong()
            } catch (e: IllegalStateException) {
                0
            }
        } else {
            0
        }
    }

    override fun seekTo(currentProgress: Float) {
        if (isPrepared && currentTrackInternal != null) {
            val dur = try {
                androidMediaPlayer.duration
            } catch (e: IllegalStateException) {
                0
            }
            if (dur > 0) {
                val clamped = currentProgress.coerceIn(0f, 1f)
                androidMediaPlayer.seekTo((clamped * dur).toInt())
            }
        }
    }

    override fun isPlaying(): Boolean {
        return if (isPrepared && currentTrackInternal != null) {
            try {
                androidMediaPlayer.isPlaying
            } catch (e: IllegalStateException) {
                e.printStackTrace()

                false
            }
        } else {
            false
        }
    }

    override fun getCurrentTrack(): PlayerItem? {
        return currentTrackInternal
    }

    override fun getProgress(): Float {
        val duration = getDuration()
        if (duration <= 0L) return 0f
        val position = getCurrentPosition().coerceIn(0L, duration)
        return position.toFloat() / duration
    }

    // Consider adding a release method if this player is to be disposed
    fun release() {
        androidMediaPlayer.release()
        listener = null
        isPrepared = false
        playWhenReady = false
    }
}
