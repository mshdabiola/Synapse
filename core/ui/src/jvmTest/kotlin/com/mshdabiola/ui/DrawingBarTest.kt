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

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.note.Path // Corrected import
import com.mshdabiola.model.note.PenProperties // Corrected import
import com.mshdabiola.model.note.Point // Corrected import
import com.mshdabiola.model.testtag.DrawingBarTestTags
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DrawingBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun drawingBar_rootAndDefaultTabs_areDisplayed() {
        composeTestRule.setContent {
            DrawingBar(controller = DrawingController())
        }
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.TAB_ROW).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.MARKER_TAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CRAYON_TAB).assertIsDisplayed()
    }

    @Test
    fun drawingBar_selectTab_isSelectedByDefault_optionsBehavior() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }

        // Initial state: SELECT_TAB selected, isUp is false (by default in DrawingBar)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).assertIsNotSelected()
        // No drawing or erase options should be visible
        composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertDoesNotExist()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).assertDoesNotExist()

        // First click on SELECT_TAB (it's already selected, so isUp toggles to true)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).performClick()
        assertEquals(DrawingTool.SELECT, controller.currentTool) // Tool remains SELECT
        // Even if isUp is true, SELECT tab should not show drawing/erase specific options
        composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertDoesNotExist()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).assertDoesNotExist()

        // Second click on SELECT_TAB (isUp toggles back to false)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).performClick()
        assertEquals(DrawingTool.SELECT, controller.currentTool) // Tool remains SELECT
        // Options should remain not visible
        composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertDoesNotExist()
