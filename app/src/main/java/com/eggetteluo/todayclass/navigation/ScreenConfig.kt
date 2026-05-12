package com.eggetteluo.todayclass.navigation

data class ScreenConfig(
    val showBottomBar: Boolean = true,
    val showFab: Boolean = false,
    val title: String = "",
    val topBarStyle: TopBarStyle = TopBarStyle.SMALL
)
