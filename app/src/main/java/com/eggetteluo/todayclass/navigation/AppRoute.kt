package com.eggetteluo.todayclass.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : Screen {
    override val config = ScreenConfig(
        title = "今日课表",
        showFab = true,
        topBarStyle = TopBarStyle.MEDIUM
    )
}

@Serializable
data object WeekRoute : Screen {
    override val config = ScreenConfig(
        title = "周课表",
        topBarStyle = TopBarStyle.MEDIUM
    )
}

@Serializable
data object SettingRoute : Screen {
    override val config = ScreenConfig(
        title = "设置",
        topBarStyle = TopBarStyle.MEDIUM
    )
}

@Serializable
data object UploadRoute : Screen {
    override val config = ScreenConfig(
        title = "导入课表",
        showBottomBar = false,
        showFab = false,
        topBarStyle = TopBarStyle.SMALL
    )
}
