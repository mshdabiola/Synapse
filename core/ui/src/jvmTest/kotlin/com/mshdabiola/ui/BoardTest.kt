package com.mshdabiola.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.mshdabiola.model.testtag.BoardTestTags
import org.junit.Rule
import org.junit.Test

class BoardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun board_displaysRootAndCanvas() {
        composeTestRule.setContent {
            Board()
        }

        composeTestRule.onNodeWithTag(BoardTestTags.SCREEN_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(BoardTestTags.CANVAS).assertIsDisplayed()
    }
}
