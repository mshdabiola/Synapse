package com.mshdabiola.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
expect fun AudioDialog(
    show: Boolean = false,
    dismiss: () -> Unit = {},
    output: (String, String) -> Unit = { _, _ -> },
)

@Composable
expect fun supportVoice(): Boolean

@Composable
expect fun ImageDialog2(
    modifier: Modifier = Modifier,
    show: Boolean = false,
    dismiss: () -> Unit = {},
    saveImage: (String) -> Unit = {},
    getUri: () -> String = { "" },

)

@Composable
expect fun ImageDialog(
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onChooseImage: () -> Unit = {},
    onSnapImage: () -> Unit = {},

)

@Preview
@Composable
fun ImageDialogPreview() {
    ImageDialog(true)
}