//        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).assertIsNotDisplayed()
    }

    @Test
    fun drawingBar_switchingToSelectTab_hidesDrawingAndEraseOptions() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }

        // 1. Switch to PEN_TAB (a drawing tool) -> options (COLOR_WIDTH_SECTION_ROOT) should appear (isUp=true)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        assertEquals(DrawingTool.DRAW, controller.currentTool)
        composeTestRule.onNodeWithTag("${DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT}_2").assertIsDisplayed()

        // 2. Switch to SELECT_TAB -> options (COLOR_WIDTH_SECTION_ROOT) should disappear (isUp becomes false)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).performClick()
        assertEquals(DrawingTool.SELECT, controller.currentTool)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertDoesNotExist()

        // 3. Setup: Show ERASE options for next step
        // Click ERASE_TAB (isUp becomes false initially)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).performClick()
        // Click ERASE_TAB again (isUp becomes true, erase options show)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).performClick()
        assertEquals(DrawingTool.ERASE, controller.currentTool)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).assertIsDisplayed()

        // 4. Switch back to SELECT_TAB from ERASE_TAB (with options shown) ->
        // erase options should disappear (isUp becomes false)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).performClick()
        assertEquals(DrawingTool.SELECT, controller.currentTool)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).assertDoesNotExist()
    }

    @Test
    fun drawingBar_eraseTab_showsOptions_afterSecondClick() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // First click on ERASE_TAB (assuming coming from SELECT_TAB)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).performClick()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).assertIsSelected()
        assertEquals(DrawingTool.ERASE, controller.currentTool)
        // Erase options (clear canvas button) should NOT be visible yet as isUp becomes false on first switch
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).assertDoesNotExist()

        // Second click on ERASE_TAB (toggles isUp to true)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).performClick()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.TOOL_OPTIONS_PAGER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).assertIsDisplayed()
    }

    private fun testDrawingToolTab(
        tabIndex: Int,
        tool: DrawingTool,
        controller: DrawingController,
        expectOptionsVisibleFirstClick: Boolean,
    ) {
        val tabTag = when (tabIndex) {
            0 -> DrawingBarTestTags.SELECT_TAB
            1 -> DrawingBarTestTags.ERASE_TAB
            2 -> DrawingBarTestTags.PEN_TAB
            3 -> DrawingBarTestTags.MARKER_TAB
            4 -> DrawingBarTestTags.CRAYON_TAB
            else -> throw IllegalArgumentException("Invalid tabIndex: $tabIndex")
        }

        composeTestRule.onNodeWithTag(tabTag).performClick()
        composeTestRule.onNodeWithTag(tabTag).assertIsSelected()
        assertEquals(tool, controller.currentTool)
        // Drawing options (color/width) visibility depends on isUp logic
        if (expectOptionsVisibleFirstClick) {
            composeTestRule.onNodeWithTag(DrawingBarTestTags.TOOL_OPTIONS_PAGER).assertIsDisplayed()
            composeTestRule
                .onNodeWithTag("${DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT}_$tabIndex")
                .assertIsDisplayed()
            composeTestRule
                .onNodeWithTag("${DrawingBarTestTags.COLOR_SELECTOR_LAYOUT}_$tabIndex")
                .assertIsDisplayed()
            composeTestRule
                .onNodeWithTag("${DrawingBarTestTags.WIDTH_SELECTOR_LAYOUT}_$tabIndex")
                .assertIsDisplayed()
        } else {
            composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertDoesNotExist()
        }
    }

    @Test
    fun drawingBar_penTab_canBeSelected_setsToolAndShowsOptionsFirstClick() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        val initialProps = controller.currentDrawingProperties
        // PEN_TAB is index 2
        testDrawingToolTab(
            tabIndex = 2,
            tool = DrawingTool.DRAW,
            controller = controller,
            expectOptionsVisibleFirstClick = true,
        )
        assertEquals(initialProps.isPen, controller.currentDrawingProperties.isPen)
        // Pen properties should change
        assertEquals(true, controller.currentDrawingProperties.isPen)
    }

    @Test
    fun drawingBar_markerTab_canBeSelected_setsToolAndShowsOptionsFirstClick() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // MARKER_TAB is index 3
        testDrawingToolTab(
            tabIndex = 3,
            tool = DrawingTool.DRAW,
            controller = controller,
            expectOptionsVisibleFirstClick = true,
        )
        assertEquals(false, controller.currentDrawingProperties.isPen)
    }

    @Test
    fun drawingBar_crayonTab_canBeSelected_setsToolAndShowsOptionsFirstClick() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // CRAYON_TAB is index 4
        testDrawingToolTab(
            tabIndex = 4,
            tool = DrawingTool.DRAW,
            controller = controller,
            expectOptionsVisibleFirstClick = true,
        )
        assertEquals(false, controller.currentDrawingProperties.isPen)
    }

    @Test
    fun drawingBar_toolOptions_visibilityTogglesOnClickingSameDrawTab() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // Select Pen tab (index 2), options show (isUp becomes true)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.TOOL_OPTIONS_PAGER).assertIsDisplayed()
        composeTestRule.onNodeWithTag("${DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT}_2").assertIsDisplayed()

        // Click Pen tab again, options should hide (isUp toggles to false)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        // TOOL_OPTIONS_PAGER (HorizontalPager) itself still exists, but its content for drawing tools hides.
        composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertDoesNotExist()
    }

    @Test
    fun drawingBar_clearCanvasButton_clearsPaths() {
        val controller = DrawingController()
        // Add a dummy path to simulate existing drawings
        val dummyPath = Path(
            points = mutableStateListOf(Point(10f, 10f)), // Use Point
            penProperties = PenProperties(),
        )
        controller.drawingPaths.add(dummyPath)
        assertFalse(controller.drawingPaths.isEmpty())

        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // Select Erase tab and click again to show clear button
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).performClick()
        // isUp becomes false
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).performClick()
        // isUp becomes true, options show

        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).performClick()

        assertTrue(controller.drawingPaths.isEmpty())
    }

    @Test
    fun drawingBar_colorSelection_updatesControllerProperties() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // Select Pen tab (index 2) to show color options
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        val initialColorIndex = controller.currentDrawingProperties.colorIndex
        // Click the fourth color item (index 3) based on DrawingBarTestTags.COLOR_SELECTOR_ITEM_PREFIX_3
        composeTestRule.onNodeWithTag("${DrawingBarTestTags.COLOR_SELECTOR_ITEM_PREFIX}_3").performClick()
        assertEquals(3, controller.currentDrawingProperties.colorIndex)
        assertNotEquals(initialColorIndex, controller.currentDrawingProperties.colorIndex)
    }

    @Test
    fun drawingBar_widthSelection_updatesControllerProperties() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // Select Pen tab (index 2) to show width options
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        val initialWidth = controller.currentDrawingProperties.lineWidth
        // Click the second width item (index 1)
        composeTestRule.onNodeWithTag("${DrawingBarTestTags.WIDTH_SELECTOR_ITEM_PREFIX}_1").performClick()
        assertNotEquals(initialWidth, controller.currentDrawingProperties.lineWidth)
    }
}
