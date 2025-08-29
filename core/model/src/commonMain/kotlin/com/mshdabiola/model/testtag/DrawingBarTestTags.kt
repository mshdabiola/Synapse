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

object DrawingBarTestTags {
    // DrawingBar general
    const val ROOT = "drawing_bar:root" // Surface element in DrawingBar

    // Tabs for drawing tools
    const val TAB_ROW = "drawing_bar:tab_row"
    const val SELECT_TAB = "drawing_bar:select_tab" // Tab for Select tool
    const val ERASE_TAB = "drawing_bar:erase_tab" // Tab for Erase tool
    const val PEN_TAB = "drawing_bar:pen_tab" // Tab for Pen tool
    const val MARKER_TAB = "drawing_bar:marker_tab" // Tab for Marker tool
    const val CRAYON_TAB = "drawing_bar:crayon_tab" // Tab for Crayon tool

    // HorizontalPager for tool options that appear when a tab is selected and 'isUp' is true
    const val TOOL_OPTIONS_PAGER = "drawing_bar:tool_options_pager"

    // Specific tool options within the Pager:
    // Eraser options:
    const val CLEAR_CANVAS_BUTTON = "drawing_bar:clear_canvas_button" // TextButton for "Clear Canvas"

    // ColorAndWidth section (dynamically shown for Pen, Marker, Crayon):
    const val COLOR_WIDTH_SECTION_ROOT = "drawing_bar:color_width_section_root"
    // Root Column of the ColorAndWidth composable

    const val COLOR_SELECTOR_LAYOUT = "drawing_bar:color_selector_layout" // FlowLayout2 for colors
    const val COLOR_SELECTOR_ITEM_PREFIX = "drawing_bar:color_item"
    // Base for individual color Box tags (append index in tests)

    const val WIDTH_SELECTOR_LAYOUT = "drawing_bar:width_selector_layout" // Row for widths
    const val WIDTH_SELECTOR_ITEM_PREFIX = "drawing_bar:width_item"
// Base for individual width Box tags (append index in tests)
}
