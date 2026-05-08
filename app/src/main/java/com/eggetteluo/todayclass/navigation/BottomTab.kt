package com.eggetteluo.todayclass.navigation

import androidx.navigation3.runtime.NavKey

sealed class BottomTab(
    val title: String,
    val route: NavKey
) {
    data object Home : BottomTab("看今日", HomeRoute)
    data object Week : BottomTab("周课表", WeekRoute)
    data object Setting : BottomTab("设置", SettingRoute)
}