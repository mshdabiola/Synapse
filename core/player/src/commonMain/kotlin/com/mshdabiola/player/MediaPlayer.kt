package com.mshdabiola.player


interface  MediaPlayer{
    fun prepare(
        mediaItem: TrackItem,
        listener: MediaPlayerListener
    )

    fun setTrackList(trackList: List<TrackItem>, currentTrackId: String)

    fun playNextTrack(): Boolean

    fun playPreviousTrack(): Boolean

    fun start()

    fun pause()

    fun getCurrentPosition(): Long?

    fun getDuration(): Long?

    fun seekTo(seconds: Long)

    fun isPlaying(): Boolean

    fun getCurrentTrack(): TrackItem?
}
