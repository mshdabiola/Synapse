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
package com.mshdabiola.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

val extendedLight = ExtendedColorScheme(
    success = ColorFamily(
        successLight,
        onSuccessLight,
        successContainerLight,
        onSuccessContainerLight,
    ),
    warning = ColorFamily(
        warningLight,
        onWarningLight,
        warningContainerLight,
        onWarningContainerLight,
    ),
    noteColor = listOf(
        ColorFamily(
            noteColor1Light,
            onNoteColor1Light,
            noteColor1ContainerLight,
            onNoteColor1ContainerLight,

        ),
        ColorFamily(
            noteColor2Light,
            onNoteColor2Light,
            noteColor2ContainerLight,
            onNoteColor2ContainerLight,
        ),
        ColorFamily(
            noteColor3Light,
            onNoteColor3Light,
            noteColor3ContainerLight,
            onNoteColor3ContainerLight,
        ),
        ColorFamily(
            noteColor4Light,
            onNoteColor4Light,
            noteColor4ContainerLight,
            onNoteColor4ContainerLight,
        ),
        ColorFamily(
            noteColor5Light,
            onNoteColor5Light,
            noteColor5ContainerLight,
            onNoteColor5ContainerLight,
        ),
        ColorFamily(
            noteColor6Light,
            onNoteColor6Light,
            noteColor6ContainerLight,
            onNoteColor6ContainerLight,
        ),
        ColorFamily(
            noteColor7Light,
            onNoteColor7Light,
            noteColor7ContainerLight,
            onNoteColor7ContainerLight,
        ),
        ColorFamily(
            noteColor8Light,
            onNoteColor8Light,
            noteColor8ContainerLight,
            onNoteColor8ContainerLight,
        ),
        ColorFamily(
            noteColor9Light,
            onNoteColor9Light,
            noteColor9ContainerLight,
            onNoteColor9ContainerLight,
        ),
        ColorFamily(
            noteColor10Light,
            onNoteColor10Light,
            noteColor10ContainerLight,
            onNoteColor10ContainerLight,
        ),
        ColorFamily(
            noteColor11Light,
            onNoteColor11Light,
            noteColor11ContainerLight,
            onNoteColor11ContainerLight,
        ),
        ColorFamily(
            noteColor12Light,
            onNoteColor12Light,
            noteColor12ContainerLight,
            onNoteColor12ContainerLight,
        ),
        ColorFamily(
            noteColor13Light,
            onNoteColor13Light,
            noteColor13ContainerLight,
            onNoteColor13ContainerLight,
        ),
        ColorFamily(
            noteColor14Light,
            onNoteColor14Light,
            noteColor14ContainerLight,
            onNoteColor14ContainerLight,
        ),
        ColorFamily(
            noteColor15Light,
            onNoteColor15Light,
            noteColor15ContainerLight,
            onNoteColor15ContainerLight,
        ),
    ),
    noteBackGround = listOf(
        ColorFamily(
            noteBg1Light,
            onNoteBg1Light,
            noteBg1ContainerLight,
            onNoteBg1ContainerLight,
        ),
        ColorFamily(
            noteBg2Light,
            onNoteBg2Light,
            noteBg2ContainerLight,
            onNoteBg2ContainerLight,
        ),
        ColorFamily(
            noteBg3Light,
            onNoteBg3Light,
            noteBg3ContainerLight,
            onNoteBg3ContainerLight,
        ),
        ColorFamily(
            noteBg4Light,
            onNoteBg4Light,
            noteBg4ContainerLight,
            onNoteBg4ContainerLight,
        ),
        ColorFamily(
            noteBg5Light,
            onNoteBg5Light,
            noteBg5ContainerLight,
            onNoteBg5ContainerLight,
        ),
        ColorFamily(
            noteBg6Light,
            onNoteBg6Light,
            noteBg6ContainerLight,
            onNoteBg6ContainerLight,
        ),
        ColorFamily(
            noteBg7Light,
            onNoteBg7Light,
            noteBg7ContainerLight,
            onNoteBg7ContainerLight,
        ),
        ColorFamily(
            noteBg8Light,
            onNoteBg8Light,
            noteBg8ContainerLight,
            onNoteBg8ContainerLight,
        ),
        ColorFamily(
            noteBg9Light,
            onNoteBg9Light,
            noteBg9ContainerLight,
            onNoteBg9ContainerLight,
        ),
        ColorFamily(
            noteBg10Light,
            onNoteBg10Light,
            noteBg10ContainerLight,
            onNoteBg10ContainerLight,
        ),
    ),
)

