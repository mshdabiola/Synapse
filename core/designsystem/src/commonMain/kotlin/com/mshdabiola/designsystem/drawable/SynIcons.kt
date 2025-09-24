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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.automirrored.rounded.MenuOpen
import androidx.compose.material.icons.automirrored.rounded.Note
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FormatColorReset
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardVoice
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NotificationAdd
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SystemUpdateAlt
import androidx.compose.material.icons.rounded.Unarchive
import androidx.compose.material.icons.rounded.ViewAgenda
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

    val KeyboardArrowDown = Icons.Rounded.KeyboardArrowDown
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

    val Alarm = Icons.Rounded.Alarm
    val Repeat = Icons.Rounded.Repeat
    val RestoreFromTrash = Icons.Rounded.Restore
    val Edit = Icons.Rounded.Edit
    val ViewAgenda = Icons.Rounded.ViewAgenda
    val GridView = Icons.Rounded.GridView
    val CheckBoxOutlineBlank = Icons.Outlined.CheckBoxOutlineBlank
    val Undo = Icons.AutoMirrored.Rounded.Undo
    val Redo = Icons.AutoMirrored.Rounded.Redo
    val AccessTime = Icons.Rounded.AccessTime
    val Share = Icons.Rounded.Share
    val ContentCopy = Icons.Rounded.ContentCopy
    val Unarchive = Icons.Rounded.Unarchive
    val PushPin = Icons.Rounded.PushPin
    val PushPinOutlined = Icons.Outlined.PushPin
    val NotificationAdd = Icons.Rounded.NotificationAdd
    val AddBox = Icons.Rounded.AddBox
    val ColorLens = Icons.Rounded.ColorLens
    val Clear = Icons.Rounded.Clear
    val PauseCircle = Icons.Rounded.PauseCircle
    val PlayCircle = Icons.Rounded.PlayCircle
    val ImageNotSupported = Icons.Rounded.ImageNotSupported
    val Done = Icons.Rounded.Done
    val FormatColorReset = Icons.Rounded.FormatColorReset
    val PhotoCamera = Icons.Rounded.PhotoCamera
    val Brush = Icons.Rounded.Brush
    val CheckBox = Icons.Rounded.CheckBox
    val Image = Icons.Rounded.Image
    val KeyboardVoice = Icons.Rounded.KeyboardVoice
    val Cancel = Icons.Rounded.Close
    val MoreVert = Icons.Rounded.MoreVert
    val Search = Icons.Rounded.Search
    val Info = Icons.Rounded.Info
    val Link = Icons.Rounded.Link

    val More = Icons.Rounded.ExpandMore
    val Less = Icons.Rounded.ExpandLess

    val Refresh = Icons.Filled.Refresh

    val DateRange = Icons.Default.DateRange

    @Composable
    fun getBackGround(index: Int): ImageVector {
        return when (index) {
            0 -> vectorResource(Res.drawable.modules_designsystem_asset_1)
            1 -> vectorResource(Res.drawable.modules_designsystem_asset_2)
            2 -> vectorResource(Res.drawable.modules_designsystem_asset_3)
            3 -> vectorResource(Res.drawable.modules_designsystem_asset_4)
            4 -> vectorResource(Res.drawable.modules_designsystem_asset_5)
            5 -> vectorResource(Res.drawable.modules_designsystem_asset_6)
            6 -> vectorResource(Res.drawable.modules_designsystem_asset_7)
            7 -> vectorResource(Res.drawable.modules_designsystem_asset_8)
            8 -> vectorResource(Res.drawable.modules_designsystem_asset_9)
            9 -> vectorResource(Res.drawable.modules_designsystem_asset_10)
            else -> vectorResource(Res.drawable.modules_designsystem_asset_1)
        }
    }
}
