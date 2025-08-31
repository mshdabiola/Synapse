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

// Test Tags for KmtScaffold and its inner components
object KmtScaffoldTestTags {
    const val MODAL_NAVIGATION_DRAWER = "scaffold:modal_nav_drawer"
    const val PERMANENT_NAVIGATION_DRAWER = "scaffold:permanent_nav_drawer"
    const val MODAL_DRAWER_SHEET = "scaffold:modal_drawer_sheet"
    const val WIDE_NAVIGATION_RAIL = "scaffold:wide_nav_rail"
    const val PERMANENT_DRAWER_SHEET = "scaffold:permanent_drawer_sheet"
    const val RAIL_TOGGLE_BUTTON = "scaffold:rail_toggle_button" // For expanding/collapsing rail
    const val SCAFFOLD_CONTENT_AREA = "scaffold:content_area" // For the main Scaffold used inside drawers

    object FabTestTags {
        const val FAB_ANIMATED_CONTENT = "fab:animated_content"
        const val SMALL_FAB = "fab:small_fab"
        const val EXTENDED_FAB = "fab:extended_fab" // Used for SplitButtonLayout acting as Extended FAB
        const val FAB_ADD_ICON = "fab:add_icon" // Common for both FAB types, and icons in dropdown
        const val FAB_ADD_TEXT = "fab:add_text" // Specific to Extended FAB text part

        // Tags for DropdownMenu items within the FAB
        const val DROPDOWN_MENU = "fab:dropdown_menu"
        const val DROPDOWN_ITEM_IMAGE = "fab:dropdown_item_image"
        const val DROPDOWN_ITEM_VOICE = "fab:dropdown_item_voice"
        const val DROPDOWN_ITEM_LIST = "fab:dropdown_item_list"
        const val DROPDOWN_ITEM_DRAWING = "fab:dropdown_item_drawing"
    }

    object DrawerContentTestTags {
        const val DRAWER_CONTENT_COLUMN = "drawer:content_column"
        const val BRAND_ROW = "drawer:brand_row"
        const val BRAND_ICON = "drawer:brand_icon" // For the main brand icon (e.g., Synapse logo)
        const val BRAND_TEXT = "drawer:brand_text" // Intended for primary brand text if present
        const val VERSION_TEXT = "drawer:version_text" // For the BuildConfig version text

        fun navigationItemTag(route: Any) = "drawer:nav_item_$route"
        fun wideNavigationRailItemTag(route: Any) = "drawer:wide_nav_rail_item_$route"

        // Tags for "Labels" section
        const val LABELS_SECTION_HEADER = "drawer:labels_section_header"
        fun labelItemTag(labelId: String) = "drawer:label_item_$labelId"
        const val EDIT_LABELS_ITEM = "drawer:edit_labels_item"
    }
}
