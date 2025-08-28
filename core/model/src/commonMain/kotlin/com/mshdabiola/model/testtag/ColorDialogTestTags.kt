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

object ColorDialogTestTags {
    const val DIALOG_ROOT = "color_dialog:root"             // For the main dialog container
    const val TITLE = "color_dialog:title"                  // For the "Note Color" Text
    const val COLOR_GRID = "color_dialog:color_grid"        // For the LazyVerticalGrid
    const val RESET_COLOR_ITEM = "color_dialog:reset_item"  // For the reset color Surface
    const val RESET_COLOR_ICON = "color_dialog:reset_icon"  // For the icon in the reset item
    const val COLOR_PICKER_ITEM = "color_dialog:picker_item" // For individual color Surfaces
                                                            // In tests, you can append an index or find by color
    const val COLOR_PICKER_ITEM_ICON = "color_dialog:picker_item_icon" // For the selected state icon on a color item
}

