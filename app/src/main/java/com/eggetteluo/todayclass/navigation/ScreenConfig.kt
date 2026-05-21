package com.eggetteluo.todayclass.navigation

data class ScreenConfig(
    val showBottomBar: Boolean = true,
    val title: String = "",
    val topBarStyle: TopBarStyle = TopBarStyle.SMALL,
    val fabStyle: FabStyle = FabStyle.NONE
)
