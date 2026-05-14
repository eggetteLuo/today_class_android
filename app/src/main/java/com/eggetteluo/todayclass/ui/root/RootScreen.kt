package com.eggetteluo.todayclass.ui.root

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.ui.NavDisplay
import com.eggetteluo.todayclass.navigation.BottomTab
import com.eggetteluo.todayclass.navigation.Navigator
import com.eggetteluo.todayclass.navigation.Screen
import com.eggetteluo.todayclass.navigation.ScreenConfig
import com.eggetteluo.todayclass.navigation.TopBarStyle
import com.eggetteluo.todayclass.navigation.UploadRoute
import com.eggetteluo.todayclass.ui.components.FabMenu
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun RootScreen() {
    val navigator: Navigator = koinInject()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val tabs = listOf(BottomTab.Home, BottomTab.Week, BottomTab.Setting)

    val currentScreen = navigator.backStack.lastOrNull() as? Screen
    val config = currentScreen?.config ?: ScreenConfig()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            when (config.topBarStyle) {
                TopBarStyle.NONE -> Unit

                TopBarStyle.SMALL -> {
                    TopAppBar(
                        title = {
                            Text(config.title)
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        )
                    )
                }

                TopBarStyle.MEDIUM -> {
                    MediumTopAppBar(
                        title = {
                            Text(
                                text = config.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        )
                    )
                }
            }
        },
        bottomBar = {
            if (config.showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = navigator.backStack.lastOrNull() == tab.route,
                            onClick = {
                                navigator.switchTab(tab)
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(tab.title)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (config.showFab) {
                FabMenu(onUploadClick = {
                    navigator.navigate(UploadRoute)
                }, onAddClick = {

                }, onCourseClick = {

                })
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { padding ->
        CompositionLocalProvider(
            LocalSnackbarHostState provides snackbarHostState
        ) {
            NavDisplay(
                modifier = Modifier.padding(padding),
                backStack = navigator.backStack,
                onBack = { navigator.back() },
                entryProvider = koinEntryProvider()
            )
        }
    }
}
