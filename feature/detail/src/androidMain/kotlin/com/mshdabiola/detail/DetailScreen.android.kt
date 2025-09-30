package com.mshdabiola.detail

import androidx.compose.foundation.content.consume
import androidx.compose.foundation.content.contentReceiver
import androidx.compose.ui.Modifier

@OptIn(markerClass = [androidx.compose.foundation.ExperimentalFoundationApi::class])
actual fun Modifier.contentReceiver(onReceive: (List<String>) -> Unit): Modifier {
    return this.contentReceiver { transferableContent ->
        val paths = mutableListOf<String>()
        val remaining = transferableContent.consume { item ->

            item.uri?.toString()?.let { path ->
                println()
                paths.add(path)
            }
            true // Indicate that we've processed this item
        }
        if (paths.isNotEmpty()) {
            onReceive(paths)
        }
        remaining // Return any unconsumed content
    }
}
