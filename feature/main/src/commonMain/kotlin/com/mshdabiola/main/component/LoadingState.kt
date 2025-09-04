package com.mshdabiola.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mshdabiola.model.testtag.LoadingStateTestTags // Added import
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import synapse.feature.main.generated.resources.Res

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/loading.json").decodeToString(),
        )
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag(LoadingStateTestTags.LOADING_ROOT), // Added root test tag
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .size(200.dp)
                .testTag(LoadingStateTestTags.LOADING_ANIMATION_IMAGE), // Added image test tag
            painter = rememberLottiePainter(
                composition = composition,
                iterations = Compottie.IterateForever,
            ),
            contentDescription = null, // Consider adding a content description for accessibility and testing
        )
    }

}
