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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.theme.LocalTintTheme
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.testtag.EmptyStateTestTags // Updated import
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.stringResource
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.features_main_empty_body
import synapse.feature.main.generated.resources.features_main_empty_title

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    noteDisplayCategory: NoteDisplayCategory,
) {
    val composition = when (noteDisplayCategory.noteCategory) {
        NoteCategory.NOTE -> rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/note.json").decodeToString(),
            )
        }
        NoteCategory.REMINDER -> rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/reminder.json").decodeToString(),
            )
        }
        NoteCategory.LABEL -> rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/label.json").decodeToString(),
            )
        }
        NoteCategory.TRASH -> rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/trash.json").decodeToString(),
            )
        }
        NoteCategory.ARCHIVE -> rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/archive.json").decodeToString(),
            )
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .testTag(EmptyStateTestTags.ROOT_COLUMN), // Updated test tag
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val iconTint = LocalTintTheme.current.iconTint
        Image(
            modifier = Modifier
                .size(200.dp)
                .testTag(EmptyStateTestTags.LOTTIE_IMAGE), // Updated test tag
            painter = rememberLottiePainter(
                composition = composition.value,
                iterations = Compottie.IterateForever,
            ),
            colorFilter = if (iconTint != Color.Unspecified) ColorFilter.tint(iconTint) else null,
            contentDescription = null, // Consider adding a content description for accessibility and testing
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(Res.string.features_main_empty_title),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(EmptyStateTestTags.TITLE_TEXT), // Updated test tag
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.features_main_empty_body),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(EmptyStateTestTags.DESCRIPTION_TEXT), // Updated test tag
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
