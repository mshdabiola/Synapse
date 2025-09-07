package com.mshdabiola.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.testtag.DrawingBarTestTags
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).assertIsSelected()
        assertEquals(DrawingTool.SELECT, controller.currentTool)

        // Click again to ensure it stays SELECT and options might toggle
        composeTestRule.onNodeWithTag(DrawingBarTestTags.SELECT_TAB).performClick()
        assertEquals(DrawingTool.SELECT, controller.currentTool)
    }

    @Test
    fun drawingBar_eraseTab_canBeSelected_andSetsCorrectTool_andShowsOptions() {
        val controller = spyk(DrawingController()) // Use spyk to verify method calls
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).performClick()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).assertIsSelected()
        assertEquals(DrawingTool.ERASE, controller.currentTool)
        // Erase options (clear canvas button) should be visible in the pager
        composeTestRule.onNodeWithTag(DrawingBarTestTags.TOOL_OPTIONS_PAGER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).assertIsDisplayed()
    }

    private fun testDrawingToolTab(tabTag: String, tool: DrawingTool, controller: DrawingController) {
        composeTestRule.onNodeWithTag(tabTag).performClick()
        composeTestRule.onNodeWithTag(tabTag).assertIsSelected()
        assertEquals(tool, controller.currentTool)
        // Drawing options (color/width) should be visible
        composeTestRule.onNodeWithTag(DrawingBarTestTags.TOOL_OPTIONS_PAGER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_SELECTOR_LAYOUT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.WIDTH_SELECTOR_LAYOUT).assertIsDisplayed()
    }

    @Test
    fun drawingBar_penTab_canBeSelected_setsToolAndShowsOptions() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        val initialProps = controller.currentDrawingProperties
        testDrawingToolTab(DrawingBarTestTags.PEN_TAB, DrawingTool.DRAW, controller)
        assertNotEquals(initialProps.isPen, controller.currentDrawingProperties.isPen) // Pen properties should change
        assertEquals(true, controller.currentDrawingProperties.isPen)
    }

    @Test
    fun drawingBar_markerTab_canBeSelected_setsToolAndShowsOptions() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        testDrawingToolTab(DrawingBarTestTags.MARKER_TAB, DrawingTool.DRAW, controller)
        assertEquals(false, controller.currentDrawingProperties.isPen)
    }

    @Test
    fun drawingBar_crayonTab_canBeSelected_setsToolAndShowsOptions() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        testDrawingToolTab(DrawingBarTestTags.CRAYON_TAB, DrawingTool.DRAW, controller)
        assertEquals(false, controller.currentDrawingProperties.isPen)
    }

    @Test
    fun drawingBar_toolOptions_visibilityTogglesOnClickingSameDrawTab() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // Select Pen tab, options show
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.TOOL_OPTIONS_PAGER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertIsDisplayed()

        // Click Pen tab again, options should hide (isUp toggles)
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.TOOL_OPTIONS_PAGER).assertDoesNotExist() // This assumes isUp makes it disappear
        composeTestRule.onNodeWithTag(DrawingBarTestTags.COLOR_WIDTH_SECTION_ROOT).assertDoesNotExist()
    }

    @Test
    fun drawingBar_clearCanvasButton_callsControllerClear() {
        val controller = spyk(DrawingController())
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // Select Erase tab to show clear button
        composeTestRule.onNodeWithTag(DrawingBarTestTags.ERASE_TAB).performClick()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawingBarTestTags.CLEAR_CANVAS_BUTTON).performClick()
        verify { controller.clear() }
    }

    @Test
    fun drawingBar_colorSelection_updatesControllerProperties() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // Select Pen tab
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        val initialColorIndex = controller.currentDrawingProperties.colorIndex
        // Click the second color item (index 1)
        composeTestRule.onNodeWithTag("${DrawingBarTestTags.COLOR_SELECTOR_ITEM_PREFIX}_1").performClick()
        assertEquals(1, controller.currentDrawingProperties.colorIndex)
        assertNotEquals(initialColorIndex, controller.currentDrawingProperties.colorIndex)
    }

    @Test
    fun drawingBar_widthSelection_updatesControllerProperties() {
        val controller = DrawingController()
        composeTestRule.setContent {
            DrawingBar(controller = controller)
        }
        // Select Pen tab
        composeTestRule.onNodeWithTag(DrawingBarTestTags.PEN_TAB).performClick()
        val initialWidth = controller.currentDrawingProperties.lineWidth
        // Click the second width item (index 1)
        composeTestRule.onNodeWithTag("${DrawingBarTestTags.WIDTH_SELECTOR_ITEM_PREFIX}_1").performClick()
        // Widths are 4.dp, 8.dp, 12.dp, 16.dp for Pen. Index 1 should be 8.dp.
        // This requires knowing the exact dp values or making controller's widths public for verification
        // For simplicity, we check it's different. A more robust test would check the exact expected width.
        assertNotEquals(initialWidth, controller.currentDrawingProperties.lineWidth)
    }
}
