package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NotePlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeVoicePlayer @Inject constructor() : NotePlayer {
    override fun playMusic(path: String, position: Int): Flow<Int> {
        return flow { 2 }
    }

    override fun pause() {
    }
}
