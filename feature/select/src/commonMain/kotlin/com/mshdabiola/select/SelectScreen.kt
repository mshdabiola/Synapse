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
package com.mshdabiola.select

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.drawable.SynIcons
import org.jetbrains.compose.resources.stringResource
import synapse.feature.select.generated.resources.Res

// Test Tags
object SelectLabelScreenTestTags {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLabelScreen(
    selectLabelUiState: SelectLabelUiState,
    onBack: () -> Unit = {},
    onCheckClick: (Int) -> Unit = {},
    onCreateLabel: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.testTag(SelectLabelScreenTestTags.SCREEN),
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag(SelectLabelScreenTestTags.TOP_APP_BAR),
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag(SelectLabelScreenTestTags.BACK_BUTTON),
                    ) {
                        Icon(imageVector = SynIcons.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(SelectLabelScreenTestTags.LABEL_QUERY_TEXT_FIELD),
                        state = selectLabelUiState.labelQuery,
                        placeholder = { Text(stringResource(Res.string.modules_designsystem_enter_text)) },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,

                            ),
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            if (selectLabelUiState.showAddLabel) {
                TextButton(
                    onClick = { onCreateLabel() },
                    modifier = Modifier.testTag(SelectLabelScreenTestTags.CREATE_LABEL_BUTTON),
                ) {
                    Icon(imageVector = SynIcons.Add, contentDescription = "add")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "${stringResource(id = Res.string.modules_designsystem_create)} \"${selectLabelUiState.labelQuery.text}\"")
                }
            }
            LazyColumn(modifier = Modifier.testTag(SelectLabelScreenTestTags.LABEL_LIST)) {
                itemsIndexed(
                    items = selectLabelUiState.labels,
                    key = { _, it -> it.id },
                ) { index, labelState ->
                    LabelText(
                        modifier = Modifier.testTag(SelectLabelScreenTestTags.labelItem(labelState.id)),
                        labelState = labelState,
                        onCheckClick = { onCheckClick(index) },
                    )
                }
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelScreenPreview() {
    val selectLabelUiState = SelectLabelUiState(
        labels = listOf(
            LabelState(1, "label1", ToggleableState.On),
            LabelState(2, "label2", ToggleableState.Off),
            LabelState(3, "label3", ToggleableState.Indeterminate),
            LabelState(4, "label4", ToggleableState.On),
            LabelState(5, "label5", ToggleableState.Off),
            LabelState(6, "label6", ToggleableState.Indeterminate),
        ),
        labelQuery = TextFieldState(""),
        showAddLabel = false,
    )
    SelectLabelScreen(selectLabelUiState = selectLabelUiState)
}

@Composable
fun LabelText(
    modifier: Modifier = Modifier, // Added modifier parameter
    labelState: LabelState,
    onCheckClick: () -> Unit = {},
) {
    Row(
        modifier = modifier // Applied the modifier here
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Icon(imageVector = SynIcons.Label, contentDescription = "")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier
                .weight(1f)
                .testTag(SelectLabelScreenTestTags.labelItemText(labelState.id)),
            text = labelState.label,
        )
        TriStateCheckbox(
            modifier = Modifier.testTag(SelectLabelScreenTestTags.labelItemCheckbox(labelState.id)),
            state = labelState.toggleableState,
            onClick = { onCheckClick() },
        )
    }
}
