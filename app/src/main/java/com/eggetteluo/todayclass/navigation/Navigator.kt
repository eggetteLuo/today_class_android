package com.eggetteluo.todayclass.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey

class Navigator {

    // 导航
    val backStack = mutableStateListOf<NavKey>(HomeRoute)

    // 当前 BottomTab
    var currentTab by mutableStateOf<BottomTab>(BottomTab.Home)

    fun switchTab(tab: BottomTab) {
        currentTab = tab
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