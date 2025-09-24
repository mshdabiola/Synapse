package com.mshdabiola.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap // For mapping if needed, though PenProperties uses index
import androidx.compose.ui.unit.Density
import com.mshdabiola.model.note.PenProperties
import com.mshdabiola.model.note.Point
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import com.mshdabiola.model.note.Path as DrawingPath // Correct alias
import androidx.compose.ui.graphics.Path as ComposePath // For creating expected shape paths if OutDrawing constructs them

// Helper extension function to convert Compose Color to ARGB Int
fun Color.asArgbInt(): Int {
    val alpha = (this.alpha * 255.0f + 0.5f).toInt()
    val red = (this.red * 255.0f + 0.5f).toInt()
    val green = (this.green * 255.0f + 0.5f).toInt()
    val blue = (this.blue * 255.0f + 0.5f).toInt()
    return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
}

class OutDrawingTest {

    private fun createTestBitmap(width: Int = 100, height: Int = 100, color: Color = Color.White): ImageBitmap {
        val bitmap = ImageBitmap(width, height, ImageBitmapConfig.Argb8888)
        val canvas = androidx.compose.ui.graphics.Canvas(bitmap)
        val paint = Paint().apply { this.color = color }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    private fun bitmapsAreDifferent(bitmap1: ImageBitmap, bitmap2: ImageBitmap): Boolean {
        if (bitmap1.width != bitmap2.width || bitmap1.height != bitmap2.height) {
            return true
        }
        val buffer1 = IntArray(bitmap1.width * bitmap1.height)
        bitmap1.readPixels(buffer1, 0, 0, bitmap1.width, bitmap1.height)
        val buffer2 = IntArray(bitmap2.width * bitmap2.height)
        bitmap2.readPixels(buffer2, 0, 0, bitmap2.width, bitmap2.height)
        return !buffer1.contentEquals(buffer2)
    }

    private fun bitmapsAreSame(bitmap1: ImageBitmap, bitmap2: ImageBitmap): Boolean {
        if (bitmap1.width != bitmap2.width || bitmap1.height != bitmap2.height) {
            return false
        }
        val buffer1 = IntArray(bitmap1.width * bitmap1.height)
        bitmap1.readPixels(buffer1, 0, 0, bitmap1.width, bitmap1.height)
        val buffer2 = IntArray(bitmap2.width * bitmap2.height)
        bitmap2.readPixels(buffer2, 0, 0, bitmap2.width, bitmap2.height)
        return buffer1.contentEquals(buffer2)
    }

    private fun isBitmapUniformColor(bitmap: ImageBitmap, expectedColor: Color): Boolean {
        val buffer = IntArray(bitmap.width * bitmap.height)
        bitmap.readPixels(buffer, 0, 0, bitmap.width, bitmap.height)
        val expectedPixel = expectedColor.asArgbInt()
        if (buffer.isEmpty() && (bitmap.width > 0 || bitmap.height > 0)) return false
        return buffer.all { it == expectedPixel }
    }

    @Test
    fun `drawPathsOnImage with empty paths results in bitmap reflecting original color`() {
        val originalColor = Color.Yellow
        val originalBitmap = createTestBitmap(color = originalColor)
        val pathsToDraw = emptyList<DrawingPath>()
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertEquals(originalBitmap.width, resultBitmap.width)
        assertEquals(originalBitmap.height, resultBitmap.height)
        assertTrue(isBitmapUniformColor(resultBitmap, originalColor), "Bitmap content should be uniformly the original color when no paths are drawn.")
    }

    @Test
    fun `drawPathsOnImage with simple pen path draws on bitmap`() {
        val originalBitmap = createTestBitmap(color = Color.White)
        val pathsToDraw = listOf(
            DrawingPath(
                points = mutableStateListOf(Point(10f, 10f), Point(50f, 50f)),
                penProperties = PenProperties(
                    isPen = true,
                    lineWidth = 2,
                    colorIndex = 0 // Assuming index 0 maps to a visible color like Black in OutDrawing.kt
                )
            )
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertTrue(bitmapsAreDifferent(originalBitmap, resultBitmap), "Bitmap content should be different after drawing a pen path.")
    }

    @Test
    fun `drawPathsOnImage with simple shape path draws on bitmap`() {
        val originalBitmap = createTestBitmap(color = Color.White)
        val pathsToDraw = listOf(
            DrawingPath(
                points = mutableStateListOf(Point(20f, 20f), Point(80f, 20f), Point(80f, 80f), Point(20f, 20f)), // Triangle
                penProperties = PenProperties(isPen = false) // color/stroke for shapes would be default in OutDrawing.kt
            )
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertTrue(bitmapsAreDifferent(originalBitmap, resultBitmap), "Bitmap content should be different after drawing a shape.")
    }

    @Test
    fun `drawPathsOnImage handles paths outside bounds with scaling and translation`() {
        val originalBitmap = createTestBitmap(width = 100, height = 100, color = Color.White)
        val pathsToDraw = listOf(
            DrawingPath(
                points = mutableStateListOf(Point(1000f, 1000f), Point(1500f, 1500f)),
                penProperties = PenProperties(
                    isPen = true,
                    lineWidth = 5 // Original large stroke width from PenProperties
                )
            )
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertTrue(bitmapsAreDifferent(originalBitmap, resultBitmap), "Bitmap should change due to scaling of out-of-bounds path.")
    }

    @Test
    fun `drawPathsOnImage with pen path with one point does not alter bitmap from initial copy`() {
        val originalBitmap = createTestBitmap(color = Color.White)
        val density = Density(1f)
        val baselineBitmap = drawPathsOnImage(originalBitmap, emptyList(), density)

        val pathsToDraw = listOf(
            DrawingPath(
                points = mutableStateListOf(Point(10f, 10f)), // Single point
                penProperties = PenProperties(isPen = true, lineWidth = 2)
            )
        )
        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertTrue(bitmapsAreSame(baselineBitmap, resultBitmap),"Bitmap should be unchanged for a single-point pen path.")
    }

    @Test
    fun `drawPathsOnImage with empty points non-pen path does not alter bitmap`() {
        val originalBitmap = createTestBitmap(color = Color.White)
        val density = Density(1f)
        val baselineBitmap = drawPathsOnImage(originalBitmap, emptyList(), density)

        val pathsToDraw = listOf(
            DrawingPath(
                points = mutableStateListOf(), // Empty points list
                penProperties = PenProperties(isPen = false)
            )
        )
        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertTrue(bitmapsAreSame(baselineBitmap, resultBitmap), "Bitmap should be unchanged for a non-pen path with empty points.")
    }

    @Test
    fun `drawPathsOnImage with zero drawing width returns bitmap like initial copy`() {
        val originalBitmap = createTestBitmap()
        val density = Density(1f)
        val baselineBitmap = drawPathsOnImage(originalBitmap, emptyList(), density)

        val pathsToDraw = listOf(
            DrawingPath(
                points = mutableStateListOf(Point(10f, 10f), Point(10f, 50f)), // Vertical line, zero drawing width for points themselves
                penProperties = PenProperties(isPen = true, lineWidth = 1)
            )
        )
        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertTrue(bitmapsAreSame(baselineBitmap, resultBitmap), "Bitmap should be unchanged if drawing area of points has zero width.")
    }

    @Test
    fun `drawPathsOnImage with zero drawing height returns bitmap like initial copy`() {
        val originalBitmap = createTestBitmap()
        val density = Density(1f)
        val baselineBitmap = drawPathsOnImage(originalBitmap, emptyList(), density)

        val pathsToDraw = listOf(
            DrawingPath(
                points = mutableStateListOf(Point(10f, 10f), Point(50f, 10f)), // Horizontal line, zero drawing height for points
                penProperties = PenProperties(isPen = true, lineWidth = 1)
            )
        )
        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertTrue(bitmapsAreSame(baselineBitmap, resultBitmap), "Bitmap should be unchanged if drawing area of points has zero height.")
    }

    @Test
    fun `drawPathsOnImage with no valid points in any path returns bitmap like initial copy`() {
        val originalBitmap = createTestBitmap()
        val density = Density(1f)
        val baselineBitmap = drawPathsOnImage(originalBitmap, emptyList(), density)

        val pathsToDraw = listOf(
            DrawingPath(
                points = mutableStateListOf(),
                penProperties = PenProperties(isPen = true)
            ),
            DrawingPath(
                points = mutableStateListOf(),
                penProperties = PenProperties(isPen = false)
            )
        )
        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertTrue(bitmapsAreSame(baselineBitmap, resultBitmap), "Bitmap should be unchanged if all paths have no valid drawing points.")
    }
}
