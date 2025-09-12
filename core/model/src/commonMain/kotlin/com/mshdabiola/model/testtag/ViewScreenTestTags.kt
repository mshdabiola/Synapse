package com.mshdabiola.model.testtag

object ViewScreenTestTags {
    const val PAGER = "gallery:pager"
    const val BACK_BUTTON = "gallery:back_button"
    const val TITLE = "gallery:title"
    const val MORE_OPTIONS_BUTTON = "gallery:more_options_button"
    const val GRAB_TEXT_MENU_ITEM = "gallery:grab_text_menu_item"
    const val COPY_MENU_ITEM = "gallery:copy_menu_item"
    const val SEND_MENU_ITEM = "gallery:send_menu_item"
    const val DELETE_MENU_ITEM = "gallery:delete_menu_item"

    fun image(page: Int) = "gallery:image_${'$'}page"
}
