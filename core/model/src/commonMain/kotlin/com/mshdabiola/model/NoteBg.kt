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
package com.mshdabiola.model

data class NoteBg(val fgColor: Long, val bg: Int) {
    companion object {
        val noteBgs = listOf(
            NoteBg(0xFF737D55, 1),
            NoteBg(0xFFDB69A3, 2),
            NoteBg(0xFFB80D57, 3),
            NoteBg(0xFFC26744, 4),
            NoteBg(0xFFA3F7B7, 5),
            NoteBg(0xFFE660B7, 6),
            NoteBg(0xFFEE7D62, 7),
            NoteBg(0xFFCACA5E, 8),
            NoteBg(0xFF8D59B6, 9),
            NoteBg(0xFFCF1879, 10),
        )
    }
}
