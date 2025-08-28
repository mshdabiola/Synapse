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

object NotificationDialogIntervalTestTags {
    const val DIALOG_ROOT = "notification_interval_dialog:root"

    // Main Interval Type Dropdown
    const val INTERVAL_TYPE_DROPDOWN_ROOT = "notification_interval_dialog:interval_type_dropdown_root"
    const val INTERVAL_TYPE_TEXT_FIELD = "notification_interval_dialog:interval_type_text_field"
    const val INTERVAL_TYPE_MENU = "notification_interval_dialog:interval_type_menu"
    const val INTERVAL_TYPE_MENU_ITEM_PREFIX = "notification_interval_dialog:interval_type_menu_item" // e.g., _daily

    // IntervalTextField instances (used for Daily, Weekly, Monthly, Yearly interval inputs)
    const val INTERVAL_TF_ROOT_PREFIX = "notification_interval_dialog:interval_tf_root" // Prefix for root of IntervalTextField instance
    const val INTERVAL_TF_TEXT_FIELD_PREFIX = "notification_interval_dialog:interval_tf_textfield" // Prefix for TextField inside IntervalTextField

    // Weekly Specific
    const val WEEKLY_DAYS_FLOW_ROW = "notification_interval_dialog:weekly_days_flow_row"
    const val WEEKLY_DAY_INPUT_CHIP_PREFIX = "notification_interval_dialog:weekly_day_chip" // e.g., _monday

    // Monthly Specific
    const val MONTHLY_SAME_DAY_ROW = "notification_interval_dialog:monthly_same_day_row"
    const val MONTHLY_SAME_DAY_RADIO = "notification_interval_dialog:monthly_same_day_radio"
    const val MONTHLY_SAME_DAY_TEXT = "notification_interval_dialog:monthly_same_day_text"

    const val MONTHLY_DAY_OF_WEEK_ROW = "notification_interval_dialog:monthly_day_of_week_row"
    const val MONTHLY_DAY_OF_WEEK_RADIO = "notification_interval_dialog:monthly_day_of_week_radio"
    const val MONTHLY_DAY_OF_WEEK_TEXT = "notification_interval_dialog:monthly_day_of_week_text"

    // IntervalRepeatEnd instances
    const val REPEAT_END_ROOT_ROW_PREFIX = "notification_interval_dialog:repeat_end_root_row" // Prefix for root Row of IntervalRepeatEnd instance
    const val REPEAT_END_TYPE_DROPDOWN_ROOT = "notification_interval_dialog:repeat_end_type_dropdown_root"
    const val REPEAT_END_TYPE_TEXT_FIELD = "notification_interval_dialog:repeat_end_type_text_field"
    const val REPEAT_END_TYPE_MENU = "notification_interval_dialog:repeat_end_type_menu"
    const val REPEAT_END_TYPE_MENU_ITEM_PREFIX = "notification_interval_dialog:repeat_end_type_menu_item" // e.g., _forever

    // IntervalRepeatEnd - EndDate Specific
    const val END_DATE_TEXT_FIELD = "notification_interval_dialog:end_date_text_field"
    const val END_DATE_ICON_BUTTON = "notification_interval_dialog:end_date_icon_button"
    const val END_DATE_PICKER_DIALOG_ROOT = "notification_interval_dialog:end_date_picker_dialog_root"
    const val END_DATE_PICKER_CONFIRM_BUTTON = "notification_interval_dialog:end_date_picker_confirm_button"
    const val END_DATE_PICKER_DISMISS_BUTTON = "notification_interval_dialog:end_date_picker_dismiss_button"
    const val END_DATE_PICKER = "notification_interval_dialog:end_date_picker"

    // IntervalRepeatEnd - NumberOfTimes Specific
    const val NUMBER_OF_TIMES_TEXT_FIELD = "notification_interval_dialog:number_of_times_text_field"

    // Dialog Actions
    const val ACTIONS_ROW = "notification_interval_dialog:actions_row"
    const val CLOSE_BUTTON = "notification_interval_dialog:close_button"
    const val SET_REPEAT_BUTTON = "notification_interval_dialog:set_repeat_button"
}
