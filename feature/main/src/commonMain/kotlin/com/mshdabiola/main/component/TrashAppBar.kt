package com.mshdabiola.main.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.drawable.SynIcons
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import synapse.feature.main.generated.resources.Res
import synapse.feature.main.generated.resources.modules_designsystem_empty_trash


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

@Composable
fun TrashAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onHamburgerMenuClick: () -> Unit = {},

    onDeleteAllTrash: () -> Unit = {},


    ) {
    TopAppBar(
        modifier=modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = onHamburgerMenuClick,
                modifier = Modifier.testTag("main:topbar_hamburger_menu_button"),
            ) {
                Icon(imageVector = SynIcons.Menu, contentDescription = "menu")
            }
        },
        title = {
            Text(text = "Trash")
        },
        subtitle = {},
        actions = {
            var showDropDown by remember {
                mutableStateOf(false)
            }
            Box {
                IconButton(
                    onClick = { showDropDown = true },
                    modifier = Modifier.testTag("main:topbar_more_options_button"),
                ) {
                    Icon(SynIcons.MoreVert, contentDescription = "more")
                }
                DropdownMenu(
                    expanded = showDropDown,
                    onDismissRequest = { showDropDown = false },
                    modifier = Modifier.testTag("main:topbar_trash_empty_options_dropdown"),
                ) {
                    DropdownMenuItem(
                        modifier = Modifier.testTag("main:topbar_empty_trash_menu_item"),
                        text = { Text(text = stringResource(Res.string.modules_designsystem_empty_trash)) },
                        onClick = {
                            showDropDown = false
                            onDeleteAllTrash()
                        },
                    )
                }
            }
        },
        titleHorizontalAlignment =
            Alignment.CenterHorizontally,
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),

        )
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun TrashAppBarPreview() {
    TrashAppBar()
}
