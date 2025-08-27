package com.mshdabiola.player.di

import com.mshdabiola.player.MediaPlayer
import com.mshdabiola.player.RealMediaPlayer
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val mediaPlayerModule: Module
    get() = module {
        singleOf(::RealMediaPlayer) bind MediaPlayer::class
    }
