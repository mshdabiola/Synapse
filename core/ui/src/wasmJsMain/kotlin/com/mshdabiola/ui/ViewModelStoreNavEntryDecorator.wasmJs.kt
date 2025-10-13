package com.mshdabiola.ui

import androidx.compose.runtime.Composable
@Composable
internal actual fun shouldRemoveViewModelStoreCallback(): () -> Boolean {
    return { true }
}
