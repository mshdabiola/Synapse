package com.mshdabiola.model.testtag

object LabelScreenTestTags {
    const val BACK_BUTTON = "label:back_button"
    const val TITLE = "label:title"
    const val LIST = "label:list"
    const val NEW_LABEL_INPUT = "label:new_label_input"
    const val NEW_LABEL_CLEAR_BUTTON = "label:new_label_clear_button"
    const val NEW_LABEL_ADD_ICON_INDICATOR = "label:new_label_add_icon_indicator"
    const val NEW_LABEL_DONE_BUTTON = "label:new_label_done_button"

    fun itemLabelInput(id: Long) = "label:item_label_input_${'$'}id"
    fun itemDeleteButton(id: Long) = "label:item_delete_button_${'$'}id"
    fun itemLabelIconIndicator(id: Long) = "label:item_label_icon_indicator_${'$'}id"
    fun itemDoneButton(id: Long) = "label:item_done_button_${'$'}id"
    fun itemEditButton(id: Long) = "label:item_edit_button_${'$'}id"
}
