package com.mshdabiola.main.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.component.SynTopAppBar
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.main.model.SelectState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.modules_designsystem_delete_forever


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectTrashAppBar(
    modifier: Modifier = Modifier,
    selectState: SelectState,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onClearSelection: () -> Unit = {},
    onDeleteForever: () -> Unit = {},
    onRestore: () -> Unit = {},
) {
    SynTopAppBar(
        modifier=modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = onClearSelection,
                modifier = Modifier.testTag("main:topbar_clear_selection_button"),
            ) {
                Icon(imageVector = SynIcons.Clear, contentDescription = "clear note")
            }
        },
        title = {
            Text(selectState.setOfSelected.size.toString())

        },
        subtitle = {},
        actions = {
            var showDropDown by remember {
                mutableStateOf(false)
            }

            IconButton(
                modifier = Modifier.testTag("main:topbar_restore_button"),
                onClick = onRestore,
            ) {
                Icon(
                    imageVector = SynIcons.RestoreFromTrash,
                    contentDescription = "restore note",
                )
            }
            Box {
                IconButton(
                    modifier = Modifier.testTag("main:topbar_more_options_button"),
                    onClick = { showDropDown = true },
                ) {
                    Icon(SynIcons.MoreVert, contentDescription = "more")
                }
                DropdownMenu(
                    expanded = showDropDown,
                    onDismissRequest = { showDropDown = false },
                    modifier = Modifier.testTag("main:topbar_trash_options_dropdown"),
                ) {
                    DropdownMenuItem(
                        modifier = Modifier.testTag("main:topbar_delete_forever_menu_item"),
                        text = {
                            Text(
                                text =
                                    stringResource(Res.string.modules_designsystem_delete_forever),
                            )
                        },
                        onClick = {
                            showDropDown = false
                            onDeleteForever()
                        },
                    )
                }
            }
        },
        color = MaterialTheme.colorScheme.secondaryContainer


    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun SelectTrashAppBarPreview() {
    SelectTrashAppBar(
        selectState = SelectState(
            colorIndex = -1,
            isAllPin = false,
            setOfSelected = setOf(1L, 2L, 3L),
            notificationUiState = null,
        ),
    )
}
