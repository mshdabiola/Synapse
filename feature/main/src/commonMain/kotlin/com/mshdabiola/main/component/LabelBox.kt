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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.designsystem.theme.LocalExtendedColorScheme
import com.mshdabiola.main.model.SearchSort
import com.mshdabiola.model.testtag.LabelBoxTestTags
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.feature_main_less
import synapse.feature.main.generated.resources.feature_main_more
import synapse.feature.main.generated.resources.feature_main_search_sort
import synapse.feature.main.generated.resources.label_box_default_title
import synapse.feature.main.generated.resources.label_box_reset_icon_cd

@Composable
fun LabelBox(
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.label_box_default_title), // Changed
    space: Dp = 16.dp,
    numPerRow: Int = 3,
    list: List<SearchSort> = emptyList(),
    onItemClick: (SearchSort?) -> Unit,
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
    val noteColors = LocalExtendedColorScheme.current.noteColor
    val typeNames = stringArrayResource(Res.array.feature_main_search_sort)
    FlowRow(
        modifier = modifier
            .animateContentSize()
            .testTag(LabelBoxTestTags.FLOW_ROW_ROOT_PREFIX + title),
        maxItemsInEachRow = numPerRow,
        maxLines = if (showMore) Int.MAX_VALUE else 2,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(space, Alignment.CenterHorizontally),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LabelBoxTestTags.TITLE_ROW_PREFIX + title),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .testTag(LabelBoxTestTags.TITLE_TEXT_PREFIX + title),
                text = title,
            )
            if (list.size > numPerRow) {
                SynTextButton(
                    onClick = { showMore = !showMore },
                    modifier = Modifier.testTag(LabelBoxTestTags.MORE_LESS_BUTTON_PREFIX + title),
                    label = if (!showMore) {
                        stringResource(Res.string.feature_main_more)
                    } else {
                        stringResource(
                            Res.string.feature_main_less,
                        )
                    },
                )
            }
        }
        list
            .forEachIndexed { index, searchSort ->
                when (searchSort) {
                    is SearchSort.Label -> {
                        SearchLabel(
                            modifier = Modifier
                                .clickable { onItemClick(searchSort) }
                                .testTag(
                                    LabelBoxTestTags.SEARCH_LABEL_ITEM_PREFIX +
                                        "${searchSort.name}_$index",
                                ),
                            iconId = searchIcons[searchSort.iconIndex],
                            name = searchSort.name,
                        )
                    }

                    is SearchSort.Type -> {
                        val typeName = typeNames.getOrNull(index) ?: ""
                        SearchLabel(
                            modifier = Modifier
                                .clickable { onItemClick(searchSort) }
                                .testTag(LabelBoxTestTags.SEARCH_TYPE_ITEM_PREFIX + "${typeName}_$index"),
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
                            color = if (searchSort.colorIndex == -1) {
                                Color.White
                            } else {
                                noteColors[searchSort.colorIndex].color
                            },
                            modifier = Modifier
                                .width(40.dp)
                                .aspectRatio(1f)
                                .testTag(
                                    LabelBoxTestTags.SEARCH_COLOR_ITEM_PREFIX +
                                        "${searchSort.colorIndex}_$index",
                                ),

                        ) {
                            if (searchSort.colorIndex == -1) {
                                Icon(
                                    imageVector = SynIcons.FormatColorReset,
                                    contentDescription = stringResource(Res.string.label_box_reset_icon_cd),
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .testTag(LabelBoxTestTags.SEARCH_COLOR_ITEM_RESET_ICON_PREFIX + index),
                                )
                            }
                        }
                    }
                }
            }
    }
}
