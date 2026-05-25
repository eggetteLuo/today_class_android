package com.eggetteluo.todayclass.util

import com.eggetteluo.todayclass.ui.theme.AppThemeMode

fun AppThemeMode.getDisplayName(): String {
    return when (this) {
        AppThemeMode.FOLLOW_SYSTEM -> "跟随系统"
        AppThemeMode.LIGHT -> "浅色模式"
        AppThemeMode.DARK -> "深色模式"
    }
}