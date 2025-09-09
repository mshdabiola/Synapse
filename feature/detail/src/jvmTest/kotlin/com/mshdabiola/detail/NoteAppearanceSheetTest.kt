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
import com.mshdabiola.model.AppConstant
import com.mshdabiola.model.NoteBg
import com.mshdabiola.model.testtag.NoteAppearanceSheetTestTags
import org.junit.Rule
import org.junit.Test

class NoteAppearanceSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun noteAppearanceSheet_whenShown_displaysAllExpectedItems() {
        composeTestRule.setContent {
            NoteAppearanceSheet(
                currentColor = -1,
                currentImage = -1,
                show = true,
            )
        }

        composeTestRule.onNodeWithTag(NoteAppearanceSheetTestTags.RESET_COLOR_BUTTON).assertIsDisplayed()
        AppConstant.noteColors.take(10).forEachIndexed { index, _ ->
            composeTestRule.onNodeWithTag(NoteAppearanceSheetTestTags.colorItem(index)).assertIsDisplayed()
        }
        composeTestRule.onNodeWithTag(NoteAppearanceSheetTestTags.RESET_IMAGE_BUTTON).assertIsDisplayed()
        NoteBg.noteBgs.take(8).forEachIndexed { index, _ ->
            composeTestRule.onNodeWithTag(NoteAppearanceSheetTestTags.imageItem(index)).assertIsDisplayed()
        }
    }

    @Test
    fun noteAppearanceSheet_colorItemClick_invokesCorrectCallback() {
        var clickedColorIndex: Int? = null
        composeTestRule.setContent {
            NoteAppearanceSheet(
                currentColor = -1,
                currentImage = -1,
                onColorClick = { clickedColorIndex = it },
                show = true,
            )
        }

        composeTestRule.onNodeWithTag(NoteAppearanceSheetTestTags.colorItem(0)).performClick()
        assert(clickedColorIndex == 0)

        composeTestRule.onNodeWithTag(NoteAppearanceSheetTestTags.RESET_COLOR_BUTTON).performClick()
        assert(clickedColorIndex == -1)
    }

    @Test
    fun noteAppearanceSheet_imageItemClick_invokesCorrectCallback() {
        var clickedImageIndex: Int? = null
        composeTestRule.setContent {
            NoteAppearanceSheet(
                currentColor = -1,
                currentImage = -1,
                onImageClick = { clickedImageIndex = it },
                show = true,
            )
        }

        composeTestRule.onNodeWithTag(NoteAppearanceSheetTestTags.imageItem(0)).performClick()
        assert(clickedImageIndex == 0)

        composeTestRule.onNodeWithTag(NoteAppearanceSheetTestTags.RESET_IMAGE_BUTTON).performClick()
        assert(clickedImageIndex == -1)
    }

    @Test
    fun noteAppearanceSheet_whenNotShown_doesNotExist() {
        composeTestRule.setContent {
            NoteAppearanceSheet(
                currentColor = -1,
                currentImage = -1,
                show = false, // Set to false
            )
        }

        composeTestRule.onNodeWithTag(NoteAppearanceSheetTestTags.RESET_COLOR_BUTTON).assertDoesNotExist()
    }
}
