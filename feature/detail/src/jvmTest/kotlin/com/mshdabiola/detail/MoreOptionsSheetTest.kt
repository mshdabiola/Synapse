package com.mshdabiola.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.testtag.MoreOptionsSheetTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class MoreOptionsSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun moreOptionsSheet_whenShown_displaysAllExpectedItems() {
        composeTestRule.setContent {
            MoreOptionsSheet(
                currentColor = -1,
                currentImage = -1,
                isNoteCheck = false,
                saveImage = { },
                saveVoice = { _, _ -> },
                getPhotoUri = { "" },
                changeToCheckBoxes = { },
                onDrawing = { },
                onDismiss = { },
                show = true,
                isVoiceSupport = true,
            )
        }

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.TAKE_PHOTO).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.ADD_IMAGE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.DRAWING).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.RECORDING).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.CHECKBOXES).assertIsDisplayed()
    }

    @Test
    fun moreOptionsSheet_whenIsNoteCheckTrue_checkboxesItemIsNotDisplayed() {
        composeTestRule.setContent {
            MoreOptionsSheet(
                currentColor = -1,
                currentImage = -1,
                isNoteCheck = true, // Set to true
                saveImage = { },
                saveVoice = { _, _ -> },
                getPhotoUri = { "" },
                changeToCheckBoxes = { },
                onDrawing = { },
                onDismiss = { },
                show = true,
                isVoiceSupport = true,
            )
        }

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.CHECKBOXES).assertDoesNotExist()
    }

    @Test
    fun moreOptionsSheet_whenIsVoiceSupportFalse_recordingItemIsNotDisplayed() {
        composeTestRule.setContent {
            MoreOptionsSheet(
                currentColor = -1,
                currentImage = -1,
                isNoteCheck = false,
                saveImage = { },
                saveVoice = { _, _ -> },
                getPhotoUri = { "" },
                changeToCheckBoxes = { },
                onDrawing = { },
                onDismiss = { },
                show = true,
                isVoiceSupport = false, // Set to false
            )
        }

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.RECORDING).assertDoesNotExist()
    }

    @Test
    fun moreOptionsSheet_itemClicks_invokeCorrectCallbacks() {
        var takePhotoClicked = false
        var addImageClicked = false
        var drawingClicked = false
        var recordingClicked = false
        var checkboxesClicked = false
        var dismissed = false

        composeTestRule.setContent {
            MoreOptionsSheet(
                currentColor = -1,
                currentImage = -1,
                isNoteCheck = false,
                saveImage = { if (it == "photo_uri") takePhotoClicked = true else addImageClicked = true },
                saveVoice = { _, _ -> recordingClicked = true },
                getPhotoUri = { "photo_uri" },
                changeToCheckBoxes = { checkboxesClicked = true },
                onDrawing = { drawingClicked = true },
                onDismiss = { dismissed = true },
                show = true,
                isVoiceSupport = true,
            )
        }

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.TAKE_PHOTO).performClick()
        assert(takePhotoClicked)
        assert(dismissed) //TAKE_PHOTO dismisses the sheet
        dismissed = false // Reset for next check


        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.ADD_IMAGE).performClick()
        assert(addImageClicked)
        assert(dismissed) //ADD_IMAGE dismisses the sheet
        dismissed = false

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.DRAWING).performClick()
        assert(drawingClicked)
        assert(dismissed) //DRAWING dismisses the sheet
        dismissed = false

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.RECORDING).performClick()
        assert(recordingClicked)
        assert(dismissed) //RECORDING dismisses the sheet
        dismissed = false

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.CHECKBOXES).performClick()
        assert(checkboxesClicked)
        assert(dismissed) //CHECKBOXES dismisses the sheet
    }
}
