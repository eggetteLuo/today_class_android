package com.eggetteluo.todayclass.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

sealed class BottomTab(
    val title: String,
    val route: NavKey,
    val icon: ImageVector
) {
    data object Home : BottomTab("看今日", HomeRoute, Icons.Default.Home)
    data object Week : BottomTab("周课表", WeekRoute, Icons.Default.ViewWeek)
    data object Setting : BottomTab("设置", SettingRoute, Icons.Default.Settings)
}
