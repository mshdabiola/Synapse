package com.mshdabiola.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.note.Path
import com.mshdabiola.model.note.PenProperties
import com.mshdabiola.model.note.Point
import com.mshdabiola.model.testtag.DrawingBarTestTags
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
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
    fun drawingBar_selectTab_isSelectedByDefault_andSetsCorrectTool() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).performClick()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).assertIsSelected()
        assertEquals(DrawingTool.SELECT, controller.currentTool)

        // Click again to ensure it stays SELECT and options might toggle (isUp logic specific to SELECT)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).performClick()
        assertEquals(DrawingTool.SELECT, controller.currentTool)
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

    private fun testDrawingToolTab(tabTag: String, tool: DrawingTool, controller: DrawingController, expectOptionsVisibleFirstClick: Boolean) {
        composeTestRule.onNodeWithTag(tabTag).performClick()
        composeTestRule.onNodeWithTag(tabTag).assertIsSelected()
        assertEquals(tool, controller.currentTool)
        // Drawing options (color/width) visibility depends on isUp logic
        if (expectOptionsVisibleFirstClick) {
            composeTestRule.onNodeWithTag(DrawingBarTestTags.TOOL_OPTIONS_PAGER).assertIsDisplayed()
            composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertIsDisplayed()
            composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_SELECTOR_LAYOUT).assertIsDisplayed()
            composeTestRule.onNodeWithTag(DrawingBarTestTags.WIDTH_SELECTOR_LAYOUT).assertIsDisplayed()
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
        // For Draw tools, options (isUp=true) appear on first click when switching to them
        testDrawingToolTab(DrawingBarTestTags.PEN_TAB, DrawingTool.DRAW, controller, expectOptionsVisibleFirstClick = true)
        assertNotEquals(initialProps.isPen, controller.currentDrawingProperties.isPen) // Pen properties should change
        assertEquals(true, controller.currentDrawingProperties.isPen)
    }

    @Test
    fun drawingBar_markerTab_canBeSelected_setsToolAndShowsOptionsFirstClick() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        testDrawingToolTab(DrawingBarTestTags.MARKER_TAB, DrawingTool.DRAW, controller, expectOptionsVisibleFirstClick = true)
        assertEquals(false, controller.currentDrawingProperties.isPen)
    }

    @Test
    fun drawingBar_crayonTab_canBeSelected_setsToolAndShowsOptionsFirstClick() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        testDrawingToolTab(DrawingBarTestTags.CRAYON_TAB, DrawingTool.DRAW, controller, expectOptionsVisibleFirstClick = true)
        assertEquals(false, controller.currentDrawingProperties.isPen)
    }

    @Test
    fun drawingBar_toolOptions_visibilityTogglesOnClickingSameDrawTab() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // Select Pen tab, options show (isUp becomes true)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.TOOL_OPTIONS_PAGER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertIsDisplayed()

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
            points = listOf(Point(10f,10f), Point(100f,100f)),
            penProperties = PenProperties(isPen = false)
        )
        controller.drawingPaths.add(dummyPath)
        assertFalse(controller.drawingPaths.isEmpty())

        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // Select Erase tab and click again to show clear button
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).performClick() // isUp becomes false
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).performClick() // isUp becomes true, options show

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
        // Select Pen tab to show color options
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        val initialColorIndex = controller.currentDrawingProperties.colorIndex
        // Click the second color item (index 1)
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
        // Select Pen tab to show width options
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        val initialWidth = controller.currentDrawingProperties.lineWidth
        // Click the second width item (index 1)
        composeTestRule.onNodeWithTag("${DrawingBarTestTags.WIDTH_SELECTOR_ITEM_PREFIX}_1").performClick()
        assertNotEquals(initialWidth, controller.currentDrawingProperties.lineWidth)
    }
}
