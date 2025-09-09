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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.TrashAppBar
import com.mshdabiola.model.testtag.TrashAppBarTestTags
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalMaterial3Api::class)
class TrashAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun trashAppBar_basicDisplay_showsCorrectly() {
        composeTestRule.setContent {
            TrashAppBar(
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
            )
        }

        composeTestRule.onNodeWithTag(TrashAppBarTestTags.ROOT_APP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TrashAppBarTestTags.NAVIGATION_ICON).assertIsDisplayed()
        composeTestRule.onNodeWithText("Trash").assertIsDisplayed() // Title text
        composeTestRule.onNodeWithTag(TrashAppBarTestTags.MORE_OPTIONS_BUTTON).assertIsDisplayed()
    }

    @Test
    fun trashAppBar_navigationClick_invokesCallback() {
        var hamburgerClicked = false
        composeTestRule.setContent {
            TrashAppBar(
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onHamburgerMenuClick = { hamburgerClicked = true },
            )
        }

        composeTestRule.onNodeWithTag(TrashAppBarTestTags.NAVIGATION_ICON).performClick()
        assertTrue(hamburgerClicked, "onHamburgerMenuClick should be called")
    }

    @Test
    fun trashAppBar_dropdownMenu_emptyTrash_invokesCallbackAndClosesMenu() {
        var deleteAllTrashClicked = false
        composeTestRule.setContent {
            TrashAppBar(
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onDeleteAllTrash = { deleteAllTrashClicked = true },
            )
        }

        // Open dropdown
        composeTestRule.onNodeWithTag(TrashAppBarTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TrashAppBarTestTags.TRASH_EMPTY_OPTIONS_DROPDOWN).assertIsDisplayed()

        // Click empty trash
        composeTestRule.onNodeWithTag(TrashAppBarTestTags.EMPTY_TRASH_MENU_ITEM).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TrashAppBarTestTags.EMPTY_TRASH_MENU_ITEM).performClick()

        // Assert callback invoked and menu closes
        assertTrue(deleteAllTrashClicked, "onDeleteAllTrash should be called")
        composeTestRule.onNodeWithTag(TrashAppBarTestTags.TRASH_EMPTY_OPTIONS_DROPDOWN).assertDoesNotExist()
    }
}
