package com.eggetteluo.todayclass.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object MainRoute : NavKey

@Serializable
data object HomeRoute : NavKey

@Serializable
data object WeekRoute : NavKey

@Serializable
data object SettingRoute : NavKey
