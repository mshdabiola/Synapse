package com.mshdabiola.model.testtag

object NoteAppearanceSheetTestTags {
    const val RESET_COLOR_BUTTON = "note_appearance:reset_color"
    fun colorItem(index: Int) = "note_appearance:color_item_$index"
    const val RESET_IMAGE_BUTTON = "note_appearance:reset_image"
    fun imageItem(index: Int) = "note_appearance:image_item_$index"
}
