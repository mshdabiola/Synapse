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
