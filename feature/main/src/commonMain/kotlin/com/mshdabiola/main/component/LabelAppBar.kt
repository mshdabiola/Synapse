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
import com.mshdabiola.model.testtag.LabelAppBarTestTags
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.feature_main_delete_label
import synapse.feature.main.generated.resources.feature_main_rename_label

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LabelAppBar(
    modifier: Modifier = Modifier,
    labelName: String? = null,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onHamburgerMenuClick: (() -> Unit)? = {},
    onSearchClick: () -> Unit = {},
    onLabelNameChange: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},
) {
    SynTopAppBar(
        modifier = modifier.testTag(LabelAppBarTestTags.APP_BAR_ROOT),
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (onHamburgerMenuClick!=null){
            IconButton(
                onClick = onHamburgerMenuClick,
                modifier = Modifier.testTag(LabelAppBarTestTags.NAVIGATION_ICON),
            ) {
                Icon(imageVector = SynIcons.Menu, contentDescription = "menu")
            }
            }
        },
        title = {
            Text(text = labelName ?: "", modifier = Modifier.testTag(LabelAppBarTestTags.TITLE_TEXT))
        },
        subtitle = {},
        actions = {
            var showDropDown by remember {
                mutableStateOf(false)
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag(LabelAppBarTestTags.SEARCH_ICON_BUTTON),
            ) {
                Icon(
                    imageVector = SynIcons.Search,
                    contentDescription = "search",
                )
            }

            Box {
                IconButton(
                    onClick = { showDropDown = true },
                    modifier = Modifier.testTag(LabelAppBarTestTags.MORE_OPTIONS_ICON_BUTTON),
                ) {
                    Icon(SynIcons.MoreVert, contentDescription = "more")
                }
                DropdownMenu(
                    expanded = showDropDown,
                    onDismissRequest = { showDropDown = false },
                    modifier = Modifier.testTag(LabelAppBarTestTags.OPTIONS_DROPDOWN_MENU),
                ) {
                    DropdownMenuItem(
                        modifier = Modifier.testTag(LabelAppBarTestTags.RENAME_LABEL_MENU_ITEM),
                        text = {
                            Text(text = stringResource(Res.string.feature_main_rename_label))
                        },
                        onClick = {
                            showDropDown = false
                            onLabelNameChange()
                        },
                    )
                    DropdownMenuItem(
                        modifier = Modifier.testTag(LabelAppBarTestTags.DELETE_LABEL_MENU_ITEM),
                        text = {
                            Text(text = stringResource(Res.string.feature_main_delete_label))
                        },
                        onClick = {
                            showDropDown = false
                            onDeleteLabel()
                        },
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun LabelAppBarPreview() {
    LabelAppBar(
        labelName = "My Label",
    )
}
