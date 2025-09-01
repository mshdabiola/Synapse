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

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.component.SynTopAppBar
import com.mshdabiola.designsystem.drawable.SynIcons
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReminderAppBar(
    modifier: Modifier = Modifier,
    isGrid: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onDisplayModeChange: () -> Unit = {},
    onHamburgerMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},

) {
    SynTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = onHamburgerMenuClick,
                modifier = Modifier.testTag("main:topbar_hamburger_menu_button"),
            ) {
                Icon(imageVector = SynIcons.Menu, contentDescription = "menu")
            }
        },
        title = {
            Text(text = "Reminder")
        },
        subtitle = {},
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("main:topbar_search_button"),
            ) {
                Icon(
                    imageVector = SynIcons.Search,
                    contentDescription = "search",
                )
            }
            IconButton(
                onClick = { onDisplayModeChange() },
                modifier = Modifier.testTag("main:topbar_display_mode_button"),
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun ReminderAppBarPreview() {
    ReminderAppBar(
        isGrid = false,
    )
}
