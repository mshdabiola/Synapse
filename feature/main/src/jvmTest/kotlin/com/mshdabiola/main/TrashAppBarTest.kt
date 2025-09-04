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
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
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
                onHamburgerMenuClick = { hamburgerClicked = true }
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
                onDeleteAllTrash = { deleteAllTrashClicked = true }
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
