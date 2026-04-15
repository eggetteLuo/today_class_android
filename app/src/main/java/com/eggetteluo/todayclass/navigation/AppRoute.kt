package com.eggetteluo.todayclass.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey

// 三个顶层页面
@Serializable
data object HomeRoute : AppRoute

@Serializable
data object WeekRoute : AppRoute

@Serializable
data object SettingRoute : AppRoute

@Serializable
data class HomeDetailRoute(val courseName: String) : AppRoute

@Serializable
data class WeekDetailRoute(val day: String) : AppRoute

@Serializable
data object SettingAboutRoute : AppRoute
