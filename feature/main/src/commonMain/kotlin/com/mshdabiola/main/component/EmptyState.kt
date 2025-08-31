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
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.testtag.MainScreenTestTags
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
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/empty_state.json").decodeToString(),
        )
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .testTag(MainScreenTestTags.EMPTY_STATE_COLUMN), // Tag for the empty state container
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val iconTint = LocalTintTheme.current.iconTint
        Image(
            modifier = Modifier
                .size(200.dp)
                .testTag(MainScreenTestTags.EMPTY_STATE_IMAGE),
            painter = rememberLottiePainter(
                composition = composition,
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
                .testTag(MainScreenTestTags.EMPTY_STATE_TITLE),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.features_main_empty_body),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(MainScreenTestTags.EMPTY_STATE_DESCRIPTION),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