val extendedDark = ExtendedColorScheme(
    success = ColorFamily(
        successDark,
        onSuccessDark,
        successContainerDark,
        onSuccessContainerDark,
    ),
    warning = ColorFamily(
        warningDark,
        onWarningDark,
        warningContainerDark,
        onWarningContainerDark,
    ),
    noteColor = listOf(
        ColorFamily(
            noteColor1Dark,
            onNoteColor1Dark,
            noteColor1ContainerDark,
            onNoteColor1ContainerDark,
        ),
        ColorFamily(
            noteColor2Dark,
            onNoteColor2Dark,
            noteColor2ContainerDark,
            onNoteColor2ContainerDark,
        ),
        ColorFamily(
            noteColor3Dark,
            onNoteColor3Dark,
            noteColor3ContainerDark,
            onNoteColor3ContainerDark,
        ),
        ColorFamily(
            noteColor4Dark,
            onNoteColor4Dark,
            noteColor4ContainerDark,
            onNoteColor4ContainerDark,
        ),
        ColorFamily(
            noteColor5Dark,
            onNoteColor5Dark,
            noteColor5ContainerDark,
            onNoteColor5ContainerDark,
        ),
        ColorFamily(
            noteColor6Dark,
            onNoteColor6Dark,
            noteColor6ContainerDark,
            onNoteColor6ContainerDark,
        ),
        ColorFamily(
            noteColor7Dark,
            onNoteColor7Dark,
            noteColor7ContainerDark,
            onNoteColor7ContainerDark,
        ),
        ColorFamily(
            noteColor8Dark,
            onNoteColor8Dark,
            noteColor8ContainerDark,
            onNoteColor8ContainerDark,
        ),
        ColorFamily(
            noteColor9Dark,
            onNoteColor9Dark,
            noteColor9ContainerDark,
            onNoteColor9ContainerDark,
        ),
        ColorFamily(
            noteColor10Dark,
            onNoteColor10Dark,
            noteColor10ContainerDark,
            onNoteColor10ContainerDark,
        ),
        ColorFamily(
            noteColor11Dark,
            onNoteColor11Dark,
            noteColor11ContainerDark,
            onNoteColor11ContainerDark,
        ),
        ColorFamily(
            noteColor12Dark,
            onNoteColor12Dark,
            noteColor12ContainerDark,
            onNoteColor12ContainerDark,
        ),
        ColorFamily(
            noteColor13Dark,
            onNoteColor13Dark,
            noteColor13ContainerDark,
            onNoteColor13ContainerDark,
        ),
        ColorFamily(
            noteColor14Dark,
            onNoteColor14Dark,
            noteColor14ContainerDark,
            onNoteColor14ContainerDark,
        ),
        ColorFamily(
            noteColor15Dark,
            onNoteColor15Dark,
            noteColor15ContainerDark,
            onNoteColor15ContainerDark,
        ),
    ),
    noteBackGround = listOf(
        ColorFamily(
            noteBg1Dark,
            onNoteBg1Dark,
            noteBg1ContainerDark,
            onNoteBg1ContainerDark,

        ),
        ColorFamily(
            noteBg2Dark,
            onNoteBg2Dark,
            noteBg2ContainerDark,
            onNoteBg2ContainerDark,
        ),
        ColorFamily(
            noteBg3Dark,
            onNoteBg3Dark,
            noteBg3ContainerDark,
            onNoteBg3ContainerDark,
        ),
        ColorFamily(
            noteBg4Dark,
            onNoteBg4Dark,
            noteBg4ContainerDark,
            onNoteBg4ContainerDark,
        ),
        ColorFamily(
            noteBg5Dark,
            onNoteBg5Dark,
            noteBg5ContainerDark,
            onNoteBg5ContainerDark,
        ),
        ColorFamily(
            noteBg6Dark,
            onNoteBg6Dark,
            noteBg6ContainerDark,
            onNoteBg6ContainerDark,
        ),
        ColorFamily(
            noteBg7Dark,
            onNoteBg7Dark,
            noteBg7ContainerDark,
            onNoteBg7ContainerDark,
        ),
        ColorFamily(
            noteBg8Dark,
            onNoteBg8Dark,
            noteBg8ContainerDark,
            onNoteBg8ContainerDark,
        ),
        ColorFamily(
            noteBg9Dark,
            onNoteBg9Dark,
            noteBg9ContainerDark,
            onNoteBg9ContainerDark,
        ),
        ColorFamily(
            noteBg10Dark,
            onNoteBg10Dark,
            noteBg10ContainerDark,
            onNoteBg10ContainerDark,
        ),
    ),
)

