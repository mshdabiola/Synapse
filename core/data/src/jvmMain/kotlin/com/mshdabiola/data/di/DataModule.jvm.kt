package com.mshdabiola.data.di

import com.mshdabiola.data.repository.AlarmManager
import com.mshdabiola.data.repository.ContentManager
import com.mshdabiola.data.repository.RealAlarmRepository
import com.mshdabiola.data.repository.RealContentManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: org.koin.core.module.Module
    get() = module {
        singleOf(::RealAlarmRepository) bind AlarmManager::class
        singleOf(::RealContentManager) bind ContentManager::class

    }
