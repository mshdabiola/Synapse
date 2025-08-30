package com.mshdabiola.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getPlatformLogics(): Logics {
    val context = LocalContext.current
    return ReaLogics(context)
}
