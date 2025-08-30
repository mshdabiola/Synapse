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
package com.mshdabiola.detail.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mshdabiola.detail.AddBottomSheet2
import com.mshdabiola.detail.ColorAndImageBottomSheet
import com.mshdabiola.detail.DetailScreen
import com.mshdabiola.detail.DetailViewModel
import com.mshdabiola.detail.NoteOptionBottomSheet
import com.mshdabiola.detail.NotificationBottomSheet
import com.mshdabiola.model.Notification
import com.mshdabiola.ui.LocalNavAnimatedContentScope
import com.mshdabiola.ui.NotificationDialogNew
import com.mshdabiola.ui.getPlatformLogics
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parameterSetOf

fun NavController.navigateToDetail(detail: Detail) {
    // val encodedId = URLEncoder.encode(topicId, URL_CHARACTER_ENCODING)
    navigate(detail) {
        launchSingleTop = true
    }
}

@OptIn(KoinExperimentalAPI::class, ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.detailScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    setNotification: (Notification) -> Unit,
    navigateToGallery: (Long, Int, Int, String) -> Unit,
    navigateToDrawing: (Long, Long?) -> Unit,
    navigateToSelectLevel: (Set<Long>) -> Unit,
) {
    composable<Detail> { backStack ->

        val detail: Detail = backStack.toRoute()
        val coroutineScope = rememberCoroutineScope()

//        val viewModel= koinViewModel<DetailViewModel>{ parametersOf(id)}
        val viewModel: DetailViewModel =
            koinViewModel(
                parameters = {
                    parameterSetOf(
                        detail.id,
                    )
                },
            )
//        val detailState = viewModel.detailState.collectAsStateWithLifecycle()
//        CompositionLocalProvider(
//            LocalNavAnimatedContentScope provides this,
//        ) {
//            DetailScreen(
//                modifier = modifier,
//                state = detailState.value,
//                detail = detail,
//                onBack = onBack,
//                onDelete = {
//                    coroutineScope.launch {
//                        setNotification(
//                            Notification.MessageWithAction(
//                                type = Type.Warning,
//                                duration = SnackbarDuration.Indefinite,
//                                message = getString(Res.string.detail_delete_confirmation_message),
//                                action = getString(Res.string.detail_delete_action_text),
//                                actionCallback = {
//                                    viewModel.onDelete()
//                                    onBack()
//                                },
//                            ),
//                        )
//                    }
//                },
//
//            )
//        }

        val editViewModel = koinViewModel<DetailViewModel>(
            parameters = {
                parameterSetOf(
                    detail,
                )
            },
        )
        val detailState by editViewModel.detailState.collectAsStateWithLifecycle()
        var showModalState by remember {
            mutableStateOf(false)
        }
        var noteModalState by remember {
            mutableStateOf(false)
        }
        var noteficationModalState by remember {
            mutableStateOf(false)
        }
        var colorModalState by remember {
            mutableStateOf(false)
        }

        var showDialog by remember {
            mutableStateOf(false)
        }
        val logics = getPlatformLogics(
            onNotification = {
                noteficationModalState = true
            }
        )
//
        CompositionLocalProvider(
            LocalNavAnimatedContentScope provides this,
        ) {
            DetailScreen(
                modifier = modifier,
                state = detailState,
                onBackClick = onBack,
                onCheckDelete = editViewModel::onCheckDelete,
//            onCheck = editViewModel::onCheck,
                addItem = editViewModel::addCheck,
                playVoice = editViewModel::playMusic,
                pauseVoice = editViewModel::pause,
                moreOptions = {
                    showModalState = true
                },
                noteOption = { noteModalState = true },
//            unCheckAllItems = editViewModel::unCheckAllItems,
                deleteCheckItems = editViewModel::deleteCheckedItems,
                hideCheckBoxes = editViewModel::hideCheckBoxes,
                pinNote = editViewModel::pinNote,
                onLabel = {
                    navigateToSelectLevel(
                        setOf(
                            detailState.notePad.id,
                        ),
                    )
                },
                onColorClick = { colorModalState = true },
                onNotification = {
                    if (logics.checkNotificationPermission()) {
                        logics.askForNotificationPermission()
                    } else {
                        noteficationModalState = true
                    }
                },
                showNotificationDialog = {
                    showDialog = true
                },
                onArchive = editViewModel::onArchive,
                deleteVoiceNote = editViewModel::deleteVoiceNote,
                navigateToGallery = navigateToGallery,
                navigateToDrawing = { navigateToDrawing(detailState.notePad.id, it) },

                )
        }
        AddBottomSheet2(
            show = showModalState,
            currentColor = detailState.notePad.color,
            currentImage = detailState.notePad.background,
            isNoteCheck = detailState.notePad.isCheck,
            saveImage = editViewModel::saveImage,
            saveVoice = editViewModel::saveVoice,
            getPhotoUri = editViewModel::getPhotoUri,
            changeToCheckBoxes = editViewModel::changeToCheckBoxes,
            onDrawing = {
                navigateToDrawing(detailState.notePad.id, null)
            },
            onDismiss = { showModalState = false },
            isVoiceSupport = logics.isVoiceAvailable(),
        )

        NoteOptionBottomSheet(
            show = noteModalState,
            currentColor = detailState.notePad.color,
            currentImage = detailState.notePad.background,
            onLabel = {
                navigateToSelectLevel(
                    setOf(
                        detailState.notePad.id,
                    ),
                )
            },
            onDelete = editViewModel::onTrash,
            onCopy = editViewModel::copyNote,
            onSendNote = {logics.shareNote(detailState.notePad)},
            onDismissRequest = { noteModalState = false },
        )
        ColorAndImageBottomSheet(
            show = colorModalState,
            currentColor = detailState.notePad.color,
            currentImage = detailState.notePad.background,
            onColorClick = editViewModel::onColorChange,
            onImageClick = editViewModel::onImageChange,
            onDismissRequest = { colorModalState = false },
        )
//
        NotificationBottomSheet(
            show = noteficationModalState,
            onAlarm = editViewModel::setAlarm,
            showDialog = { showDialog = true },
            currentColor = detailState.notePad.color,
            currentImage = detailState.notePad.background,

            ) { noteficationModalState = false }
//
        NotificationDialogNew(
            initState = editViewModel.notificationUiState,
            showDialog = showDialog,
            onDismissRequest = { showDialog = false },
            isEdit = false,
            onSetAlarm = {},
            onDeleteAlarm = { },
        )
    }
}
