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
import com.mshdabiola.model.testtag.ArchiveAppBarTestTags
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.archive_app_bar_column_content_description
import synapse.feature.main.generated.resources.archive_app_bar_grid_content_description
import synapse.feature.main.generated.resources.archive_app_bar_menu_content_description
import synapse.feature.main.generated.resources.archive_app_bar_search_content_description
import synapse.feature.main.generated.resources.archive_app_bar_title

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArchiveAppBar(
    modifier: Modifier = Modifier,
    isGrid: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onDisplayModeChange: () -> Unit = {},
    onHamburgerMenuClick: (() -> Unit)? = {},
    onSearchClick: () -> Unit = {},
) {
    SynTopAppBar(
        modifier = modifier.testTag(ArchiveAppBarTestTags.SCREEN_ROOT),
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (onHamburgerMenuClick != null) {
                IconButton(
                    onClick = onHamburgerMenuClick,
                    modifier = Modifier.testTag(ArchiveAppBarTestTags.NAVIGATION_ICON),
                ) {
                    Icon(
                        imageVector = SynIcons.Menu,
                        contentDescription =
                        stringResource(Res.string.archive_app_bar_menu_content_description),
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(Res.string.archive_app_bar_title),
                modifier = Modifier.testTag(ArchiveAppBarTestTags.TITLE),
            )
        },
        subtitle = {},
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag(ArchiveAppBarTestTags.SEARCH_ICON),
            ) {
                Icon(
                    imageVector = SynIcons.Search,
                    contentDescription =
                    stringResource(Res.string.archive_app_bar_search_content_description),
                )
            }
            IconButton(
                onClick = { onDisplayModeChange() },
                modifier = Modifier.testTag(ArchiveAppBarTestTags.DISPLAY_MODE_ICON),
            ) {
                if (!isGrid) {
                    Icon(
                        imageVector = SynIcons.GridView,
                        contentDescription =
                        stringResource(Res.string.archive_app_bar_grid_content_description),
                    )
                } else {
                    Icon(
                        imageVector = SynIcons.ViewAgenda,
                        contentDescription =
                        stringResource(Res.string.archive_app_bar_column_content_description),
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun ArchiveAppBarPreview() {
    ArchiveAppBar(
        isGrid = false,
    )
}
