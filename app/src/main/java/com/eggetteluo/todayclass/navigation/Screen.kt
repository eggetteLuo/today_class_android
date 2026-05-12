package com.eggetteluo.todayclass.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {
    val config: ScreenConfig
}
