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
import com.mshdabiola.main.model.SearchSort
import com.mshdabiola.model.AppConstant
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.modules_designsystem_less
import synapse.feature.main.generated.resources.modules_designsystem_more
import synapse.feature.main.generated.resources.modules_designsystem_search_sort

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
                SynTextButton(
                    onClick = { showMore = !showMore },
                    modifier = Modifier.testTag("label_box_more_less_button_$title"),
                    label = if (!showMore) {
                        stringResource(Res.string.modules_designsystem_more)
                    } else {
                        stringResource(
                            Res.string.modules_designsystem_less,
                        )
                    },
                )
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
