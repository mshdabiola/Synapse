package com.mshdabiola.main

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.main.component.LabelBox
import com.mshdabiola.main.model.SearchSort
import com.mshdabiola.model.testtag.LabelBoxTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LabelBoxTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testTitle = "Test Labels"
    private val numPerRow = 3
//
//    @Test
//    fun labelBox_basicDisplay_noMoreButton() {
//        // Arrange
//        val items = listOf(
//            SearchSort.Label(name = "Label1", iconIndex = 0, id = 1),
//            SearchSort.Label(name = "Label2", iconIndex = 1,id = 2),
//        )
//
//        composeTestRule.setContent {
//            LabelBox(title = testTitle, list = items, numPerRow = numPerRow, onItemClick = {})
//        }
//
//        // Assert
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.FLOW_ROW_ROOT_PREFIX + testTitle).assertIsDisplayed()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.TITLE_ROW_PREFIX + testTitle).assertIsDisplayed()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.TITLE_TEXT_PREFIX + testTitle).assertIsDisplayed()
//        composeTestRule.onNodeWithText(testTitle).assertIsDisplayed()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.MORE_LESS_BUTTON_PREFIX + testTitle).assertDoesNotExist()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_LABEL_ITEM_PREFIX + "Label1_0").assertIsDisplayed()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_LABEL_ITEM_PREFIX + "Label2_1").assertIsDisplayed()
//    }
//
//    @Test
//    fun labelBox_moreButtonFunctionality() {
//        // Arrange
////        val items = List(5) { SearchSort.Label(name = "Label$it", iconIndex = it % SynIcons.entries.size) }
//                val items = List(5) { SearchSort.Label(name = "Label$it", iconIndex =1, id = it.toLong()) }
//
//        // String resources for More/Less might need to be handled carefully in test
//        // For now, assume they are simple strings "More" and "Less"
//
//        composeTestRule.setContent {
//            LabelBox(title = testTitle, list = items, numPerRow = numPerRow, onItemClick = {})
//        }
//
//        // Assert: Initially "More" button, limited items
//        val moreButton = composeTestRule.onNodeWithTag(LabelBoxTestTags.MORE_LESS_BUTTON_PREFIX + testTitle)
//        moreButton.assertIsDisplayed()
//        composeTestRule.onNodeWithText("More").assertIsDisplayed() // Check for "More" text
//        // Default maxLines = 2. Max (2 * numPerRow) items should be accessible before expanding.
//        // We'll check the first 3 (one row) and the 4th one (start of second row)
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_LABEL_ITEM_PREFIX + "Label0_0").assertIsDisplayed()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_LABEL_ITEM_PREFIX + "Label3_3").assertIsDisplayed()
//        // The 5th item (index 4) should not be displayed if it goes beyond 2 lines with 3 per row (total 6 visible slots)
//        // This needs careful checking based on FlowRow's layout logic. For simplicity, we check if last item is hidden.
//        // If numPerRow=3, maxLines=2, items.size=5. Item 4 (Label4) should be on the 2nd row.
//        // The logic is maxLines=2, if (showMore) Int.MAX_VALUE else 2. So it will show up to 2 * numPerRow items by default.
//        // So Label0, Label1, Label2 (row1), Label3, Label4 (row2) should be visible if numPerRow=3, items=5
//        // Let's make items = 7 to definitely hide some
////        val manyItems = List(7) { SearchSort.Label(name = "Label$it", iconIndex = it % SynIcons.entries.size) }
//        val manyItems = List(7) { SearchSort.Label(name = "Label$it", iconIndex = it,id = it.toLong()) }
//
//        composeTestRule.setContent {
//            LabelBox(title = testTitle, list = manyItems, numPerRow = numPerRow, onItemClick = {})
//        }
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_LABEL_ITEM_PREFIX + "Label6_6").assertIsNotDisplayed()
//
//        // Act: Click "More"
//        moreButton.performClick()
//
//        // Assert: "Less" button, all items displayed
//        composeTestRule.onNodeWithText("Less").assertIsDisplayed()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_LABEL_ITEM_PREFIX + "Label6_6").assertIsDisplayed()
//
//        // Act: Click "Less"
//        moreButton.performClick()
//
//        // Assert: "More" button, limited items
//        composeTestRule.onNodeWithText("More").assertIsDisplayed()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_LABEL_ITEM_PREFIX + "Label6_6").assertIsNotDisplayed()
//    }
//
//    @Test
//    fun labelBox_itemClick_invokesCallback() {
//        // Arrange
//        val labelItem = SearchSort.Label(name = "ClickLabel", iconIndex = 0, id = 1)
//        val typeItem = SearchSort.Type(index = 1) // typeNames requires resource access
//        val colorItem = SearchSort.Color(colorIndex = 2)
//        val items = listOf(labelItem, typeItem, colorItem)
//        var clickedItem: SearchSort? = null
//
//        composeTestRule.setContent {
//            LabelBox(title = testTitle, list = items, onItemClick = { clickedItem = it })
//        }
//
//        // Act & Assert: Label click
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_LABEL_ITEM_PREFIX + "ClickLabel_0").performClick()
//        assertEquals(labelItem, clickedItem)
//
//        // Act & Assert: Type click (TypeName comes from stringArrayResource, needs careful handling)
//        // Assuming index 1 typeName is "Type1" for test purposes or find via tag
//        // The tag is SEARCH_TYPE_ITEM_PREFIX + typeName + _index. TypeName is resolved at runtime.
//        // We'll target the Nth item of this type if names are tricky.
//        // For now, let's assume it's the second item overall in the list provided it's unique enough.
//        // A better way is to find all nodes with SEARCH_TYPE_ITEM_PREFIX and pick the first.
//        composeTestRule.onAllNodesWithTag(LabelBoxTestTags.SEARCH_TYPE_ITEM_PREFIX, useUnmergedTree = true)[0].performClick()
//        assertEquals(typeItem, clickedItem)
//
//        // Act & Assert: Color click
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_COLOR_ITEM_PREFIX + "2_2").performClick()
//        assertEquals(colorItem, clickedItem)
//    }
//
//    @Test
//    fun labelBox_differentSearchSortTypes_displayCorrectly() {
//        // Arrange
//        val items = listOf(
//            SearchSort.Label(name = "DisplayLabel", iconIndex = 0, id = 1),
//            SearchSort.Type(index = 1), // Assuming typeNames[1] exists
//            SearchSort.Color(colorIndex = 3),
//            SearchSort.Color(colorIndex = -1) // Reset color
//        )
//
//        composeTestRule.setContent {
//            LabelBox(title = testTitle, list = items, onItemClick = {})
//        }
//
//        // Assert
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_LABEL_ITEM_PREFIX + "DisplayLabel_0").assertIsDisplayed()
//        // composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_TYPE_ITEM_PREFIX + <resolved_type_name> + "_1").assertIsDisplayed()
//        // Similar to above, finding by resolved type name is tricky. Find by generic type prefix for now.
//        composeTestRule.onAllNodesWithTag(LabelBoxTestTags.SEARCH_TYPE_ITEM_PREFIX, useUnmergedTree = true)[0].assertIsDisplayed()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_COLOR_ITEM_PREFIX + "3_2").assertIsDisplayed()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_COLOR_ITEM_PREFIX + "-1_3").assertIsDisplayed()
//        composeTestRule.onNodeWithTag(LabelBoxTestTags.SEARCH_COLOR_ITEM_RESET_ICON_PREFIX + "3").assertIsDisplayed()
//    }
}
