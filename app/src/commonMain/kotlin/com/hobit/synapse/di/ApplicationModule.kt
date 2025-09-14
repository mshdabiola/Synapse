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
package com.hobit.synapse.di

import com.hobit.synapse.MainAppViewModel
import com.mshdabiola.data.di.dataModule
import com.mshdabiola.detail.detailModule
import com.mshdabiola.domain.di.domainModule
import com.mshdabiola.draw.drawModule
import com.mshdabiola.label.labelModule
import com.mshdabiola.main.mainModule
import com.mshdabiola.select.selectModule
import com.mshdabiola.setting.settingModule
import com.mshdabiola.ui.getLoggerWithTag
import com.mshdabiola.view.viewModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        includes(
            domainModule,
            dataModule,
            detailModule,
            mainModule,
            settingModule,
            drawModule,
            viewModule,
            labelModule,
            selectModule,
        )
        viewModel {
            MainAppViewModel(
                userDataRepository = get(),
                networkRepository = get(),
                labelRepository = get(),
                contentManager = get(),
                logger = getLoggerWithTag("MainAppViewModel"),
            )
        }
    }