val extendedLightMediumContrast = ExtendedColorScheme(
    success = ColorFamily(
        successLightMediumContrast,
        onSuccessLightMediumContrast,
        successContainerLightMediumContrast,
        onSuccessContainerLightMediumContrast,
    ),
    warning = ColorFamily(
        warningLightMediumContrast,
        onWarningLightMediumContrast,
        warningContainerLightMediumContrast,
        onWarningContainerLightMediumContrast,
    ),
    noteColor = listOf(
        ColorFamily(
            noteColor1Light,
            onNoteColor1Light,
            noteColor1ContainerLight,
            onNoteColor1ContainerLight,

        ),
        ColorFamily(
            noteColor2Light,
            onNoteColor2Light,
            noteColor2ContainerLight,
            onNoteColor2ContainerLight,
        ),
        ColorFamily(
            noteColor3Light,
            onNoteColor3Light,
            noteColor3ContainerLight,
            onNoteColor3ContainerLight,
        ),
        ColorFamily(
            noteColor4Light,
            onNoteColor4Light,
            noteColor4ContainerLight,
            onNoteColor4ContainerLight,
        ),
        ColorFamily(
            noteColor5Light,
            onNoteColor5Light,
            noteColor5ContainerLight,
            onNoteColor5ContainerLight,
        ),
        ColorFamily(
            noteColor6Light,
            onNoteColor6Light,
            noteColor6ContainerLight,
            onNoteColor6ContainerLight,
        ),
        ColorFamily(
            noteColor7Light,
            onNoteColor7Light,
            noteColor7ContainerLight,
            onNoteColor7ContainerLight,
        ),
        ColorFamily(
            noteColor8Light,
            onNoteColor8Light,
            noteColor8ContainerLight,
            onNoteColor8ContainerLight,
        ),
        ColorFamily(
            noteColor9Light,
            onNoteColor9Light,
            noteColor9ContainerLight,
            onNoteColor9ContainerLight,
        ),
        ColorFamily(
            noteColor10Light,
            onNoteColor10Light,
            noteColor10ContainerLight,
            onNoteColor10ContainerLight,
        ),
        ColorFamily(
            noteColor11Light,
            onNoteColor11Light,
            noteColor11ContainerLight,
            onNoteColor11ContainerLight,
        ),
        ColorFamily(
            noteColor12Light,
            onNoteColor12Light,
            noteColor12ContainerLight,
            onNoteColor12ContainerLight,
        ),
        ColorFamily(
            noteColor13Light,
            onNoteColor13Light,
            noteColor13ContainerLight,
            onNoteColor13ContainerLight,
        ),
        ColorFamily(
            noteColor14Light,
            onNoteColor14Light,
            noteColor14ContainerLight,
            onNoteColor14ContainerLight,
        ),
        ColorFamily(
            noteColor15Light,
            onNoteColor15Light,
            noteColor15ContainerLight,
            onNoteColor15ContainerLight,
        ),
    ),
    noteBackGround = listOf(
        ColorFamily(
            noteBg1Light,
            onNoteBg1Light,
            noteBg1ContainerLight,
            onNoteBg1ContainerLight,
        ),
        ColorFamily(
            noteBg2Light,
            onNoteBg2Light,
            noteBg2ContainerLight,
            onNoteBg2ContainerLight,
        ),
        ColorFamily(
            noteBg3Light,
            onNoteBg3Light,
            noteBg3ContainerLight,
            onNoteBg3ContainerLight,
        ),
        ColorFamily(
            noteBg4Light,
            onNoteBg4Light,
            noteBg4ContainerLight,
            onNoteBg4ContainerLight,
        ),
        ColorFamily(
            noteBg5Light,
            onNoteBg5Light,
            noteBg5ContainerLight,
            onNoteBg5ContainerLight,
        ),
        ColorFamily(
            noteBg6Light,
            onNoteBg6Light,
            noteBg6ContainerLight,
            onNoteBg6ContainerLight,
        ),
        ColorFamily(
            noteBg7Light,
            onNoteBg7Light,
            noteBg7ContainerLight,
            onNoteBg7ContainerLight,
        ),
        ColorFamily(
            noteBg8Light,
            onNoteBg8Light,
            noteBg8ContainerLight,
            onNoteBg8ContainerLight,
        ),
        ColorFamily(
            noteBg9Light,
            onNoteBg9Light,
            noteBg9ContainerLight,
            onNoteBg9ContainerLight,
        ),
        ColorFamily(
            noteBg10Light,
            onNoteBg10Light,
            noteBg10ContainerLight,
            onNoteBg10ContainerLight,
        ),
    ),
)

