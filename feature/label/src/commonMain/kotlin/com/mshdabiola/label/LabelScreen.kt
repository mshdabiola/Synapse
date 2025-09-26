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
package com.mshdabiola.label

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import com.mshdabiola.designsystem.component.SynTextField
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.testtag.LabelScreenTestTags // Added import
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.label.generated.resources.Res
import synapse.feature.label.generated.resources.modules_designsystem_create_new_label
import synapse.feature.label.generated.resources.modules_designsystem_edit_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelScreen(
    labelUiState: LabelUiState,
    onBack: () -> Unit = {},
    onDelete: (Long) -> Unit = {},
    onAdd: (Int) -> Unit = {},
) {
    var currentFocus by remember {
        mutableStateOf(-1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag(LabelScreenTestTags.BACK_BUTTON),
                    ) {
                        Icon(imageVector = SynIcons.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.modules_designsystem_edit_label),
                        modifier = Modifier.testTag(LabelScreenTestTags.TITLE),
                    )
                },
            )
        },
    ) { paddingValues ->

        LazyColumn(
            Modifier
                .padding(paddingValues)
                .testTag(LabelScreenTestTags.LIST),
        ) {
            item {
                EditLabelTextField(
                    labelState = labelUiState.newLabel,
                    labels = labelUiState.labels,
                    isCurrentFocus = currentFocus == -1,
                    isEditMode = labelUiState.isEditMode,
                    onAdd = { onAdd(-1) },
                    onFocused = { currentFocus = -1 },
                )
            }

            itemsIndexed(labelUiState.labels, key = { index, item -> item.id }) { index, item ->
                LabelTextField(
                    labelState = item,
                    labels = labelUiState.labels,
                    isCurrentFocus = currentFocus == index,
                    onFocused = { currentFocus = index },
                    onAdd = { onAdd(index) },
                    onDelete = { onDelete(item.id) },

                )
            }
        }
    }
}

@Preview
@Composable
fun LabelScreenPreview() {
    val labelUiState = LabelUiState(
        labels = listOf(
            LabelState(1, TextFieldState("Java")),
            LabelState(2, TextFieldState("Kotlin")),
            LabelState(3, TextFieldState("Python")),
            LabelState(4, TextFieldState("C sharper")),
            LabelState(5, TextFieldState("JavaScript")),

        ),
        newLabel = LabelState(-1, TextFieldState("new")),
        isEditMode = false,
    )
    LabelScreen(labelUiState = labelUiState, onBack = {}, onDelete = {}, onAdd = {})
}

@Composable
fun EditLabelTextField(
    labelState: LabelState,
    labels: List<LabelState> = emptyList(),
    isEditMode: Boolean = false,
    isCurrentFocus: Boolean = false,
    onAdd: () -> Unit = { },
    onFocused: () -> Unit = { },
) {
    val focusRequester by remember {
        mutableStateOf(FocusRequester())
    }
    var enableError by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        key1 = isEditMode,
        block = {
            if (isEditMode) {
                focusRequester.requestFocus()
            }
        },
    )
    LaunchedEffect(
        key1 = labelState.label.text,
        key2 = labels,
    ) {
        val text = labelState.label.text
        if (text.isNotBlank() && labels.isNotEmpty()) {
            enableError = labels.any { it.label.text.contentEquals(text) }
        }
    }

    SynTextField(
        modifier =
        Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    onFocused()
                }
            }
            .testTag(LabelScreenTestTags.NEW_LABEL_INPUT),
        state = labelState.label,
        placeholder = stringResource(Res.string.modules_designsystem_create_new_label),
        supportingText = if (enableError) "Label already exists" else null,
        //            stringResource(Rd.string.modules_designsystem_label_already_exists)
        isError = enableError,
        leadingIcon = {
            if (labelState.label.text.isNotBlank() && isCurrentFocus) {
                IconButton(
                    onClick = {
                        labelState.label.clearText()
                        focusRequester.freeFocus()
                    },
                    modifier = Modifier.testTag(LabelScreenTestTags.NEW_LABEL_CLEAR_BUTTON),
                ) {
                    Icon(imageVector = SynIcons.Clear, contentDescription = "Clear")
                }
            } else {
                Icon(
                    imageVector = SynIcons.Add,
                    contentDescription = "add",
                    modifier = Modifier.testTag(LabelScreenTestTags.NEW_LABEL_ADD_ICON_INDICATOR),
                )
            }
        },
        trailingIcon = {
            if (labelState.label.text.isNotBlank() && !enableError) {
                IconButton(
                    onClick = { onAdd() },
                    modifier = Modifier.testTag(LabelScreenTestTags.NEW_LABEL_DONE_BUTTON),
                ) {
                    Icon(imageVector = SynIcons.Done, contentDescription = "add")
                }
            }
        },
        imeAction = ImeAction.Done,
        keyboardAction = {
            if (!enableError) {
                onAdd()
            }
        },
    )
}

@Composable
fun LabelTextField(
    labelState: LabelState,
    labels: List<LabelState> = emptyList(),
    isCurrentFocus: Boolean = false,
    onAdd: () -> Unit = { },
    onDelete: (Long) -> Unit = {},
    onFocused: () -> Unit = { },
) {
    val focusRequester by remember {
        mutableStateOf(FocusRequester())
    }

    var enableError by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        key1 = labelState.label.text,
        key2 = labels,
    ) {
        val text = labelState.label.text
        if (text.isNotBlank() && labels.isNotEmpty()) {
            enableError = labels.any { it.id != labelState.id && it.label.text.contentEquals(text) }
        }
    }

    val focusManager = LocalFocusManager.current
    SynTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    onFocused()
                }
            }
            .testTag(LabelScreenTestTags.itemLabelInput(labelState.id)),
        state = labelState.label,
        leadingIcon = {
            if (isCurrentFocus) {
                IconButton(
                    onClick = { onDelete(labelState.id) },
                    modifier = Modifier.testTag(LabelScreenTestTags.itemDeleteButton(labelState.id)),
                ) {
                    Icon(imageVector = SynIcons.Delete, contentDescription = "delete")
                }
            } else {
                Icon(
                    imageVector = SynIcons.Label,
                    contentDescription = "label",
                    modifier = Modifier.testTag(LabelScreenTestTags.itemLabelIconIndicator(labelState.id)),
                )
            }
        },
        trailingIcon = {
            if (isCurrentFocus) {
                if (labelState.label.text.isNotBlank() && !enableError) {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onAdd()
                        },
                        modifier = Modifier.testTag(LabelScreenTestTags.itemDoneButton(labelState.id)),
                    ) {
                        Icon(imageVector = SynIcons.Done, contentDescription = "add")
                    }
                }
            } else {
                IconButton(
                    onClick = { focusRequester.requestFocus() },
                    modifier = Modifier.testTag(LabelScreenTestTags.itemEditButton(labelState.id)),
                ) {
                    Icon(imageVector = SynIcons.Edit, contentDescription = "edit")
                }
            }
        },
        placeholder = stringResource(Res.string.modules_designsystem_create_new_label),
        supportingText = if (enableError) "Label already exists" else null,
        isError = enableError,
        imeAction = ImeAction.Done,
        keyboardAction = {
            if (!enableError) {
                focusManager.clearFocus()
                onAdd()
            }
        },
    )
}
