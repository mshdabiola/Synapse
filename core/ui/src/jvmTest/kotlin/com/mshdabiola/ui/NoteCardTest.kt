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
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.testtag.NoteCardTestTags
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalSharedTransitionApi::class)
class NoteCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testNote = NotePad(id = 1L, title = "Test Title", detail = "This is test content.")

    @Test
    fun noteCard_displaysTitleAndContent() {
        composeTestRule.setContent {
            SharedTransitionContainer {
                // Required by sharedBounds
                NoteCard(notePad = testNote, onCardClick = {_,_,_->})
            }
        }

    }

}