val extendedLightHighContrast = ExtendedColorScheme(
    success = ColorFamily(
        successLightHighContrast,
        onSuccessLightHighContrast,
        successContainerLightHighContrast,
        onSuccessContainerLightHighContrast,
    ),
    warning = ColorFamily(
        warningLightHighContrast,
        onWarningLightHighContrast,
        warningContainerLightHighContrast,
        onWarningContainerLightHighContrast,
    ),
    noteColor = listOf(
        ColorFamily(
            noteColor1Light,
            onNoteColor1Light,
            noteColor1ContainerLight,
            onNoteColor1ContainerLight,

        ),
        ColorFamily(
            noteColor2Light,
            onNoteColor2Light,
            noteColor2ContainerLight,
            onNoteColor2ContainerLight,
        ),
        ColorFamily(
            noteColor3Light,
            onNoteColor3Light,
            noteColor3ContainerLight,
            onNoteColor3ContainerLight,
        ),
        ColorFamily(
            noteColor4Light,
            onNoteColor4Light,
            noteColor4ContainerLight,
            onNoteColor4ContainerLight,
        ),
        ColorFamily(
            noteColor5Light,
            onNoteColor5Light,
            noteColor5ContainerLight,
            onNoteColor5ContainerLight,
        ),
        ColorFamily(
            noteColor6Light,
            onNoteColor6Light,
            noteColor6ContainerLight,
            onNoteColor6ContainerLight,
        ),
        ColorFamily(
            noteColor7Light,
            onNoteColor7Light,
            noteColor7ContainerLight,
            onNoteColor7ContainerLight,
        ),
        ColorFamily(
            noteColor8Light,
            onNoteColor8Light,
            noteColor8ContainerLight,
            onNoteColor8ContainerLight,
        ),
        ColorFamily(
            noteColor9Light,
            onNoteColor9Light,
            noteColor9ContainerLight,
            onNoteColor9ContainerLight,
        ),
        ColorFamily(
            noteColor10Light,
            onNoteColor10Light,
            noteColor10ContainerLight,
            onNoteColor10ContainerLight,
        ),
        ColorFamily(
            noteColor11Light,
            onNoteColor11Light,
            noteColor11ContainerLight,
            onNoteColor11ContainerLight,
        ),
        ColorFamily(
            noteColor12Light,
            onNoteColor12Light,
            noteColor12ContainerLight,
            onNoteColor12ContainerLight,
        ),
        ColorFamily(
            noteColor13Light,
            onNoteColor13Light,
            noteColor13ContainerLight,
            onNoteColor13ContainerLight,
        ),
        ColorFamily(
            noteColor14Light,
            onNoteColor14Light,
            noteColor14ContainerLight,
            onNoteColor14ContainerLight,
        ),
        ColorFamily(
            noteColor15Light,
            onNoteColor15Light,
            noteColor15ContainerLight,
            onNoteColor15ContainerLight,
        ),
    ),
    noteBackGround = listOf(
        ColorFamily(
            noteBg1Light,
            onNoteBg1Light,
            noteBg1ContainerLight,
            onNoteBg1ContainerLight,
        ),
        ColorFamily(
            noteBg2Light,
            onNoteBg2Light,
            noteBg2ContainerLight,
            onNoteBg2ContainerLight,
        ),
        ColorFamily(
            noteBg3Light,
            onNoteBg3Light,
            noteBg3ContainerLight,
            onNoteBg3ContainerLight,
        ),
        ColorFamily(
            noteBg4Light,
            onNoteBg4Light,
            noteBg4ContainerLight,
            onNoteBg4ContainerLight,
        ),
        ColorFamily(
            noteBg5Light,
            onNoteBg5Light,
            noteBg5ContainerLight,
            onNoteBg5ContainerLight,
        ),
        ColorFamily(
            noteBg6Light,
            onNoteBg6Light,
            noteBg6ContainerLight,
            onNoteBg6ContainerLight,
        ),
        ColorFamily(
            noteBg7Light,
            onNoteBg7Light,
            noteBg7ContainerLight,
            onNoteBg7ContainerLight,
        ),
        ColorFamily(
            noteBg8Light,
            onNoteBg8Light,
            noteBg8ContainerLight,
            onNoteBg8ContainerLight,
        ),
        ColorFamily(
            noteBg9Light,
            onNoteBg9Light,
            noteBg9ContainerLight,
            onNoteBg9ContainerLight,
        ),
        ColorFamily(
            noteBg10Light,
            onNoteBg10Light,
            noteBg10ContainerLight,
            onNoteBg10ContainerLight,
        ),
    ),
)

