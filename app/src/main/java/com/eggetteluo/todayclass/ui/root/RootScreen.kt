package com.eggetteluo.todayclass.ui.root

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.NavDisplay
import com.eggetteluo.todayclass.navigation.BottomTab
import com.eggetteluo.todayclass.navigation.Navigator
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun RootScreen() {
    val navigator: Navigator = koinInject()
    val snackbarHostState = remember { SnackbarHostState() }

    val tabs = listOf(BottomTab.Home, BottomTab.Week, BottomTab.Setting)

    val currentRoute = navigator.backStack.lastOrNull()
    val shouldShowBottomBar = tabs.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        CompositionLocalProvider(
            LocalSnackbarHostState provides snackbarHostState
        ) {
            NavDisplay(
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                backStack = navigator.backStack,
                onBack = { navigator.back() },
                entryProvider = koinEntryProvider()
            )
        }
    }
}
