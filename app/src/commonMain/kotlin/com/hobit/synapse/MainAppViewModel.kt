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
package com.hobit.synapse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.hobit.synapse.MainActivityUiState.Loading
import com.hobit.synapse.MainActivityUiState.Success
import com.mshdabiola.data.repository.ContentManager
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.NetworkRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.model.ReleaseInfo
import com.mshdabiola.model.UpdateException
import com.mshdabiola.model.UserSettings
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteDisplayCategory
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainAppViewModel(
    private val userDataRepository: UserDataRepository,
    private val networkRepository: NetworkRepository,
    private val labelRepository: LabelRepository,
    private val contentManager: ContentManager,
    private val logger: Logger,
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> =
        combine(userDataRepository.userSettings, labelRepository.getAll()) { userSettings, labels ->
            Success(userSettings, labels)
        }
            .stateIn(
                scope = viewModelScope,
                initialValue = Loading,
                started = SharingStarted.WhileSubscribed(5_000),
            )

    /**
     * Asynchronously fetches the latest release information for the given app version.
     *
     * The function launches a coroutine in the ViewModel's scope and first reads stored user settings.
     * If the user's `showUpdateDialog` setting is true, it delegates to the network repository to
     * retrieve release info, passing the user's `updateFromPreRelease` preference as `allowPreRelease`.
     * If `showUpdateDialog` is false, it immediately returns a `ReleaseInfo.Error` with the message
     * "Update dialog is disabled".
     *
     * @param currentVersion The current app version string to compare against releases.
     * @return A [Deferred] that completes with the fetched [ReleaseInfo] or an error placeholder.
     */
    fun getLatestReleaseInfo(currentVersion: String): Deferred<ReleaseInfo> {
        return viewModelScope.async {
            val userSettings = userDataRepository.userSettings.first()
            if (userSettings.showUpdateDialog) {
                networkRepository.getLatestReleaseInfo(
                    currentVersion = currentVersion,
                    allowPreRelease = userSettings.updateFromPreRelease,
                )
            } else {
                ReleaseInfo.Error(UpdateException("Update dialog is disabled"))
            }
        }
    }

    fun pictureUri(): String {
        return contentManager.pictureUri()
    }
    fun copyImageToInternal(uri: String): String {
        return contentManager.saveImage(uri)
    }

    fun setMainData(noteDisplayCategory: NoteDisplayCategory) {
        viewModelScope.launch {
            userDataRepository.setNoteCategory(noteDisplayCategory)
        }
    }

    fun log(message: String) {
        logger.i(message)
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(
        val userSettings: UserSettings,
        val labels: List<Label>,
    ) : MainActivityUiState
}