val extendedDarkMediumContrast = ExtendedColorScheme(
    success = ColorFamily(
        successDarkMediumContrast,
        onSuccessDarkMediumContrast,
        successContainerDarkMediumContrast,
        onSuccessContainerDarkMediumContrast,
    ),
    warning = ColorFamily(
        warningDarkMediumContrast,
        onWarningDarkMediumContrast,
        warningContainerDarkMediumContrast,
        onWarningContainerDarkMediumContrast,
    ),
    noteColor = listOf(
        ColorFamily(
            noteColor1Dark,
            onNoteColor1Dark,
            noteColor1ContainerDark,
            onNoteColor1ContainerDark,
        ),
        ColorFamily(
            noteColor2Dark,
            onNoteColor2Dark,
            noteColor2ContainerDark,
            onNoteColor2ContainerDark,
        ),
        ColorFamily(
            noteColor3Dark,
            onNoteColor3Dark,
            noteColor3ContainerDark,
            onNoteColor3ContainerDark,
        ),
        ColorFamily(
            noteColor4Dark,
            onNoteColor4Dark,
            noteColor4ContainerDark,
            onNoteColor4ContainerDark,
        ),
        ColorFamily(
            noteColor5Dark,
            onNoteColor5Dark,
            noteColor5ContainerDark,
            onNoteColor5ContainerDark,
        ),
        ColorFamily(
            noteColor6Dark,
            onNoteColor6Dark,
            noteColor6ContainerDark,
            onNoteColor6ContainerDark,
        ),
        ColorFamily(
            noteColor7Dark,
            onNoteColor7Dark,
            noteColor7ContainerDark,
            onNoteColor7ContainerDark,
        ),
        ColorFamily(
            noteColor8Dark,
            onNoteColor8Dark,
            noteColor8ContainerDark,
            onNoteColor8ContainerDark,
        ),
        ColorFamily(
            noteColor9Dark,
            onNoteColor9Dark,
            noteColor9ContainerDark,
            onNoteColor9ContainerDark,
        ),
        ColorFamily(
            noteColor10Dark,
            onNoteColor10Dark,
            noteColor10ContainerDark,
            onNoteColor10ContainerDark,
        ),
        ColorFamily(
            noteColor11Dark,
            onNoteColor11Dark,
            noteColor11ContainerDark,
            onNoteColor11ContainerDark,
        ),
        ColorFamily(
            noteColor12Dark,
            onNoteColor12Dark,
            noteColor12ContainerDark,
            onNoteColor12ContainerDark,
        ),
        ColorFamily(
            noteColor13Dark,
            onNoteColor13Dark,
            noteColor13ContainerDark,
            onNoteColor13ContainerDark,
        ),
        ColorFamily(
            noteColor14Dark,
            onNoteColor14Dark,
            noteColor14ContainerDark,
            onNoteColor14ContainerDark,
        ),
        ColorFamily(
            noteColor15Dark,
            onNoteColor15Dark,
            noteColor15ContainerDark,
            onNoteColor15ContainerDark,
        ),
    ),
    noteBackGround = listOf(
        ColorFamily(
            noteBg1Dark,
            onNoteBg1Dark,
            noteBg1ContainerDark,
            onNoteBg1ContainerDark,

        ),
        ColorFamily(
            noteBg2Dark,
            onNoteBg2Dark,
            noteBg2ContainerDark,
            onNoteBg2ContainerDark,
        ),
        ColorFamily(
            noteBg3Dark,
            onNoteBg3Dark,
            noteBg3ContainerDark,
            onNoteBg3ContainerDark,
        ),
        ColorFamily(
            noteBg4Dark,
            onNoteBg4Dark,
            noteBg4ContainerDark,
            onNoteBg4ContainerDark,
        ),
        ColorFamily(
            noteBg5Dark,
            onNoteBg5Dark,
            noteBg5ContainerDark,
            onNoteBg5ContainerDark,
        ),
        ColorFamily(
            noteBg6Dark,
            onNoteBg6Dark,
            noteBg6ContainerDark,
            onNoteBg6ContainerDark,
        ),
        ColorFamily(
            noteBg7Dark,
            onNoteBg7Dark,
            noteBg7ContainerDark,
            onNoteBg7ContainerDark,
        ),
        ColorFamily(
            noteBg8Dark,
            onNoteBg8Dark,
            noteBg8ContainerDark,
            onNoteBg8ContainerDark,
        ),
        ColorFamily(
            noteBg9Dark,
            onNoteBg9Dark,
            noteBg9ContainerDark,
            onNoteBg9ContainerDark,
        ),
        ColorFamily(
            noteBg10Dark,
            onNoteBg10Dark,
            noteBg10ContainerDark,
            onNoteBg10ContainerDark,
        ),
    ),
)

