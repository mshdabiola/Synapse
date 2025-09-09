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
package com.mshdabiola.main.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.mshdabiola.main.MainScreen
import com.mshdabiola.main.MainViewModel
import com.mshdabiola.main.component.DeleteForeverDialog
import com.mshdabiola.main.component.DeleteLabelAlertDialog
import com.mshdabiola.main.component.EmptyTrashDialog
import com.mshdabiola.main.component.RenameLabelAlertDialog
import com.mshdabiola.main.component.SearchBar
import com.mshdabiola.main.component.SearchInputField
import com.mshdabiola.main.model.MainState
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.ui.ColorDialog
import com.mshdabiola.ui.LocalNavAnimatedContentScope
import com.mshdabiola.ui.NotificationDialog
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

fun NavController.navigateToMain(
    navOptions: NavOptions = navOptions { launchSingleTop = true },
) = navigate(Main, navOptions)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
fun NavGraphBuilder.mainScreen(
    modifier: Modifier = Modifier,
    onDrawer: (() -> Unit)?,
    navigateToDetail: (NotePad) -> Unit,
    navigateToSelectLevel: (Set<Long>) -> Unit,
    navigateToSearch: () -> Unit,
) {
    composable<Main> {
        val mainViewModel = koinViewModel<MainViewModel>()

        val mainState = mainViewModel.mainState.collectAsStateWithLifecycle()
        val searchState = mainViewModel.searchState.collectAsStateWithLifecycle()

        var showDialog by remember {
            mutableStateOf(false)
        }
        var showColor by remember {
            mutableStateOf(false)
        }
        var showRenameLabel by remember {
            mutableStateOf(false)
        }
        var showDeleteLabel by remember {
            mutableStateOf(false)
        }
        var showDeleteForever by remember {
            mutableStateOf(false)
        }
        var showEmptyTrash by remember {
            mutableStateOf(false)
        }
        val scope = rememberCoroutineScope()
        val searchBarState = rememberSearchBarState()
        val inputField = @Composable {
            SearchInputField(
                searchBarState = searchBarState,
                searchTextFieldState = mainViewModel.searchTextFieldState,
                isGrid = (mainState.value as? MainState.ViewState)?.isGrid ?: false,
                onDisplayModeChange = mainViewModel::onDisplayModeChange,
                onDrawer = onDrawer ?: {},
            )
        }

        val searchBarState2 = rememberSearchBarState()

        CompositionLocalProvider(
            LocalNavAnimatedContentScope provides this,
        ) {
            MainScreen(
                modifier = modifier,
                mainState = mainState.value,
                searchBarState = searchBarState,
                navigateToNoteEditor = navigateToDetail,
                onNoteSelected = mainViewModel::handleCardSelection,
                onClearSelection = mainViewModel::deselectNotes,
                onPinNotes = mainViewModel::pinOrUnpinNotes,
                onNotificationClick = { showDialog = true },
                onSelectColor = { showColor = true },
                onLabelNotes = {
                    val selected = (mainState.value as? MainState.ViewState)
                        ?.selectState?.setOfSelected.orEmpty()
                    if (selected.isNotEmpty()) navigateToSelectLevel(selected)
                },
                onCopyNote = mainViewModel::onCopyNote,
                onDeleteNotes = mainViewModel::onDeleteNote,
                onArchive = mainViewModel::onArchiveNote,
                onShareNote = {
//                val notePads = mainViewModel.onSendNote()
//                val intent = ShareCompat.IntentBuilder(context)
//                    .setText(notePads.toString())
//                    .setType("text/*")
//                    .setChooserTitle("From Notepad")
//                    .createChooserIntent()
//                context.startActivity(Intent(intent))
                },
                onLabelNameChange = { showRenameLabel = true },
                onDeleteLabel = { showDeleteLabel = true },
                onDeleteAllTrash = { showEmptyTrash = true },
                onHamburgerMenuClick = onDrawer ?: {},
                onDisplayModeChange = mainViewModel::onDisplayModeChange,
                onRestore = mainViewModel::onRestore,
                onDeletedForever = { showDeleteForever = true },
                onSearchClick = {
                    scope.launch { searchBarState2.animateToExpanded() }
                },
                inputField = inputField,
            )
        }
        SearchBar(
            modifier = Modifier.testTag("main:search_bar"),
            searchBarState = searchBarState,
            onSetSearch = mainViewModel::onSetSearch,
            onNoteClick = navigateToDetail,
            searchState = searchState.value,
            searchTextFieldState = mainViewModel.searchTextFieldState,
            inputField = inputField,
        )
        SearchBar(
            modifier = Modifier.testTag("main:search_bar"),
            searchBarState = searchBarState2,
            onSetSearch = mainViewModel::onSetSearch,
            onNoteClick = navigateToDetail,
            searchState = searchState.value,
            searchTextFieldState = mainViewModel.searchTextFieldState,
            inputField = {
                SearchInputField(
                    searchBarState = searchBarState2,
                    searchTextFieldState = mainViewModel.searchTextFieldState,
                    isGrid = (mainState.value as? MainState.ViewState)?.isGrid ?: false,
                    onDisplayModeChange = mainViewModel::onDisplayModeChange,
                    onDrawer = onDrawer ?: {},
                )
            },
        )
        NotificationDialog(
            showDialog = showDialog,
            onDismissRequest = { showDialog = false },
            isEdit = false,
            initState = (mainState.value as? MainState.ViewState)?.selectState?.notificationUiState,
            onSetAlarm = mainViewModel::setAlarm,
            onDeleteAlarm = mainViewModel::onDeleteAlarm,
        )

        ColorDialog(
            show = showColor,
            onDismissRequest = { showColor = false },
            onColorClick = mainViewModel::setAllColor,
            currentColor = (mainState.value as? MainState.ViewState)?.selectState?.colorIndex ?: -1,
        )

        RenameLabelAlertDialog(
            show = showRenameLabel,
            label = (mainState.value as? MainState.ViewState)?.labelName ?: "",
            onDismissRequest = { showRenameLabel = false },
            onChangeName = mainViewModel::renameLabel,
        )

        DeleteLabelAlertDialog(
            show = showDeleteLabel,
            onDismissRequest = { showDeleteLabel = false },
            onDelete = mainViewModel::deleteLabel,
        )
        DeleteForeverDialog(
            show = showDeleteForever,
            onDismissRequest = { showDeleteForever = false },
            onDelete = mainViewModel::onDeleteForever,
        )

        EmptyTrashDialog(
            show = showEmptyTrash,
            onDismissRequest = { showEmptyTrash = false },
            onDelete = mainViewModel::onDeleteAllTrash,
        )
    }
}
