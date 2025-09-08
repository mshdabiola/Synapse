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
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.mshdabiola.model.note.Path as DrawingPath
import com.mshdabiola.model.note.NoteItem
import com.mshdabiola.model.note.NoteDrawing
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.note.NoteVoice
import com.mshdabiola.model.testtag.NoteCardTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

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
            NoteItem(id = 1, content = "Item 1", isCheck = false),
            NoteItem(id = 2, content = "Item 2", isCheck = true),
            NoteItem(id = 3, content = "Item 3", isCheck = false),
        ),
        isCheck = true,
    )
    private val voiceNote = NotePad(id = 4L, title = "Voice Note", voices = listOf(NoteVoice(id = 1, path = "voice_path.mp3")))
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
            SharedTransitionContainer {
                NoteCard(notePad = simpleTextNote)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.TITLE_TEXT, useUnmergedTree = true).assertTextEquals("Simple Title")
        composeTestRule.onNodeWithTag(NoteCardTestTags.DETAIL_TEXT, useUnmergedTree = true).assertTextEquals("This is simple detail content.")
        composeTestRule.onNodeWithTag(NoteCardTestTags.CONTENT_COLUMN, useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun noteCard_displaysDetailAsTitle_whenTitleIsEmpty() {
        composeTestRule.setContent {
            SharedTransitionContainer {
                NoteCard(notePad = detailOnlyNote)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.TITLE_TEXT, useUnmergedTree = true).assertTextEquals("This is detail only content.")
    }

    @Test
    fun noteCard_displaysChecklistItemsAndCount_forChecklistNote() {
        composeTestRule.setContent {
            SharedTransitionContainer {
                NoteCard(notePad = checklistNote)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.TITLE_TEXT, useUnmergedTree = true).assertTextEquals("Checklist Title")
        composeTestRule.onNodeWithTag("${NoteCardTestTags.CHECKLIST_ITEM_TEXT_PREFIX}_0", useUnmergedTree = true).assertTextEquals("Item 1")
        composeTestRule.onNodeWithTag("${NoteCardTestTags.CHECKLIST_ITEM_TEXT_PREFIX}_1", useUnmergedTree = true).assertTextEquals("Item 3")
        composeTestRule.onNodeWithTag(NoteCardTestTags.CHECKLIST_COUNT_TEXT, useUnmergedTree = true).assertIsDisplayed()
         composeTestRule.onNodeWithText("1 checked items", substring = true).assertIsDisplayed()
    }

    @Test
    fun noteCard_displaysVoiceIcon_whenVoiceNoteProvided() {
        composeTestRule.setContent {
            SharedTransitionContainer {
                NoteCard(notePad = voiceNote)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.VOICE_ICON, useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun noteCard_handlesClick() {
        var onClickCalled = false
        var clickedNotePad: NotePad? = null
        composeTestRule.setContent {
            SharedTransitionContainer {
                NoteCard(
                    notePad = simpleTextNote,
                    onCardClick = { note ->
                        onClickCalled = true
                        clickedNotePad = note
                    }
                )
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.ROOT_CARD).performClick()
        assertTrue("onCardClick should have been called", onClickCalled)
        assertNotNull("Clicked NotePad should not be null", clickedNotePad)
        assertEquals("Clicked NotePad should match the input NotePad", simpleTextNote, clickedNotePad)
    }

    @Test
    fun noteCard_handlesLongClick() {
        var onLongClickCalled = false
        var longClickedId: Long? = null
        composeTestRule.setContent {
            SharedTransitionContainer {
                NoteCard(
                    notePad = simpleTextNote,
                    onLongClick = { id ->
                        onLongClickCalled = true
                        longClickedId = id
                    }
                )
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.ROOT_CARD).performTouchInput {
            longClick()
        }
        assertTrue("onLongClick should have been called", onLongClickCalled)
        assertNotNull("Long-clicked ID should not be null", longClickedId)
        assertEquals("Long-clicked ID should match the NotePad ID", simpleTextNote.id, longClickedId)
    }

    @Test
    fun noteCard_displaysImage_whenImageNoteProvided() {
        composeTestRule.setContent {
            SharedTransitionContainer {
                NoteCard(notePad = imageNote)
            }
        }
        composeTestRule.onNodeWithTag("${NoteCardTestTags.ASYNC_IMAGE_PREFIX}_0_0", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun noteCard_displaysDrawing_whenDrawingNoteProvided() {
        composeTestRule.setContent {
            SharedTransitionContainer {
                NoteCard(notePad = drawingNote)
            }
        }
        composeTestRule.onNodeWithTag("${NoteCardTestTags.BOARD_VIEWER_PREFIX}_0_0", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun noteCard_displaysRootCard() {
        composeTestRule.setContent {
            SharedTransitionContainer {
                NoteCard(notePad = simpleTextNote)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.ROOT_CARD).assertIsDisplayed()
    }

    @Test
    fun noteCard_displaysBackgroundImage_whenBackgroundIndexIsValid() {
        composeTestRule.setContent {
            SharedTransitionContainer {
                NoteCard(notePad = noteWithBackgroundImage)
            }
        }
        composeTestRule.onNodeWithTag(NoteCardTestTags.BACKGROUND_IMAGE, useUnmergedTree = true).assertIsDisplayed()
    }
}
