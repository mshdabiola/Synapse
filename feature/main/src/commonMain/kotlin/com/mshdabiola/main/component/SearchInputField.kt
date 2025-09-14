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

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.testtag.SearchInputFieldTestTags
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInputField(
    searchBarState: SearchBarState,
    searchTextFieldState: TextFieldState,
    isGrid: Boolean,
    onDisplayModeChange: () -> Unit,
    onDrawer:(() -> Unit)?,
) {
    val scope = rememberCoroutineScope()
    SearchBarDefaults.InputField(
        modifier = Modifier.testTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_ROOT), // Added root test tag
        searchBarState = searchBarState,
        textFieldState = searchTextFieldState,
        onSearch = {
//                    scope.launch { searchBarState.animateToCollapsed() }
        },
        placeholder = { Text("Search Synapse") },
        leadingIcon = {
            if (searchBarState.currentValue == SearchBarValue.Expanded) {
                TooltipBox(
                    positionProvider =
                        TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = { PlainTooltip { Text("Back") } },
                    state = rememberTooltipState(),
                ) {
                    IconButton(
                        onClick = {
                            searchTextFieldState.clearText()
                            scope.launch { searchBarState.animateToCollapsed() }
                        },
                        modifier = Modifier.testTag(SearchInputFieldTestTags.SEARCH_INPUT_FIELD_BACK_BUTTON),
                        // Added back button test tag
                    ) {
                        Icon(
                            SynIcons.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            } else {
                if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                    if (onDrawer != null){
                        IconButton(
                            onClick = onDrawer,
                            modifier = Modifier.testTag(SearchInputFieldTestTags.MAIN_TOPBAR_HAMBURGER_MENU_BUTTON),
                        ) {
                            Icon(imageVector = SynIcons.Menu, contentDescription = "menu")
                        }
                    }

                }
            }
        },
        trailingIcon = {
            IconButton(
                onClick = { onDisplayModeChange() },
                modifier = Modifier.testTag(SearchInputFieldTestTags.MAIN_TOPBAR_DISPLAY_MODE_BUTTON),
            ) {
                if (!isGrid) {
                    Icon(imageVector = SynIcons.GridView, contentDescription = "grid")
                } else {
                    Icon(
                        imageVector = SynIcons.ViewAgenda,
                        contentDescription = "column",
                    )
                }
            }
        },
    )
}
