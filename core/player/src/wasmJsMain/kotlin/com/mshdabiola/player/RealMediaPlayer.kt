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

import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement

internal class RealMediaPlayer : MediaPlayer {
    private val audioElement = document.createElement("audio") as HTMLAudioElement
    private var listener: MediaPlayerListener? = null
    private var currentTrack: PlayerItem? = null

    private var trackList: List<PlayerItem> = emptyList()
    private var currentTrackIndex: Int = -1

    override fun prepare(
        mediaItem: PlayerItem,
        listener: MediaPlayerListener,
    ) {
        this.listener = listener
        this.currentTrack = mediaItem

        if (trackList.isNotEmpty()) {
            val index = trackList.indexOfFirst { it.id == mediaItem.id }
            if (index >= 0) {
                currentTrackIndex = index
            }
        }

        listener.onBufferingStateChanged(true)

        audioElement.src = mediaItem.path
        audioElement.addEventListener("canplaythrough", {
            // Audio is ready to play without interruption
            listener.onBufferingStateChanged(false)
            listener.onReady()
            audioElement.play()
            listener.onPlaybackStateChanged(true)
        })

        audioElement.onended = {
            val nextTrackPlayed = playNextTrack()
            if (!nextTrackPlayed) {
                listener.onAudioCompleted()
            }
        }
        audioElement.addEventListener("error", {
            listener.onError()
        })
    }

    override fun start() {
        audioElement.play()
        listener?.onPlaybackStateChanged(true)
    }

    override fun pause() {
        audioElement.pause()
        listener?.onPlaybackStateChanged(false)
    }

        override fun seekTo(currentProgress: Float) {
               val durationSec = audioElement.duration
              if (durationSec.isFinite() && durationSec > 0) {
                       val clamped = currentProgress.coerceIn(0f, 1f)
                       audioElement.currentTime = durationSec * clamped
                  }
          }

    override fun getCurrentPosition(): Long {
        return (audioElement.currentTime * 1000).toLong()
    }

    override fun getDuration(): Long {
        val d = audioElement.duration
        return if (d.isFinite() && d > 0) (d * 1000).toLong() else 0L
    }

    override fun isPlaying(): Boolean {
        return !audioElement.paused
    }

    override fun setTrackList(trackList: List<PlayerItem>, currentTrackId: Long) {
        this.trackList = trackList
        this.currentTrackIndex = trackList.indexOfFirst { it.id == currentTrackId }.takeIf { it >= 0 } ?: 0
    }

    override fun playNextTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex < 0) {
            return false
        }

        val nextIndex = currentTrackIndex + 1
        if (nextIndex >= trackList.size) {
            return false
        }

        currentTrackIndex = nextIndex
        val nextTrack = trackList[nextIndex]

        listener?.onTrackChanged(nextTrack.id)

        prepare(nextTrack, listener ?: return false)
        return true
    }

    override fun playPreviousTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex <= 0) {
            return false
        }

        val previousIndex = currentTrackIndex - 1
        currentTrackIndex = previousIndex
        val previousTrack = trackList[previousIndex]

        listener?.onTrackChanged(previousTrack.id)

        prepare(previousTrack, listener ?: return false)
        return true
    }

    override fun getCurrentTrack(): PlayerItem? {
        currentTrack?.let { return it }

        if (trackList.isEmpty() || currentTrackIndex < 0 || currentTrackIndex >= trackList.size) {
            return null
        }
        return trackList[currentTrackIndex]
    }

    override fun getProgress(): Float {
        val duration = getDuration().toFloat()
        if (duration <= 0f) return 0f
        return getCurrentPosition() / duration
    }
}
