package com.mshdabiola.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mshdabiola.designsystem.component.SynButton
import com.mshdabiola.designsystem.component.SynTextButton
import org.jetbrains.compose.resources.stringResource
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.modules_designsystem_cancel
import synapse.feature.main.generated.resources.modules_designsystem_close
import synapse.feature.main.generated.resources.modules_designsystem_delete
import synapse.feature.main.generated.resources.modules_designsystem_dialog_delete_forever
import synapse.feature.main.generated.resources.modules_designsystem_dialog_delete_forever_content
import synapse.feature.main.generated.resources.modules_designsystem_dialog_empty_trash
import synapse.feature.main.generated.resources.modules_designsystem_dialog_empty_trash_content
import synapse.feature.main.generated.resources.modules_designsystem_rename
import synapse.feature.main.generated.resources.modules_designsystem_rename_label

@Composable
fun RenameLabelAlertDialog(
    show: Boolean = false,
    label: String = "Label",
    onDismissRequest: () -> Unit = {},
    onChangeName: (String) -> Unit = {},
) {
    var name by remember(label) {
        mutableStateOf(label)
    }

    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Res.string.modules_designsystem_rename_label)) },
            text = {
                TextField(value = name, onValueChange = { name = it })
            },
            confirmButton = {
                SynButton(
                    onClick = {
                        onDismissRequest()
                        onChangeName(name)
                    },
                    label = stringResource(Res.string.modules_designsystem_rename),
                )
            },
            dismissButton = {
                SynTextButton(
                    onClick = { onDismissRequest() },
                    label = stringResource(Res.string.modules_designsystem_cancel),
                )
            },
        )
    }
}
