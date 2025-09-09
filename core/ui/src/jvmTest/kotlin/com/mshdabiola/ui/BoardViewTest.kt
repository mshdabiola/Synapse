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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
            BoardViewer(
                modifier = Modifier.fillMaxSize(),

                drawingPaths = emptyList(),
            )
        }
        composeTestRule.onNodeWithTag(BoardViewTestTags.SCREEN_ROOT).assertExists()

        composeTestRule.onNodeWithTag(BoardViewTestTags.SCREEN_ROOT).assertIsDisplayed()
    }

    @Test
    fun boardViewer_displaysRootAndCanvas_withDrawingPaths() {
        val samplePath = Path().apply {
            moveTo(10f, 10f)
            lineTo(100f, 100f)
        }
        val sampleDrawingPaths = listOf(
            DrawingPath(
                points = listOf(Point(10f, 10f), Point(100f, 100f)),
                penProperties = PenProperties(isPen = false),
            ),
            DrawingPath(
                points = listOf(Point(50f, 50f), Point(150f, 50f), Point(150f, 150f)),
                penProperties = PenProperties(isPen = true), // Example of a pen stroke
            ),
        )

        composeTestRule.setContent {
            BoardViewer(
                modifier = Modifier.fillMaxSize(),

                drawingPaths = sampleDrawingPaths,
            )
        }

        composeTestRule.onNodeWithTag(BoardViewTestTags.SCREEN_ROOT).assertIsDisplayed()
    }
}
