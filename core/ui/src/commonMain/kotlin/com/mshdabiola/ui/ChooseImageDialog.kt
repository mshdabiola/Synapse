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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import org.jetbrains.compose.resources.stringResource
import synapse.core.ui.generated.resources.Res
import synapse.core.ui.generated.resources.feature_mainscreen_choose_image
import synapse.core.ui.generated.resources.feature_mainscreen_take_image

@Composable
fun ChooseImageDialog(
    show: Boolean,
    dismiss: () -> Unit,
    saveImage: (String) -> Unit,
    getUri: () -> String,
) {
    val logics = getPlatformLogics(
        saveImage = saveImage,
        savePhoto = {
            saveImage(getUri())
        },
    )

    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = dismiss,
            //  title = { Text(text = stringResource(R.string.feature_mainscreen_add_image)) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .clickable {  logics.snapImage(getUri())
                                dismiss() }
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = SynIcons.PhotoCamera,
                            contentDescription = stringResource(Res.string.feature_mainscreen_take_image),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.feature_mainscreen_take_image)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .clickable {   logics.chooseImage(getUri())
                                dismiss()}
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = SynIcons.Image,
                            contentDescription = stringResource(Res.string.feature_mainscreen_choose_image),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.feature_mainscreen_choose_image)
                        )
                    }
                }
            },
            confirmButton = {},
        )
    }
}
