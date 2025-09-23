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
package com.mshdabiola.model.note

data class NotePad(
    val id: Long = -1,
    val title: String = "",
    val detail: String = "",
    val editDate: Long = 0,
    val isCheck: Boolean = false,
    val color: Int = -1,
    val background: Int = -1,
    val isPin: Boolean = false,
    val noteCategory: NoteCategory = NoteCategory.NOTE,
    val notification: Notification? = null,
    val drawings: List<NoteDrawing> = emptyList(),
    val images: List<NoteImage> = emptyList(),
    val voices: List<NoteVoice> = emptyList(),
    val checks: List<NoteItem> = emptyList(),
    val labels: List<Label> = emptyList(),
    val uris: List<NoteLink> = emptyList(),
) {
    fun getVisuals(): List<NoteVisual> {
        return (drawings + images)
            .sortedBy { it.key }
    }

    override fun toString(): String {
        return """
            $title
            $detail
            ${checks.joinToString(separator = " ")}
        """.trimIndent()
    }

    fun isEmpty(): Boolean {
        val titleIsBlank = title.isBlank()
        val detailIsBlank = detail.isBlank()
        val emptyImage = getVisuals().isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && emptyImage && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }

    fun isImageOnly(): Boolean {
        val titleIsBlank = title.isBlank()
        val detailIsBlank = detail.isBlank()
        val emptyImage = (images + drawings).isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank &&
            detailIsBlank &&
            !emptyImage &&
            voiceEmpty &&
            checkIsEmpty &&
            checksBlank &&
            labelsIsEmpty
    }

    fun isDrawingOnly(): Boolean {
        val titleIsBlank = title.isBlank()
        val detailIsBlank = detail.isBlank()
        val emptyImage = images.isEmpty()
        val emptyDrawing = drawings.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank &&
            detailIsBlank &&
            emptyImage &&
            !emptyDrawing &&
            voiceEmpty &&
            checkIsEmpty &&
            checksBlank &&
            labelsIsEmpty
    }
}
