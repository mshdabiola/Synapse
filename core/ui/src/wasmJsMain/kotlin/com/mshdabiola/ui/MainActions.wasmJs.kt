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
package com.mshdabiola.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun AudioDialog(
    show: Boolean,
    dismiss: () -> Unit,
    output: (String, String) -> Unit,
) {
}

@Composable
actual fun supportVoice(): Boolean {
    return false
}

@Composable
actual fun ImageDialog2(
    modifier: Modifier,
    show: Boolean,
    dismiss: () -> Unit,
    saveImage: (String) -> Unit,
    getUri: () -> String,
) {
}

@Composable
actual fun ImageDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onChooseImage: () -> Unit,
    onSnapImage: () -> Unit,
) {
}
