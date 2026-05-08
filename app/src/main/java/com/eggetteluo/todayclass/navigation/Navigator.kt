package com.eggetteluo.todayclass.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

class Navigator {

    // 导航
    val backStack = mutableStateListOf<NavKey>(HomeRoute)

    val currentTab: BottomTab
        get() = when (backStack.lastOrNull()) {
            WeekRoute -> BottomTab.Week
            SettingRoute -> BottomTab.Setting
            else -> BottomTab.Home
        }

    fun switchTab(tab: BottomTab) {
        if (backStack.lastOrNull() != tab.route) {
            backStack.clear()
            backStack.add(tab.route)
        }
    }

    fun navigate(route: NavKey) {
        backStack.add(route)
    }

    fun back() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

}
