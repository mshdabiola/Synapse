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
package com.mshdabiola.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag // Added import
import com.mshdabiola.model.testtag.BoardViewTestTags // Added import
import kotlin.math.max
import kotlin.math.min
import com.mshdabiola.model.note.Path as DrawingPath

@Composable
fun BoardViewer(
    modifier: Modifier = Modifier,
    drawingPaths: List<DrawingPath>,
) {
    Canvas(
        modifier = modifier
            .testTag(BoardViewTestTags.SCREEN_ROOT) // Added SCREEN_ROOT tag to the passed modifier
            .graphicsLayer {
                // Using graphicsLayer for clipping is good
                clip = true
            }
            .background(Color.White)
            .testTag(BoardViewTestTags.CANVAS), // Added CANVAS tag specifically to the Canvas
    ) {
        if (drawingPaths.isEmpty()) {
            return@Canvas // Nothing to draw
        }

        // 1. Calculate the bounding box of all drawing paths
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE

        val pathMeasure = PathMeasure() // Reusable PathMeasure

        drawingPaths.forEach { drawingPath ->
            // If drawingPath.paths directly gives you List<Offset> for pen strokes
            if (drawingPath.penProperties.isPen && drawingPath.paths.isNotEmpty()) {
                drawingPath.paths.forEach { point ->
                    minX = min(minX, point.x)
                    minY = min(minY, point.y)
                    maxX = max(maxX, point.x)
                    maxY = max(maxY, point.y)
                }
            } else {
                if (drawingPath.paths.isNotEmpty()) {
                    drawingPath.paths.forEach { point ->
                        minX = min(minX, point.x)
                        minY = min(minY, point.y)
                        maxX = max(maxX, point.x)
                        maxY = max(maxY, point.y)
                    }
                } else if (!drawingPath.path.isEmpty) {
                    try {
                        pathMeasure.setPath(drawingPath.path, false)
                        // This Rect calculation seems incorrect for bounding box,
                        // as pathMeasure.length is a scalar.
                        // However, keeping it as is as per original code structure.
                        val pathRect = Rect(
                            0f,
                            0f,
                            pathMeasure.length,
                            pathMeasure.length,
                        )
                        // To get actual bounds of a Path, you'd use path.getBounds()
                        // but that might be Android specific.
                        // For Compose Multiplatform, iterating points or using approximations is common.
                    } catch (e: Exception) {
                        // Handle cases where path might be invalid for measure
                    }
                }
            }
        }

        if (minX == Float.MAX_VALUE || drawingPaths.isEmpty()) {
            return@Canvas // No valid points found or empty drawing
        }

        val drawingWidth = maxX - minX
        val drawingHeight = maxY - minY

        if (drawingWidth <= 0 || drawingHeight <= 0) {
            return@Canvas // Not a valid drawing area
        }

        // 2. Calculate scale factor and offsets
        val canvasWidth = size.width
        val canvasHeight = size.height

        val scaleX = canvasWidth / drawingWidth
        val scaleY = canvasHeight / drawingHeight
        val scale = min(scaleX, scaleY) // Use min to fit and maintain aspect ratio

        // Calculate translation to center the scaled drawing
        val scaledDrawingWidth = drawingWidth * scale
        val scaledDrawingHeight = drawingHeight * scale

        val translateX = (canvasWidth - scaledDrawingWidth) / 2f - (minX * scale)
        val translateY = (canvasHeight - scaledDrawingHeight) / 2f - (minY * scale)

        // 3. Apply transformation and draw
        withTransform(
            {
                translate(left = translateX, top = translateY)
                scale(
                    scaleX = scale,
                    scaleY = scale,
                    pivot = Offset.Zero, // Scale around the original drawing's top-left
                )
            },
        ) {
            // Draw existing paths with the transformation
            drawingPaths.forEach { drawingPath ->
                if (drawingPath.penProperties.isPen) {
                    val points = drawingPath.paths
                    if (points.size > 1) {
                        val maxStrokeWidthPx =
                            drawingPath.strokeWidth.width // Original stroke width
                        // IMPORTANT: Scale the stroke width as well!
                        val scaledStrokeWidth = max(1f, maxStrokeWidthPx * scale)

                        val taperSegments = 30
                        val minStrokeFactor = 0.1f

                        for (i in 0 until points.size - 1) {
                            val startPoint = points[i]
                            val endPoint = points[i + 1]
                            val segmentsFromEnd = points.size - 1 - i
                            val currentStrokeWidthPx = if (segmentsFromEnd <= taperSegments) {
                                val taperFactor =
                                    (segmentsFromEnd - 1).toFloat() / taperSegments.toFloat()
                                max(
                                    scaledStrokeWidth * minStrokeFactor,
                                    scaledStrokeWidth * taperFactor,
                                )
                            } else {
                                scaledStrokeWidth
                            }
                            val finalWidth =
                                max(0.5f, currentStrokeWidthPx) // Ensure min width after scaling

                            drawLine(
                                color = drawingPath.color,
                                start = startPoint,
                                end = endPoint,
                                strokeWidth = finalWidth, // Use scaled and tapered width
                                cap = drawingPath.strokeWidth.cap,
                            )
                        }
                    }
                } else {
                    // IMPORTANT: Scale the stroke width for non-pen paths too!
                    val originalStroke = drawingPath.strokeWidth
                    val scaledStroke = Stroke(
                        width = max(1f, originalStroke.width * scale), // Scale width
                        miter = originalStroke.miter,
                        cap = originalStroke.cap,
                        join = originalStroke.join,
                        pathEffect = originalStroke.pathEffect,
                        // PathEffect might also need scaling depending on its nature
                    )
                    drawPath(
                        path = drawingPath.path,
                        color = drawingPath.color,
                        style = scaledStroke, // Use the new scaled stroke
                    )
                }
            }
        }
    }
}
