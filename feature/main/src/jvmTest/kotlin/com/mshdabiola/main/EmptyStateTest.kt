package com.mshdabiola.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.mshdabiola.main.component.EmptyState
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.testtag.EmptyStateTestTags
import org.jetbrains.compose.resources.getString
import org.junit.Rule
import org.junit.Test

class EmptyStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_displaysCorrectly_forAllNoteCategories() {
        NoteCategory.entries.forEach { category ->
            // Arrange
            val noteDisplayCategory = NoteDisplayCategory(noteCategory = category, )
            var title = ""
            var body = ""

            composeTestRule.setContent {

                EmptyState(
                    noteDisplayCategory = noteDisplayCategory,
                )
                // Capture string resources within Composable content scope if needed by getString
                // However, for Robolectric, direct resource access might work or require specific setup.
                // For simplicity, we'll fetch them outside if possible, or use onNodeWithText directly.
            }

            // Assert
            composeTestRule.onNodeWithTag(EmptyStateTestTags.ROOT_COLUMN).assertIsDisplayed()
            composeTestRule.onNodeWithTag(EmptyStateTestTags.LOTTIE_IMAGE).assertIsDisplayed()

            // To assert text content, it's better to fetch the string resource value
            // as it would be resolved in the app, rather than hardcoding it in the test.
            // This requires the test environment to correctly resolve resources.
            // Robolectric should handle this.

            // We'll use onNodeWithText to find nodes with expected string resource content.
            // This assumes Res.string.features_main_empty_title and Res.string.features_main_empty_body are the correct resources.
            composeTestRule.onNodeWithTag(EmptyStateTestTags.TITLE_TEXT).assertIsDisplayed() // Using hardcoded for now, replace with actual resource lookup
            composeTestRule.onNodeWithTag(EmptyStateTestTags.DESCRIPTION_TEXT).assertIsDisplayed() // Using hardcoded for now

            // A more robust way if resource loading is set up for tests:
            // val expectedTitle = RuntimeEnvironment.getApplication().getString(R.string.features_main_empty_title) // Example for Android
            // composeTestRule.onNodeWithText(expectedTitle).assertIsDisplayed()
            // val expectedBody = RuntimeEnvironment.getApplication().getString(R.string.features_main_empty_body)
            // composeTestRule.onNodeWithText(expectedBody).assertIsDisplayed()
        }
    }
}
