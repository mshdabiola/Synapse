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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.main.model.SearchSort
import com.mshdabiola.main.model.SearchState
import com.mshdabiola.model.testtag.SearchBarTestTags
import com.mshdabiola.ui.NoteCard
import org.jetbrains.compose.resources.stringResource
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.modules_designsystem_colors
import synapse.feature.main.generated.resources.modules_designsystem_labels
import synapse.feature.main.generated.resources.modules_designsystem_no_result
import synapse.feature.main.generated.resources.modules_designsystem_types

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchBarState: SearchBarState = rememberSearchBarState(),
    searchState: SearchState,
    searchTextFieldState: TextFieldState,
    onNoteClick: (Long, Int, Int) -> Unit = { _, _, _ -> },
    onSetSearch: (SearchSort?) -> Unit = {},
    inputField: @Composable () -> Unit = {},
) {
    val gridState = rememberLazyStaggeredGridState()

    ExpandedFullScreenSearchBar(
        modifier = modifier.testTag(SearchBarTestTags.EXPANDED_SEARCH_BAR),
        state = searchBarState,
        inputField = inputField
    ) {
        when (searchState) {
            is SearchState.ViewState -> {
                if (searchTextFieldState.text.isNotBlank() && searchState.searches.isEmpty()) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .testTag(SearchBarTestTags.SEARCH_NO_RESULTS_COLUMN),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = SynIcons.Search,
                            contentDescription = "search",
                            modifier = Modifier.testTag(SearchBarTestTags.SEARCH_NO_RESULTS_ICON),
                        )
                        Text(
                            text = stringResource(Res.string.modules_designsystem_no_result),
                            modifier = Modifier.testTag(SearchBarTestTags.SEARCH_NO_RESULTS_TEXT),
                        )
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .testTag(SearchBarTestTags.SEARCH_RESULTS_GRID),
                        state = gridState,
//                        contentPadding = paddingValues,
                        columns = StaggeredGridCells.Fixed(if (searchState.isGrid) 2 else 1),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp,
                    ) {
                        items(items = searchState.searches, key = { it.id }) { notepad ->
                            NoteCard(
                                modifier = Modifier.testTag(SearchBarTestTags.SEARCH_RESULT_ITEM_PREFIX + notepad.id),
                                notePad = notepad,
                                onCardClick = onNoteClick,
                                onLongClick = {},
                                isSelect = false,
                                type = "search",
                            )
                        }
                    }
                }
            }

            is SearchState.FilterState -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
//                        .padding(paddingValues)
                        .padding(16.dp)
                        .testTag(SearchBarTestTags.SEARCH_FILTER_STATE_COLUMN),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (searchState.types.isNotEmpty()) {
                        LabelBox(
                            modifier = Modifier.testTag(SearchBarTestTags.SEARCH_TYPES_LABEL_BOX),
                            title = stringResource(Res.string.modules_designsystem_types),
                            space = 32.dp,
                            numPerRow = 3,
                            list = searchState.types,
                            onItemClick = onSetSearch,
                        )
                    }

                    if (searchState.label.isNotEmpty()) {
                        LabelBox(
                            modifier = Modifier.testTag(SearchBarTestTags.SEARCH_LABELS_LABEL_BOX),
                            title = stringResource(Res.string.modules_designsystem_labels),
                            space = 32.dp,
                            numPerRow = 3,
                            list = searchState.label,
                            onItemClick = onSetSearch,
                        )
                    }
                    if (searchState.color.isNotEmpty()) {
                        LabelBox(
                            modifier = Modifier.testTag(SearchBarTestTags.SEARCH_COLORS_LABEL_BOX),
                            title = stringResource(Res.string.modules_designsystem_colors),
                            space = 8.dp,
                            numPerRow = 6,
                            list = searchState.color,
                            onItemClick = onSetSearch,
                        )
                    }
                }
            }
        }
//                SearchResults(
//                    onResultClick = { result ->
//                        textFieldState.setTextAndPlaceCursorAtEnd(result)
//                        scope.launch { searchBarState.animateToCollapsed() }
//                    }
//                )
    }
}
