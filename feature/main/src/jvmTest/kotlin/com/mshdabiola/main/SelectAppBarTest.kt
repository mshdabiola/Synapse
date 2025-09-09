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
import com.mshdabiola.main.component.SelectAppBar
import com.mshdabiola.main.model.SelectState
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.testtag.SelectAppBarTestTags
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalMaterial3Api::class)
class SelectAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultNoteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE)
    private val archiveNoteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.ARCHIVE)

    private fun createSelectState(count: Int, isAllPin: Boolean = false) = SelectState(
        setOfSelected = List(count) { it.toLong() }.toSet(),
        isAllPin = isAllPin,
        colorIndex = -1,
        notificationUiState = null,
    )

    @Test
    fun selectAppBar_basicDisplayAndTitle_showsCorrectly() {
        val selectState = createSelectState(3)
        composeTestRule.setContent {
            SelectAppBar(
                noteDisplayCategory = defaultNoteDisplayCategory,
                selectState = selectState,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            )
        }

        composeTestRule.onNodeWithTag(SelectAppBarTestTags.ROOT_APP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.CLEAR_SELECTION_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithText("3").assertIsDisplayed() // Title is the count
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.PIN_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.NOTIFICATION_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.COLOR_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.LABEL_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.MORE_OPTIONS_BUTTON).assertIsDisplayed()
    }

    // Pin button icon state test would require checking contentDescription or specific icon tags
    // For now, we assume the button itself is sufficient to test its presence.

    @Test
    fun selectAppBar_mainButtonClickActions_areInvoked() {
        var clearClicked = false
        var pinClicked = false
        var notificationClicked = false
        var colorClicked = false
        var labelClicked = false
        val selectState = createSelectState(1)

        composeTestRule.setContent {
            SelectAppBar(
                noteDisplayCategory = defaultNoteDisplayCategory,
                selectState = selectState,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                onClearSelection = { clearClicked = true },
                onPinNotes = { pinClicked = true },
                onNotificationClick = { notificationClicked = true },
                onSelectColor = { colorClicked = true },
                onLabelNotes = { labelClicked = true },
            )
        }

        composeTestRule.onNodeWithTag(SelectAppBarTestTags.CLEAR_SELECTION_BUTTON).performClick()
        assertTrue(clearClicked)
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.PIN_BUTTON).performClick()
        assertTrue(pinClicked)
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.NOTIFICATION_BUTTON).performClick()
        assertTrue(notificationClicked)
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.COLOR_BUTTON).performClick()
        assertTrue(colorClicked)
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.LABEL_BUTTON).performClick()
        assertTrue(labelClicked)
    }

    @Test
    fun selectAppBar_dropdownMenu_noteCategory_archiveAndDelete() {
        var archiveClicked = false
        var deleteClicked = false
        val selectState = createSelectState(1)

        composeTestRule.setContent {
            SelectAppBar(
                noteDisplayCategory = defaultNoteDisplayCategory,
                selectState = selectState,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                onArchive = { archiveClicked = true },
                onDeleteNotes = { deleteClicked = true },
            )
        }
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.GENERAL_OPTIONS_DROPDOWN).assertIsDisplayed()
        // Assuming text for archive is "Archive" from string resource
        composeTestRule.onNodeWithTag(
            SelectAppBarTestTags.ARCHIVE_UNARCHIVE_MENU_ITEM,
        ).assertIsDisplayed().performClick()
        assertTrue(archiveClicked)
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.GENERAL_OPTIONS_DROPDOWN).assertDoesNotExist() // Menu closes

        composeTestRule.onNodeWithTag(SelectAppBarTestTags.MORE_OPTIONS_BUTTON).performClick() // Reopen
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.DELETE_MENU_ITEM).assertIsDisplayed().performClick()
        assertTrue(deleteClicked)
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.GENERAL_OPTIONS_DROPDOWN).assertDoesNotExist()
    }

    @Test
    fun selectAppBar_dropdownMenu_archiveCategory_unarchive() {
        var archiveClicked = false
        val selectState = createSelectState(1)

        composeTestRule.setContent {
            SelectAppBar(
                noteDisplayCategory = archiveNoteDisplayCategory, // Archive category
                selectState = selectState,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                onArchive = { archiveClicked = true },
            )
        }
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.MORE_OPTIONS_BUTTON).performClick()
        // Assuming text for unarchive is "Unarchive"
        composeTestRule.onNodeWithTag(
            SelectAppBarTestTags.ARCHIVE_UNARCHIVE_MENU_ITEM,
        ).assertIsDisplayed().performClick()
        assertTrue(archiveClicked)
    }

    @Test
    fun selectAppBar_dropdownMenu_singleItemSelected_showsCopyAndSend() {
        var copyClicked = false
        var sendClicked = false
        val selectState = createSelectState(1) // Single item

        composeTestRule.setContent {
            SelectAppBar(
                noteDisplayCategory = defaultNoteDisplayCategory,
                selectState = selectState,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                onCopyNote = { copyClicked = true },
                onShareNote = { sendClicked = true },
            )
        }
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.MAKE_COPY_MENU_ITEM).assertIsDisplayed().performClick()
        assertTrue(copyClicked)
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.GENERAL_OPTIONS_DROPDOWN).assertDoesNotExist() // Closes

        composeTestRule.onNodeWithTag(SelectAppBarTestTags.MORE_OPTIONS_BUTTON).performClick() // Reopen
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.SEND_MENU_ITEM).assertIsDisplayed().performClick()
        assertTrue(sendClicked)
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.GENERAL_OPTIONS_DROPDOWN).assertDoesNotExist()
    }

    @Test
    fun selectAppBar_dropdownMenu_multipleItemsSelected_hidesCopyAndSend() {
        val selectState = createSelectState(2) // Multiple items

        composeTestRule.setContent {
            SelectAppBar(
                noteDisplayCategory = defaultNoteDisplayCategory,
                selectState = selectState,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            )
        }
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.MAKE_COPY_MENU_ITEM).assertDoesNotExist()
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.SEND_MENU_ITEM).assertDoesNotExist()
    }
}
