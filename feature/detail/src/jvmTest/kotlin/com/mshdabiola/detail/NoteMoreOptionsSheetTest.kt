package com.mshdabiola.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.testtag.NoteMoreOptionsSheetTestTags
import org.junit.Rule
import org.junit.Test

class NoteMoreOptionsSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun noteMoreOptionsSheet_whenShown_displaysAllExpectedItems() {
        composeTestRule.setContent {
            NoteOptionsMenu(
                show = true,
                currentColor = -1,
                currentImage = -1,
            )
        }

        composeTestRule.onNodeWithTag(NoteMoreOptionsSheetTestTags.DELETE_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NoteMoreOptionsSheetTestTags.COPY_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NoteMoreOptionsSheetTestTags.SEND_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NoteMoreOptionsSheetTestTags.LABEL_BUTTON).assertIsDisplayed()
    }

    @Test
    fun noteMoreOptionsSheet_itemClicks_invokeCorrectCallbacksAndDismiss() {
        var deleteClicked = false
        var copyClicked = false
        var sendClicked = false
        var labelClicked = false
        var dismissed = false

        composeTestRule.setContent {
            NoteOptionsMenu(
                show = true,
                currentColor = -1,
                currentImage = -1,
                onDelete = { deleteClicked = true },
                onCopy = { copyClicked = true },
                onSendNote = { sendClicked = true },
                onLabel = { labelClicked = true },
                onDismissRequest = { dismissed = true },
            )
        }

        composeTestRule.onNodeWithTag(NoteMoreOptionsSheetTestTags.DELETE_BUTTON).performClick()
        assert(deleteClicked)
        assert(dismissed)
        dismissed = false // Reset for next check

        composeTestRule.onNodeWithTag(NoteMoreOptionsSheetTestTags.COPY_BUTTON).performClick()
        assert(copyClicked)
        assert(dismissed)
        dismissed = false

        composeTestRule.onNodeWithTag(NoteMoreOptionsSheetTestTags.SEND_BUTTON).performClick()
        assert(sendClicked)
        assert(dismissed)
        dismissed = false

        composeTestRule.onNodeWithTag(NoteMoreOptionsSheetTestTags.LABEL_BUTTON).performClick()
        assert(labelClicked)
        assert(dismissed)
    }

    @Test
    fun noteMoreOptionsSheet_whenNotShown_doesNotExist() {
        composeTestRule.setContent {
            NoteOptionsMenu(
                show = false, // Set to false
                currentColor = -1,
                currentImage = -1,
            )
        }

        composeTestRule.onNodeWithTag(NoteMoreOptionsSheetTestTags.DELETE_BUTTON).assertDoesNotExist()
    }
}