val extendedDarkHighContrast = ExtendedColorScheme(
    success = ColorFamily(
        successDarkHighContrast,
        onSuccessDarkHighContrast,
        successContainerDarkHighContrast,
        onSuccessContainerDarkHighContrast,
    ),
    warning = ColorFamily(
        warningDarkHighContrast,
        onWarningDarkHighContrast,
        warningContainerDarkHighContrast,
        onWarningContainerDarkHighContrast,
    ),
    noteColor = listOf(
        ColorFamily(
            noteColor1Dark,
            onNoteColor1Dark,
            noteColor1ContainerDark,
            onNoteColor1ContainerDark,
        ),
        ColorFamily(
            noteColor2Dark,
            onNoteColor2Dark,
            noteColor2ContainerDark,
            onNoteColor2ContainerDark,
        ),
        ColorFamily(
            noteColor3Dark,
            onNoteColor3Dark,
            noteColor3ContainerDark,
            onNoteColor3ContainerDark,
        ),
        ColorFamily(
            noteColor4Dark,
            onNoteColor4Dark,
            noteColor4ContainerDark,
            onNoteColor4ContainerDark,
        ),
        ColorFamily(
            noteColor5Dark,
            onNoteColor5Dark,
            noteColor5ContainerDark,
            onNoteColor5ContainerDark,
        ),
        ColorFamily(
            noteColor6Dark,
            onNoteColor6Dark,
            noteColor6ContainerDark,
            onNoteColor6ContainerDark,
        ),
        ColorFamily(
            noteColor7Dark,
            onNoteColor7Dark,
            noteColor7ContainerDark,
            onNoteColor7ContainerDark,
        ),
        ColorFamily(
            noteColor8Dark,
            onNoteColor8Dark,
            noteColor8ContainerDark,
            onNoteColor8ContainerDark,
        ),
        ColorFamily(
            noteColor9Dark,
            onNoteColor9Dark,
            noteColor9ContainerDark,
            onNoteColor9ContainerDark,
        ),
        ColorFamily(
            noteColor10Dark,
            onNoteColor10Dark,
            noteColor10ContainerDark,
            onNoteColor10ContainerDark,
        ),
        ColorFamily(
            noteColor11Dark,
            onNoteColor11Dark,
            noteColor11ContainerDark,
            onNoteColor11ContainerDark,
        ),
        ColorFamily(
            noteColor12Dark,
            onNoteColor12Dark,
            noteColor12ContainerDark,
            onNoteColor12ContainerDark,
        ),
        ColorFamily(
            noteColor13Dark,
            onNoteColor13Dark,
            noteColor13ContainerDark,
            onNoteColor13ContainerDark,
        ),
        ColorFamily(
            noteColor14Dark,
            onNoteColor14Dark,
            noteColor14ContainerDark,
            onNoteColor14ContainerDark,
        ),
        ColorFamily(
            noteColor15Dark,
            onNoteColor15Dark,
            noteColor15ContainerDark,
            onNoteColor15ContainerDark,
        ),
    ),
    noteBackGround = listOf(
        ColorFamily(
            noteBg1Dark,
            onNoteBg1Dark,
            noteBg1ContainerDark,
            onNoteBg1ContainerDark,

        ),
        ColorFamily(
            noteBg2Dark,
            onNoteBg2Dark,
            noteBg2ContainerDark,
            onNoteBg2ContainerDark,
        ),
        ColorFamily(
            noteBg3Dark,
            onNoteBg3Dark,
            noteBg3ContainerDark,
            onNoteBg3ContainerDark,
        ),
        ColorFamily(
            noteBg4Dark,
            onNoteBg4Dark,
            noteBg4ContainerDark,
            onNoteBg4ContainerDark,
        ),
        ColorFamily(
            noteBg5Dark,
            onNoteBg5Dark,
            noteBg5ContainerDark,
            onNoteBg5ContainerDark,
        ),
        ColorFamily(
            noteBg6Dark,
            onNoteBg6Dark,
            noteBg6ContainerDark,
            onNoteBg6ContainerDark,
        ),
        ColorFamily(
            noteBg7Dark,
            onNoteBg7Dark,
            noteBg7ContainerDark,
            onNoteBg7ContainerDark,
        ),
        ColorFamily(
            noteBg8Dark,
            onNoteBg8Dark,
            noteBg8ContainerDark,
            onNoteBg8ContainerDark,
        ),
        ColorFamily(
            noteBg9Dark,
            onNoteBg9Dark,
            noteBg9ContainerDark,
            onNoteBg9ContainerDark,
        ),
        ColorFamily(
            noteBg10Dark,
            onNoteBg10Dark,
            noteBg10ContainerDark,
            onNoteBg10ContainerDark,
        ),
    ),
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color,
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified,
    Color.Unspecified,
    Color.Unspecified,
    Color.Unspecified,
)

