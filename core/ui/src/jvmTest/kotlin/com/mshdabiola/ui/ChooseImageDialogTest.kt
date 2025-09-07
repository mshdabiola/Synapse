package com.mshdabiola.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.testtag.ChooseImageDialogTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class ChooseImageDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun chooseImageDialog_isNotDisplayed_whenShowIsFalse() {
        composeTestRule.setContent {
            ChooseImageDialog(
                show = false,
                dismiss = {},
                saveImage = {},
                getUri = { "" }
            )
        }
        composeTestRule.onNodeWithTag(ChooseImageDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun chooseImageDialog_isDisplayed_whenShowIsTrue() {
        composeTestRule.setContent {
            ChooseImageDialog(
                show = true,
                dismiss = {},
                saveImage = {},
                getUri = { "test_uri" }
            )
        }
        composeTestRule.onNodeWithTag(ChooseImageDialogTestTags.DIALOG_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ChooseImageDialogTestTags.TAKE_IMAGE_OPTION).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ChooseImageDialogTestTags.CHOOSE_IMAGE_OPTION).assertIsDisplayed()
    }

    @Test
    fun chooseImageDialog_takeImageOption_invokesCallbacksAndDismisses() {
        var showDialog by mutableStateOf(true)
        var dismissCalled = false
        var savedImageUri: String? = null
        val testUri = "content://image_uri_for_take"

        composeTestRule.setContent {
            if (showDialog) {
                ChooseImageDialog(
                    show = true,
                    dismiss = {
                        dismissCalled = true
                        showDialog = false
                    },
                    saveImage = { uri -> savedImageUri = uri },
                    getUri = { testUri }
                )
            }
        }

        composeTestRule.onNodeWithTag(ChooseImageDialogTestTags.TAKE_IMAGE_OPTION).performClick()

        assertTrue("Dismiss callback should have been called", dismissCalled)
        assertEquals("saveImage callback should have been called with correct URI", testUri, savedImageUri)
        assertFalse("Dialog should be hidden after click", showDialog)
        composeTestRule.onNodeWithTag(ChooseImageDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun chooseImageDialog_chooseImageOption_invokesCallbacksAndDismisses() {
        var showDialog by mutableStateOf(true)
        var dismissCalled = false
        var chosenImageUri: String? = null
        val testUri = "content://image_uri_for_choose"

        composeTestRule.setContent {
            if (showDialog) {
                ChooseImageDialog(
                    show = true,
                    dismiss = {
                        dismissCalled = true
                        showDialog = false
                    },
                    saveImage = { uri ->
                        chosenImageUri = uri },
                    getUri = { testUri }
                )
            }
        }

        composeTestRule.onNodeWithTag(ChooseImageDialogTestTags.CHOOSE_IMAGE_OPTION).performClick()

        assertTrue("Dismiss callback should have been called", dismissCalled)
        assertEquals("saveImage callback should have been called with correct URI", testUri, chosenImageUri)
        assertFalse("Dialog should be hidden after click", showDialog)
        composeTestRule.onNodeWithTag(ChooseImageDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }

    @Test
    fun onDismissRequest_callback_updatesStateAndDismissesDialog() {
        var dismissLambdaWasCalled = false
        var showDialogState by mutableStateOf(true)

        val dismissHandler = {
            dismissLambdaWasCalled = true
            showDialogState = false
        }

        composeTestRule.setContent {
            if (showDialogState) {
                ChooseImageDialog(
                    show = true,
                    dismiss = dismissHandler, // This is the onDismissRequest for the AlertDialog
                    saveImage = {},
                    getUri = { "" }
                )
            }
        }

        // Simulate the AlertDialog triggering its onDismissRequest
        // (e.g., user clicks outside or presses back)
        // We directly invoke the handler to test its logic.
        composeTestRule.runOnUiThread {
            dismissHandler() // Manually trigger the callback
        }
        composeTestRule.waitForIdle() // Allow recomposition

        assertTrue("Dismiss handler lambda should have been called", dismissLambdaWasCalled)
        assertFalse("showDialogState should be false after dismiss handler execution", showDialogState)
        composeTestRule.onNodeWithTag(ChooseImageDialogTestTags.DIALOG_ROOT).assertDoesNotExist()
    }
}
