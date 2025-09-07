package com.mshdabiola.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.mshdabiola.model.note.PenProperties
import com.mshdabiola.model.note.Point
import com.mshdabiola.model.testtag.BoardViewTestTags
import org.junit.Rule
import org.junit.Test
import com.mshdabiola.model.note.Path as DrawingPath // Alias to match BoardView.kt

class BoardViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun boardViewer_displaysRootAndCanvas_whenDrawingPathsIsEmpty() {
        composeTestRule.setContent {
            BoardViewer(drawingPaths = emptyList())
        }

        composeTestRule.onNodeWithTag(BoardViewTestTags.SCREEN_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(BoardViewTestTags.CANVAS).assertIsDisplayed()
    }

    @Test
    fun boardViewer_displaysRootAndCanvas_withDrawingPaths() {
        val samplePath = Path().apply {
            moveTo(10f, 10f)
            lineTo(100f, 100f)
        }
        val sampleDrawingPaths = listOf(
            DrawingPath(
                points = listOf(Point(10f,10f), Point(100f,100f)),
                penProperties = PenProperties(isPen = false)
            ),
             DrawingPath(
                points = listOf(Point(50f,50f), Point(150f,50f), Point(150f,150f)),
                penProperties = PenProperties(isPen = true) // Example of a pen stroke
            )
        )

        composeTestRule.setContent {
            BoardViewer(drawingPaths = sampleDrawingPaths)
        }

        composeTestRule.onNodeWithTag(BoardViewTestTags.SCREEN_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(BoardViewTestTags.CANVAS).assertIsDisplayed()
    }
}
