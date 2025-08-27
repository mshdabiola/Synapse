package com.mshdabiola.player

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Manual mock for MediaPlayerListener
class MockMediaPlayerListener : MediaPlayerListener {
    var readyCalled = false
    var audioCompletedCalled = false
    var errorCalled = false
    var trackChangedTo: String? = null
    val bufferingStates = mutableListOf<Boolean>()
    val playbackStates = mutableListOf<Boolean>()
    var errorCount = 0

    fun reset() {
        readyCalled = false
        audioCompletedCalled = false
        errorCalled = false
        trackChangedTo = null
        bufferingStates.clear()
        playbackStates.clear()
        errorCount = 0
    }

    override fun onReady() { readyCalled = true }
    override fun onAudioCompleted() { audioCompletedCalled = true }
    override fun onError() {
        errorCalled = true
        errorCount++
    }
    override fun onTrackChanged(trackId: String) { trackChangedTo = trackId }
    override fun onBufferingStateChanged(isBuffering: Boolean) { bufferingStates.add(isBuffering) }
    override fun onPlaybackStateChanged(isPlaying:
 Boolean) { playbackStates.add(isPlaying) }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerTest {

    private lateinit var mediaPlayer: RealMediaPlayer
    private lateinit var mockListener: MockMediaPlayerListener
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mediaPlayer = RealMediaPlayer() // RealMediaPlayer from jvmMain
        mockListener = MockMediaPlayerListener()
        // Note: RealMediaPlayer's init block and initMediaPlayer() can cause issues
        // in a test environment without VLC. Tests need to be mindful of this.
    }

    @After
    fun tearDown() {
        mediaPlayer.release()
        Dispatchers.resetMain()
    }

    @Test
    fun `setTrackList updates internal list and current track`() {
        val tracks = listOf(
            NoteItem("id1", "path1"),
            NoteItem("id2", "path2"),
            NoteItem("id3", "path3")
        )

        mediaPlayer.setTrackList(tracks, "id2")
        assertEquals(tracks[1], mediaPlayer.getCurrentTrack(), "Current track should be 'id2'")

        // Test with a non-existent currentTrackId, should default to index 0 (as per RealMediaPlayer impl.)
        mediaPlayer.setTrackList(tracks, "nonExistentId")
        assertEquals(tracks[0], mediaPlayer.getCurrentTrack(), "Current track should default to first if id not found")

        mediaPlayer.setTrackList(emptyList(), "id1")
        assertNull(mediaPlayer.getCurrentTrack(), "Current track should be null for empty list")
    }

    @Test
    fun `getCurrentTrack returns correct track`() {
        val tracks = listOf(
            NoteItem("track1", "path/to/track1"),
            NoteItem("track2", "path/to/track2")
        )
        mediaPlayer.setTrackList(tracks, "track1")
        assertEquals(tracks[0], mediaPlayer.getCurrentTrack(), "Should return track1")

        mediaPlayer.setTrackList(tracks, "track2")
        assertEquals(tracks[1], mediaPlayer.getCurrentTrack(), "Should return track2")
    }



    @Test
    fun `prepare with blank path calls onError`() = runTest {
        val itemWithBlankPath = NoteItem("2", "")
        mediaPlayer.prepare(itemWithBlankPath, mockListener)
        assertTrue(mockListener.errorCalled, "onError should be called for blank path")
        assertEquals(1, mockListener.errorCount)
    }

    @Test
    fun `prepare with valid path attempts VLCJ init - likely calls onError in test env`() = runTest {
        val itemWithPath = NoteItem("3", "some/path/file.mp3")
        mediaPlayer.prepare(itemWithPath, mockListener)
        advanceUntilIdle() // Allow coroutines launched by prepare to run

        // In a test environment without VLC, initMediaPlayer() is expected to fail,
        // leading to listener.onError() being called.
        assertTrue(mockListener.errorCalled, "onError should be called when VLCJ initialization fails")
    }


