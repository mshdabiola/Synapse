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
package com.mshdabiola.designsystem.drawable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcon: ImageVector
    get() {
        if (_AppIcon != null) {
            return _AppIcon!!
        }
        _AppIcon = ImageVector.Builder(
            name = "AppIcon",
            defaultWidth = 128.dp,
            defaultHeight = 176.dp,
            viewportWidth = 128f,
            viewportHeight = 176f,
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(51.53f, 24.4f)
                horizontalLineToRelative(0f)
                arcToRelative(6.11f, 6.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6.06f, -6.16f)
                verticalLineTo(6.15f)
                arcTo(6.11f, 6.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, 51.53f, 0f)
                horizontalLineToRelative(0f)
                arcToRelative(6.12f, 6.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.07f, 6.15f)
                verticalLineTo(18.24f)
                arcTo(6.12f, 6.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 51.53f, 24.4f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(76.47f, 24.4f)
                horizontalLineToRelative(0f)
                arcToRelative(6.12f, 6.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6.07f, -6.16f)
                verticalLineTo(6.15f)
                arcTo(6.12f, 6.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 76.47f, 0f)
                horizontalLineToRelative(0f)
                arcToRelative(6.11f, 6.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.06f, 6.15f)
                verticalLineTo(18.24f)
                arcTo(6.11f, 6.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, 76.47f, 24.4f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(101.42f, 24.4f)
                horizontalLineToRelative(0f)
                arcToRelative(6.11f, 6.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6.06f, -6.16f)
                verticalLineTo(6.15f)
                arcTo(6.11f, 6.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, 101.42f, 0f)
                horizontalLineToRelative(0f)
                arcToRelative(6.12f, 6.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.07f, 6.15f)
                verticalLineTo(18.24f)
                arcTo(6.12f, 6.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 101.42f, 24.4f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(26.58f, 24.4f)
                horizontalLineToRelative(0f)
                arcToRelative(6.12f, 6.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6.07f, -6.16f)
                verticalLineTo(6.15f)
                arcTo(6.12f, 6.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 26.58f, 0f)
                horizontalLineToRelative(0f)
                arcToRelative(6.11f, 6.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.06f, 6.15f)
                verticalLineTo(18.24f)
                arcTo(6.11f, 6.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, 26.58f, 24.4f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(10.93f, 155.69f)
                arcToRelative(2.13f, 2.13f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.76f, -1.92f)
                curveToRelative(-0.19f, -0.66f, -0.86f, -1.33f, -1.3f, -1.25f)
                arcToRelative(45.79f, 45.79f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.24f, 1.3f)
                curveToRelative(-1.37f, 0.46f, -2.76f, 0.86f, -4.15f, 1.28f)
                verticalLineToRelative(6.33f)
                curveToRelative(0.18f, -0.07f, 0.36f, -0.13f, 0.54f, -0.21f)
                curveTo(4.08f, 159.52f, 7.5f, 157.6f, 10.93f, 155.69f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(10.14f, 149.26f)
                arcToRelative(7.52f, 7.52f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.2f, -5.42f)
                arcToRelative(1.19f, 1.19f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.36f, -1f)
                curveToRelative(-0.14f, -2.2f, -0.3f, -4.4f, -0.56f, -6.58f)
                curveToRelative(-0.1f, -0.84f, -0.2f, -0.91f, -0.93f, -0.43f)
                curveToRelative(-1.64f, 1.07f, -3.28f, 2.16f, -4.9f, 3.27f)
                curveTo(4.12f, 140.6f, 2.09f, 142.3f, 0f, 143.92f)
                verticalLineToRelative(8.39f)
                arcToRelative(73.8f, 73.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9.38f, -2.2f)
                curveTo(9.87f, 150f, 10.14f, 149.84f, 10.14f, 149.26f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(21.61f, 168.63f)
                arcToRelative(59.05f, 59.05f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.34f, -11.7f)
                curveToRelative(-0.08f, -0.46f, -0.27f, -0.55f, -0.68f, -0.5f)
                arcToRelative(5.53f, 5.53f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.94f, -0.16f)
                arcToRelative(1.72f, 1.72f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.16f, 1.34f)
                arcTo(109.71f, 109.71f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9.69f, 171f)
                curveToRelative(-0.62f, 1.71f, -1.38f, 3.35f, -2f, 5.05f)
                horizontalLineToRelative(7.25f)
                curveToRelative(0.14f, -0.14f, 0.27f, -0.28f, 0.42f, -0.41f)
                curveToRelative(2f, -1.81f, 4f, -3.68f, 6f, -5.59f)
                arcTo(1.1f, 1.1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 21.61f, 168.63f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(0.78f, 114.28f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.78f, -0.53f)
                verticalLineToRelative(5.3f)
                arcToRelative(13.65f, 13.65f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.33f, -3.32f)
                curveTo(1.57f, 115.06f, 1.53f, 114.56f, 0.78f, 114.28f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(33.64f, 156.45f)
                curveToRelative(1.17f, -1.46f, 2.37f, -2.9f, 3.55f, -4.36f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.3f, -0.71f)
                arcToRelative(7.29f, 7.29f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.26f, -3.77f)
                curveToRelative(-0.19f, -0.31f, -0.48f, -0.29f, -0.77f, -0.23f)
                curveToRelative(-3.31f, 0.66f, -6.62f, 1.35f, -9.93f, 2f)
                curveToRelative(-0.86f, 0.16f, -1.43f, 0.43f, -1.45f, 1.43f)
                arcToRelative(3.68f, 3.68f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.37f, 1.18f)
                curveToRelative(-0.17f, 0.41f, -0.1f, 0.59f, 0.32f, 0.77f)
                curveToRelative(2.92f, 1.28f, 5.83f, 2.59f, 8.74f, 3.9f)
                arcTo(0.62f, 0.62f, 0f, isMoreThanHalf = false, isPositiveArc = false, 33.64f, 156.45f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(20.45f, 131f)
                curveToRelative(-0.17f, -0.16f, -0.36f, 0f, -0.53f, 0.08f)
                curveToRelative(-1.8f, 0.83f, -3.53f, 1.79f, -5.28f, 2.73f)
                arcToRelative(0.64f, 0.64f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.4f, 0.71f)
                arcToRelative(58.62f, 58.62f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.74f, 7.36f)
                curveToRelative(0f, 0.36f, 0.23f, 0.38f, 0.51f, 0.31f)
                arcToRelative(5.32f, 5.32f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.86f, -0.2f)
                curveToRelative(0.5f, 0f, 0.67f, -0.22f, 0.74f, -0.68f)
                arcToRelative(57.27f, 57.27f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.32f, -9.76f)
                curveTo(20.47f, 131.35f, 20.63f, 131.15f, 20.45f, 131f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(40.55f, 163.7f)
                arcToRelative(0.78f, 0.78f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.52f, -0.91f)
                quadToRelative(-2.81f, -1.38f, -5.59f, -2.79f)
                arcToRelative(0.68f, 0.68f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.95f, 0.2f)
                curveToRelative(-2.67f, 3.16f, -5.53f, 6.14f, -8.38f, 9.14f)
                arcToRelative(0.92f, 0.92f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.2f, 1.17f)
                arcTo(56.6f, 56.6f, 0f, isMoreThanHalf = false, isPositiveArc = false, 27.84f, 176f)
                horizontalLineTo(41f)
                arcToRelative(23.06f, 23.06f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.41f, -7.39f)
                arcToRelative(1.11f, 1.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.39f, -1f)
                arcTo(4.45f, 4.45f, 0f, isMoreThanHalf = false, isPositiveArc = true, 40.55f, 163.7f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(49.78f, 138f)
                curveToRelative(0.63f, 0.6f, 1.24f, 1.23f, 1.86f, 1.84f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.76f, 0.37f)
                arcToRelative(2.64f, 2.64f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.42f, -0.11f)
                arcTo(55f, 55f, 0f, isMoreThanHalf = false, isPositiveArc = false, 64.67f, 134f)
                curveToRelative(0.51f, -0.35f, 0.44f, -0.61f, 0.11f, -1f)
                arcToRelative(18.91f, 18.91f, 0f, isMoreThanHalf = false, isPositiveArc = false, -9.84f, -6.64f)
                curveToRelative(-0.56f, -0.17f, -0.77f, 0f, -0.93f, 0.54f)
                arcToRelative(66f, 66f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.36f, 10.24f)
                arcTo(0.67f, 0.67f, 0f, isMoreThanHalf = false, isPositiveArc = false, 49.78f, 138f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(47.56f, 122.59f)
                arcToRelative(30.16f, 30.16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.44f, 0.59f)
                curveToRelative(0.57f, 0.14f, 0.73f, -0.09f, 0.84f, -0.58f)
                arcToRelative(44.9f, 44.9f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.08f, -9.1f)
                arcToRelative(34.72f, 34.72f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.41f, -5.2f)
                curveToRelative(-0.06f, -0.47f, -0.22f, -0.66f, -0.7f, -0.73f)
                arcToRelative(3.71f, 3.71f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.13f, -2.71f)
                arcToRelative(4.58f, 4.58f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.16f, -0.56f)
                arcToRelative(0.73f, 0.73f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.87f, -0.38f)
                curveToRelative(-0.29f, 0f, -0.17f, 0.36f, -0.16f, 0.56f)
                arcTo(51.49f, 51.49f, 0f, isMoreThanHalf = false, isPositiveArc = true, 47f, 121.83f)
                curveTo(46.85f, 122.41f, 47f, 122.55f, 47.56f, 122.59f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(11.45f, 132.4f)
                arcToRelative(49.91f, 49.91f, 0f, isMoreThanHalf = false, isPositiveArc = false, -7.08f, -16.72f)
                curveToRelative(-0.11f, -0.18f, -0.19f, -0.47f, -0.43f, -0.45f)
                reflectiveCurveToRelative(-0.27f, 0.36f, -0.33f, 0.58f)
                arcTo(22.85f, 22.85f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 120.36f)
                arcToRelative(11.56f, 11.56f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 3f)
                verticalLineTo(141f)
                arcToRelative(97.68f, 97.68f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.89f, -7.64f)
                curveTo(11.23f, 133.12f, 11.58f, 133f, 11.45f, 132.4f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(48.08f, 139.94f)
                curveToRelative(-0.39f, 0.63f, -0.76f, 1.26f, -1.23f, 2f)
                arcToRelative(15.59f, 15.59f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.65f, -0.76f)
                curveToRelative(0.13f, 0f, 0.24f, -0.15f, 0.11f, -0.27f)
                curveToRelative(-0.37f, -0.37f, -0.76f, -0.73f, -1.16f, -1.08f)
                curveTo(48.32f, 139.74f, 48.17f, 139.79f, 48.08f, 139.94f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(45.56f, 126f)
                arcToRelative(31.83f, 31.83f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.39f, 4.69f)
                arcToRelative(0.82f, 0.82f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.17f, 1.25f)
                quadToRelative(1.95f, 1.76f, 3.83f, 3.58f)
                curveToRelative(0.46f, 0.44f, 0.62f, 0.24f, 0.82f, -0.19f)
                arcToRelative(87.85f, 87.85f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3.83f, -9.12f)
                curveToRelative(0.17f, -0.5f, 0f, -0.67f, -0.42f, -0.74f)
                curveToRelative(-1.41f, -0.2f, -2.81f, -0.42f, -4.41f, -0.52f)
                curveTo(46.26f, 124.7f, 45.9f, 125.19f, 45.56f, 126f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(34.59f, 144.57f)
                curveToRelative(-0.3f, -0.53f, -0.63f, -1f, -0.91f, -1.58f)
                reflectiveCurveToRelative(-0.45f, -0.45f, -0.81f, -0.11f)
                curveToRelative(-1.25f, 1.18f, -2.57f, 2.29f, -3.9f, 3.45f)
                curveToRelative(1.79f, -0.37f, 3.58f, -0.76f, 5.37f, -1.12f)
                curveTo(34.76f, 145.12f, 34.77f, 144.88f, 34.59f, 144.57f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(10.74f, 158.54f)
                curveToRelative(-0.16f, -0.1f, -0.31f, 0.09f, -0.45f, 0.17f)
                curveTo(6.91f, 160.6f, 3.5f, 162.44f, 0f, 164.08f)
                verticalLineToRelative(5.31f)
                arcToRelative(6.59f, 6.59f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5.14f, 6.46f)
                curveToRelative(0.14f, -0.35f, 0.29f, -0.71f, 0.44f, -1.06f)
                arcToRelative(96.4f, 96.4f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5.2f, -15.71f)
                curveTo(10.82f, 158.9f, 10.94f, 158.67f, 10.74f, 158.54f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(25.48f, 129.32f)
                arcToRelative(1.8f, 1.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.06f, 0.21f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.18f, 0.82f)
                curveToRelative(-0.29f, 1f, -0.67f, 1.89f, -1f, 2.84f)
                arcToRelative(63f, 63f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.94f, 8.9f)
                arcToRelative(0.65f, 0.65f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.33f, 0.74f)
                arcToRelative(7.3f, 7.3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.15f, 3.81f)
                curveToRelative(0.17f, 0.44f, 0.36f, 0.43f, 0.69f, 0.17f)
                curveToRelative(2.46f, -1.94f, 4.94f, -3.85f, 7.22f, -6f)
                arcToRelative(0.66f, 0.66f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.14f, -1f)
                curveToRelative(-0.29f, -0.43f, -0.53f, -0.88f, -0.78f, -1.33f)
                curveToRelative(-1.64f, -2.86f, -3.16f, -5.79f, -4.6f, -8.76f)
                curveTo(26.23f, 129.19f, 26f, 129f, 25.48f, 129.32f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(22.52f, 154.79f)
                arcToRelative(3.94f, 3.94f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.45f, -0.15f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.54f, 1.88f)
                arcToRelative(58.41f, 58.41f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.84f, 10.32f)
                curveToRelative(0.21f, 0.56f, 0.41f, 0.55f, 0.78f, 0.15f)
                curveToRelative(1.24f, -1.34f, 2.51f, -2.65f, 3.75f, -4f)
                reflectiveCurveToRelative(2.23f, -2.51f, 3.35f, -3.75f)
                curveToRelative(0.3f, -0.34f, 0.33f, -0.53f, -0.14f, -0.75f)
                curveTo(28.28f, 157.17f, 25.39f, 156f, 22.52f, 154.79f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(0.94f, 106.91f)
                arcToRelative(4.14f, 4.14f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.47f, -0.18f)
                arcToRelative(4.16f, 4.16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.24f, 3.85f)
                curveToRelative(0f, 0.58f, 0.15f, 0.77f, 0.65f, 0.89f)
                arcToRelative(58.22f, 58.22f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7.42f, 2.26f)
                lineToRelative(1.8f, 0.69f)
                arcTo(65.9f, 65.9f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 99.18f)
                verticalLineToRelative(7.95f)
                curveTo(0.41f, 107.49f, 0.65f, 107.09f, 0.94f, 106.91f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(121.49f, 13.44f)
                horizontalLineTo(109.24f)
                verticalLineTo(20f)
                arcToRelative(7.87f, 7.87f, 0f, isMoreThanHalf = false, isPositiveArc = true, -7.82f, 7.93f)
                horizontalLineToRelative(0f)
                arcTo(7.87f, 7.87f, 0f, isMoreThanHalf = false, isPositiveArc = true, 93.6f, 20f)
                verticalLineTo(13.44f)
                horizontalLineTo(84.29f)
                verticalLineTo(20f)
                arcToRelative(7.87f, 7.87f, 0f, isMoreThanHalf = false, isPositiveArc = true, -7.82f, 7.93f)
                horizontalLineToRelative(0f)
                arcTo(7.87f, 7.87f, 0f, isMoreThanHalf = false, isPositiveArc = true, 68.66f, 20f)
                verticalLineTo(13.44f)
                horizontalLineTo(59.34f)
                verticalLineTo(20f)
                arcToRelative(7.82f, 7.82f, 0f, isMoreThanHalf = true, isPositiveArc = true, -15.63f, 0f)
                verticalLineTo(13.44f)
                horizontalLineTo(34.4f)
                verticalLineTo(20f)
                arcToRelative(7.87f, 7.87f, 0f, isMoreThanHalf = false, isPositiveArc = true, -7.82f, 7.93f)
                horizontalLineToRelative(0f)
                arcTo(7.87f, 7.87f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18.76f, 20f)
                verticalLineTo(13.44f)
                horizontalLineTo(6.51f)
                arcTo(6.56f, 6.56f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 20f)
                verticalLineTo(96.53f)
                arcToRelative(49.73f, 49.73f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6.21f, 4f)
                arcToRelative(81.66f, 81.66f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.85f, 9.51f)
                arcToRelative(13.52f, 13.52f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.53f, 1.61f)
                arcToRelative(1.43f, 1.43f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.13f, -0.89f)
                arcTo(88.53f, 88.53f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15.4f, 95.33f)
                arcToRelative(54.07f, 54.07f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.36f, -7f)
                curveToRelative(0f, -0.54f, 0.15f, -0.77f, 0.71f, -0.67f)
                curveToRelative(1.8f, 0.31f, 1.65f, -0.25f, 1.66f, 1.82f)
                arcToRelative(80.34f, 80.34f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.31f, 17.17f)
                curveToRelative(0.26f, -0.15f, 0.2f, -0.38f, 0.24f, -0.57f)
                reflectiveCurveToRelative(0f, -0.39f, 0.08f, -0.58f)
                arcToRelative(25.84f, 25.84f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6.51f, -12.92f)
                arcToRelative(1.1f, 1.1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.32f, -1f)
                arcToRelative(2.83f, 2.83f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.57f, -3.27f)
                arcToRelative(2.79f, 2.79f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.86f, 2.79f)
                arcToRelative(2.63f, 2.63f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.93f, 2.79f)
                arcToRelative(1.89f, 1.89f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2f, 0.9f)
                arcToRelative(24.25f, 24.25f, 0f, isMoreThanHalf = false, isPositiveArc = false, -5.58f, 17.46f)
                arcToRelative(15.41f, 15.41f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.21f, 4.6f)
                arcToRelative(1.06f, 1.06f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.47f, 0.55f)
                curveToRelative(0.81f, 0.49f, 1.62f, 1f, 2.42f, 1.47f)
                curveToRelative(0.33f, 0.21f, 0.53f, 0.17f, 0.67f, -0.2f)
                arcToRelative(1.41f, 1.41f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.17f, -0.32f)
                arcToRelative(2.22f, 2.22f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.12f, -2.61f)
                arcToRelative(4.17f, 4.17f, 0f, isMoreThanHalf = true, isPositiveArc = true, 7.7f, -3.07f)
                arcToRelative(4.28f, 4.28f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.37f, 5.46f)
                arcToRelative(2.26f, 2.26f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1f, 0f)
                curveToRelative(-0.67f, -0.2f, -0.95f, 0.11f, -1.22f, 0.67f)
                curveToRelative(-0.7f, 1.49f, -0.71f, 1.47f, 0.64f, 2.38f)
                curveToRelative(1.14f, 0.78f, 2.28f, 1.57f, 3.41f, 2.37f)
                arcToRelative(1.1f, 1.1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.93f, 0.22f)
                arcToRelative(54.27f, 54.27f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9.91f, -1.24f)
                arcToRelative(0.67f, 0.67f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.73f, -0.56f)
                arcToRelative(43.8f, 43.8f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.63f, -9f)
                arcToRelative(47.05f, 47.05f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.1f, -8.88f)
                curveToRelative(-0.06f, -0.58f, -0.23f, -0.83f, -0.82f, -0.83f)
                curveToRelative(-1f, 0f, -2f, -0.08f, -3f, -0.14f)
                arcToRelative(1.06f, 1.06f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, 0.47f)
                arcToRelative(2.31f, 2.31f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.29f, 0.46f)
                arcToRelative(1.33f, 1.33f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.28f, -0.2f)
                arcToRelative(25f, 25f, 0f, isMoreThanHalf = false, isPositiveArc = false, -5.08f, 1.94f)
                curveToRelative(-0.48f, 0.26f, -0.75f, 0.22f, -1f, -0.34f)
                curveToRelative(-0.66f, -1.48f, -0.8f, -1.18f, 0.63f, -1.86f)
                arcToRelative(21.82f, 21.82f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.14f, -1.75f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.85f, -0.66f)
                arcToRelative(2.25f, 2.25f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.85f, -0.65f)
                arcToRelative(1.23f, 1.23f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.12f, 0.5f)
                arcToRelative(17.05f, 17.05f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.67f, 0.12f)
                curveToRelative(0.46f, 0f, 0.68f, -0.06f, 0.56f, -0.61f)
                arcToRelative(39.17f, 39.17f, 0f, isMoreThanHalf = false, isPositiveArc = false, -4.25f, -11.52f)
                arcToRelative(0.68f, 0.68f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.82f, -0.38f)
                arcToRelative(3.28f, 3.28f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.45f, -2f)
                arcToRelative(3.44f, 3.44f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.87f, -3.89f)
                arcToRelative(3.37f, 3.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.3f, 3.73f)
                arcToRelative(1.66f, 1.66f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.2f, 1.73f)
                arcToRelative(39.65f, 39.65f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.22f, 10.91f)
                arcToRelative(9f, 9f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.3f, 1.44f)
                arcToRelative(1.19f, 1.19f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.16f, 1.22f)
                arcToRelative(0.89f, 0.89f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.1f, -0.49f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7.05f, 0.89f)
                arcToRelative(3.76f, 3.76f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.88f, 4.14f)
                arcToRelative(1.63f, 1.63f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.48f, 1.68f)
                arcToRelative(32.36f, 32.36f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.23f, 9f)
                arcToRelative(37.39f, 37.39f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1f, 6.15f)
                curveToRelative(-0.15f, 0.56f, 0.15f, 0.68f, 0.56f, 0.81f)
                arcTo(24.05f, 24.05f, 0f, isMoreThanHalf = false, isPositiveArc = true, 61f, 126.4f)
                arcToRelative(19.64f, 19.64f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.64f, 5.11f)
                curveToRelative(0.34f, 0.47f, 0.55f, 0.52f, 1f, 0.1f)
                curveToRelative(0.91f, -0.88f, 1.82f, -1.75f, 2.7f, -2.66f)
                arcToRelative(0.73f, 0.73f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.19f, -1f)
                arcToRelative(3.82f, 3.82f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.66f, -4.31f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 78.28f, 126f)
                arcToRelative(4.06f, 4.06f, 0f, isMoreThanHalf = false, isPositiveArc = true, -5f, 4.29f)
                arcToRelative(1.26f, 1.26f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.52f, 0.47f)
                arcToRelative(25.51f, 25.51f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3f, 2.93f)
                arcToRelative(0.69f, 0.69f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.19f, 1f)
                arcToRelative(21.24f, 21.24f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 6.65f)
                arcToRelative(0.81f, 0.81f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.47f, 0.69f)
                arcToRelative(2.24f, 2.24f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.3f, 1.9f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.73f, 1f)
                lineToRelative(0.06f, 0f)
                arcToRelative(2.48f, 2.48f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3.24f, 0.13f)
                curveToRelative(1.72f, -1.15f, 3.93f, -0.2f, 5.09f, 1.79f)
                curveToRelative(0.07f, 0.13f, 0.16f, 0.24f, 0.24f, 0.37f)
                verticalLineToRelative(2.64f)
                arcToRelative(3.93f, 3.93f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.28f, 2.68f)
                arcToRelative(0.83f, 0.83f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.87f, 0.5f)
                curveToRelative(-1.34f, 2.39f, -2.77f, 4.72f, -4.31f, 7f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 1.13f)
                arcToRelative(4.18f, 4.18f, 0f, isMoreThanHalf = true, isPositiveArc = true, -7.44f, 0.26f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.75f, -2.27f)
                arcToRelative(0.85f, 0.85f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.09f, -0.41f)
                curveToRelative(1.38f, -2.27f, 2.78f, -4.54f, 4.18f, -6.8f)
                arcToRelative(0.64f, 0.64f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, -0.86f)
                arcToRelative(2.36f, 2.36f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.71f, -1.8f)
                curveToRelative(0.23f, -1.35f, -0.54f, -1.89f, -1.57f, -2.31f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.38f, -0.21f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.15f, 0f)
                arcToRelative(2.21f, 2.21f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.61f, 0.25f)
                arcToRelative(2.68f, 2.68f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.26f, -4.48f)
                curveToRelative(0.24f, -0.26f, 0.47f, -0.46f, 0.39f, -0.88f)
                arcToRelative(23.31f, 23.31f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.68f, -5.46f)
                curveToRelative(-0.22f, -0.49f, -0.45f, -0.53f, -0.87f, -0.22f)
                arcToRelative(49.81f, 49.81f, 0f, isMoreThanHalf = false, isPositiveArc = true, -11.27f, 6f)
                curveToRelative(-0.51f, 0.2f, -0.66f, 0.31f, -0.19f, 0.79f)
                curveToRelative(1.69f, 1.75f, 3.36f, 3.53f, 5f, 5.35f)
                curveToRelative(0.58f, 0.66f, 1.08f, 1.25f, 2f, 0.78f)
                arcToRelative(0.72f, 0.72f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.36f, 0f)
                arcToRelative(3.17f, 3.17f, 0f, isMoreThanHalf = true, isPositiveArc = true, -0.48f, 6.32f)
                arcToRelative(3.17f, 3.17f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.6f, -3.83f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.24f, -1f)
                curveToRelative(-2.07f, -2.39f, -4.32f, -4.62f, -6.46f, -7f)
                arcToRelative(0.8f, 0.8f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, -0.18f)
                arcToRelative(23.74f, 23.74f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.6f, 1.12f)
                arcToRelative(4.71f, 4.71f, 0f, isMoreThanHalf = false, isPositiveArc = false, -3.93f, 3f)
                arcToRelative(1.22f, 1.22f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.12f, 0.19f)
                arcToRelative(0.63f, 0.63f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 0.74f)
                arcTo(3.35f, 3.35f, 0f, isMoreThanHalf = false, isPositiveArc = true, 42f, 153f)
                curveToRelative(-0.29f, 0.16f, -0.4f, 0.29f, -0.27f, 0.62f)
                arcToRelative(57.74f, 57.74f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.89f, 5.56f)
                arcToRelative(0.43f, 0.43f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.58f, 0.37f)
                arcToRelative(4.51f, 4.51f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.29f, 5.2f)
                arcToRelative(0.67f, 0.67f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.44f, 0.79f)
                quadToRelative(8.16f, 4.54f, 16.32f, 9.12f)
                arcTo(9.58f, 9.58f, 0f, isMoreThanHalf = false, isPositiveArc = true, 68.44f, 176f)
                horizontalLineToRelative(53.05f)
                arcToRelative(6.56f, 6.56f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6.51f, -6.61f)
                verticalLineTo(20f)
                arcTo(6.56f, 6.56f, 0f, isMoreThanHalf = false, isPositiveArc = false, 121.49f, 13.44f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(48.68f, 167.55f)
                arcToRelative(0.57f, 0.57f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.77f, 0.11f)
                arcToRelative(3.79f, 3.79f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.57f, 1f)
                curveToRelative(-0.46f, 0f, -0.65f, 0.17f, -0.66f, 0.64f)
                arcTo(26.25f, 26.25f, 0f, isMoreThanHalf = false, isPositiveArc = true, 43.5f, 176f)
                horizontalLineTo(63.65f)
                arcToRelative(0.55f, 0.55f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.17f, -0.13f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(20.6f, 119.11f)
                arcToRelative(1.59f, 1.59f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.86f, -1.08f)
                arcToRelative(50.47f, 50.47f, 0f, isMoreThanHalf = false, isPositiveArc = false, -7.95f, -3.17f)
                curveToRelative(-1.54f, -0.49f, -3.11f, -0.87f, -4.47f, -1.24f)
                curveToRelative(-0.51f, -0.05f, -0.74f, 0.08f, -0.89f, 0.31f)
                reflectiveCurveToRelative(0f, 0.52f, 0.15f, 0.73f)
                arcTo(59f, 59f, 0f, isMoreThanHalf = false, isPositiveArc = true, 11f, 123.32f)
                arcToRelative(58.22f, 58.22f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.5f, 7.67f)
                curveToRelative(0.08f, 0.32f, 0.09f, 0.74f, 0.63f, 0.43f)
                curveToRelative(2.06f, -1.16f, 4.19f, -2.22f, 6.36f, -3.17f)
                curveToRelative(0.34f, -0.15f, 0.41f, -0.32f, 0.25f, -0.68f)
                arcToRelative(3.68f, 3.68f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.74f, -4.26f)
                arcToRelative(0.71f, 0.71f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.17f, -0.85f)
                curveTo(21.31f, 121.35f, 20.92f, 120.24f, 20.6f, 119.11f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(43.88f, 142.64f)
                quadToRelative(1.2f, -2.1f, 2.41f, -4.19f)
                arcToRelative(0.55f, 0.55f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.06f, -0.78f)
                lineToRelative(-4.34f, -4f)
                curveToRelative(-0.23f, -0.22f, -0.43f, -0.27f, -0.65f, 0f)
                arcToRelative(54.35f, 54.35f, 0f, isMoreThanHalf = false, isPositiveArc = true, -5.81f, 6.76f)
                arcToRelative(0.56f, 0.56f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.13f, 0.78f)
                curveToRelative(0.56f, 1f, 1.11f, 2f, 1.67f, 2.93f)
                arcToRelative(0.56f, 0.56f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.59f, 0.32f)
                lineToRelative(0.22f, 0f)
                lineToRelative(5.43f, -1.3f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 43.88f, 142.64f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(35.88f, 158.06f)
                lineToRelative(5.49f, 2.71f)
                arcToRelative(0.41f, 0.41f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.06f, -0.45f)
                arcToRelative(61.25f, 61.25f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.08f, -6.49f)
                curveToRelative(-0.13f, -0.37f, -0.29f, -0.39f, -0.54f, -0.08f)
                quadToRelative(-1.52f, 1.88f, -3.05f, 3.73f)
                curveTo(35.56f, 157.73f, 35.59f, 157.92f, 35.88f, 158.06f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(22.54f, 172f)
                curveToRelative(-1.32f, 1.33f, -2.65f, 2.64f, -4f, 4f)
                horizontalLineToRelative(6.67f)
                arcToRelative(40.73f, 40.73f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, -3.74f)
                curveTo(23.07f, 171.94f, 22.92f, 171.66f, 22.54f, 172f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(32.83f, 126.41f)
                curveToRelative(-1.28f, 0.36f, -2.56f, 0.75f, -3.84f, 1.07f)
                curveToRelative(-0.66f, 0.16f, -0.84f, 0.41f, -0.5f, 1.06f)
                curveToRelative(0.82f, 1.55f, 1.56f, 3.15f, 2.39f, 4.7f)
                reflectiveCurveToRelative(1.81f, 3.3f, 2.75f, 4.93f)
                curveToRelative(0.1f, 0.19f, 0.19f, 0.69f, 0.6f, 0.26f)
                arcToRelative(55.31f, 55.31f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5.19f, -6.13f)
                arcToRelative(0.57f, 0.57f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.14f, -0.92f)
                curveToRelative(-1.94f, -1.53f, -3.87f, -3.09f, -5.8f, -4.64f)
                arcTo(1.1f, 1.1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 32.83f, 126.41f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(27.12f, 122.56f)
                curveToRelative(-0.25f, 0.27f, -0.54f, 0.63f, -0.31f, 1f)
                arcToRelative(7.14f, 7.14f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.66f, 1.53f)
                curveToRelative(0.08f, 0.27f, 0.26f, 0.32f, 0.51f, 0.24f)
                curveToRelative(0.75f, -0.25f, 1.5f, -0.48f, 2.44f, -0.77f)
                curveToRelative(-1f, -0.68f, -1.8f, -1.26f, -2.64f, -1.82f)
                curveTo(27.58f, 122.65f, 27.37f, 122.29f, 27.12f, 122.56f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(43.09f, 124.9f)
                arcToRelative(53.12f, 53.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.53f, 0.78f)
                curveToRelative(-0.11f, 0f, -0.26f, 0f, -0.3f, 0.16f)
                reflectiveCurveToRelative(0.1f, 0.25f, 0.21f, 0.34f)
                curveToRelative(1.37f, 1.09f, 2.74f, 2.17f, 4.1f, 3.27f)
                curveToRelative(0.35f, 0.29f, 0.56f, 0.27f, 0.77f, -0.16f)
                curveToRelative(0.63f, -1.27f, 1.28f, -2.53f, 1.92f, -3.79f)
                arcToRelative(2.84f, 2.84f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.14f, -0.32f)
                curveTo(43.44f, 124.9f, 43.28f, 124.88f, 43.09f, 124.9f)
                close()
            }
        }.build()

        return _AppIcon!!
    }

@Suppress("ObjectPropertyName")
private var _AppIcon: ImageVector? = null

val Redo: ImageVector
    get() {
        if (_Redo != null) return _Redo!!

        _Redo = ImageVector.Builder(
            name = "Redo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
            ) {
                moveTo(396f, 760f)
                quadToRelative(-97f, 0f, -166.5f, -63f)
                reflectiveQuadTo(160f, 540f)
                reflectiveQuadToRelative(69.5f, -157f)
                reflectiveQuadTo(396f, 320f)
                horizontalLineToRelative(252f)
                lineTo(544f, 216f)
                lineToRelative(56f, -56f)
                lineToRelative(200f, 200f)
                lineToRelative(-200f, 200f)
                lineToRelative(-56f, -56f)
                lineToRelative(104f, -104f)
                horizontalLineTo(396f)
                quadToRelative(-63f, 0f, -109.5f, 40f)
                reflectiveQuadTo(240f, 540f)
                reflectiveQuadToRelative(46.5f, 100f)
                reflectiveQuadTo(396f, 680f)
                horizontalLineToRelative(284f)
                verticalLineToRelative(80f)
                close()
            }
        }.build()

        return _Redo!!
    }

@Suppress("ObjectPropertyName")
private var _Redo: ImageVector? = null

val Undo: ImageVector
    get() {
        if (_Undo != null) return _Undo!!

        _Undo = ImageVector.Builder(
            name = "Undo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
            ) {
                moveTo(280f, 760f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(284f)
                quadToRelative(63f, 0f, 109.5f, -40f)
                reflectiveQuadTo(720f, 540f)
                reflectiveQuadToRelative(-46.5f, -100f)
                reflectiveQuadTo(564f, 400f)
                horizontalLineTo(312f)
                lineToRelative(104f, 104f)
                lineToRelative(-56f, 56f)
                lineToRelative(-200f, -200f)
                lineToRelative(200f, -200f)
                lineToRelative(56f, 56f)
                lineToRelative(-104f, 104f)
                horizontalLineToRelative(252f)
                quadToRelative(97f, 0f, 166.5f, 63f)
                reflectiveQuadTo(800f, 540f)
                reflectiveQuadToRelative(-69.5f, 157f)
                reflectiveQuadTo(564f, 760f)
                close()
            }
        }.build()

        return _Undo!!
    }

@Suppress("ObjectPropertyName")
private var _Undo: ImageVector? = null
