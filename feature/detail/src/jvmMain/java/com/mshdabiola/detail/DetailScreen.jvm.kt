package com.mshdabiola.detail

import androidx.compose.foundation.content.contentReceiver
import androidx.compose.ui.Modifier

// Assuming ReceiveContentElement is defined elsewhere and its constructor/factory
// takes a parameter named `receiveContentListener` of type `(List<String>) -> Unit`
@OptIn(markerClass = [androidx.compose.foundation.ExperimentalFoundationApi::class])
actual fun Modifier.contentReceiver(onReceive: (List<String>) -> Unit): Modifier {
    return this
}
