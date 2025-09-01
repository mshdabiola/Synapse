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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.main.model.MainState
import com.mshdabiola.main.model.SearchSort
import com.mshdabiola.model.AppConstant
import com.mshdabiola.ui.NoteCard
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.modules_designsystem_colors
import synapse.feature.main.generated.resources.modules_designsystem_labels
import synapse.feature.main.generated.resources.modules_designsystem_less
import synapse.feature.main.generated.resources.modules_designsystem_more
import synapse.feature.main.generated.resources.modules_designsystem_no_result
import synapse.feature.main.generated.resources.modules_designsystem_search_sort
import synapse.feature.main.generated.resources.modules_designsystem_types

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainAppBar(
    modifier: Modifier = Modifier,
    mainState: MainState,
    searchTextFieldState: TextFieldState = rememberTextFieldState(),
    isGrid: Boolean = false,
    scrollBehavior: SearchBarScrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(),
    searchBarState: SearchBarState = rememberSearchBarState(),
    onDisplayModeChange: () -> Unit = {},
    onHamburgerMenuClick: () -> Unit = {},
    onSearchOpen: (Boolean) -> Unit = {},
    onSetSearch: (SearchSort?) -> Unit = {},
    onNoteClick: (Long, Int, Int) -> Unit = { _, _, _ -> },

) {
    val gridState = rememberLazyStaggeredGridState()
    val scope = rememberCoroutineScope()
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = searchTextFieldState,
                onSearch = {
                    onSearchOpen(true)
                    scope.launch { searchBarState.animateToCollapsed() }
                },
                placeholder = { Text("Search Synapse") },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        TooltipBox(
                            positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                // TooltipAnchorPosition.Above
                            ),
                            tooltip = { PlainTooltip { Text("Back") } },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(
                                onClick = {
                                    onSearchOpen(false)
                                    scope.launch { searchBarState.animateToCollapsed() }
                                },
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = "Back",
                                )
                            }
                        }
                    } else {
                        if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                            IconButton(
                                onClick = onHamburgerMenuClick,
                                modifier = Modifier.testTag("main:topbar_hamburger_menu_button"),
                            ) {
                                Icon(imageVector = SynIcons.Menu, contentDescription = "menu")
                            }
                        }
                    }
                },
                trailingIcon = {
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

    TopSearchBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        state = searchBarState,
        inputField = inputField,
    )
    ExpandedFullScreenSearchBar(state = searchBarState, inputField = inputField) {
        when (mainState) {
            is MainState.SearchState -> {
                if (searchTextFieldState.text.isNotBlank() && mainState.searches.isEmpty()) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .testTag("search_no_results_column"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = SynIcons.Search,
                            contentDescription = "search",
                            modifier = Modifier.testTag("search_no_results_icon"),
                        )
                        Text(
                            text = stringResource(Res.string.modules_designsystem_no_result),
                            modifier = Modifier.testTag("search_no_results_text"),
                        )
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .testTag("search_results_grid"),
                        state = gridState,
//                        contentPadding = paddingValues,
                        columns = StaggeredGridCells.Fixed(if (mainState.isGrid) 2 else 1),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp,
                    ) {
                        items(items = mainState.searches, key = { it.id }) { notepad ->
                            NoteCard(
                                modifier = Modifier.testTag("search_result_item_${notepad.id}"),
                                notePad = notepad,
                                onCardClick = onNoteClick,
                                onLongClick = {},
                                isSelect = false,
                            )
                        }
                    }
                }
            }

            is MainState.FilterState -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
//                        .padding(paddingValues)
                        .padding(16.dp)
                        .testTag("search_select_state_column"),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (mainState.types.isNotEmpty()) {
                        LabelBox(
                            modifier = Modifier.testTag("search_types_label_box"),
                            title = stringResource(Res.string.modules_designsystem_types),
                            space = 32.dp,
                            numPerRow = 3,
                            mainState.types,
                            onItemClick = onSetSearch,
                        )
                    }

                    if (mainState.label.isNotEmpty()) {
                        LabelBox(
                            modifier = Modifier.testTag("search_labels_label_box"),
                            title = stringResource(Res.string.modules_designsystem_labels),
                            space = 32.dp,
                            numPerRow = 3,
                            mainState.label,
                            onItemClick = onSetSearch,
                        )
                    }
                    if (mainState.color.isNotEmpty()) {
                        LabelBox(
                            modifier = Modifier.testTag("search_colors_label_box"),
                            title = stringResource(Res.string.modules_designsystem_colors),
                            space = 8.dp,
                            numPerRow = 6,
                            mainState.color,
                            onItemClick = onSetSearch,
                        )
                    }
                }
            }
            else -> {}
        }
//                SearchResults(
//                    onResultClick = { result ->
//                        textFieldState.setTextAndPlaceCursorAtEnd(result)
//                        scope.launch { searchBarState.animateToCollapsed() }
//                    }
//                )
    }
