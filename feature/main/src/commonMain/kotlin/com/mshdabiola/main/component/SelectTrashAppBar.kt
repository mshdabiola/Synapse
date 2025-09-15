/*
 * Designed and developed by 2024 mshdabiola (lawal abiola)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.component.SynTopAppBar
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.main.model.SelectState
import com.mshdabiola.model.testtag.SelectTrashAppBarTestTags
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.feature_main_delete_forever

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
        modifier = modifier.testTag(SelectTrashAppBarTestTags.ROOT_APP_BAR),
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = onClearSelection,
                modifier = Modifier.testTag(SelectTrashAppBarTestTags.CLEAR_SELECTION_BUTTON),
            ) {
                Icon(imageVector = SynIcons.Clear, contentDescription = "clear note")
            }
        },
        title = {
            Text(
                text = selectState.setOfSelected.size.toString(),
                modifier = Modifier.testTag(SelectTrashAppBarTestTags.TITLE_TEXT),
            )
        },
        subtitle = {},
        actions = {
            var showDropDown by remember {
                mutableStateOf(false)
            }

            IconButton(
                modifier = Modifier.testTag(SelectTrashAppBarTestTags.RESTORE_BUTTON),
                onClick = onRestore,
            ) {
                Icon(
                    imageVector = SynIcons.RestoreFromTrash,
                    contentDescription = "restore note",
                )
            }
            Box {
                IconButton(
                    modifier = Modifier.testTag(SelectTrashAppBarTestTags.MORE_OPTIONS_BUTTON),
                    onClick = { showDropDown = true },
                ) {
                    Icon(SynIcons.MoreVert, contentDescription = "more")
                }
                DropdownMenu(
                    expanded = showDropDown,
                    onDismissRequest = { showDropDown = false },
                    modifier = Modifier.testTag(SelectTrashAppBarTestTags.TRASH_OPTIONS_DROPDOWN),
                ) {
                    DropdownMenuItem(
                        modifier = Modifier.testTag(SelectTrashAppBarTestTags.DELETE_FOREVER_MENU_ITEM),
                        text = {
                            Text(
                                text =
                                stringResource(Res.string.feature_main_delete_forever),
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
        color = MaterialTheme.colorScheme.secondaryContainer,

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
