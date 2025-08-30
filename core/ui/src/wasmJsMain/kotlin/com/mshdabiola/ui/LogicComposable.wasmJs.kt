package com.mshdabiola.ui

import androidx.compose.runtime.Composable

@Composable
actual fun getPlatformLogics(
    outputVoice: (String, String) -> Unit,
    saveImage: (String) -> Unit,
    savePhoto: () -> Unit,
    onNotification: () -> Unit,
): Logics {
    return ReaLogics()
}
