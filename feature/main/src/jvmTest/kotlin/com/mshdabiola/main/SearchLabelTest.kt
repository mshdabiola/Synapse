package com.mshdabiola.main

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.main.component.SearchLabel
import com.mshdabiola.model.testtag.SearchLabelTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

class SearchLabelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testName = "TestSampleLabel"
    private val testIcon = SynIcons.Notification // Sample icon

    @Test
    fun searchLabel_displaysCorrectly_withGivenNameAndIcon() {
        // Arrange
        composeTestRule.setContent {
            SearchLabel(
                iconId = testIcon,
                name = testName
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(SearchLabelTestTags.COLUMN_PREFIX + testName).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchLabelTestTags.SURFACE_PREFIX + testName).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SearchLabelTestTags.ICON_PREFIX + testName).assertIsDisplayed()
        // Could add more specific icon assertion if ImageVector had a way to be identified in test
        composeTestRule.onNodeWithTag(SearchLabelTestTags.TEXT_PREFIX + testName).assertIsDisplayed()
        // Check the actual text content displayed for the name
        // composeTestRule.onNodeWithText(testName).assertIsDisplayed() // This might be redundant if TEXT_PREFIX + testName is specific enough
    }

//    @Test
//    fun searchLabel_isClickable_whenModifierIsApplied() {
//        // Arrange
//        var clicked = false
//        val clickableTestTag = "clickableSearchLabel"
//        composeTestRule.setContent {
//            SearchLabel(
//                modifier = Modifier.testTag(clickableTestTag).performClick { clicked = true }, // Making the root column clickable for test
//                iconId = testIcon,
//                name = testName
//            )
//        }
//
//        // Act: Perform click on the component's root (via the testTag applied to modifier)
//        // Note: The original SearchLabel is a Column. For it to be clickable,
//        // the clickable modifier must be applied to it, like in LabelBox.
//        // Here, we simulate that by applying it directly for test purposes.
//        // If SearchLabel itself is meant to be clickable, its own modifier should include it.
//        // This test assumes the modifier passed to SearchLabel could make it clickable.
//
//        // To test clickability, we'd typically click the specific node that has the clickable modifier.
//        // If SearchLabel itself isn't inherently clickable, this tests a passed-in clickable modifier.
//        // Let's assume the root Column has the clickable modifier.
//        composeTestRule.onNodeWithTag(SearchLabelTestTags.COLUMN_PREFIX + testName)
//            .performClick() // This will fail if the Column itself isn't clickable
//
//        // To make this test pass as intended (testing a passed-in clickable modifier):
//        // We'd need to modify SearchLabel to accept a Modifier and apply it, and then in the test,
//        // pass a Modifier.clickable().
//        // The current structure of SearchLabel doesn't make the Column itself clickable directly.
//
//        // Let's adjust the test to check if the root element can receive a click if made clickable.
//        // This is more about testing the test setup itself for clickability.
//
//        var wasClicked = false
//        composeTestRule.setContent {
//            SearchLabel(
//                name = testName,
//                iconId = testIcon,
//                modifier = Modifier.clickable { wasClicked = true } // Apply clickable to the SearchLabel itself
//            )
//        }
//
//        composeTestRule.onNodeWithTag(SearchLabelTestTags.COLUMN_PREFIX + testName).performClick()
//        assertTrue(wasClicked, "SearchLabel with a clickable modifier should respond to click.")
//    }
}
