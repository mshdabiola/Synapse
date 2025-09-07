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
package com.mshdabiola.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performLongClick
import com.mshdabiola.model.note.DrawingPath
import com.mshdabiola.model.note.NoteCheck
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.NoteVoice
import com.mshdabiola.model.testtag.NoteCardTestTags
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalSharedTransitionApi::class)
class NoteCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val simpleTextNote = NotePad(id = 1L, title = "Simple Title", detail = "This is simple detail content.")
    private val detailOnlyNote = NotePad(id = 2L, title = "", detail = "This is detail only content.")
    private val checklistNote = NotePad(
        id = 3L,
        title = "Checklist Title",
        checks = listOf(
            NoteCheck(id = 1, content = "Item 1", isCheck = false),
            NoteCheck(id = 2, content = "Item 2", isCheck = true),
            NoteCheck(id = 3, content = "Item 3", isCheck = false),
        ),
        isCheck = true,
    )
    private val voiceNote = NotePad(id = 4L, title = "Voice Note", voices = listOf(NoteVoice(id = 1, path = "voice_path.mp3", name = "Recording 1")))
    private val imageNote = NotePad(
        id = 5L,
        title = "Image Note",
        images = listOf(NoteImage(id = 1, path = "image_path.jpg", noteId = 5L))
    )
    private val drawingNote = NotePad(
        id = 6L,
        title = "Drawing Note",
        drawings = listOf(NoteDrawing(id = 1, paths = listOf(DrawingPath()), noteId = 6L))
    )
    private val noteWithBackgroundImage = NotePad(id = 8L, title = "BG Image Note", background = 0) // Assumes 0 is a valid index

    @Test
    fun noteCard_displaysTitleAndContent_whenBothProvided() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                NoteCard(notePad = simpleTextNote, animatedVisibilityScope = this)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.TITLE_TEXT).assertTextEquals("Simple Title")
        composeTestRule.onNodeWithTag(NoteCardTestTags.DETAIL_TEXT).assertTextEquals("This is simple detail content.")
        composeTestRule.onNodeWithTag(NoteCardTestTags.CONTENT_COLUMN).assertIsDisplayed()
    }

    @Test
    fun noteCard_displaysDetailAsTitle_whenTitleIsEmpty() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                NoteCard(notePad = detailOnlyNote, animatedVisibilityScope = this)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.TITLE_TEXT).assertTextEquals("This is detail only content.")
    }

    @Test
    fun noteCard_displaysChecklistItemsAndCount_forChecklistNote() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                NoteCard(notePad = checklistNote, animatedVisibilityScope = this)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.TITLE_TEXT).assertTextEquals("Checklist Title")
        composeTestRule.onNodeWithTag("${NoteCardTestTags.CHECKLIST_ITEM_TEXT_PREFIX}_0").assertTextEquals("Item 1")
        composeTestRule.onNodeWithTag("${NoteCardTestTags.CHECKLIST_ITEM_TEXT_PREFIX}_1").assertTextEquals("Item 3")
        composeTestRule.onNodeWithTag(NoteCardTestTags.CHECKLIST_COUNT_TEXT).assertIsDisplayed()
        // More specific check for the count text if needed and resource access is set up:
        // composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.modules_designsystem_checked_items_value, 1)).assertIsDisplayed()
         composeTestRule.onNodeWithText("1 checked items", substring = true).assertIsDisplayed()
    }

    @Test
    fun noteCard_displaysVoiceIcon_whenVoiceNoteProvided() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                NoteCard(notePad = voiceNote, animatedVisibilityScope = this)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.VOICE_ICON).assertIsDisplayed()
    }

    @Test
    fun noteCard_handlesClick() {
        val onCardClickMock = mockk<(NotePad) -> Unit>(relaxed = true)
        composeTestRule.setContent {
            SharedTransitionLayout {
                NoteCard(notePad = simpleTextNote, onCardClick = onCardClickMock, animatedVisibilityScope = this)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.ROOT_CARD).performClick()
        verify(exactly = 1) { onCardClickMock(simpleTextNote) }
    }

    @Test
    fun noteCard_handlesLongClick() {
        val onLongClickMock = mockk<(Long) -> Unit>(relaxed = true)
        composeTestRule.setContent {
            SharedTransitionLayout {
                NoteCard(notePad = simpleTextNote, onLongClick = onLongClickMock, animatedVisibilityScope = this)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.ROOT_CARD).performLongClick()
        verify(exactly = 1) { onLongClickMock(simpleTextNote.id) }
    }

    @Test
    fun noteCard_displaysImage_whenImageNoteProvided() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                NoteCard(notePad = imageNote, animatedVisibilityScope = this)
            }
        }
        // Assumes one row, one item for simplicity based on imageNote definition
        composeTestRule.onNodeWithTag("${NoteCardTestTags.ASYNC_IMAGE_PREFIX}_0_0").assertIsDisplayed()
    }

    @Test
    fun noteCard_displaysDrawing_whenDrawingNoteProvided() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                NoteCard(notePad = drawingNote, animatedVisibilityScope = this)
            }
        }
        // Assumes one row, one item for simplicity
        composeTestRule.onNodeWithTag("${NoteCardTestTags.BOARD_VIEWER_PREFIX}_0_0").assertIsDisplayed()
    }

    @Test
    fun noteCard_displaysRootCard() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                NoteCard(notePad = simpleTextNote, animatedVisibilityScope = this)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.ROOT_CARD).assertIsDisplayed()
    }

    @Test
    fun noteCard_displaysBackgroundImage_whenBackgroundIndexIsValid() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                NoteCard(notePad = noteWithBackgroundImage, animatedVisibilityScope = this)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.BACKGROUND_IMAGE).assertIsDisplayed()
    }
}
