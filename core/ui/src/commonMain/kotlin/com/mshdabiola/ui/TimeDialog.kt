package com.mshdabiola.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import com.mshdabiola.designsystem.component.SynButton
import com.mshdabiola.designsystem.component.SynTextButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(
    state: TimePickerState = TimePickerState(12, 4, is24Hour = false),
    showDialog: Boolean = true,
    onDismissRequest: () -> Unit = {},
    onSetTime: () -> Unit = {},
) {
    AnimatedVisibility(visible = showDialog) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                SynButton(onClick = {
                    onSetTime()

                    onDismissRequest()
                },
                    label = "Set time"

                )
            },
            dismissButton = {
                SynTextButton(onClick = onDismissRequest, label = "Cancel")
            },
        ) {
            TimePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TimeDialogPreview() {
    TimeDialog()
}
