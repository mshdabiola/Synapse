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
package com.mshdabiola.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.testtag.MoreOptionsSheetTestTags
import org.junit.Rule
import org.junit.Test

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
                saveImage = {
                    takePhotoClicked = true
                    addImageClicked = true
                },
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
        assert(dismissed) // TAKE_PHOTO dismisses the sheet
        dismissed = false // Reset for next check

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.ADD_IMAGE).performClick()
        assert(addImageClicked)
        assert(dismissed) // ADD_IMAGE dismisses the sheet
        dismissed = false

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.DRAWING).performClick()
        assert(drawingClicked)
        assert(dismissed) // DRAWING dismisses the sheet
        dismissed = false

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.RECORDING).performClick()
        assert(recordingClicked)
        assert(dismissed) // RECORDING dismisses the sheet
        dismissed = false

        composeTestRule.onNodeWithTag(MoreOptionsSheetTestTags.CHECKBOXES).performClick()
        assert(checkboxesClicked)
        assert(dismissed) // CHECKBOXES dismisses the sheet
    }
}
