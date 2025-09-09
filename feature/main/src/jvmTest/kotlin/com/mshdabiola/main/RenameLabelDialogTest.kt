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

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.mshdabiola.main.component.RenameLabelAlertDialog
import com.mshdabiola.model.testtag.RenameLabelDialogTestTags
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RenameLabelDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val initialLabel = "Initial Label"
    private val newLabelText = "Updated Label"

    @Test
    fun renameLabelDialog_whenShowIsFalse_isHidden() {
        composeTestRule.setContent {
            RenameLabelAlertDialog(show = false, label = initialLabel)
        }
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun renameLabelDialog_whenShowIsTrue_displaysCorrectlyWithInitialLabel() {
        composeTestRule.setContent {
            RenameLabelAlertDialog(show = true, label = initialLabel)
        }
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.TITLE_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(initialLabel).assertIsDisplayed() // Checks TextField initial value
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.NAME_TEXT_FIELD).assertIsDisplayed()
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.CONFIRM_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.DISMISS_BUTTON).assertIsDisplayed()
    }

    @Test
    fun renameLabelDialog_textFieldInteraction_updatesText() {
        composeTestRule.setContent {
            RenameLabelAlertDialog(show = true, label = initialLabel)
        }
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.NAME_TEXT_FIELD)
            .performTextClearance()
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.NAME_TEXT_FIELD)
            .performTextInput(newLabelText)
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.NAME_TEXT_FIELD)
            .assertTextContains(newLabelText)
    }

    @Test
    fun renameLabelDialog_confirmButton_invokesCallbacksWithNewName() {
        var changedName: String? = null
        var dismissed = false
        val showDialog = mutableStateOf(true)

        composeTestRule.setContent {
            RenameLabelAlertDialog(
                show = showDialog.value,
                label = initialLabel,
                onDismissRequest = {
                    dismissed = true
                    showDialog.value = false
                },
                onChangeName = { changedName = it },
            )
        }

        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.NAME_TEXT_FIELD)
            .performTextClearance()
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.NAME_TEXT_FIELD).performTextInput(newLabelText)
        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.CONFIRM_BUTTON).performClick()

        assertTrue(dismissed, "onDismissRequest should be called")
        assertEquals(newLabelText, changedName, "onChangeName should be called with the new label text")
        assertFalse(showDialog.value, "Dialog should be hidden")
    }

    @Test
    fun renameLabelDialog_dismissButton_invokesDismissCallbackOnly() {
        var onChangeNameCalled = false
        var dismissed = false
        val showDialog = mutableStateOf(true)

        composeTestRule.setContent {
            RenameLabelAlertDialog(
                show = showDialog.value,
                label = initialLabel,
                onDismissRequest = {
                    dismissed = true
                    showDialog.value = false
                },
                onChangeName = { onChangeNameCalled = true },
            )
        }

        composeTestRule.onNodeWithTag(RenameLabelDialogTestTags.DISMISS_BUTTON).performClick()

        assertTrue(dismissed, "onDismissRequest should be called")
        assertFalse(onChangeNameCalled, "onChangeName should not be called")
        assertFalse(showDialog.value, "Dialog should be hidden")
    }
}