val LocalExtendedColorScheme = staticCompositionLocalOf { extendedLight }

@Composable
fun SynTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    contrast: Int = 0,
    disableDynamicTheming: Boolean = true,
    content: @Composable () -> Unit,
) {
    val extendedColorScheme =
        when {
            !disableDynamicTheming && supportsDynamicTheming() -> {
                if (darkTheme) extendedDark else extendedLight
            }

            else -> when (contrast) {
                0 -> if (darkTheme) extendedDark else extendedLight
                1 -> if (darkTheme) extendedDarkMediumContrast else extendedLightMediumContrast
                else -> if (darkTheme) extendedDarkHighContrast else extendedLightHighContrast
            }
        }

    val colorScheme =
        when {
            !disableDynamicTheming && supportsDynamicTheming() -> {
                getDynamicColor(darkTheme)
            }

            else -> when (contrast) {
                0 -> if (darkTheme) darkScheme else lightScheme
                1 -> if (darkTheme) mediumContrastDarkColorScheme else mediumContrastLightColorScheme
                else -> if (darkTheme) highContrastDarkColorScheme else highContrastLightColorScheme
            }
        }
//

    // Gradient colors
    val emptyGradientColors = GradientColors(container = colorScheme.surfaceColorAtElevation(2.dp))
    val defaultGradientColors =
        GradientColors(
            top = colorScheme.inverseOnSurface,
            bottom = colorScheme.primaryContainer,
            container = colorScheme.surface,
        )
    val gradientColors =
        when {
            !disableDynamicTheming && supportsDynamicTheming() -> emptyGradientColors
            else -> defaultGradientColors
        }
    // Background theme
    val defaultBackgroundTheme =
        BackgroundTheme(
            color = colorScheme.surface,
            tonalElevation = 2.dp,
        )

    val tintTheme =
        when {
            !disableDynamicTheming && supportsDynamicTheming() -> TintTheme(colorScheme.primary)
            else -> TintTheme()
        }
    // Composition locals
    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides defaultBackgroundTheme,
        LocalTintTheme provides tintTheme,
        LocalExtendedColorScheme provides extendedColorScheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SynTypography,
            content = content,
        )
    }
}

object KmtExtendedTheme {
    val colors: ExtendedColorScheme
        @Composable get() = LocalExtendedColorScheme.current
}

expect fun supportsDynamicTheming(): Boolean

@Composable
expect fun getDynamicColor(darkTheme: Boolean): ColorScheme
