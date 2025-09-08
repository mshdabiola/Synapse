package com.mshdabiola.detail

import com.mshdabiola.player.MediaPlayer
import com.mshdabiola.player.MediaPlayerListener
import com.mshdabiola.player.NoteItem

class FakeMediaPlayer : MediaPlayer {
    private var _isPlaying: Boolean = false
    private var _currentTrack: NoteItem? = null
    private var _trackList: List<NoteItem> = emptyList()
    private var _currentIndex: Int = -1
    private var _listener: MediaPlayerListener? = null
    private var _currentPosition: Long = 0L
    private var _duration: Long = 0L

    var preparedTrackId: String? = null
    var trackListSet: Boolean = false
    var startedCalled: Boolean = false
    var pausedCalled: Boolean = false
    var nextTrackCalled: Boolean = false
    var previousTrackCalled: Boolean = false
    var seekPosition: Long? = null

    override fun prepare(mediaItem: NoteItem, listener: MediaPlayerListener) {
        _currentTrack = mediaItem
        _listener = listener
        _duration = 200000L // Default duration 200s if not specified
        _currentPosition = 0L
        _isPlaying = false
        preparedTrackId = mediaItem.id
    }

    override fun setTrackList(trackList: List<NoteItem>, currentTrackId: String) {
        _trackList = trackList
        _currentIndex = _trackList.indexOfFirst { it.id == currentTrackId }
        if (_currentIndex != -1) {
            _currentTrack = _trackList[_currentIndex]
            // Optionally call prepare or onReady if the behavior is to auto-prepare
            // For this fake, we assume prepare is called separately or listener is updated if needed
            _listener?.onTrackChanged(_trackList[_currentIndex].path)
        } else {
            _currentTrack = null
        }
        trackListSet = true
    }

    override fun playNextTrack(): Boolean {
        nextTrackCalled = true
        if (_trackList.isEmpty() || _currentIndex >= _trackList.size - 1) {
            return false
        }
        _currentIndex++
        _currentTrack = _trackList[_currentIndex]
        _currentPosition = 0L
        _duration =  200000L
        _listener?.onTrackChanged(_currentTrack!!.path)
        // Simulate auto-play on next track if that's the desired fake behavior
        // start()
        return true
    }

    override fun playPreviousTrack(): Boolean {
        previousTrackCalled = true
        if (_trackList.isEmpty() || _currentIndex <= 0) {
            return false
        }
        _currentIndex--
        _currentTrack = _trackList[_currentIndex]
        _currentPosition = 0L
        _duration =  200000L
        _listener?.onTrackChanged(_currentTrack!!.path)
        // Simulate auto-play on previous track if that's the desired fake behavior
        // start()
        return true
    }

    override fun start() {
        if (_currentTrack != null) {
            _isPlaying = true
            startedCalled = true
            pausedCalled = false

        }
    }

    override fun pause() {
        _isPlaying = false
        pausedCalled = true
        startedCalled = false

    }

    override fun getCurrentPosition(): Long? {
        return if (_currentTrack != null) _currentPosition else null
    }

    override fun getDuration(): Long? {
        return if (_currentTrack != null) _duration else null
    }

    override fun seekTo(seconds: Long) {
        seekPosition = seconds
        val newPosition = seconds * 1000 // Assuming seconds to milliseconds
        _currentPosition = if (_currentTrack != null) {
            newPosition.coerceIn(0, _duration)
        } else {
            0L
        }

    }

    override fun isPlaying(): Boolean {
        return _isPlaying
    }

    override fun getCurrentTrack(): NoteItem? {
        return _currentTrack
    }




    fun reset() {
        _isPlaying = false
        _currentTrack = null
        _trackList = emptyList()
        _currentIndex = -1
        _listener = null
        _currentPosition = 0L
        _duration = 0L
        preparedTrackId = null
        trackListSet = false
        startedCalled = false
        pausedCalled = false
        nextTrackCalled = false
        previousTrackCalled = false
        seekPosition = null
    }
}
