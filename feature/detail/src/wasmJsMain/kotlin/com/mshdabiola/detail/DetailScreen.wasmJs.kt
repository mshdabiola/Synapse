package com.mshdabiola.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.content.contentReceiver
import androidx.compose.ui.Modifier

@OptIn(markerClass = [ExperimentalFoundationApi::class])
actual fun Modifier.contentReceiver(onReceive: (List<String>) -> Unit): Modifier {
    return this
}
