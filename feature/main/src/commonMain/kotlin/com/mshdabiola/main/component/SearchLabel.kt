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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.testtag.SearchLabelTestTags
import org.jetbrains.compose.resources.stringResource
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.search_label_default_name
import synapse.feature.main.generated.resources.search_label_icon_cd

@Composable
fun SearchLabel(
    modifier: Modifier = Modifier,
    iconId: ImageVector = SynIcons.Label,
    name: String = stringResource(Res.string.search_label_default_name),
) {
    Column(
        modifier = modifier.testTag(SearchLabelTestTags.COLUMN_PREFIX + name),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .width(72.dp)
                .aspectRatio(1f)
                .testTag(SearchLabelTestTags.SURFACE_PREFIX + name),
        ) {
            Icon(
                imageVector = iconId,
                contentDescription = stringResource(Res.string.search_label_icon_cd, name),
                modifier = Modifier
                    .padding(16.dp)
                    .testTag(SearchLabelTestTags.ICON_PREFIX + name),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            modifier = Modifier.testTag(SearchLabelTestTags.TEXT_PREFIX + name),
        )
    }
}
