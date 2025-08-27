package com.mshdabiola.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer as Mp
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.util.Locale
import java.util.logging.Logger

internal class RealMediaPlayer : MediaPlayer {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var mediaPlayer: Mp? = null
    private var listener: MediaPlayerListener? = null
    private var currentTrack: NoteItem? = null
    private var trackList: List<NoteItem> = emptyList()
    private var currentTrackIndex: Int = -1
    private val logger = Logger.getLogger(MediaPlayer::class.java.name)

    init {
        System.setProperty("vlcj.log", "DEBUG")
    }

    private fun initMediaPlayer(): Boolean {
        try {
            NativeDiscovery().discover()
            releaseMediaPlayer()

            val component = if (isMacOS()) CallbackMediaPlayerComponent() else EmbeddedMediaPlayerComponent()
            mediaPlayer = component.mediaPlayerFactory().mediaPlayers().newMediaPlayer()

            mediaPlayer?.events()?.addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {

                override fun mediaPlayerReady(mediaPlayer: Mp?) {
                    scope.launch { listener?.onReady() }
                }

                override fun finished(mediaPlayer: Mp?) {
                    scope.launch {
                        if (!playNextTrack()) {
                            listener?.onAudioCompleted()
                        }
                    }
                }

                override fun error(mediaPlayer: Mp?) {
                    scope.launch { listener?.onError() }
                }

                override fun playing(mediaPlayer: Mp?) {
                    scope.launch { listener?.onPlaybackStateChanged(true) }
                }

                override fun paused(mediaPlayer: Mp?) {
                    scope.launch { listener?.onPlaybackStateChanged(false) }
                }

                override fun buffering(mediaPlayer: Mp?, newCache: Float) {
                    scope.launch { listener?.onBufferingStateChanged(newCache < 100f) }
                }
            })

            return true
        } catch (e: Exception) {
            logger.severe("Failed to initialize media player: ${e.message}")
            listener?.onError()
            return false
        }
    }

    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.controls()?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            logger.severe("Error releasing media player: ${e.message}")
        }
    }

     override fun prepare(mediaItem: NoteItem, listener: MediaPlayerListener) {
        this.listener = listener
        this.currentTrack = mediaItem

        if (mediaItem.path.isNullOrBlank()) {
            listener.onError()
            return
        }

        scope.launch {
            try {
                // Initialize player if needed
                if (mediaPlayer == null) {
                    if (!initMediaPlayer()) return@launch
                }

                // Update track index if in playlist
                if (trackList.isNotEmpty()) {
                    trackList.indexOfFirst { it.id == mediaItem.id }
                        .takeIf { it >= 0 }
                        ?.let { currentTrackIndex = it }
                }

                // Prepare and play media
                mediaPlayer?.media()?.prepare(mediaItem.path)
                mediaPlayer?.controls()?.play()
                listener.onBufferingStateChanged(true)
            } catch (e: Exception) {
                // Try to recover once
                try {
                    if (initMediaPlayer()) {
                        mediaPlayer?.media()?.prepare(mediaItem.path)
                        mediaPlayer?.controls()?.play()
                        listener.onBufferingStateChanged(true)
                    } else {
                        listener.onError()
                    }
                } catch (e: Exception) {
                    listener.onError()
                }
            }
        }
    }

    private fun playTrackAt(index: Int): Boolean {
        if (index < 0 || index >= trackList.size || listener == null) return false

        currentTrackIndex = index
        val track = trackList[index]
        listener?.onTrackChanged(track.id)

        try {
            prepare(track, listener!!)
            return true
        } catch (e: Exception) {
            listener?.onError()
            return false
        }
    }

    override fun playNextTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex < 0) return false

        val nextIndex = currentTrackIndex + 1
        if (nextIndex >= trackList.size) return false

        return playTrackAt(nextIndex)
    }

    override fun playPreviousTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex <= 0) return false

        return playTrackAt(currentTrackIndex - 1)
    }

    fun release() {
        releaseMediaPlayer()
        scope.cancel()
    }

    private fun isMacOS(): Boolean {
        val os = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
        return os.contains("mac") || os.contains("darwin")
    }

    override fun setTrackList(trackList: List<NoteItem>, currentTrackId: String) {
        this.trackList = trackList
        this.currentTrackIndex = trackList.indexOfFirst { it.id == currentTrackId }.takeIf { it >= 0 } ?: 0
    }

    override fun getCurrentTrack(): NoteItem? {
        return currentTrack ?: trackList.getOrNull(currentTrackIndex)
    }

    override fun start() {
        mediaPlayer?.controls()?.play()
        listener?.onPlaybackStateChanged(true)
    }

    override fun pause() {
        mediaPlayer?.controls()?.pause()
        listener?.onPlaybackStateChanged(false)
    }

    override fun getCurrentPosition(): Long? {
        return mediaPlayer?.status()?.time()?.toLong() ?: 0L
    }

    override fun getDuration(): Long? {
        return mediaPlayer?.status()?.length() ?: 0L
    }

    override fun seekTo(seconds: Long) {
        mediaPlayer?.controls()?.setTime(seconds)
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.status()?.isPlaying ?: false
    }
}
