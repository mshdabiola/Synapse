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

@OptIn(ExperimentalMaterial3Api::class)
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
                },
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(MainAppBarTestTags.TOP_SEARCH_BAR_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(inputFieldTestTag).assertIsDisplayed()
        composeTestRule.onNodeWithText(inputText).assertIsDisplayed()
    }
}