    @Test
    fun `playNextTrack updates track and calls onTrackChanged if list is valid`() = runTest {
        val tracks = listOf(
            NoteItem("1", "path1"),
            NoteItem("2", "path2"),
            NoteItem("3", "path3")
        )
        mediaPlayer.setTrackList(tracks, "1")
        // `prepare` is needed to set the internal listener for `playNextTrack` to use for `onTrackChanged`.
        // However, `prepare` itself will likely fail to fully initialize VLCJ in a test env.
        // `playNextTrack` calls `playTrackAt` which calls `onTrackChanged` *before* `prepare`.

        // Simulate that the listener is set (as if an initial prepare was called and succeeded in setting it)
        // This is a workaround because testing the full prepare chain is hard.
        // The internal listener is set in RealMediaPlayer.prepare().
        // For this test to be isolated for playNextTrack's logic for onTrackChanged,
        // we assume the listener is already in place.
        // The first `prepare` call in `playTrackAt` will use this `mockListener`.
         mediaPlayer.prepare(tracks[0], mockListener) // This sets the listener internally
         advanceUntilIdle() // Let prepare's coroutine run
         mockListener.reset() // Reset after initial prepare's effects


        val playedNext = mediaPlayer.playNextTrack() // Should attempt to play "2"
        advanceUntilIdle() // Let the new prepare call in playNextTrack run

        assertTrue(playedNext, "playNextTrack should return true indicating an attempt")
        assertEquals("2", mockListener.trackChangedTo, "onTrackChanged should be called with the new track id '2'")
        assertEquals(tracks[1], mediaPlayer.getCurrentTrack(), "Current track should be updated to 'id2'")

        // It's also expected that onError is called because the prepare within playNextTrack will fail.
        assertTrue(mockListener.errorCalled, "onError should be called due to VLCJ init failure in subsequent prepare")
    }

    @Test
    fun `playNextTrack at end of list returns false`() = runTest {
        val tracks = listOf(
            NoteItem("1", "path1"),
            NoteItem("2", "path2")
        )
        mediaPlayer.setTrackList(tracks, "2") // Current is the last track
        mediaPlayer.prepare(tracks[1], mockListener) // Set listener
        advanceUntilIdle()
        mockListener.reset()

        val playedNext = mediaPlayer.playNextTrack()

        assertFalse(playedNext, "playNextTrack should return false at the end of the list")
        assertNull(mockListener.trackChangedTo, "onTrackChanged should not be called")
        assertEquals(tracks[1], mediaPlayer.getCurrentTrack(), "Current track should remain the last track")
        assertFalse(mockListener.errorCalled, "onError should not be called if no attempt to prepare was made")
    }

    @Test
    fun `playPreviousTrack updates track and calls onTrackChanged`() = runTest {
        val tracks = listOf(
            NoteItem("1", "path1"),
            NoteItem("2", "path2"),
            NoteItem("3", "path3")
        )
        mediaPlayer.setTrackList(tracks, "2")
        mediaPlayer.prepare(tracks[1], mockListener) // Set listener
        advanceUntilIdle()
        mockListener.reset()

        val playedPrev = mediaPlayer.playPreviousTrack() // Should attempt to play "1"
        advanceUntilIdle()

        assertTrue(playedPrev, "playPreviousTrack should return true")
        assertEquals("1", mockListener.trackChangedTo, "onTrackChanged should be called with 'id1'")
        assertEquals(tracks[0], mediaPlayer.getCurrentTrack(), "Current track should be 'id1'")
        assertTrue(mockListener.errorCalled, "onError should be called due to VLCJ init failure")
    }

    @Test
    fun `playPreviousTrack at start of list returns false`() = runTest {
        val tracks = listOf(
            NoteItem("1", "path1"),
            NoteItem("2", "path2")
        )
        mediaPlayer.setTrackList(tracks, "1") // Current is the first track
        mediaPlayer.prepare(tracks[0], mockListener)
        advanceUntilIdle()
        mockListener.reset()

        val playedPrev = mediaPlayer.playPreviousTrack()

        assertFalse(playedPrev, "playPreviousTrack should return false at the start of the list")
        assertNull(mockListener.trackChangedTo, "onTrackChanged should not be called")
        assertEquals(tracks[0], mediaPlayer.getCurrentTrack(), "Current track should remain the first track")
        assertFalse(mockListener.errorCalled)
    }

    @Test
    fun `release does not throw exception`() {
        try {
            mediaPlayer.release()
            // No specific assertion other than it doesn't crash.
            // Verifying VLCJ release or scope cancellation is complex without deeper integration.
        } catch (e: Exception) {
            throw AssertionError("mediaPlayer.release() should not throw an exception", e)
        }
    }

    // Tests for start, pause, seekTo, getDuration, getCurrentPosition, isPlaying
    // are omitted as they directly call VLCJ methods that won't work reliably
    // in a unit test environment without a real VLC instance and extensive mocking.
    // Testing them would require integration tests or a different testing strategy.
}
