package com.mshdabiola.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun AudioDialog(
    show: Boolean,
    dismiss: () -> Unit,
    output: (String, String) -> Unit
) {
}

@Composable
actual fun supportVoice(): Boolean {
    return false
}

@Composable
actual fun ImageDialog2(
    modifier: Modifier,
    show: Boolean,
    dismiss: () -> Unit,
    saveImage: (String) -> Unit,
    getUri: () -> String,
) {
}

@Composable
actual fun ImageDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onChooseImage: () -> Unit,
    onSnapImage: () -> Unit,
) {
}
