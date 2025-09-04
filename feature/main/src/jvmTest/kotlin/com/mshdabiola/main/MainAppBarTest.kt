package com.mshdabiola.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.mshdabiola.main.component.MainAppBar
import com.mshdabiola.model.testtag.MainAppBarTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(RobolectricTestRunner::class)
class MainAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainAppBar_displaysCorrectly_withInputField() {
        // Arrange
        val inputFieldTestTag = "inputFieldContent"
        val inputText = "Test Input Field"

        composeTestRule.setContent {
            val searchBarState = rememberSearchBarState()
            MainAppBar(
                searchBarState = searchBarState,
                inputField = {
                    Text(text = inputText, modifier = Modifier.testTag(inputFieldTestTag))
                }
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(MainAppBarTestTags.TOP_SEARCH_BAR_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(inputFieldTestTag).assertIsDisplayed()
        composeTestRule.onNodeWithText(inputText).assertIsDisplayed()
    }
}
