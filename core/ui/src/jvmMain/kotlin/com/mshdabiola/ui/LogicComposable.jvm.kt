package com.mshdabiola.ui

import androidx.compose.runtime.Composable

@Composable
actual fun getPlatformLogics(
    outputVoice: (String, String) -> Unit = { _, _ -> },
    saveImage: (String) -> Unit = {},
    getUri: () -> String = { "" }
): Logics {
    return ReaLogics()
}
