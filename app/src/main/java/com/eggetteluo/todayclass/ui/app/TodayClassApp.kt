package com.eggetteluo.todayclass.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.eggetteluo.todayclass.feature.home.HomeDetailScreen
import com.eggetteluo.todayclass.feature.home.HomeScreen
import com.eggetteluo.todayclass.feature.setting.SettingAboutScreen
import com.eggetteluo.todayclass.feature.setting.SettingScreen
import com.eggetteluo.todayclass.feature.week.WeekDetailScreen
import com.eggetteluo.todayclass.feature.week.WeekScreen
import com.eggetteluo.todayclass.navigation.HomeDetailRoute
import com.eggetteluo.todayclass.navigation.HomeRoute
import com.eggetteluo.todayclass.navigation.SettingAboutRoute
import com.eggetteluo.todayclass.navigation.SettingRoute
import com.eggetteluo.todayclass.navigation.WeekDetailRoute
import com.eggetteluo.todayclass.navigation.WeekRoute
import com.eggetteluo.todayclass.navigation.bottomTabs

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TodayClassApp() {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val homeBackStack = rememberNavBackStack(HomeRoute)
    val weekBackStack = rememberNavBackStack(WeekRoute)
    val settingBackStack = rememberNavBackStack(SettingRoute)

    val homeEntries = rememberHomeEntries(homeBackStack)
    val weekEntries = rememberWeekEntries(weekBackStack)
    val settingEntries = rememberSettingEntries(settingBackStack)

    val currentEntries = when (selectedTabIndex) {
        0 -> homeEntries
        1 -> weekEntries
        else -> settingEntries
    }

    val currentBackStack = when (selectedTabIndex) {
        0 -> homeBackStack
        1 -> weekBackStack
        else -> settingBackStack
    }
    val currentRoute = currentBackStack.lastOrNull()
    val canNavigateBack = currentBackStack.size > 1
    val isTopLevelRoute = currentRoute == HomeRoute || currentRoute == WeekRoute || currentRoute == SettingRoute
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (isTopLevelRoute) {
                LargeTopAppBar(
                    title = {
                        Column {
                            Text(
                                text = routeTitle(currentRoute),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = routeSubtitle(currentRoute),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(),
                    scrollBehavior = scrollBehavior
                )
            } else {
                TopAppBar(
                    title = {
                        Text(text = routeTitle(currentRoute))
                    },
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = { currentBackStack.removeLastOrNull() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "返回"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors()
                )
            }
        },
        bottomBar = {
            NavigationBar {
                bottomTabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        label = { Text(text = tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            entries = currentEntries,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = {
                if (currentBackStack.size > 1) {
                    currentBackStack.removeLastOrNull()
                } else if (selectedTabIndex != 0) {
                    selectedTabIndex = 0
                }
            }
        )
    }
}

@Composable
private fun rememberHomeEntries(backStack: NavBackStack<NavKey>): List<NavEntry<NavKey>> {
    val decorators = rememberTabDecorators()
    val providers = entryProvider<NavKey> {
        entry<HomeRoute> {
            HomeScreen(onOpenDetail = { courseName ->
                backStack.add(HomeDetailRoute(courseName))
            })
        }
        entry<HomeDetailRoute> { route ->
            HomeDetailScreen(courseName = route.courseName)
        }
    }
    return rememberDecoratedNavEntries(backStack, decorators, providers)
}

@Composable
private fun rememberWeekEntries(backStack: NavBackStack<NavKey>): List<NavEntry<NavKey>> {
    val decorators = rememberTabDecorators()
    val providers = entryProvider<NavKey> {
        entry<WeekRoute> {
            WeekScreen(onOpenDetail = { day ->
                backStack.add(WeekDetailRoute(day))
            })
        }
        entry<WeekDetailRoute> { route ->
            WeekDetailScreen(day = route.day)
        }
    }
    return rememberDecoratedNavEntries(backStack, decorators, providers)
}

@Composable
private fun rememberSettingEntries(backStack: NavBackStack<NavKey>): List<NavEntry<NavKey>> {
    val decorators = rememberTabDecorators()
    val providers = entryProvider<NavKey> {
        entry<SettingRoute> {
            SettingScreen(onOpenAbout = {
                backStack.add(SettingAboutRoute)
            })
        }
        entry<SettingAboutRoute> {
            SettingAboutScreen()
        }
    }
    return rememberDecoratedNavEntries(backStack, decorators, providers)
}

@Composable
private fun rememberTabDecorators(): List<NavEntryDecorator<NavKey>> {
    return listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator()
    )
}

private fun routeTitle(route: NavKey?): String {
    return when (route) {
        HomeRoute -> "看今日"
        is HomeDetailRoute -> route.courseName
        WeekRoute -> "周课表"
        is WeekDetailRoute -> "${route.day}详情"
        SettingRoute -> "设置"
        SettingAboutRoute -> "关于"
        else -> "TodayClass"
    }
}

private fun routeSubtitle(route: NavKey?): String {
    return when (route) {
        HomeRoute -> "今天的课程与安排"
        WeekRoute -> "本周课表快速浏览"
        SettingRoute -> "偏好、提醒与账号设置"
        else -> ""
    }
}
