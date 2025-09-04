package com.mshdabiola.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.main.component.SelectTrashAppBar
import com.mshdabiola.main.model.SelectState
import com.mshdabiola.model.testtag.SelectTrashAppBarTestTags
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalMaterial3Api::class)
class SelectTrashAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createSelectState(count: Int) = SelectState(
        setOfSelected = List(count) { it.toLong() }.toSet(),
        isAllPin = false,
        colorIndex = -1,
        notificationUiState = null
    )

    @Test
    fun selectTrashAppBar_basicDisplayAndTitle_showsCorrectly() {
        val selectCount = 2
        val selectState = createSelectState(selectCount)
        composeTestRule.setContent {
            SelectTrashAppBar(
                selectState = selectState,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            )
        }

        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.ROOT_APP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.CLEAR_SELECTION_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithText(selectCount.toString()).assertIsDisplayed() // Title is the count
        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.RESTORE_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.MORE_OPTIONS_BUTTON).assertIsDisplayed()
    }

    @Test
    fun selectTrashAppBar_mainButtonClickActions_areInvoked() {
        var clearClicked = false
        var restoreClicked = false
        val selectState = createSelectState(1)

        composeTestRule.setContent {
            SelectTrashAppBar(
                selectState = selectState,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                onClearSelection = { clearClicked = true },
                onRestore = { restoreClicked = true }
            )
        }

        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.CLEAR_SELECTION_BUTTON).performClick()
        assertTrue(clearClicked, "onClearSelection should be called")

        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.RESTORE_BUTTON).performClick()
        assertTrue(restoreClicked, "onRestore should be called")
    }

    @Test
    fun selectTrashAppBar_dropdownMenu_deleteForever_isInvokedAndClosesMenu() {
        var deleteForeverClicked = false
        val selectState = createSelectState(1)

        composeTestRule.setContent {
            SelectTrashAppBar(
                selectState = selectState,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                onDeleteForever = { deleteForeverClicked = true }
            )
        }

        // Open dropdown
        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.TRASH_OPTIONS_DROPDOWN).assertIsDisplayed()

        // Click delete forever
        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.DELETE_FOREVER_MENU_ITEM).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.DELETE_FOREVER_MENU_ITEM).performClick()

        // Assert callback invoked and menu closes
        assertTrue(deleteForeverClicked, "onDeleteForever should be called")
        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.TRASH_OPTIONS_DROPDOWN).assertDoesNotExist()
    }
}
