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
import com.mshdabiola.player.PlayerItem

class FakeMediaPlayer : MediaPlayer {
    override fun prepare(
        mediaItem: PlayerItem,
        listener: MediaPlayerListener,
    ) {
        TODO("Not yet implemented")
    }

    override fun setTrackList(
        trackList: List<PlayerItem>,
        currentTrackId: Long,
    ) {
        TODO("Not yet implemented")
    }

    override fun playNextTrack(): Boolean {
        TODO("Not yet implemented")
    }

    override fun playPreviousTrack(): Boolean {
        TODO("Not yet implemented")
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun getCurrentPosition(): Long {
        TODO("Not yet implemented")
    }

    override fun getDuration(): Long {
        TODO("Not yet implemented")
    }

    override fun seekTo(currentProgress: Float) {
        TODO("Not yet implemented")
    }

    override fun isPlaying(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCurrentTrack(): PlayerItem? {
        TODO("Not yet implemented")
    }

    override fun getProgress(): Float {
        TODO("Not yet implemented")
    }

}
