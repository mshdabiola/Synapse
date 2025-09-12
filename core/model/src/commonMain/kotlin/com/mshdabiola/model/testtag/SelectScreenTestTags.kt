package com.mshdabiola.model.testtag

object SelectScreenTestTags {
    const val SCREEN = "selectLabel:screen"
    const val TOP_APP_BAR = "selectLabel:topAppBar"
    const val BACK_BUTTON = "selectLabel:backButton"
    const val LABEL_QUERY_TEXT_FIELD = "selectLabel:labelQueryTextField"
    const val CREATE_LABEL_BUTTON = "selectLabel:createLabelButton"
    const val LABEL_LIST = "selectLabel:labelList"
    fun labelItem(labelId: Long) = "selectLabel:item:$labelId"
    fun labelItemCheckbox(labelId: Long) = "selectLabel:itemCheckbox:$labelId"
    fun labelItemText(labelId: Long) = "selectLabel:itemText:$labelId"
}