//    SynTopAppBar(
//        modifier=modifier,
//        scrollBehavior = scrollBehavior,
//        navigationIcon = {
//            IconButton(
//                onClick = onHamburgerMenuClick,
//                modifier = Modifier.testTag("main:topbar_hamburger_menu_button"),
//            ) {
//                Icon(imageVector = SynIcons.Menu, contentDescription = "menu")
//            }
//        },
//        title = {
//            Text(text = "Note")
//        },
//        subtitle = {},
//        actions = {
//            IconButton(
//                onClick = { onDisplayModeChange() },
//                modifier = Modifier.testTag("main:topbar_display_mode_button"),
//            ) {
//                if (!isGrid) {
//                    Icon(imageVector = SynIcons.GridView, contentDescription = "grid")
//                } else {
//                    Icon(
//                        imageVector = SynIcons.ViewAgenda,
//                        contentDescription = "column",
//                    )
//                }
//            }
//        },
//       isCenterAligned = true
//        )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun NoteAppBarPreview() {
//    MainAppBar(
//        isGrid = false,
//    )
}

@Composable
fun LabelBox(
    modifier: Modifier = Modifier, // Added modifier parameter
    title: String = "Label",
    space: Dp = 16.dp,
    numPerRow: Int = 3,
    list: List<SearchSort> = emptyList(),
    onItemClick: (SearchSort?) -> Unit, // = {},
) {
    var showMore by remember { mutableStateOf(false) }
    val searchIcons = arrayOf(
        SynIcons.Notification,
        SynIcons.CheckBox,
        SynIcons.Image,
        SynIcons.KeyboardVoice,
        SynIcons.Brush,
        SynIcons.Link,
        SynIcons.Label,
    )
    val typeNames = stringArrayResource(Res.array.modules_designsystem_search_sort)
    FlowRow(
        modifier // Use the passed modifier
            .animateContentSize()
            .testTag("label_box_flow_row_$title"), // Unique tag for FlowRow
        maxItemsInEachRow = numPerRow,
        maxLines = if (showMore) Int.MAX_VALUE else 2,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(space, Alignment.CenterHorizontally),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("label_box_title_row_$title"),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .testTag("label_box_title_text_$title"),
                text = title,
            )
            if (list.size > numPerRow) {
                TextButton(
                    onClick = { showMore = !showMore },
                    modifier = Modifier.testTag("label_box_more_less_button_$title"),
                ) {
                    Text(
                        text = if (!showMore) {
                            stringResource(Res.string.modules_designsystem_more)
                        } else {
                            stringResource(
                                Res.string.modules_designsystem_less,
                            )
                        },
                        modifier = Modifier.testTag("label_box_more_less_text_$title"),
                    )
                }
            }
        }
        list
            // .take()
            .forEachIndexed { index, searchSort ->
                // Added index for more unique tags
                when (searchSort) {
                    is SearchSort.Label -> {
                        SearchLabel(
                            modifier = Modifier
                                .clickable { onItemClick(searchSort) }
                                .testTag("search_label_item_${searchSort.name}_$index"),
                            iconId = searchIcons[searchSort.iconIndex],
                            name = searchSort.name,
                        )
                    }

                    is SearchSort.Type -> {
                        val typeName = typeNames.getOrNull(index) ?: ""
                        SearchLabel(
                            modifier = Modifier
                                .clickable { onItemClick(searchSort) }
                                .testTag("search_type_item_${typeName}_$index"),
                            iconId = searchIcons[searchSort.index],
                            name = typeName,
                        )
                    }

                    is SearchSort.Color -> {
                        Surface(
                            onClick = {
                                onItemClick(searchSort)
                            },
                            shape = CircleShape,
                            color = if (searchSort.colorIndex ==
                                -1
                            ) {
                                Color.White
                            } else {
                                Color(AppConstant.noteColors[searchSort.colorIndex])
                            },
                            modifier = Modifier
                                .width(40.dp)
                                .aspectRatio(1f)
                                .testTag("search_color_item_${searchSort.colorIndex}_$index"),

                        ) {
                            if (searchSort.colorIndex == -1) {
                                Icon(
                                    imageVector = SynIcons.FormatColorReset,
                                    contentDescription = "done", // "reset color" might be better
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .testTag("search_color_item_reset_icon_$index"),
                                )
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun SearchLabel(
    modifier: Modifier = Modifier, // Keep modifier
    iconId: ImageVector = SynIcons.Label,
    name: String = "Label",
) {
    Column(
        modifier = modifier.testTag("search_label_column_$name"), // Use the passed modifier
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .width(72.dp)
                .aspectRatio(1f)
                .testTag("search_label_surface_$name"),
        ) {
            Icon(
                imageVector = iconId,
                contentDescription = "$name icon", // More descriptive
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("search_label_icon_$name"),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            modifier = Modifier.testTag("search_label_text_$name"),
        )
    }
}
