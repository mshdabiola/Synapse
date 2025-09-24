package com.mshdabiola.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.Density
import com.mshdabiola.model.note.PenProperties
import com.mshdabiola.model.note.Point
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import androidx.compose.ui.graphics.Path as ComposePath
import com.mshdabiola.model.note.Path as DrawingPath

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

    @Test
    fun `drawPathsOnImage with empty paths returns original-like bitmap`() {
        val originalBitmap = createTestBitmap()
        val pathsToDraw = emptyList<DrawingPath>()
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertEquals(originalBitmap.width, resultBitmap.width)
        assertEquals(originalBitmap.height, resultBitmap.height)
        assertTrue(bitmapsAreSame(originalBitmap, resultBitmap), "Bitmap content should be the same when no paths are drawn.")
    }

    @Test
    fun `drawPathsOnImage with simple pen path draws on bitmap`() {
        val originalBitmap = createTestBitmap(color = Color.White)
        val pathsToDraw = listOf(
            DrawingPath(
                points = listOf(Point(10f, 10f), Point(100f, 100f)),
                penProperties = PenProperties(isPen = false),
            ),
            DrawingPath(
                points = listOf(Point(50f, 50f), Point(150f, 50f), Point(150f, 150f)),
                penProperties = PenProperties(isPen = true), // Example of a pen stroke
            ),
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertEquals(originalBitmap.width, resultBitmap.width)
        assertEquals(originalBitmap.height, resultBitmap.height)
        assertTrue(bitmapsAreDifferent(originalBitmap, resultBitmap), "Bitmap content should be different after drawing.")
    }

    @Test
    fun `drawPathsOnImage with simple shape path draws on bitmap`() {
        val originalBitmap = createTestBitmap(color = Color.White)
        val composePath = ComposePath().apply {
            moveTo(20f, 20f)
            lineTo(80f, 20f)
            lineTo(80f, 80f)
            close()
        }
        val pathsToDraw = listOf(
            DrawingPath(
                points = listOf(Point(10f, 10f), Point(100f, 100f)),
                penProperties = PenProperties(isPen = false),
            ),
            DrawingPath(
                points = listOf(Point(50f, 50f), Point(150f, 50f), Point(150f, 150f)),
                penProperties = PenProperties(isPen = true), // Example of a pen stroke
            ),
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertEquals(originalBitmap.width, resultBitmap.width)
        assertEquals(originalBitmap.height, resultBitmap.height)
        assertTrue(bitmapsAreDifferent(originalBitmap, resultBitmap), "Bitmap content should be different after drawing a shape.")
    }

    @Test
    fun `drawPathsOnImage handles paths outside bounds with scaling and translation`() {
        val originalBitmap = createTestBitmap(width = 100, height = 100, color = Color.White)
        val pathsToDraw = listOf(
            DrawingPath(
                points = listOf(Point(10f, 10f), Point(100f, 100f)),
                penProperties = PenProperties(isPen = false),
            ),
            DrawingPath(
                points = listOf(Point(50f, 50f), Point(150f, 50f), Point(150f, 150f)),
                penProperties = PenProperties(isPen = true), // Example of a pen stroke
            ),
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertEquals(originalBitmap.width, resultBitmap.width)
        assertEquals(originalBitmap.height, resultBitmap.height)
        assertTrue(bitmapsAreDifferent(originalBitmap, resultBitmap), "Bitmap should change even if paths are initially out of bounds due to scaling.")
    }

    @Test
    fun `drawPathsOnImage with pen path with one point does not draw`() {
        val originalBitmap = createTestBitmap(color = Color.White)
        val pathsToDraw = listOf(
            DrawingPath(
                points = listOf(Point(10f, 10f), Point(100f, 100f)),
                penProperties = PenProperties(isPen = false),
            ),
            DrawingPath(
                points = listOf(Point(50f, 50f), Point(150f, 50f), Point(150f, 150f)),
                penProperties = PenProperties(isPen = true), // Example of a pen stroke
            ),
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertEquals(originalBitmap.width, resultBitmap.width)
        assertEquals(originalBitmap.height, resultBitmap.height)
        assertTrue(bitmapsAreSame(originalBitmap, resultBitmap),"Bitmap should be unchanged for a single-point pen path as the drawing loop requires >1 points.")
    }

    @Test
    fun `drawPathsOnImage with empty non-pen path does not draw`() {
        val originalBitmap = createTestBitmap(color = Color.White)
        val pathsToDraw = listOf(
            DrawingPath(
                points = listOf(Point(10f, 10f), Point(100f, 100f)),
                penProperties = PenProperties(isPen = false),
            ),
            DrawingPath(
                points = listOf(Point(50f, 50f), Point(150f, 50f), Point(150f, 150f)),
                penProperties = PenProperties(isPen = true), // Example of a pen stroke
            ),
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertEquals(originalBitmap.width, resultBitmap.width)
        assertEquals(originalBitmap.height, resultBitmap.height)
        assertTrue(bitmapsAreSame(originalBitmap, resultBitmap), "Bitmap should be unchanged for an empty non-pen path.")
    }

    @Test
    fun `drawPathsOnImage with zero drawing width returns original-like bitmap`() {
        val originalBitmap = createTestBitmap()
        val pathsToDraw = listOf(
            DrawingPath(
                points = listOf(Point(10f, 10f), Point(100f, 100f)),
                penProperties = PenProperties(isPen = false),
            ),
            DrawingPath(
                points = listOf(Point(50f, 50f), Point(150f, 50f), Point(150f, 150f)),
                penProperties = PenProperties(isPen = true), // Example of a pen stroke
            ),
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertEquals(originalBitmap.width, resultBitmap.width)
        assertEquals(originalBitmap.height, resultBitmap.height)
        assertTrue(bitmapsAreSame(originalBitmap, resultBitmap), "Bitmap should be unchanged if drawing area has zero width.")
    }

    @Test
    fun `drawPathsOnImage with zero drawing height returns original-like bitmap`() {
        val originalBitmap = createTestBitmap()
        val pathsToDraw = listOf(
            DrawingPath(
                points = listOf(Point(10f, 10f), Point(100f, 100f)),
                penProperties = PenProperties(isPen = false),
            ),
            DrawingPath(
                points = listOf(Point(50f, 50f), Point(150f, 50f), Point(150f, 150f)),
                penProperties = PenProperties(isPen = true), // Example of a pen stroke
            ),
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertEquals(originalBitmap.width, resultBitmap.width)
        assertEquals(originalBitmap.height, resultBitmap.height)
        assertTrue(bitmapsAreSame(originalBitmap, resultBitmap), "Bitmap should be unchanged if drawing area has zero height.")
    }

    @Test
    fun `drawPathsOnImage with no valid points returns original-like bitmap`() {
        val originalBitmap = createTestBitmap()
        // Paths that won't contribute to min/max calculations in a meaningful way
        // or DrawingPath.path.isEmpty is true for non-pen
        val pathsToDraw = listOf(
            DrawingPath(
                points = listOf(Point(10f, 10f), Point(100f, 100f)),
                penProperties = PenProperties(isPen = false),
            ),
            DrawingPath(
                points = listOf(Point(50f, 50f), Point(150f, 50f), Point(150f, 150f)),
                penProperties = PenProperties(isPen = true), // Example of a pen stroke
            ),
        )
        val density = Density(1f)

        val resultBitmap = drawPathsOnImage(originalBitmap, pathsToDraw, density)

        assertNotNull(resultBitmap)
        assertEquals(originalBitmap.width, resultBitmap.width)
        assertEquals(originalBitmap.height, resultBitmap.height)
        assertTrue(bitmapsAreSame(originalBitmap, resultBitmap), "Bitmap should be unchanged if no valid drawing points are found.")
    }
}
