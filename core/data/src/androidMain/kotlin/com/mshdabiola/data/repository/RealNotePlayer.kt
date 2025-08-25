package com.mshdabiola.data.repository

import android.media.MediaPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class RealNotePlayer : NotePlayer{
    private val mediaPlayer = MediaPlayer()

    override fun playMusic(path: String, position: Int): Flow<Int> {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepare()

        mediaPlayer.seekTo(position)
        mediaPlayer.start()

        return mediaPlayer.listerner()
    }

    override fun pause() {
        mediaPlayer.pause()
    }
}

fun MediaPlayer.listerner() = flow {
    if (!isPlaying) return@flow
    while (currentPosition < duration) {
        emit(currentPosition)
        delay(100)
    }
    // emit(currentPosition)
}.distinctUntilChanged()
