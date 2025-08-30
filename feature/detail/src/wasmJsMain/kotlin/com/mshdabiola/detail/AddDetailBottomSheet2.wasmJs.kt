package com.mshdabiola.detail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

@OptIn(markerClass = [androidx.compose.material3.ExperimentalMaterial3Api::class])
@androidx.compose.runtime.Composable
actual fun AddBottomSheet2(
    currentColor: Int,
    currentImage: Int,
    isNoteCheck: Boolean,
    saveImage: (String) -> Unit,
    saveVoice: (String, String) -> Unit,
    getPhotoUri: () -> String,
    changeToCheckBoxes: () -> Unit,
    onDrawing: () -> Unit,
    onDismiss: () -> Unit,
    show: Boolean,
    isVoiceSupport: Boolean,
) {
}
