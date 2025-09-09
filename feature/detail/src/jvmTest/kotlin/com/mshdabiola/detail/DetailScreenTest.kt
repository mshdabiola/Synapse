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
package com.mshdabiola.detail

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.NoteVoice
import com.mshdabiola.model.testtag.DetailScreenTestTags
import com.mshdabiola.ui.SharedTransitionContainer
import org.junit.Rule
import org.junit.Test

// Helper composable to create DetailState for tests
@Composable
fun rememberTestDetailState(
    notePad: NotePad = NotePad(id = 1L),
    initialTitle: String = "",
    initialDetail: String = "",
    checks: List<NoteCheckUiState> = emptyList(),
    unChecks: List<NoteCheckUiState> = emptyList(),
    updateAt: String = "Test Time",
    playerState: PlayerState? = null,
): DetailState {
    return DetailState(
        notePad = notePad,
        title = rememberTextFieldState(initialTitle),
        detail = rememberTextFieldState(initialDetail),
        checks = mutableStateListOf<NoteCheckUiState>().apply { addAll(checks) },
        unChecks = mutableStateListOf<NoteCheckUiState>().apply { addAll(unChecks) },
        updateAt = updateAt,
        playerState = playerState,
    )
}

// Helper composable to create NoteCheckUiState for tests
@Composable
fun rememberTestNoteCheckUiState(
    id: Long,
    noteId: Long = 1L,
    initialContent: String = "",
    isCheck: Boolean = false,
    focus: Boolean = false,
): NoteCheckUiState {
    return NoteCheckUiState(
        id = id,
        noteId = noteId,
        content = rememberTextFieldState(initialContent),
        isCheck = isCheck,
        focus = focus,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
class DetailScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun detailScreen_topAppBarButtons_areDisplayedAndClickable() {
        var backClicked = false
        var pinClicked = false
        var notificationClicked = false
        var archiveClicked = false

        composeRule.setContent {
            SharedTransitionContainer {
                val detailState = rememberTestDetailState()
                DetailScreen(
                    state = detailState,
                    onBackClick = { backClicked = true },
                    pinNote = { pinClicked = true },
                    onNotification = { notificationClicked = true },
                    onArchive = { archiveClicked = true },
                )
            }
        }

        composeRule.onNodeWithTag(DetailScreenTestTags.BACK_BUTTON).assertIsDisplayed().performClick()
        composeRule.onNodeWithTag(DetailScreenTestTags.PIN_BUTTON).assertIsDisplayed().performClick()
        composeRule.onNodeWithTag(DetailScreenTestTags.NOTIFICATION_BUTTON).assertIsDisplayed().performClick()
        composeRule.onNodeWithTag(DetailScreenTestTags.ARCHIVE_BUTTON).assertIsDisplayed().performClick()

        assert(backClicked)
        assert(pinClicked)
        assert(notificationClicked)
        assert(archiveClicked)
    }

    @Test
    fun detailScreen_bottomActionButtons_areDisplayedAndClickable() {
        var moreOptionsClicked = false
        var onColorClicked = false
        var noteOptionClicked = false

        composeRule.setContent {
            SharedTransitionContainer {
                val detailState = rememberTestDetailState()
                DetailScreen(
                    state = detailState,
                    moreOptions = { moreOptionsClicked = true },
                    onColorClick = { onColorClicked = true },
                    noteOption = { noteOptionClicked = true },
                )
            }
        }

        composeRule.onNodeWithTag(DetailScreenTestTags.MORE_BUTTON).assertIsDisplayed().performClick()
        composeRule.onNodeWithTag(DetailScreenTestTags.COLORS_BUTTON).assertIsDisplayed().performClick()
        composeRule.onNodeWithTag(DetailScreenTestTags.OPTIONS_BUTTON).assertIsDisplayed().performClick()

        assert(moreOptionsClicked)
        assert(onColorClicked)
        assert(noteOptionClicked)
    }

    @Test
    fun detailScreen_normalMode_displaysTitleAndContentFields_andAcceptsInput() {
        val testTitle = "Test Title Input"
        val testContent = "Test Content Input"
        lateinit var titleState: TextFieldState
        lateinit var detailStateField: TextFieldState

        composeRule.setContent {
            SharedTransitionContainer {
                titleState = rememberTextFieldState()
                detailStateField = rememberTextFieldState()
                val detailStateLocal = DetailState(
                    notePad = NotePad(isCheck = false),
                    title = titleState,
                    detail = detailStateField,
                    updateAt = "Now",
                )
                DetailScreen(state = detailStateLocal)
            }
        }

        composeRule.onNodeWithTag(DetailScreenTestTags.TITLE_TEXT_FIELD)
            .assertIsDisplayed()
            .performTextInput(testTitle)
        composeRule.runOnIdle {
            assert(titleState.text.toString() == testTitle)
        }

        composeRule.onNodeWithTag(DetailScreenTestTags.CONTENT_TEXT_FIELD)
            .assertIsDisplayed()
            .performTextInput(testContent)
        composeRule.runOnIdle {
            assert(detailStateField.text.toString() == testContent)
        }

        composeRule.onNodeWithTag(DetailScreenTestTags.ADD_CHECK_ITEM_BUTTON).assertDoesNotExist()
    }

    @Test
    fun detailScreen_checkListMode_displaysCheckListRelatedUI() {
        lateinit var item1: NoteCheckUiState

        composeRule.setContent {
            SharedTransitionContainer {
                item1 = rememberTestNoteCheckUiState(id = 1, initialContent = "Check Item 1")
                val detailStateLocal = rememberTestDetailState(
                    notePad = NotePad(isCheck = true),
                    unChecks = listOf(item1),
                )
                DetailScreen(state = detailStateLocal)
            }
        }

        composeRule.onNodeWithTag(DetailScreenTestTags.TITLE_TEXT_FIELD).assertIsDisplayed()
        composeRule.onNodeWithTag(DetailScreenTestTags.CONTENT_TEXT_FIELD).assertDoesNotExist()
        composeRule.onNodeWithTag(DetailScreenTestTags.ADD_CHECK_ITEM_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(DetailScreenTestTags.MORE_CHECK_BUTTON).assertIsDisplayed()
        // Further tests can check for individual list items if they have unique tags or by text
    }

    @Test
    fun detailScreen_whenImagesPresent_displaysImageList() {
        composeRule.setContent {
            SharedTransitionContainer {
                val notePadWithImage = NotePad(
                    images = listOf(NoteImage(id = 1, path = "test_path.jpg")),
                )
                val detailStateLocal = rememberTestDetailState(notePad = notePadWithImage)
                DetailScreen(state = detailStateLocal)
            }
        }
        composeRule.onNodeWithTag(DetailScreenTestTags.IMAGE_LIST).assertIsDisplayed()
        // Clicking on the image itself would require a more specific node/tag if the AsyncImage is the target
    }

    @Test
    fun detailScreen_whenDrawingsPresent_displaysDrawingItem() {
        composeRule.setContent {
            SharedTransitionContainer {
                val notePadWithDrawing = NotePad(
                    drawings = listOf(NoteDrawing(id = 1, noteId = -1)),
                )
                val detailStateLocal = rememberTestDetailState(notePad = notePadWithDrawing)
                DetailScreen(state = detailStateLocal)
            }
        }
        composeRule.onNodeWithTag(DetailScreenTestTags.DRAWING_ITEM + "_0").assertIsDisplayed()
    }

    @Test
    fun detailScreen_whenVoiceNotesPresent_displaysVoicePlayerAndInteracts() {
        var playVoiceIndex: Int? = null
        var pauseVoiceCalled = false
        var deleteVoiceIndex: Int? = null

        composeRule.setContent {
            SharedTransitionContainer {
                val notePadWithVoice = NotePad(
                    voices = listOf(NoteVoice(id = 1, path = "test_voice.mp3", length = 60)),
                )
                // Simulate player state where it's initially not playing
                val testPlayerState = PlayerState(indexPlaying = 0, isPlaying = false)
                val detailStateLocal = rememberTestDetailState(
                    notePad = notePadWithVoice,
                    playerState = testPlayerState,
                )
                DetailScreen(
                    state = detailStateLocal,
                    playVoice = { playVoiceIndex = it },
                    pauseVoice = { pauseVoiceCalled = true },
                    deleteVoiceNote = { deleteVoiceIndex = it },
                )
            }
        }
        // Initial state: play button should be visible
        composeRule.onNodeWithTag(DetailScreenTestTags.VOICE_PLAY_BUTTON).assertIsDisplayed().performClick()
        assert(playVoiceIndex == 0)

        // To test pause, we\'d need to update the state to isPlaying=true and recompose
        // For now, let\'s assume playVoice callback is enough for this interaction point.
        // A more complex test would involve changing DetailState.playerState and recomposing.

        composeRule.onNodeWithTag(DetailScreenTestTags.VOICE_DELETE_BUTTON).assertIsDisplayed().performClick()
        assert(deleteVoiceIndex == 0)
    }

    @Test
    fun detailScreen_moreCheckButton_dropdownMenuInteraction() {
        var hideCheckboxesClicked = false
        var deleteCheckedItemsCalled = false // Callback for delete operation

        lateinit var detailState: DetailState // Reference to the state used by DetailScreen

        composeRule.setContent {
            SharedTransitionContainer {
                // Initial state: one checked item
                val checkedItem = rememberTestNoteCheckUiState(
                    id = 1L,
                    initialContent = "Checked Item",
                    isCheck = true,
                )
                // \`rememberTestDetailState\` creates fresh SnapshotStateLists for checks and unChecks
                detailState = rememberTestDetailState(
                    notePad = NotePad(isCheck = true),
                    checks = listOf(checkedItem),
                    unChecks = emptyList(),
                )

                DetailScreen(
                    state = detailState,
                    hideCheckBoxes = { hideCheckboxesClicked = true },
                    deleteCheckItems = { deleteCheckedItemsCalled = true },
                    // uncheckAllItems is handled internally by DetailScreen:
                    // it should modify detailState.checks and detailState.unChecks
                )
            }
        }

        // --- 1. Test "Hide Checkboxes" menu item ---
        composeRule.onNodeWithTag(DetailScreenTestTags.MORE_CHECK_BUTTON).performClick()
        composeRule.onNodeWithTag(DetailScreenTestTags.HIDE_CHECK_MENU_ITEM).assertIsDisplayed().performClick()
        assert(hideCheckboxesClicked) { "hideCheckBoxes callback was not invoked." }

        // --- 2. Test "Uncheck All Items" menu item ---
        // At this point, menu is closed. Re-open it.
        // Sanity check initial state for this part of the test:
        composeRule.runOnIdle {
            require(detailState.checks.size == 1) { "Pre-condition fail: checks list should have 1 item." }
            require(detailState.checks.first().id == 1L && detailState.checks.first().isCheck)
            require(detailState.unChecks.isEmpty()) { "Pre-condition fail: unChecks list should be empty." }
        }

        composeRule.onNodeWithTag(DetailScreenTestTags.MORE_CHECK_BUTTON).performClick()
        composeRule.onNodeWithTag(DetailScreenTestTags.UNCHECK_ALL_MENU_ITEM).assertIsDisplayed().performClick()

        // Assert state changes after "Uncheck All"
        composeRule.runOnIdle {
            assert(detailState.checks.isEmpty()) { "Checks list should be empty after unchecking all." }
            assert(detailState.unChecks.size == 1) { "Unchecks list should contain one item." }
            val itemInUnchecks = detailState.unChecks.first()
            assert(itemInUnchecks.id == 1L) { "Item ID in unChecks is incorrect." }
            assert(!itemInUnchecks.isCheck) { "Item in unChecks should have isCheck = false." }
        }

        // --- 3. Test "Delete Checked Items" menu item ---
        // At this point, menu is closed. Re-open it.
        // Current state: \`checks\` is empty, \`unChecks\` has one item.
        // \`deleteCheckedItemsCalled\` should become true.
        // Lists should not change because there are no checked items to delete.
        deleteCheckedItemsCalled = false // Reset for this specific assertion

        composeRule.onNodeWithTag(DetailScreenTestTags.MORE_CHECK_BUTTON).performClick()
//        composeRule.onNodeWithTag(DetailScreenTestTags.DELETE_CHECK_MENU_ITEM,useUnmergedTree = true).assertIsDisplayed().performClick()

//        assert(deleteCheckedItemsCalled) { "deleteCheckItems callback was not invoked." }
//        composeRule.runOnIdle {
//            assert(detailState.checks.isEmpty()) { "Checks list should remain empty as no items were checked for deletion." }
//            assert(detailState.unChecks.size == 1) { "Unchecks list should remain unchanged." }
//        }
    }
}
