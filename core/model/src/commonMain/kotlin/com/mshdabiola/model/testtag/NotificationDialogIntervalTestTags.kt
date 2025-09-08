package com.mshdabiola.model.testtag

object NotificationDialogIntervalTestTags {
    // Root Dialog
    const val DIALOG_ROOT = "NotificationDialogIntervalRoot"

    // Interval Type Selection
    const val INTERVAL_TYPE_DROPDOWN_ROOT = "IntervalTypeDropdownRoot"
    const val INTERVAL_TYPE_TEXT_FIELD = "IntervalTypeTextField"
    const val INTERVAL_TYPE_MENU = "IntervalTypeMenu"
    const val INTERVAL_TYPE_MENU_ITEM_PREFIX = "interval_type_menu_item" // Suffixed with type

    // Generic Interval TextField (used by Daily, Weekly, Monthly, Yearly)
    const val INTERVAL_TF_ROOT_PREFIX = "interval_tf_root" // Suffixed with type
//    const val INTERVAL_TF_TEXT_FIELD_PREFIX = "interval_tf_text_field" // Suffixed with type

    // Weekly Specific
    const val WEEKLY_DAYS_FLOW_ROW = "WeeklyDaysFlowRow"
    const val WEEKLY_DAY_INPUT_CHIP_PREFIX = "weekly_day_input_chip" // Suffixed with day

    // Monthly Specific
    const val MONTHLY_SAME_DAY_ROW = "MonthlySameDayRow"
    const val MONTHLY_SAME_DAY_RADIO = "MonthlySameDayRadio"
    const val MONTHLY_SAME_DAY_TEXT = "MonthlySameDayText"
    const val MONTHLY_DAY_OF_WEEK_ROW = "MonthlyDayOfWeekRow"
    const val MONTHLY_DAY_OF_WEEK_RADIO = "MonthlyDayOfWeekRadio"
    const val MONTHLY_DAY_OF_WEEK_TEXT = "MonthlyDayOfWeekText"

    // IntervalRepeatEnd Composable (called with REPEAT_END_ROOT_ROW_PREFIX)
    const val REPEAT_END_ROOT_ROW_PREFIX = "repeat_end_root_row" // Suffixed with type

    // Tags *inside* IntervalRepeatEnd composable
    const val REPEAT_END_TYPE_DROPDOWN_ROOT = "RepeatEndTypeDropdownRoot"
    const val REPEAT_END_TYPE_TEXT_FIELD = "RepeatEndTypeTextField"
    const val REPEAT_END_TYPE_MENU = "RepeatEndTypeMenu"
    const val REPEAT_END_TYPE_MENU_ITEM_PREFIX = "repeat_end_type_menu_item" // Suffixed with item text
    const val END_DATE_TEXT_FIELD = "EndDateTextField"
    const val END_DATE_ICON_BUTTON = "EndDateIconButton"
    const val END_DATE_PICKER_DIALOG_ROOT = "EndDatePickerDialogRoot"
    const val END_DATE_PICKER_CONFIRM_BUTTON = "EndDatePickerConfirmButton"
    const val END_DATE_PICKER_DISMISS_BUTTON = "EndDatePickerDismissButton"
    const val END_DATE_PICKER = "EndDatePicker"
    const val NUMBER_OF_TIMES_TEXT_FIELD = "NumberOfTimesTextField"

    // Dialog Action Buttons
    const val ACTIONS_ROW = "NotificationDialogIntervalActionsRow"
    const val CLOSE_BUTTON = "NotificationDialogIntervalCloseButton"
    const val SET_REPEAT_BUTTON = "NotificationDialogIntervalSetRepeatButton"
}
