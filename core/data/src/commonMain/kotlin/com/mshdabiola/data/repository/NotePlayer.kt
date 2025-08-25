package com.mshdabiola.data.repository

import kotlinx.coroutines.flow.Flow

interface NotePlayer {
    fun playMusic(path: String, position: Int): Flow<Int>
    fun pause()
}
