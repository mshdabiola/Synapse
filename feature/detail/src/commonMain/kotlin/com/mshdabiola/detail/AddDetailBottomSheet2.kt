package com.mshdabiola.detail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
expect fun AddBottomSheet2(
    currentColor: Int,
    currentImage: Int,
    isNoteCheck: Boolean,
    saveImage: (String) -> Unit = {},
    saveVoice: (String, String) -> Unit = { _, _ -> },
    getPhotoUri: () -> String = { "" },
    changeToCheckBoxes: () -> Unit = {},
    onDrawing: () -> Unit = {},
    onDismiss: () -> Unit = {},
    show: Boolean,
    isVoiceSupport: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddBottomSheet2Preview() {
    // val coroutineScope= rememberCoroutineScope()

    AddBottomSheet2(
        currentColor = 2,
        currentImage = 2,
        isNoteCheck = true,
        show = true,
    )
}
