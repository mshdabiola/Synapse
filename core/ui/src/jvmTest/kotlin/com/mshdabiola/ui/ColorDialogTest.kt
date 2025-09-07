package com.mshdabiola.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.AppConstant
import com.mshdabiola.model.testtag.ColorDialogTestTags
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class ColorDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun colorDialog_isNotDisplayed_whenShowIsFalse() {
        composeTestRule.setContent {
            ColorDialog(show = false, onDismissRequest = {}, onColorClick = {})
        }
        composeTestRule.onNodeWithTag(ColorDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun colorDialog_isDisplayed_whenShowIsTrue() {
        composeTestRule.setContent {
            ColorDialog(show = true, onDismissRequest = {}, onColorClick = {})
        }
        composeTestRule.onNodeWithTag(ColorDialogTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ColorDialogTestTags.TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithText("Note Color").assertIsDisplayed() // Verify title text
        composeTestRule.onNodeWithTag(ColorDialogTestTags.COLOR_GRID).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ColorDialogTestTags.RESET_COLOR_ITEM).assertIsDisplayed()
        // Check at least the first color picker item if colors are available
        if (AppConstant.noteColors.isNotEmpty()) {
            composeTestRule.onNodeWithTag("${ColorDialogTestTags.COLOR_PICKER_ITEM}_0").assertIsDisplayed()
        }
    }

    @Test
    fun colorDialog_resetItem_showsDoneIcon_whenCurrentColorIsReset() {
        composeTestRule.setContent {
            ColorDialog(show = true, currentColor = -1, onDismissRequest = {}, onColorClick = {})
        }
        // When currentColor is -1, the icon within RESET_COLOR_ITEM should be SynIcons.Done
        // and have contentDescription "done"
        composeTestRule.onNodeWithTag(ColorDialogTestTags.RESET_COLOR_ITEM)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(ColorDialogTestTags.RESET_COLOR_ICON, useUnmergedTree = true)
            .assertContentDescriptionEquals("done")
    }

    @Test
    fun colorDialog_resetItem_showsResetIcon_whenCurrentColorIsNotReset() {
        // Assuming there's at least one color to select
        if (AppConstant.noteColors.isNotEmpty()) {
            composeTestRule.setContent {
                ColorDialog(show = true, currentColor = 0, onDismissRequest = {}, onColorClick = {})
            }
            // When another color is selected, the icon should be SynIcons.FormatColorReset
            // and have contentDescription "reset"
            composeTestRule.onNodeWithTag(ColorDialogTestTags.RESET_COLOR_ITEM)
                .assertIsDisplayed()
            composeTestRule.onNodeWithTag(ColorDialogTestTags.RESET_COLOR_ICON, useUnmergedTree = true)
                .assertContentDescriptionEquals("reset")
        }
    }

    @Test
    fun colorDialog_resetItem_click_invokesCallbacks() {
        val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)
        val onColorClickMock = mockk<(Int) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            ColorDialog(
                show = true,
                onDismissRequest = onDismissRequestMock,
                onColorClick = onColorClickMock
            )
        }
        composeTestRule.onNodeWithTag(ColorDialogTestTags.RESET_COLOR_ITEM).performClick()
        verify { onDismissRequestMock() }
        verify { onColorClickMock(-1) }
    }

    @Test
    fun colorDialog_colorPickerItem_showsDoneIcon_whenSelected() {
        if (AppConstant.noteColors.isNotEmpty()) {
            val selectedIndex = 0
            composeTestRule.setContent {
                ColorDialog(
                    show = true,
                    currentColor = selectedIndex,
                    onDismissRequest = {},
                    onColorClick = {}
                )
            }
            // The icon with COLOR_PICKER_ITEM_ICON tag is the SynIcons.Done icon
            composeTestRule.onNodeWithTag("${ColorDialogTestTags.COLOR_PICKER_ITEM}_$selectedIndex")
                .assertIsDisplayed()
            composeTestRule.onNode(hasContentDescription("done") and hasParent(hasTestTag("${ColorDialogTestTags.COLOR_PICKER_ITEM}_$selectedIndex"))).assertIsDisplayed()
        }
    }

    @Test
    fun colorDialog_colorPickerItem_noDoneIcon_whenNotSelected() {
        if (AppConstant.noteColors.size > 1) {
            val selectedIndex = 0
            val notSelectedIndex = 1
            composeTestRule.setContent {
                ColorDialog(
                    show = true,
                    currentColor = selectedIndex, // A different color is selected
                    onDismissRequest = {},
                    onColorClick = {}
                )
            }
            // Check that the COLOR_PICKER_ITEM_ICON (Done icon) is NOT a child of the unselected color item
           composeTestRule.onNode(hasContentDescription("done") and hasParent(hasTestTag("${ColorDialogTestTags.COLOR_PICKER_ITEM}_$notSelectedIndex"))).assertDoesNotExist()
        }
    }

    @Test
    fun colorDialog_colorPickerItem_click_invokesCallbacks() {
        if (AppConstant.noteColors.isNotEmpty()) {
            val clickIndex = 0
            val onDismissRequestMock = mockk<() -> Unit>(relaxed = true)
            val onColorClickMock = mockk<(Int) -> Unit>(relaxed = true)

            composeTestRule.setContent {
                ColorDialog(
                    show = true,
                    currentColor = -1, // Start with no color selected
                    onDismissRequest = onDismissRequestMock,
                    onColorClick = onColorClickMock
                )
            }
            composeTestRule.onNodeWithTag("${ColorDialogTestTags.COLOR_PICKER_ITEM}_$clickIndex").performClick()
            verify { onDismissRequestMock() }
            verify { onColorClickMock(clickIndex) }
        }
    }

    @Test
    fun colorDialog_dismisses_whenOnDismissRequestIsTriggered() {
        val onDismissMock = mockk<() -> Unit>(relaxed = true)
        val showState = mutableStateOf(true)

        composeTestRule.setContent {
            if (showState.value) {
                ColorDialog(
                    show = true,
                    onDismissRequest = {
                        onDismissMock()
                        showState.value = false // Simulate the action of dismissing
                    },
                    onColorClick = {}
                )
            }
        }

        // Manually trigger the state change that would occur if AlertDialog's onDismissRequest was called
        composeTestRule.runOnUiThread {
            // This simulates the external event or internal logic that leads to dismissal
            // In a real scenario, the AlertDialog itself would call the lambda provided to its onDismissRequest.
            // We are testing that our provided lambda (onDismissMock and state change) is working.
            showState.value = false
        }
        // Wait for recomposition and UI update
        composeTestRule.waitForIdle()

        verify { onDismissMock() } // Verify our callback was invoked
        composeTestRule.onNodeWithTag(ColorDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }
}
