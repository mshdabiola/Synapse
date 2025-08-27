package com.mshdabiola.player.di

import com.mshdabiola.player.MediaPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import org.koin.core.module.Module

expect val mediaPlayerModule : Module



fun MediaPlayer.currentPositionFlow() = flow {
    if (!isPlaying()) return@flow
    while (getCurrentPosition()!! < getDuration()!!) {
        emit(getCurrentPosition()!!)
        delay(100)
    }
    // emit(currentPosition)
}.distinctUntilChanged()
