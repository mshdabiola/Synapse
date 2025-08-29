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
package com.mshdabiola.designsystem.drawable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.automirrored.outlined.Note
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.automirrored.rounded.MenuOpen
import androidx.compose.material.icons.automirrored.rounded.Note
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FormatColorReset
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardVoice
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Note
import androidx.compose.material.icons.outlined.NotificationAdd
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Note
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SystemUpdateAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.vectorResource
import synapse.core.designsystem.generated.resources.Res
import synapse.core.designsystem.generated.resources.modules_designsystem_asset_1
import synapse.core.designsystem.generated.resources.modules_designsystem_asset_10
import synapse.core.designsystem.generated.resources.modules_designsystem_asset_2
import synapse.core.designsystem.generated.resources.modules_designsystem_asset_3
import synapse.core.designsystem.generated.resources.modules_designsystem_asset_4
import synapse.core.designsystem.generated.resources.modules_designsystem_asset_5
import synapse.core.designsystem.generated.resources.modules_designsystem_asset_6
import synapse.core.designsystem.generated.resources.modules_designsystem_asset_7
import synapse.core.designsystem.generated.resources.modules_designsystem_asset_8
import synapse.core.designsystem.generated.resources.modules_designsystem_asset_9

object SynIcons {

    val Check = Icons.Rounded.Check
    val Language = Icons.Rounded.Language
    val ExpandLess = Icons.Rounded.ExpandLess
    val ExpandMore = Icons.Rounded.ExpandMore
    val LightMode = Icons.Rounded.LightMode
    val Contrast = Icons.Rounded.Contrast
    val DarkMode = Icons.Rounded.DarkMode
    val Add = Icons.Rounded.Add
    val ArrowBack = Icons.AutoMirrored.Rounded.ArrowBack


    val Note = Icons.AutoMirrored.Rounded.Note
    val NoteOutlined = Icons.AutoMirrored.Outlined.Note

    val About = Icons.Rounded.Info

    val Settings = Icons.Rounded.Settings
    val SettingsOutlined = Icons.Outlined.Settings
    val Notification = Icons.Rounded.Notifications
    val NotificationOutlined = Icons.Outlined.Notifications
    val Archive = Icons.Rounded.Archive
    val ArchiveOutlined = Icons.Outlined.Archive
    val Delete = Icons.Rounded.Delete
    val DeleteOutlined = Icons.Outlined.Delete
    val Label = Icons.AutoMirrored.Rounded.Label
    val LabelOutlined = Icons.AutoMirrored.Outlined.Label


    val Menu = Icons.Rounded.Menu
    val MenuOpen = Icons.AutoMirrored.Rounded.MenuOpen

    val Update = Icons.Rounded.SystemUpdateAlt
    val Appearance = Icons.Rounded.DisplaySettings

    val BugReport = Icons.Rounded.BugReport
    val Faq = Icons.AutoMirrored.Rounded.Chat

    val AppIcon = com.mshdabiola.designsystem.drawable.AppIcon

    val Alarm = Icons.Outlined.Alarm
    val Repeat = Icons.Outlined.Repeat
    val RestoreFromTrash = Icons.Outlined.Restore
    val Edit = Icons.Outlined.Edit
    val ViewAgenda = Icons.Outlined.ViewAgenda
    val GridView = Icons.Outlined.GridView
    val CheckBoxOutlineBlank = Icons.Outlined.CheckBoxOutlineBlank
    val Undo = Icons.AutoMirrored.Outlined.Undo
    val Redo = Icons.AutoMirrored.Outlined.Redo
    val AccessTime = Icons.Outlined.AccessTime
    val Share = Icons.Outlined.Share
    val ContentCopy = Icons.Outlined.ContentCopy
    val Unarchive = Icons.Outlined.Unarchive
    val PushPinD = Icons.Default.PushPin
    val PushPin = Icons.Outlined.PushPin
    val NotificationAdd = Icons.Outlined.NotificationAdd
    val AddBox = Icons.Outlined.AddBox
    val ColorLens = Icons.Outlined.ColorLens
    val Clear = Icons.Outlined.Clear
    val PauseCircle = Icons.Outlined.PauseCircle
    val PlayCircle = Icons.Outlined.PlayCircle
    val ImageNotSupported = Icons.Outlined.ImageNotSupported
    val Done = Icons.Outlined.Done
    val FormatColorReset = Icons.Outlined.FormatColorReset
    val PhotoCamera = Icons.Outlined.PhotoCamera
    val Brush = Icons.Outlined.Brush
    val CheckBox = Icons.Outlined.CheckBox
    val Image = Icons.Outlined.Image
    val KeyboardVoice = Icons.Outlined.KeyboardVoice
    val Cancel = Icons.Outlined.Close
    val MoreVert = Icons.Outlined.MoreVert
    val Search = Icons.Outlined.Search
    val Info = Icons.Outlined.Info
    val Link = Icons.Outlined.Link

    val More = Icons.Outlined.ExpandMore
    val Less = Icons.Outlined.ExpandLess

    val Refresh = Icons.Filled.Refresh

    val DateRange = Icons.Default.DateRange

    @Composable
    fun getBackGround(index: Int): ImageVector {
        return when (index) {
            1 -> vectorResource(Res.drawable.modules_designsystem_asset_1)
            2 -> vectorResource(Res.drawable.modules_designsystem_asset_2)
            3 -> vectorResource(Res.drawable.modules_designsystem_asset_3)
            4 -> vectorResource(Res.drawable.modules_designsystem_asset_4)
            5 -> vectorResource(Res.drawable.modules_designsystem_asset_5)
            6 -> vectorResource(Res.drawable.modules_designsystem_asset_6)
            7 -> vectorResource(Res.drawable.modules_designsystem_asset_7)
            8 -> vectorResource(Res.drawable.modules_designsystem_asset_8)
            9 -> vectorResource(Res.drawable.modules_designsystem_asset_9)
            10 -> vectorResource(Res.drawable.modules_designsystem_asset_10)
            else -> vectorResource(Res.drawable.modules_designsystem_asset_1)
        }
    }
}
