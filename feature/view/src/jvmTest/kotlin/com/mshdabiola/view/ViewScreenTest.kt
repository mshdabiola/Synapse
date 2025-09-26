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
package com.mshdabiola.view

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import com.mshdabiola.model.note.NoteImage
import com.mshdabiola.model.testtag.ViewScreenTestTags // Added import
import com.mshdabiola.ui.SharedTransitionContainer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalSharedTransitionApi::class)
class ViewScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var onBackCalled = false
    private var onToTextWithPath: String? = null
    private var onSendCalled = false
    private var onCopyCalled = false
    private var onDeleteCalled = false

    private val sampleImages = listOf(
        NoteImage(id = 1, path = "path/to/image1.jpg"),
        NoteImage(id = 2, path = "path/to/image2.png"),
        NoteImage(id = 3, path = "path/to/image3.jpeg"),
    )

    private fun setupGalleryScreen(
        images: List<NoteImage> = sampleImages,
        initialPage: Int = 0,
    ) {
        onBackCalled = false
        onToTextWithPath = null
        onSendCalled = false
        onCopyCalled = false
        onDeleteCalled = false

        composeTestRule.setContent {
            SharedTransitionContainer {
                val pagerState = rememberPagerState(
                    initialPage = initialPage,
                    pageCount = { images.size },
                )
                ViewScreen(
                    modifier = Modifier,
                    viewUiState = ViewUiState(images = images),
                    pagerState = pagerState,
                    onBack = { onBackCalled = true },
                    onToText = { path -> onToTextWithPath = path },
                    onSend = { onSendCalled = true },
                    onCopy = { onCopyCalled = true },
                    onDeleteImage = { onDeleteCalled = true },
                )
            }
        }
    }

    @Test
    fun initialElements_areDisplayed_firstPage() {
        setupGalleryScreen(initialPage = 0)
        composeTestRule.onNodeWithTag(ViewScreenTestTags.BACK_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.TITLE).assertTextEquals("1 of ${sampleImages.size}")
        composeTestRule.onNodeWithTag(ViewScreenTestTags.MORE_OPTIONS_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.PAGER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.image(0)).assertIsDisplayed()
    }

    @Test
    fun initialElements_areDisplayed_secondPage() {
        setupGalleryScreen(initialPage = 1)
        composeTestRule.onNodeWithTag(ViewScreenTestTags.TITLE).assertTextEquals("2 of ${sampleImages.size}")
        composeTestRule.onNodeWithTag(ViewScreenTestTags.image(1)).assertIsDisplayed()
    }

    @Test
    fun backButton_invokesCallback() {
        setupGalleryScreen()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.BACK_BUTTON).performClick()
        assertTrue(onBackCalled)
    }

    @Test
    fun moreOptionsButton_opensMenu_andItemsAreDisplayed() {
        setupGalleryScreen()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.GRAB_TEXT_MENU_ITEM).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.COPY_MENU_ITEM).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.SEND_MENU_ITEM).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.DELETE_MENU_ITEM).assertIsDisplayed()
    }

    @Test
    fun grabTextMenuItem_invokesCallback_withCorrectPath() {
        setupGalleryScreen(initialPage = 0)
        composeTestRule.onNodeWithTag(ViewScreenTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.GRAB_TEXT_MENU_ITEM).performClick()
        assertEquals(sampleImages[0].path, onToTextWithPath)
    }

    @Test
    fun copyMenuItem_invokesCallback() {
        setupGalleryScreen()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.COPY_MENU_ITEM).performClick()
        assertTrue(onCopyCalled)
    }

    @Test
    fun sendMenuItem_invokesCallback() {
        setupGalleryScreen()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.SEND_MENU_ITEM).performClick()
        assertTrue(onSendCalled)
    }

    @Test
    fun deleteMenuItem_invokesCallback() {
        setupGalleryScreen()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.DELETE_MENU_ITEM).performClick()
        assertTrue(onDeleteCalled)
    }

    @Test
    fun pagerSwipe_updatesTitleAndImage_andGrabTextPath() {
        setupGalleryScreen(images = sampleImages, initialPage = 0)
        composeTestRule.onNodeWithTag(ViewScreenTestTags.image(0)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.TITLE).assertTextEquals("1 of ${sampleImages.size}")

        composeTestRule.onNodeWithTag(ViewScreenTestTags.PAGER).performScrollToIndex(1)
        composeTestRule.mainClock.advanceTimeByFrame() // Allow recomposition

        composeTestRule.onNodeWithTag(ViewScreenTestTags.image(1)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.TITLE).assertTextEquals("2 of ${sampleImages.size}")

        // Verify grab text path after swipe
        composeTestRule.onNodeWithTag(ViewScreenTestTags.MORE_OPTIONS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(ViewScreenTestTags.GRAB_TEXT_MENU_ITEM).performClick()
        assertEquals(sampleImages[1].path, onToTextWithPath)
    }
}
