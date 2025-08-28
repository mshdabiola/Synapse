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

object NewDialogTestTags {
    // NotificationDialogNew specific
    const val DIALOG_ROOT = "new_dialog:root"
    const val DIALOG_TITLE = "new_dialog:title"
    const val CONFIRM_BUTTON = "new_dialog:confirm_button" // Save
    const val DELETE_BUTTON = "new_dialog:delete_button"
    const val CANCEL_BUTTON = "new_dialog:cancel_button"

    // TextDropbox instances (use these as prefixes if needed, or apply directly)
    const val TIME_DROPBOX_ROOT = "new_dialog:time_dropbox_root"
    const val DATE_DROPBOX_ROOT = "new_dialog:date_dropbox_root"
    const val INTERVAL_DROPBOX_ROOT = "new_dialog:interval_dropbox_root"

    // Inside TextDropbox (can be made more specific by combining with above, or by context)
    // It might be better to tag the TextField specifically for time, date, interval.
    const val DROPBOX_TEXT_FIELD = "new_dialog:dropbox_text_field" // General for any TextField in a TextDropbox
    const val TIME_DROPBOX_TEXT_FIELD = "new_dialog:time_dropbox_text_field"
    const val DATE_DROPBOX_TEXT_FIELD = "new_dialog:date_dropbox_text_field"
    const val INTERVAL_DROPBOX_TEXT_FIELD = "new_dialog:interval_dropbox_text_field"

    const val DROPBOX_ERROR_TEXT = "new_dialog:dropbox_error_text" // For the "Time as past" error

    const val EXPOSED_DROPDOWN_MENU = "new_dialog:exposed_dropdown_menu" // The popup menu itself
    const val MENU_ITEM_PREFIX = "new_dialog:menu_item" // Prefix for DropdownMenuItem, e.g., menu_item_time_morning
}
