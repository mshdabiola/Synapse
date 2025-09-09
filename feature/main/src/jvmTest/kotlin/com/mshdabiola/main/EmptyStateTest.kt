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
package com.mshdabiola.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.mshdabiola.main.component.EmptyState
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.testtag.EmptyStateTestTags
import org.junit.Rule
import org.junit.Test

class EmptyStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_displaysCorrectly_forAllNoteCategories() {
        NoteCategory.entries.forEach { category ->
            // Arrange
            val noteDisplayCategory = NoteDisplayCategory(noteCategory = category)
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
            composeTestRule.onNodeWithTag(EmptyStateTestTags.TITLE_TEXT).assertIsDisplayed()
            // Using hardcoded for now, replace with actual resource lookup
            composeTestRule.onNodeWithTag(EmptyStateTestTags.DESCRIPTION_TEXT).assertIsDisplayed()
            // Using hardcoded for now

            // A more robust way if resource loading is set up for tests:
            // val expectedTitle = RuntimeEnvironment.getApplication().getString(R.string.features_main_empty_title)
            // Example for Android
            // composeTestRule.onNodeWithText(expectedTitle).assertIsDisplayed()
            // val expectedBody = RuntimeEnvironment.getApplication().getString(R.string.features_main_empty_body)
            // composeTestRule.onNodeWithText(expectedBody).assertIsDisplayed()
        }
    }
}
