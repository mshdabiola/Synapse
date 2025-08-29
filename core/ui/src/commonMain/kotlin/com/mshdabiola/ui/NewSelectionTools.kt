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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag // Added import
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.designsystem.theme.SynTheme
import com.mshdabiola.model.testtag.NewSelectionToolsTestTags // Added import
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

@Composable
fun ResizableRectangleWithHandles2(
    rectangle: MutableState<Rect>,
    rotationAngle: MutableState<Float>,
) {
    val density = LocalDensity.current
    val configuration = LocalWindowInfo.current
    val screenWidthDp = configuration.containerSize.width
    val screenHeightDp = configuration.containerSize.height

    with(density) {
        val screenWidthPx = screenWidthDp
        val screenHeightPx = screenHeightDp

        val handleSize = 24.dp
        val handleSizePx = handleSize.toPx()
        val minDimensionPx = handleSizePx * 2

        val rotationPivotX = (rectangle.value.width + handleSizePx) / 2f
        val rotationPivotY = (rectangle.value.height + handleSizePx.times(2.5f)) / 2f

        Box(
            Modifier
                .graphicsLayer(
                    rotationZ = rotationAngle.value,
                    transformOrigin = TransformOrigin(
                        rotationPivotX / (rectangle.value.width + handleSizePx),
                        rotationPivotY / (rectangle.value.height + handleSizePx),
                    ),
                )
                .fillMaxSize()
                .testTag(NewSelectionToolsTestTags.RESIZABLE_RECT_ROOT),
        ) {
            Column(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            rectangle.value.topLeft.x.roundToInt(),
                            rectangle.value.topLeft.y.roundToInt(),
                        )
                    }
                    .testTag(NewSelectionToolsTestTags.RESIZABLE_RECT_OFFSET_COLUMN),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(handleSize)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val rectCenterX = rectangle.value.center.x
                                val rectCenterY = rectangle.value.center.y
                                val prevPos = change.previousPosition - Offset(rectCenterX, rectCenterY)
                                val currentPos = change.position - Offset(rectCenterX, rectCenterY)
                                val prevAngle = atan2(prevPos.y, prevPos.x)
                                val currentAngle = atan2(currentPos.y, currentPos.x)
                                val angleDiff =
                                    ((currentAngle - prevAngle) * 180.0 / PI).toFloat()
                                rotationAngle.value += angleDiff
                            }
                        }
                        .background(Color.Blue, CircleShape)
                        .testTag(NewSelectionToolsTestTags.ROTATION_HANDLE_ROOT),
                ) {
                    Icon(
                        SynIcons.Refresh,
                        contentDescription = "Rotate",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(2.dp)
                            .testTag(NewSelectionToolsTestTags.ROTATION_HANDLE_ICON),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                VerticalDivider(
                    modifier = Modifier.height(handleSize / 2),
                    thickness = 4.dp,
                    color = Color.Blue,
                )
                Box(
                    modifier = Modifier
                        .size(
                            (rectangle.value.width.toDp() + handleSize),
                            (rectangle.value.height.toDp() + handleSize),
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                rectangle.value = rectangle.value.translate(dragAmount.x, dragAmount.y)
                            }
                        }
                        .testTag(NewSelectionToolsTestTags.MAIN_DRAGGABLE_RESIZABLE_AREA),
                ) {
                    Box(
                        modifier = Modifier
                            .size(
                                rectangle.value.width.toDp(),
                                rectangle.value.height.toDp(),
                            )
                            .align(Alignment.Center)
                            .border(4.dp, Color.Blue)
                            .testTag(NewSelectionToolsTestTags.RESIZABLE_BORDER_BOX),
                    )

                    // Top-Left handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.TopStart)
                            .testTag(NewSelectionToolsTestTags.HANDLE_TOP_LEFT),
                    ) { dragAmount ->
                        val newWidth = (rectangle.value.width - 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx)
                        val newHeight = (rectangle.value.height - 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f))
                        val newTopLeft = Offset(
                            rectangle.value.center.x - newWidth / 2,
                            rectangle.value.center.y - newHeight / 2,
                        )
                        rectangle.value = Rect(newTopLeft, Size(newWidth, newHeight))
                    }
                    // Top-Center handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.TopCenter)
                            .testTag(NewSelectionToolsTestTags.HANDLE_TOP_CENTER),
                    ) { dragAmount ->
                        val newHeight = (rectangle.value.height - 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f))
                        val newTopLeft = Offset(rectangle.value.topLeft.x, rectangle.value.center.y - newHeight / 2)
                        rectangle.value =
                            Rect(
                                newTopLeft,
                                Size(
                                    rectangle.value.width.coerceIn(
                                        minDimensionPx,
                                        screenWidthPx - handleSizePx,
                                    ),
                                    newHeight,
                                ),
                            )
                    }
                    // Top-End handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.TopEnd)
                            .testTag(NewSelectionToolsTestTags.HANDLE_TOP_END),
                    ) { dragAmount ->
                        val newWidth = (rectangle.value.width + 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx)
                        val newHeight = (rectangle.value.height - 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f))
                        val newTopLeft = Offset(
                            rectangle.value.center.x - newWidth / 2,
                            rectangle.value.center.y - newHeight / 2,
                        )
                        rectangle.value = Rect(newTopLeft, Size(newWidth, newHeight))
                    }
                    // Center-Start handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.CenterStart)
                            .testTag(NewSelectionToolsTestTags.HANDLE_CENTER_START),
                    ) { dragAmount ->
                        val newWidth = (rectangle.value.width - 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx)
                        val newTopLeft = Offset(rectangle.value.center.x - newWidth / 2, rectangle.value.topLeft.y)
                        rectangle.value =
                            Rect(
                                newTopLeft,
                                Size(
                                    newWidth,
                                    rectangle.value.height.coerceIn(
                                        minDimensionPx,
                                        screenHeightPx - handleSizePx,
                                    ),
                                ),
                            )
                    }
                    // Center-End handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.CenterEnd)
                            .testTag(NewSelectionToolsTestTags.HANDLE_CENTER_END),
                    ) { dragAmount ->
                        val newWidth = (rectangle.value.width + 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx)
                        val newTopLeft = Offset(rectangle.value.center.x - newWidth / 2, rectangle.value.topLeft.y)
                        rectangle.value =
                            Rect(
                                newTopLeft,
                                Size(
                                    newWidth,
                                    rectangle.value.height.coerceIn(
                                        minDimensionPx,
                                        screenHeightPx - handleSizePx,
                                    ),
                                ),
                            )
                    }
                    // Bottom-Start handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.BottomStart)
                            .testTag(NewSelectionToolsTestTags.HANDLE_BOTTOM_START),
                    ) { dragAmount ->
                        val newWidth = (rectangle.value.width - 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx)
                        val newHeight = (rectangle.value.height + 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f))
                        val newTopLeft = Offset(
                            rectangle.value.center.x - newWidth / 2,
                            rectangle.value.center.y - newHeight / 2,
                        )
                        rectangle.value = Rect(newTopLeft, Size(newWidth, newHeight))
                    }
                    // Bottom-Center handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.BottomCenter)
                            .testTag(NewSelectionToolsTestTags.HANDLE_BOTTOM_CENTER),
                    ) { dragAmount ->
                        val newHeight = (rectangle.value.height + 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f))
                        val newTopLeft = Offset(rectangle.value.topLeft.x, rectangle.value.center.y - newHeight / 2)
                        rectangle.value =
                            Rect(
                                newTopLeft,
                                Size(
                                    rectangle.value.width.coerceIn(
                                        minDimensionPx,
                                        screenWidthPx - handleSizePx,
                                    ),
                                    newHeight,
                                ),
                            )
                    }
                    // Bottom-End handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.BottomEnd)
                            .testTag(NewSelectionToolsTestTags.HANDLE_BOTTOM_END),
                    ) { dragAmount ->
                        val newWidth = (rectangle.value.width + 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx)
                        val newHeight = (rectangle.value.height + 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f))
                        val newTopLeft = Offset(
                            rectangle.value.center.x - newWidth / 2,
                            rectangle.value.center.y - newHeight / 2,
                        )
                        rectangle.value = Rect(newTopLeft, Size(newWidth, newHeight))
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableHandle(
    modifier: Modifier = Modifier,
    onDrag: (dragAmount: Offset) -> Unit, // dragAmount is in pixels
) {
    Box(
        modifier = modifier // If DRAGGABLE_HANDLE_ROOT is defined, it would be applied here.
            .background(Color.Blue, RoundedCornerShape(2.dp))
            .border(1.dp, Color.White, RoundedCornerShape(2.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount) // Pass drag amount in pixels
                }
            },
    ) {
        // No content needed, it's just a visual box for the handle
    }
}

@Preview(showBackground = true)
@Composable
fun ResizableRectangleWithHandlesPreview() {
    val rectangle = remember { mutableStateOf(Rect(0f, 0f, 100f, 100f)) }
    val rotationAngle = remember { mutableFloatStateOf(0f) }

    SynTheme {
        ResizableRectangleWithHandles2(
            rectangle = rectangle,
            rotationAngle = rotationAngle,
        )
    }
}
