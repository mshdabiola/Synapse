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

object NewSelectionToolsTestTags {
    // ResizableRectangleWithHandles2
    const val RESIZABLE_RECT_ROOT = "new_selection_tools:resizable_rect_root" // Outer Box with graphicsLayer
    const val RESIZABLE_RECT_OFFSET_COLUMN = "new_selection_tools:resizable_rect_offset_column" // Column for offset
    const val ROTATION_HANDLE_ROOT = "new_selection_tools:rotation_handle_root"
    const val ROTATION_HANDLE_ICON = "new_selection_tools:rotation_handle_icon"
    const val MAIN_DRAGGABLE_RESIZABLE_AREA = "new_selection_tools:main_draggable_resizable_area" // Box containing border and handles
    const val RESIZABLE_BORDER_BOX = "new_selection_tools:resizable_border_box" // The visible blue border

    // DraggableHandle instances within ResizableRectangleWithHandles2
    const val HANDLE_TOP_LEFT = "new_selection_tools:handle_top_left"
    const val HANDLE_TOP_CENTER = "new_selection_tools:handle_top_center"
    const val HANDLE_TOP_END = "new_selection_tools:handle_top_end"
    const val HANDLE_CENTER_START = "new_selection_tools:handle_center_start"
    const val HANDLE_CENTER_END = "new_selection_tools:handle_center_end"
    const val HANDLE_BOTTOM_START = "new_selection_tools:handle_bottom_start"
    const val HANDLE_BOTTOM_CENTER = "new_selection_tools:handle_bottom_center"
    const val HANDLE_BOTTOM_END = "new_selection_tools:handle_bottom_end"

    // Optional: If DraggableHandle itself needs a generic root tag (applied in its definition)
    // const val DRAGGABLE_HANDLE_ROOT = "new_selection_tools:draggable_handle_root"
}
