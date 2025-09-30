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
package com.mshdabiola.detail

import androidx.compose.foundation.content.consume
import androidx.compose.foundation.content.contentReceiver
import androidx.compose.ui.Modifier

@OptIn(markerClass = [androidx.compose.foundation.ExperimentalFoundationApi::class])
actual fun Modifier.contentReceiver(onReceive: (List<String>) -> Unit): Modifier {
    return this.contentReceiver { transferableContent ->
        val paths = mutableListOf<String>()
        val remaining = transferableContent.consume { item ->

            item.uri?.toString()?.let { path ->
                println()
                paths.add(path)
            }
            true // Indicate that we've processed this item
        }
        if (paths.isNotEmpty()) {
            onReceive(paths)
        }
        remaining // Return any unconsumed content
    }
}
