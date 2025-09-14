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
package com.mshdabiola.draw

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.note.Path
import com.mshdabiola.model.testtag.DrawScreenTestTags // Added import
import com.mshdabiola.ui.DrawingController
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DrawScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Callbacks verification
    private var onBackClicked = false
    private var onDeleteImageClicked = false
    private var onCopyClicked = false
    private var onSendClicked = false

    private lateinit var fakeDrawingController: FakeDrawingController

    private fun setupScreen(
        drawUiState: DrawUiState = DrawUiState(),
        drawingController: DrawingController = FakeDrawingController(),
    ) {
        onBackClicked = false
        onDeleteImageClicked = false
        onCopyClicked = false
        onSendClicked = false
        fakeDrawingController = drawingController as FakeDrawingController

        composeTestRule.setContent {
            DrawScreen(
                modifier = Modifier,
                onBack = { onBackClicked = true },
                controller = drawingController,
                drawUiState = drawUiState,
                onDeleteImage = { onDeleteImageClicked = true },
                onCopy = { onCopyClicked = true },
                onSend = { onSendClicked = true },
            )
        }
    }

    // Fake controller for testing
    class FakeDrawingController :
        DrawingController() {
        var undoCalled = false
        var redoCalled = false

        override fun undo() {
            undoCalled = true
            // Simulate state change for canUndo/canRedo if needed for complex tests
            canUndo = false
            canRedo = true
        }

        override fun redo() {
            redoCalled = true
            // Simulate state change
            canRedo = false
            canUndo = true
        }
    }

    @Test
    fun initialElements_areDisplayed() {
        setupScreen()
        composeTestRule.onNodeWithTag(DrawScreenTestTags.BACK_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawScreenTestTags.TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawScreenTestTags.UNDO_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawScreenTestTags.REDO_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawScreenTestTags.MORE_OPTIONS_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawScreenTestTags.DRAWING_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DrawScreenTestTags.BOARD).assertIsDisplayed()
    }

    @Test
    fun backButton_invokesCallback() {
        setupScreen()
        composeTestRule.onNodeWithTag(DrawScreenTestTags.BACK_BUTTON).performClick()
        assertTrue(onBackClicked)
    }

    @Test
    fun undoButton_whenCanUndo_invokesControllerUndo() {
        val controller = FakeDrawingController().apply { canUndo = true }
        setupScreen(drawingController = controller)

        composeTestRule.onNodeWithTag(DrawScreenTestTags.UNDO_BUTTON).assertIsEnabled().performClick()
        assertTrue(controller.undoCalled)
    }

    @Test
    fun undoButton_whenCannotUndo_isDisabled() {
        val controller = FakeDrawingController().apply { canUndo = false }
        setupScreen(drawingController = controller)
        composeTestRule.onNodeWithTag(DrawScreenTestTags.UNDO_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun redoButton_whenCanRedo_invokesControllerRedo() {
        val controller = FakeDrawingController().apply { canRedo = true }
        setupScreen(drawingController = controller)

        composeTestRule.onNodeWithTag(DrawScreenTestTags.REDO_BUTTON).assertIsEnabled().performClick()
        assertTrue(controller.redoCalled)
    }

    @Test
    fun redoButton_whenCannotRedo_isDisabled() {
        val controller = FakeDrawingController().apply { canRedo = false }
        setupScreen(drawingController = controller)
        composeTestRule.onNodeWithTag(DrawScreenTestTags.REDO_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun moreOptionsButton_whenDrawingsExist_isEnabledAndOpensMenu_thenMenuItemsWork() {
        setupScreen(
            drawUiState = DrawUiState(
                drawings = listOf(Path()),
            ),
        ) // Provide a NON-EMPTY list

        composeTestRule.onNodeWithTag(DrawScreenTestTags.MORE_OPTIONS_BUTTON)
            .assertIsEnabled() // Crucially, assert it's enabled now
            .performClick()

        // It's good practice to wait a bit for the menu to appear, especially if there are animations.
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithTag(DrawScreenTestTags.COPY_MENU_ITEM, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify menu items are displayed and clickable
        composeTestRule.onNodeWithTag(DrawScreenTestTags.COPY_MENU_ITEM, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertTrue(onCopyClicked)
        assertFalse(onSendClicked) // Ensure others weren't called
        assertFalse(onDeleteImageClicked)
        composeTestRule.waitForIdle() // Wait for menu to close

        // Re-open menu for next item
        composeTestRule.onNodeWithTag(DrawScreenTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithTag(DrawScreenTestTags.SEND_MENU_ITEM, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag(DrawScreenTestTags.SEND_MENU_ITEM, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertTrue(onSendClicked)
        composeTestRule.waitForIdle() // Wait for menu to close

        composeTestRule.onNodeWithTag(DrawScreenTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithTag(DrawScreenTestTags.DELETE_MENU_ITEM, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag(DrawScreenTestTags.DELETE_MENU_ITEM, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertTrue(onDeleteImageClicked)
    }

    @Test
    fun moreOptionsButton_whenNoDrawings_isDisabled() {
        setupScreen(drawUiState = DrawUiState(drawings = emptyList()))
        composeTestRule.onNodeWithTag(DrawScreenTestTags.MORE_OPTIONS_BUTTON).assertIsNotEnabled()
    }
}
