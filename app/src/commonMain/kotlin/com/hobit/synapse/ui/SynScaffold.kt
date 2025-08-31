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
package com.hobit.synapse.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hobit.synapse.app.generated.resources.Res
import com.hobit.synapse.app.generated.resources.add_content_description
import com.hobit.synapse.app.generated.resources.brand_content_description
import com.hobit.synapse.app.generated.resources.fab_add_note_text
import com.hobit.synapse.app.generated.resources.modules_designsystem_create_new_label
import com.hobit.synapse.app.generated.resources.modules_designsystem_drawing
import com.hobit.synapse.app.generated.resources.modules_designsystem_edit
import com.hobit.synapse.app.generated.resources.modules_designsystem_image
import com.hobit.synapse.app.generated.resources.modules_designsystem_labels
import com.hobit.synapse.app.generated.resources.modules_designsystem_list
import com.hobit.synapse.app.generated.resources.modules_designsystem_voice
import com.hobit.synapse.app.generated.resources.rail_action_collapse
import com.hobit.synapse.app.generated.resources.rail_action_expand
import com.hobit.synapse.app.generated.resources.rail_state_collapsed
import com.hobit.synapse.app.generated.resources.rail_state_expanded
import com.hobit.synapse.app.generated.resources.route
import com.mshdabiola.designsystem.component.CustomWideNavigationRailItem
import com.mshdabiola.designsystem.component.SynTextButton
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.main.navigation.Main
import com.mshdabiola.model.BuildConfig
import com.mshdabiola.model.note.Label
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.note.NoteType
import com.mshdabiola.model.testtag.SynScaffoldTestTags
import com.mshdabiola.setting.navigation.Setting
import com.mshdabiola.ui.LocalSharedTransitionScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SynScaffold(
    modifier: Modifier = Modifier,
    appState: SynAppState,
    noteDisplayCategory: NoteDisplayCategory,
    labels: List<Label> = emptyList(),
    isVoiceAvailable: Boolean,

    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    onNavigation: (NoteDisplayCategory) -> Unit = {},
    navigateToLevel: (Boolean) -> Unit = {},
    onAddNote: (NoteType) -> Unit={},
    content: @Composable (PaddingValues) -> Unit,
) {
    val sharedScope = LocalSharedTransitionScope.current

    val topDestination = remember {
        setOf(
            TopLevelRoute(
                route = Route.Main(NoteDisplayCategory()),
                selectedIcon = SynIcons.Note,
                unselectedIcon = SynIcons.NoteOutlined,
                label = 0,
            ),
            TopLevelRoute(
                route = Route.Main(
                    NoteDisplayCategory(
                        noteCategory = NoteCategory.REMINDER,
                    ),
                ),
                selectedIcon = SynIcons.Notification,
                unselectedIcon = SynIcons.NotificationOutlined,
                label = 1,
            ),

        )
    }

    val lastDestination = remember {
        setOf(
            TopLevelRoute(
                route = Route.Main(
                    NoteDisplayCategory(
                        noteCategory = NoteCategory.ARCHIVE,
                    ),
                ),
                selectedIcon = SynIcons.Archive,
                unselectedIcon = SynIcons.ArchiveOutlined,
                label = 2,
            ),
            TopLevelRoute(
                route = Route.Main(
                    NoteDisplayCategory(
                        noteCategory = NoteCategory.TRASH,
                    ),
                ),
                selectedIcon = SynIcons.Delete,
                unselectedIcon = SynIcons.DeleteOutlined,
                label = 3,
            ),
            TopLevelRoute(
                route = Route.Setting,
                selectedIcon = SynIcons.Settings,
                unselectedIcon = SynIcons.SettingsOutlined,
                label = 4,
            ),

        )
    }

    val levels = listOf(Main, Setting)

    val currentDestination = appState.navController
        .currentBackStackEntryAsState().value?.destination
    val isMain = remember(currentDestination) {
        currentDestination?.hasRoute(Main::class) == true
    }
    val isTopDestination = remember(currentDestination) {
        levels.any {
            currentDestination
                ?.hasRoute(it::class)
                ?: false
        }
    }

    with(sharedScope) {
        if (appState is Compact) {
            ModalNavigationDrawer(
                modifier = modifier.testTag(SynScaffoldTestTags.MODAL_NAVIGATION_DRAWER),
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier
                            .width(300.dp)
                            .testTag(SynScaffoldTestTags.MODAL_DRAWER_SHEET),
                        drawerState = appState.drawerState,
                    ) {
                        DrawerContent(
                            modifier = Modifier.padding(16.dp),
                            appState = appState,
                            isMain = isMain,
                            topDestination = topDestination,
                            lastDestination = lastDestination,
                            labels = labels,
                            noteDisplayCategory = noteDisplayCategory,
                            onNavigation = onNavigation,
                            navigateToLevel = navigateToLevel,
                            onAddNote = onAddNote,
                            isVoiceAvailable = isVoiceAvailable,

                        )
                    }
                },
                drawerState = appState.drawerState,
                gesturesEnabled = isTopDestination,
            ) {
                Scaffold(
                    modifier = Modifier.testTag(SynScaffoldTestTags.SCAFFOLD_CONTENT_AREA + "_compact"),
                    containerColor = containerColor,
                    contentWindowInsets = contentWindowInsets,
                    contentColor = contentColor,
                    topBar = topBar,
                    bottomBar = bottomBar,
                    snackbarHost = snackbarHost,
                    floatingActionButton = {
                        AnimatedVisibility(isMain) {
                            Fab(
                                appState = appState,
                                onAddNote = onAddNote,
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.safeDrawing)
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState("note_-1"),
                                        animatedVisibilityScope = this,
                                    ),
                                isVoiceAvailable = isVoiceAvailable,

                                )
                        }
                    },
                ) { paddingValues ->
                    content(paddingValues)
                }
            }
        } else {
            PermanentNavigationDrawer(
                modifier = modifier.testTag(SynScaffoldTestTags.PERMANENT_NAVIGATION_DRAWER),
                drawerContent = {
                    if (isTopDestination) {
                        if (appState is Medium) {
                            WideNavigationRail(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .testTag(SynScaffoldTestTags.WIDE_NAVIGATION_RAIL),
                                state = appState.wideNavigationRailState,
                                colors = WideNavigationRailDefaults.colors(containerColor = containerColor),
                                header = {
                                    val expand = stringResource(Res.string.rail_state_expanded)
                                    val collapse = stringResource(Res.string.rail_state_collapsed)
                                    IconButton(
                                        modifier = Modifier
                                            .padding(start = 24.dp)
                                            .semantics {
                                                stateDescription =
                                                    if (appState.wideNavigationRailState.currentValue ==
                                                        WideNavigationRailValue.Expanded
                                                    ) {
                                                        expand
                                                    } else {
                                                        collapse
                                                    }
                                            }
                                            .testTag(SynScaffoldTestTags.RAIL_TOGGLE_BUTTON),
                                        onClick = {
                                            if (appState.wideNavigationRailState.targetValue ==
                                                WideNavigationRailValue.Expanded
                                            ) {
                                                appState.collapse()
                                            } else {
                                                appState.expand()
                                            }
                                        },
                                    ) {
                                        if (appState.wideNavigationRailState.targetValue ==
                                            WideNavigationRailValue.Expanded
                                        ) {
                                            Icon(SynIcons.MenuOpen, stringResource(Res.string.rail_action_collapse))
                                        } else {
                                            Icon(SynIcons.Menu, stringResource(Res.string.rail_action_expand))
                                        }
                                    }
                                },
                            ) {
                                DrawerContent(
                                    appState = appState,
                                    isMain = isMain,
                                    topDestination = topDestination,
                                    lastDestination = lastDestination,
                                    labels = labels,
                                    noteDisplayCategory = noteDisplayCategory,
                                    onNavigation = onNavigation,
                                    navigateToLevel = navigateToLevel,
                                    onAddNote = onAddNote,
                                    isVoiceAvailable = isVoiceAvailable,

                                )
                            }
                        }
                        if (appState is Expand) {
                            PermanentDrawerSheet(
                                drawerContainerColor = containerColor,
                                modifier = Modifier
                                    .width(300.dp)
                                    .testTag(SynScaffoldTestTags.PERMANENT_DRAWER_SHEET),
                            ) {
                                DrawerContent(
                                    modifier = Modifier.padding(16.dp),
                                    appState = appState,
                                    isMain = isMain,
                                    topDestination = topDestination,
                                    lastDestination = lastDestination,
                                    labels = labels,
                                    noteDisplayCategory = noteDisplayCategory,
                                    onNavigation = onNavigation,
                                    navigateToLevel = navigateToLevel,
                                    onAddNote = onAddNote,
                                    isVoiceAvailable = isVoiceAvailable,

                                )
                            }
                        }
                    }
                },
            ) {
                Scaffold(
                    modifier = Modifier.testTag(SynScaffoldTestTags.SCAFFOLD_CONTENT_AREA + "_permanent"),
                    containerColor = containerColor,
                    contentWindowInsets = contentWindowInsets,
                    contentColor = contentColor,
                    topBar = topBar,
                    bottomBar = bottomBar,
                    snackbarHost = snackbarHost,
                ) { paddingValues ->
                    content(paddingValues)
                }
            }
        }
    }
}

