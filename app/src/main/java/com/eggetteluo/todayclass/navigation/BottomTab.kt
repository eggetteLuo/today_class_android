package com.eggetteluo.todayclass.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomTab(
    val route: AppRoute,
    val label: String,
    val icon: ImageVector
)

val bottomTabs = listOf(
    BottomTab(HomeRoute, "看今日", Icons.Outlined.Home),
    BottomTab(WeekRoute, "周课表", Icons.Outlined.ViewWeek),
    BottomTab(SettingRoute, "设置", Icons.Outlined.Settings)
)