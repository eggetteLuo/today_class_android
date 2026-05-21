package com.eggetteluo.todayclass.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : Screen {
    override val config = ScreenConfig(
        title = "今日课表",
        topBarStyle = TopBarStyle.MEDIUM,
        fabStyle = FabStyle.MENU
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
        topBarStyle = TopBarStyle.SMALL,
        showBottomBar = false
    )
}

@Serializable
data object ScheduleRoute : Screen {
    override val config = ScreenConfig(
        title = "课程详情",
        topBarStyle = TopBarStyle.SMALL,
        showBottomBar = false,
        fabStyle = FabStyle.TOOL
    )
}