//
// @OptIn(ExperimentalMaterial3ExpressiveApi::class)
// @Preview
// @Composable
// fun KmtScaffoldPreview() {
//    val navController = rememberNavController().apply {
//        graph =
//            createGraph(startDestination = Main) {
//                composable<Main> { }
//                composable<Detail> { }
//                composable<Setting> { }
//            }
//    }
//    val appState = Expand(
//        navController = navController,
//        snackbarHostState = SnackbarHostState(),
//        coroutineScope = rememberCoroutineScope(),
//    )
//
//    SharedTransitionContainer {
//        KmtScaffold(appState = appState, noteDisplayCategory = NoteDisplayCategory()) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(it),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Text(
//                    modifier = Modifier.padding(16.dp),
//                    text =
//                    "Note: This demo is best shown in portrait mode, as landscape mode" +
//                        " may result in a compact height in certain devices. For any" +
//                        " compact screen dimensions, use a Navigation Bar instead.",
//                )
//            }
//        }
//    }
// }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    appState: SynAppState,
    isMain: Boolean,
    isVoiceAvailable: Boolean,
    noteDisplayCategory: NoteDisplayCategory,
    topDestination: Set<TopLevelRoute>,
    lastDestination: Set<TopLevelRoute>,
    labels: List<Label> = emptyList(),
    onNavigation: (NoteDisplayCategory) -> Unit = {},
    navigateToLevel: (Boolean) -> Unit = {},
    onAddNote: (NoteType) -> Unit,
) {
    val scrollState = rememberScrollState()
    val routeArray = stringArrayResource(Res.array.route)

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .testTag(SynScaffoldTestTags.DrawerContentTestTags.DRAWER_CONTENT_COLUMN),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AnimatedVisibility(appState !is Medium) {
            Row(
                modifier = Modifier.testTag(SynScaffoldTestTags.DrawerContentTestTags.BRAND_ROW),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .testTag(SynScaffoldTestTags.DrawerContentTestTags.BRAND_ICON),
                    imageVector = SynIcons.AppIcon,
                    contentDescription = stringResource(Res.string.brand_content_description),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    BuildConfig.BRAND_NAME, // Assuming KmtStrings.brand is already a resource or intended to be so.
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag(SynScaffoldTestTags.DrawerContentTestTags.BRAND_TEXT),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(64.dp))
        }
        AnimatedVisibility(appState !is Compact && isMain) {
            val fabModifier = if (appState is Medium) {
                Modifier.padding(start = 24.dp)
            } else {
                Modifier
            }
            Fab(
                modifier = fabModifier, // Modifier for FAB is passed, its internal tags handle specifics
                appState = appState,
                onAddNote = onAddNote,
                isVoiceAvailable = isVoiceAvailable,
            )

            Spacer(modifier = Modifier.height(64.dp))
        }
        topDestination.forEach { item ->

            if (appState is Medium) {
                CustomWideNavigationRailItem(
                    modifier = Modifier.testTag(
                        SynScaffoldTestTags.DrawerContentTestTags.wideNavigationRailItemTag(item.route),
                    ),
                    railExpanded = appState.wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded,
                    icon = {
                        val imageVector =
                            if (appState.isInCurrentRoute(item.route, noteDisplayCategory)) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            }
                        Icon(
                            imageVector = imageVector,
                            contentDescription = routeArray.getOrElse(item.label, { "" }),
                        )
                    },
                    label = { Text(routeArray.getOrElse(item.label, { "" })) },
                    selected = appState.isInCurrentRoute(item.route, noteDisplayCategory),
                    onClick = {
                        if (item.route is Route.Main) {
                            onNavigation(item.route.noteDisplayCategory)
                        }
                        appState.navigateTopRoute(item.route)
                    },
                )
            } else {
                NavigationDrawerItem(
                    modifier = Modifier.testTag(
                        SynScaffoldTestTags
                            .DrawerContentTestTags.navigationItemTag(item.route),
                    ),
                    icon = {
                        val imageVector =
                            if (appState.isInCurrentRoute(item.route, noteDisplayCategory)) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            }
                        Icon(
                            imageVector = imageVector,
                            contentDescription = routeArray.getOrElse(item.label, { "" }),
                        )
                    },
                    label = { Text(routeArray.getOrElse(item.label, { "" })) },
                    selected = appState.isInCurrentRoute(item.route, noteDisplayCategory),
                    onClick = {
                        if (item.route is Route.Main) {
                            onNavigation(item.route.noteDisplayCategory)
                        }
                        appState.navigateTopRoute(item.route)
                        if (appState is Compact) {
                            appState.coroutineScope.launch {
                                appState.onDrawerToggle()
                            }
                        }
                    },
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (appState.isExpanded && labels.isNotEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f)
                        .testTag(SynScaffoldTestTags.DrawerContentTestTags.LABELS_SECTION_HEADER),
                    text = stringResource(Res.string.modules_designsystem_labels),
                )
                SynTextButton(
                    modifier = Modifier.testTag(SynScaffoldTestTags.DrawerContentTestTags.EDIT_LABELS_ITEM),
                    onClick = { navigateToLevel(false) },
                    label = stringResource(Res.string.modules_designsystem_edit),
                )
            }
            labels.forEachIndexed { index, item ->
                val topLevelRoute = TopLevelRoute(
                    route = Route.Main(
                        NoteDisplayCategory(
                            labelId = item.id,
                            noteCategory = NoteCategory.LABEL,
                        ),
                    ),
                    selectedIcon = SynIcons.Label,
                    unselectedIcon = SynIcons.LabelOutlined,
                    label = 1,
                )
                if (appState is Medium) {
                    CustomWideNavigationRailItem(
                        modifier = Modifier.testTag(
                            SynScaffoldTestTags.DrawerContentTestTags
                                .wideNavigationRailItemTag(topLevelRoute.route),
                        ),
                        railExpanded = appState.wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded,
                        icon = {
                            val imageVector =
                                if (appState.isInCurrentRoute(topLevelRoute.route, noteDisplayCategory)) {
                                    topLevelRoute.selectedIcon
                                } else {
                                    topLevelRoute.unselectedIcon
                                }
                            Icon(
                                imageVector = imageVector,
                                contentDescription = item.name,
                            )
                        },
                        label = { Text(item.name) },
                        selected = appState.isInCurrentRoute(topLevelRoute.route, noteDisplayCategory),
                        onClick = {
                            if (topLevelRoute.route is Route.Main) {
                                onNavigation(topLevelRoute.route.noteDisplayCategory)
                            }
                            appState.navigateTopRoute(topLevelRoute.route)
                        },
                    )
                } else {
                    NavigationDrawerItem(
                        modifier = Modifier.testTag(
                            SynScaffoldTestTags
                                .DrawerContentTestTags.navigationItemTag(topLevelRoute.route),
                        ),
                        icon = {
                            val imageVector =
                                if (appState.isInCurrentRoute(topLevelRoute.route, noteDisplayCategory)) {
                                    topLevelRoute.selectedIcon
                                } else {
                                    topLevelRoute.unselectedIcon
                                }
                            Icon(
                                imageVector = imageVector,
                                contentDescription = item.name,
                            )
                        },
                        label = { Text(item.name) },
                        selected = appState.isInCurrentRoute(topLevelRoute.route, noteDisplayCategory),
                        onClick = {
                            if (topLevelRoute.route is Route.Main) {
                                onNavigation(topLevelRoute.route.noteDisplayCategory)
                            }
                            appState.navigateTopRoute(topLevelRoute.route)
                            if (appState is Compact) {
                                appState.coroutineScope.launch {
                                    appState.onDrawerToggle()
                                }
                            }
                        },
                    )
                }
            }
            NavigationDrawerItem(
                modifier = Modifier
                    .testTag(
                        SynScaffoldTestTags
                            .DrawerContentTestTags.navigationItemTag("create_new_label"),
                    ),
                icon = {
                    Icon(imageVector = SynIcons.Add, contentDescription = "")
                },
                label = { Text(text = stringResource(Res.string.modules_designsystem_create_new_label)) },
                selected = false,
                onClick = { navigateToLevel(true) },
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        lastDestination.forEach { item ->

            if (appState is Medium) {
                CustomWideNavigationRailItem(
                    modifier = Modifier.testTag(
                        SynScaffoldTestTags.DrawerContentTestTags.wideNavigationRailItemTag(item.route),
                    ),
                    railExpanded = appState.wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded,
                    icon = {
                        val imageVector =
                            if (appState.isInCurrentRoute(item.route, noteDisplayCategory)) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            }
                        Icon(
                            imageVector = imageVector,
                            contentDescription = routeArray.getOrElse(item.label, { "" }),
                        )
                    },
                    label = { Text(routeArray.getOrElse(item.label, { "" })) },
                    selected = appState.isInCurrentRoute(item.route, noteDisplayCategory),
                    onClick = {
                        if (item.route is Route.Main) {
                            onNavigation(item.route.noteDisplayCategory)
                        }
                        appState.navigateTopRoute(item.route)
                    },
                )
            } else {
                NavigationDrawerItem(
                    modifier = Modifier.testTag(
                        SynScaffoldTestTags
                            .DrawerContentTestTags.navigationItemTag(item.route),
                    ),
                    icon = {
                        val imageVector =
                            if (appState.isInCurrentRoute(item.route, noteDisplayCategory)) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            }
                        Icon(
                            imageVector = imageVector,
                            contentDescription = routeArray.getOrElse(item.label, { "" }),
                        )
                    },
                    label = { Text(routeArray.getOrElse(item.label, { "" })) },
                    selected = appState.isInCurrentRoute(item.route, noteDisplayCategory),
                    onClick = {
                        appState.navigateTopRoute(item.route)
                        if (item.route is Route.Main) {
                            onNavigation(item.route.noteDisplayCategory)
                        }
                        if (appState is Compact) {
                            appState.coroutineScope.launch {
                                appState.onDrawerToggle()
                            }
                        }
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Fab(
    modifier: Modifier = Modifier, // The passed modifier might already include sharedBounds
    appState: SynAppState,
    isVoiceAvailable: Boolean,
    onAddNote: (NoteType) -> Unit,


) {
    val size = SplitButtonDefaults.MediumContainerHeight

    AnimatedContent(
        targetState = appState is Medium &&
            appState.wideNavigationRailState.targetValue == WideNavigationRailValue.Collapsed,

        modifier = modifier.testTag(SynScaffoldTestTags.FabTestTags.FAB_ANIMATED_CONTENT),
        // Tag the AnimatedContent wrapper
    ) { isCollapsedMediumFab ->
        if (isCollapsedMediumFab) {
            SplitButtonDefaults.TrailingButton(
                checked = true,
                onCheckedChange = {
                    onAddNote(NoteType.Text)
                },
                modifier = Modifier
                    .heightIn(size)
                    .testTag(SynScaffoldTestTags.FabTestTags.SMALL_FAB),
                shapes = SplitButtonDefaults.trailingButtonShapesFor(size),
                contentPadding = SplitButtonDefaults.trailingButtonContentPaddingFor(size),
            ) {
                Icon(
                    imageVector = SynIcons.Add,
                    contentDescription = stringResource(Res.string.add_content_description),
                    modifier = Modifier.testTag(SynScaffoldTestTags.FabTestTags.FAB_ADD_ICON),
                )
            }
        } else {
            var checked by remember { mutableStateOf(false) }
            Box {
                SplitButtonLayout(
                    leadingButton = {
                        SplitButtonDefaults.LeadingButton(
                            onClick = {
                                onAddNote(NoteType.Text)
                            },
                            modifier = Modifier
                                .heightIn(size)
                                .testTag(SynScaffoldTestTags.FabTestTags.EXTENDED_FAB),
                            shapes = SplitButtonDefaults.leadingButtonShapesFor(size),
                            contentPadding = SplitButtonDefaults.leadingButtonContentPaddingFor(size),
                        ) {
                            Icon(
                                imageVector = SynIcons.Add,
                                contentDescription = stringResource(Res.string.add_content_description),
                                modifier = Modifier
                                    .size(SplitButtonDefaults.leadingButtonIconSizeFor(size))
                                    .testTag(SynScaffoldTestTags.FabTestTags.FAB_ADD_ICON),
                            )

                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))

                            Text(
                                stringResource(Res.string.fab_add_note_text),
                                style = ButtonDefaults.textStyleFor(size),
                                modifier = Modifier.testTag(SynScaffoldTestTags.FabTestTags.FAB_ADD_TEXT),
                            )
                        }
                    },
                    trailingButton = {
                        SplitButtonDefaults.TrailingButton(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            modifier =
                            Modifier.heightIn(size).semantics {
                                stateDescription = if (checked) "Expanded" else "Collapsed"
                                contentDescription = "Toggle Button"
                            },
                            shapes = SplitButtonDefaults.trailingButtonShapesFor(size),
                            contentPadding = SplitButtonDefaults.trailingButtonContentPaddingFor(size),
                        ) {
                            val rotation: Float by
                                animateFloatAsState(
                                    targetValue = if (checked) 180f else 0f,
                                    label = "Trailing Icon Rotation",
                                )
                            Icon(
                                SynIcons.KeyboardArrowDown,
                                modifier =
                                Modifier.size(SplitButtonDefaults.trailingButtonIconSizeFor(size))
                                    .graphicsLayer { this.rotationZ = rotation },
                                contentDescription = "Localized description",
                            )
                        }
                    },
                )
                DropdownMenu(
                    modifier = Modifier.testTag(SynScaffoldTestTags.FabTestTags.DROPDOWN_MENU),
                    expanded = checked,
                    onDismissRequest = { checked = false },
                    offset = DpOffset(96.dp, 0.dp),
                ) {
                    DropdownMenuItem(
                        modifier = Modifier.testTag(SynScaffoldTestTags.FabTestTags.DROPDOWN_ITEM_LIST),
                        text = { Text(stringResource(Res.string.modules_designsystem_list)) },
                        onClick = {
                            checked = false
                            onAddNote(NoteType.List)
                        },
                        leadingIcon = {
                            Icon(
                                SynIcons.CheckBox,
                                contentDescription = stringResource(Res.string.modules_designsystem_list),
                            )
                        },
                    )
                    DropdownMenuItem(
                        modifier = Modifier.testTag(SynScaffoldTestTags.FabTestTags.DROPDOWN_ITEM_DRAWING),
                        text = { Text(stringResource(Res.string.modules_designsystem_drawing)) },
                        onClick = {
                            checked = false
                            onAddNote(NoteType.Drawing)
                        },
                        leadingIcon = {
                            Icon(
                                SynIcons.Brush,
                                contentDescription = stringResource(Res.string.modules_designsystem_drawing),
                            )
                        },
                    )
                    DropdownMenuItem(
                        modifier = Modifier.testTag(SynScaffoldTestTags.FabTestTags.DROPDOWN_ITEM_VOICE),
                        text = { Text(stringResource(Res.string.modules_designsystem_voice)) },
                        onClick = {
                            checked = false
                            onAddNote(NoteType.Voice)
                        },
                        enabled = isVoiceAvailable,
                        leadingIcon = {
                            Icon(
                                SynIcons.KeyboardVoice,
                                contentDescription = stringResource(Res.string.modules_designsystem_voice),
                            )
                        },
                    )
                    DropdownMenuItem(
                        modifier = Modifier.testTag(SynScaffoldTestTags.FabTestTags.DROPDOWN_ITEM_IMAGE),
                        text = { Text(stringResource(Res.string.modules_designsystem_image)) },
                        onClick = {
                            checked = false
                            onAddNote(NoteType.Image)
                        },
                        leadingIcon = {
                            Icon(
                                SynIcons.Image,
                                contentDescription = stringResource(Res.string.modules_designsystem_image),
                            )
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun FabPreview() {
    val appState = Expand(
        navController = rememberNavController(),
        snackbarHostState = SnackbarHostState(),
        coroutineScope = rememberCoroutineScope(),
    )
    Fab(appState = appState, onAddNote = {},isVoiceAvailable = true)
}

