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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.designsystem.component.SynTextField
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.testtag.SelectScreenTestTags
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.select.generated.resources.Res
import synapse.feature.select.generated.resources.select_screen_back_cd
import synapse.feature.select.generated.resources.select_screen_create
import synapse.feature.select.generated.resources.select_screen_enter_text
import synapse.feature.select.generated.resources.select_screen_label_icon_cd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLabelScreen(
    selectUiState: SelectUiState,
    onBack: () -> Unit = {},
    onCheckClick: (Int) -> Unit = {},
    onCreateLabel: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.testTag(SelectScreenTestTags.SCREEN),
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag(SelectScreenTestTags.TOP_APP_BAR),
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag(SelectScreenTestTags.BACK_BUTTON),
                    ) {
                        Icon(
                            imageVector = SynIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.select_screen_back_cd),
                        )
                    }
                },
                title = {
                    SynTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(SelectScreenTestTags.LABEL_QUERY_TEXT_FIELD),
                        state = selectUiState.labelQuery,
                        placeholder = stringResource(Res.string.select_screen_enter_text),
                        textStyle = MaterialTheme.typography.bodyMedium,

                    )
                },
            )
        },
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            if (selectUiState.showAddLabel) {
                SynTextButton(
                    onClick = { onCreateLabel() },
                    modifier = Modifier.testTag(SelectScreenTestTags.CREATE_LABEL_BUTTON),
                    icon = SynIcons.Add,
                    label = "${stringResource(
                        Res.string.select_screen_create,
                    )} \"${selectUiState.labelQuery.text}\"",
                )
            }
            LazyColumn(modifier = Modifier.testTag(SelectScreenTestTags.LABEL_LIST)) {
                itemsIndexed(
                    items = selectUiState.labels,
                    key = { _, it -> it.id },
                ) { index, labelState ->
                    LabelText(
                        modifier = Modifier.testTag(SelectScreenTestTags.labelItem(labelState.id)),
                        labelUiState = labelState,
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
    val selectUiState = SelectUiState(
        labels = listOf(
            LabelUiState(1, "label1", ToggleableState.On),
            LabelUiState(2, "label2", ToggleableState.Off),
            LabelUiState(3, "label3", ToggleableState.Indeterminate),
            LabelUiState(4, "label4", ToggleableState.On),
            LabelUiState(5, "label5", ToggleableState.Off),
            LabelUiState(6, "label6", ToggleableState.Indeterminate),
        ),
        labelQuery = TextFieldState(""),
        showAddLabel = false,
    )
    SelectLabelScreen(selectUiState = selectUiState)
}

@Composable
fun LabelText(
    modifier: Modifier = Modifier, // Added modifier parameter
    labelUiState: LabelUiState,
    onCheckClick: () -> Unit = {},
) {
    Row(
        modifier = modifier // Applied the modifier here
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,

    ) {
        Icon(imageVector = SynIcons.Label, contentDescription = stringResource(Res.string.select_screen_label_icon_cd))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier
                .weight(1f)
                .testTag(SelectScreenTestTags.labelItemText(labelUiState.id)),
            text = labelUiState.label,
        )
        TriStateCheckbox(
            modifier = Modifier.testTag(SelectScreenTestTags.labelItemCheckbox(labelUiState.id)),
            state = labelUiState.toggleableState,
            onClick = { onCheckClick() },
        )
    }
}
