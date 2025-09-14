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

object LabelScreenTestTags {
    const val BACK_BUTTON = "label:back_button"
    const val TITLE = "label:title"
    const val LIST = "label:list"
    const val NEW_LABEL_INPUT = "label:new_label_input"
    const val NEW_LABEL_CLEAR_BUTTON = "label:new_label_clear_button"
    const val NEW_LABEL_ADD_ICON_INDICATOR = "label:new_label_add_icon_indicator"
    const val NEW_LABEL_DONE_BUTTON = "label:new_label_done_button"

    fun itemLabelInput(id: Long) = "label:item_label_input_$id"
    fun itemDeleteButton(id: Long) = "label:item_delete_button_$id"
    fun itemLabelIconIndicator(id: Long) = "label:item_label_icon_indicator_$id"
    fun itemDoneButton(id: Long) = "label:item_done_button_$id"
    fun itemEditButton(id: Long) = "label:item_edit_button_$id"
}
