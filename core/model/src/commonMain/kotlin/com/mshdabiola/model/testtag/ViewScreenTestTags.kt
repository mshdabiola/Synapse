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
