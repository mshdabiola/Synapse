package com.mshdabiola.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import synapse.feature.main.generated.resources.modules_designsystem_rename_label
import synapse.feature.main.generated.resources.modules_designsystem_rename_label_detail


@Composable
fun DeleteLabelAlertDialog(
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Res.string.modules_designsystem_rename_label)) },
            text = {
                Text(text = stringResource(Res.string.modules_designsystem_rename_label_detail))
            },
            confirmButton = {
                SynTextButton(
                    onClick = {
                        onDismissRequest()
                        onDelete()
                    },
                    label = stringResource(Res.string.modules_designsystem_delete),
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
