package com.mshdabiola.main.component

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mshdabiola.designsystem.drawable.SynIcons
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

@Composable
fun ReminderAppBar(
    modifier: Modifier = Modifier,
    isGrid: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onDisplayModeChange: () -> Unit = {},
    onHamburgerMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},

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
            Text(text = "Reminder")
        },
        subtitle = {},
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("main:topbar_search_button"),
            ) {
                Icon(
                    imageVector = SynIcons.Search,
                    contentDescription = "search",
                )
            }
            IconButton(
                onClick = { onDisplayModeChange() },
                modifier = Modifier.testTag("main:topbar_display_mode_button"),
            ) {
                if (!isGrid) {
                    Icon(imageVector = SynIcons.GridView, contentDescription = "grid")
                } else {
                    Icon(
                        imageVector = SynIcons.ViewAgenda,
                        contentDescription = "column",
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
fun ReminderAppBarPreview() {
    ReminderAppBar(
        isGrid = false,
    )
}
