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
package com.mshdabiola.model.testtag

object NoteCardTestTags {
    const val ROOT_CARD = "note_card:root"

    // Visual content (Images/Drawings)
    const val BACKGROUND_IMAGE = "note_card:background_image"
    const val IMAGE_ROW_PREFIX = "note_card:image_row" // Append index: e.g., image_row_0
    const val ASYNC_IMAGE_PREFIX = "note_card:async_image" // Append row_index and item_index: e.g., async_image_0_1
    const val BOARD_VIEWER_PREFIX = "note_card:board_viewer" // Append row_index and item_index: e.g., board_viewer_0_1

    // Textual Content
    const val CONTENT_COLUMN = "note_card:content_column"
    const val TITLE_TEXT = "note_card:title_text" // Primary text (title or detail)
    const val DETAIL_TEXT = "note_card:detail_text" // Secondary detail text

    // Checklist specific
    const val CHECKLIST_ITEM_ROW_PREFIX = "note_card:checklist_item_row" // Append index
    const val CHECKLIST_ITEM_ICON_PREFIX = "note_card:checklist_item_icon" // Append index
    const val CHECKLIST_ITEM_TEXT_PREFIX = "note_card:checklist_item_text" // Append index
    const val CHECKLIST_ELLIPSIS_TEXT = "note_card:checklist_ellipsis"
    const val CHECKLIST_COUNT_TEXT = "note_card:checklist_count"

    // Footer items (inside FlowLayout2)
    const val FOOTER_FLOW_LAYOUT = "note_card:footer_flow_layout"
    const val VOICE_ICON = "note_card:voice_icon"
    const val REMINDER_CARD = "note_card:reminder_card" // Only one ReminderCard expected
    const val LABEL_CARD_PREFIX = "note_card:label_card" // Append index or label name
}
