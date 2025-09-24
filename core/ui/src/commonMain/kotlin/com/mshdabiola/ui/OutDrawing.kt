package com.mshdabiola.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import kotlin.math.max
import kotlin.math.min
import com.mshdabiola.model.note.Path as DrawingPath

/**
 * Creates a new ImageBitmap with the given paths drawn onto a copy of the original bitmap.
 *
 * @param originalBitmap The base image.
 * @param pathsToDraw The paths to draw onto the image.
 * @param density Current density, needed for proper stroke width scaling if your
 *                stroke widths are defined in Dp. If they are already in pixels,
 *                you might not need this or can use Density(1f).
 * @return A new ImageBitmap with the paths rendered on it.
 */
fun drawPathsOnImage(
    originalBitmap: ImageBitmap,
    pathsToDraw: List<DrawingPath>,
    density: Density, // Important for consistent stroke width if defined in Dp
): ImageBitmap {
    // Create a mutable copy of the original bitmap to draw on
    // Or, if you want to draw on a new blank bitmap of the same size:
    // val outputBitmap = ImageBitmap(originalBitmap.width, originalBitmap.height, ImageBitmapConfig.Argb8888)
    val outputBitmap = ImageBitmap(
        width = originalBitmap.width,
        height = originalBitmap.height,
        config = originalBitmap.config, // Use the same config
    )

    val canvas = Canvas(outputBitmap) // Create a Canvas backed by the new bitmap

    // Optional: Draw the original image onto the new canvas first if you want it as a background
    // If originalBitmap is just a background color or placeholder, you might skip this
    // or fill with a background color.
    // For this example, let's assume we are drawing ON TOP of the original's content.
    // To do that, we first draw the originalBitmap onto our outputBitmap's canvas.
    canvas.drawImageRect(
        image = originalBitmap,
        dstOffset = Offset.Zero.round(), // Draw at top-left
        dstSize = IntSize(originalBitmap.width, originalBitmap.height),
        paint = Paint(), // Default paint
    )


    // --- Transformation Logic (similar to BoardViewer, but adapted for ImageBitmap) ---
    if (pathsToDraw.isEmpty()) {
        return outputBitmap // Return the bitmap (possibly with original image drawn)
    }

    var minX = Float.MAX_VALUE
    var minY = Float.MAX_VALUE
    var maxX = Float.MIN_VALUE
    var maxY = Float.MIN_VALUE

    pathsToDraw.forEach { drawingPath ->
        if (drawingPath.penProperties.isPen && drawingPath.paths.isNotEmpty()) {
            drawingPath.paths.forEach { point ->
                minX = min(minX, point.x)
                minY = min(minY, point.y)
                maxX = max(maxX, point.x)
                maxY = max(maxY, point.y)
            }
        } else if (!drawingPath.path.isEmpty) { // Assuming your DrawingPath has a compose Path
            val r = drawingPath.path.getBounds()
            minX = min(minX, r.left)
            minY = min(minY, r.top)
            maxX = max(maxX, r.right)
            maxY = max(maxY, r.bottom)
        }
    }

    if (minX == Float.MAX_VALUE) return outputBitmap // No valid path points

    val drawingWidth = maxX - minX
    val drawingHeight = maxY - minY

    if (drawingWidth <= 0 || drawingHeight <= 0) return outputBitmap

    val imageCanvasWidth = outputBitmap.width.toFloat()
    val imageCanvasHeight = outputBitmap.height.toFloat()

    val scaleX = imageCanvasWidth / drawingWidth
    val scaleY = imageCanvasHeight / drawingHeight
    val scale = min(scaleX, scaleY) // Fit and maintain aspect ratio

    val scaledDrawingWidth = drawingWidth * scale
    val scaledDrawingHeight = drawingHeight * scale

    val translateX = (imageCanvasWidth - scaledDrawingWidth) / 2f - (minX * scale)
    val translateY = (imageCanvasHeight - scaledDrawingHeight) / 2f - (minY * scale)
    // --- End Transformation Logic ---


    canvas.save() // Save current canvas state
    canvas.translate(translateX, translateY)
    canvas.scale(scale, scale) // Scale around the original drawing's top-left
    canvas.drawRect(minX,minY,maxX,maxY,Paint().apply {
        color= androidx.compose.ui.graphics.Color.White
    })
    pathsToDraw.forEach { drawingPath ->
        val paint = Paint().apply {
            this.color = drawingPath.color
            this.style = PaintingStyle.Stroke
            this.strokeWidth = max(0.5f, drawingPath.strokeWidth.width * scale) // Scale stroke width
            this.strokeCap = drawingPath.strokeWidth.cap
            this.strokeJoin = drawingPath.strokeWidth.join
            // Note: PathEffect might also need scaling depending on its nature.
            // This is simpler for the ImageBitmap canvas.
        }

        if (drawingPath.penProperties.isPen) {
            if (drawingPath.paths.size > 1) {
                // For pen, we might need to construct a Path object to draw smoothly
                // or draw line segments like in BoardViewer.
                // For simplicity here, let's draw line segments. Tapering can also be added.
                val scaledBaseStrokeWidth = max(0.5f, drawingPath.strokeWidth.width * scale)
                val taperSegments = 30
                val minStrokeFactor = 0.1f

                for (i in 0 until drawingPath.paths.size - 1) {
                    val start = drawingPath.paths[i]
                    val end = drawingPath.paths[i + 1]

                    val segmentsFromEnd = drawingPath.paths.size - 1 - i
                    val currentStrokeWidthPx = if (segmentsFromEnd <= taperSegments) {
                        val taperFactor = (segmentsFromEnd - 1).toFloat() / taperSegments.toFloat()
                        max(
                            scaledBaseStrokeWidth * minStrokeFactor,
                            scaledBaseStrokeWidth * taperFactor,
                        )
                    } else {
                        scaledBaseStrokeWidth
                    }
                    paint.strokeWidth = max(0.5f, currentStrokeWidthPx)

                    canvas.drawLine(start, end, paint)
                }
            }
        } else {
            // Ensure drawingPath.path is an androidx.compose.ui.graphics.Path
            canvas.drawPath(drawingPath.path, paint)
        }
    }

    canvas.restore() // Restore canvas state

    return outputBitmap
}
