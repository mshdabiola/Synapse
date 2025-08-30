package com.mshdabiola.ui

import androidx.compose.runtime.Composable

@Composable
expect fun getPlatformLogics(
    outputVoice: (String, String) -> Unit={_,_->},
    saveImage: (String) -> Unit={},
    savePhoto: () -> Unit={},
    onNotification: () -> Unit={},
): Logics
