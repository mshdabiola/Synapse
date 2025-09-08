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
import org.junit.runner.RunWith

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
