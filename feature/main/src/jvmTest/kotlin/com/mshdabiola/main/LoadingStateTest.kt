package com.mshdabiola.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.mshdabiola.main.component.LoadingState
import com.mshdabiola.model.testtag.LoadingStateTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class LoadingStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingState_displaysCorrectly() {
        // Arrange
        composeTestRule.setContent {
            LoadingState()
        }

        // Assert
        composeTestRule.onNodeWithTag(LoadingStateTestTags.LOADING_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LoadingStateTestTags.LOADING_ANIMATION_IMAGE).assertIsDisplayed()
    }
}
