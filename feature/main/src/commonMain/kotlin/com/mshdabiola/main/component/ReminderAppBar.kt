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
import com.mshdabiola.model.testtag.ReminderAppBarTestTags
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.reminder_app_bar_column_cd
import synapse.feature.main.generated.resources.reminder_app_bar_grid_cd
import synapse.feature.main.generated.resources.reminder_app_bar_menu_cd
import synapse.feature.main.generated.resources.reminder_app_bar_search_cd
import synapse.feature.main.generated.resources.reminder_app_bar_title

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReminderAppBar(
    modifier: Modifier = Modifier,
    isGrid: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onDisplayModeChange: () -> Unit = {},
    onHamburgerMenuClick: (() -> Unit)? = {},
    onSearchClick: () -> Unit = {},

) {
    SynTopAppBar(
        modifier = modifier.testTag(ReminderAppBarTestTags.APP_BAR_ROOT),
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (onHamburgerMenuClick != null) {
                IconButton(
                    onClick = onHamburgerMenuClick,
                    modifier = Modifier.testTag(ReminderAppBarTestTags.NAVIGATION_ICON),
                ) {
                    Icon(
                        imageVector = SynIcons.Menu,
                        contentDescription = stringResource(Res.string.reminder_app_bar_menu_cd),
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(Res.string.reminder_app_bar_title),
                modifier = Modifier.testTag(ReminderAppBarTestTags.TITLE_TEXT),
            )
        },
        subtitle = {},
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag(ReminderAppBarTestTags.SEARCH_ICON_BUTTON),
            ) {
                Icon(
                    imageVector = SynIcons.Search,
                    contentDescription = stringResource(Res.string.reminder_app_bar_search_cd),
                )
            }
            IconButton(
                onClick = { onDisplayModeChange() },
                modifier = Modifier.testTag(ReminderAppBarTestTags.DISPLAY_MODE_ICON_BUTTON),
            ) {
                if (!isGrid) {
                    Icon(
                        imageVector = SynIcons.GridView,
                        contentDescription = stringResource(Res.string.reminder_app_bar_grid_cd),
                    )
                } else {
                    Icon(
                        imageVector = SynIcons.ViewAgenda,
                        contentDescription = stringResource(Res.string.reminder_app_bar_column_cd),
                    )
                }
            }
        },
    )
}

// ... (Preview)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun ReminderAppBarPreview() {
    ReminderAppBar(
        isGrid = false,
    )
}
