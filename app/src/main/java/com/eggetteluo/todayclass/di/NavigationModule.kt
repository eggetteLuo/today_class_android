package com.eggetteluo.todayclass.di

import com.eggetteluo.todayclass.feature.home.HomeScreen
import com.eggetteluo.todayclass.feature.home.HomeViewModel
import com.eggetteluo.todayclass.feature.schedule.ScheduleScreen
import com.eggetteluo.todayclass.feature.schedule.ScheduleViewModel
import com.eggetteluo.todayclass.feature.setting.SettingScreen
import com.eggetteluo.todayclass.feature.upload.UploadScreen
import com.eggetteluo.todayclass.feature.week.WeekScreen
import com.eggetteluo.todayclass.navigation.HomeRoute
import com.eggetteluo.todayclass.navigation.Navigator
import com.eggetteluo.todayclass.navigation.ScheduleRoute
import com.eggetteluo.todayclass.navigation.SettingRoute
import com.eggetteluo.todayclass.navigation.UploadRoute
import com.eggetteluo.todayclass.navigation.WeekRoute
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {

    singleOf(::Navigator)

    navigation<HomeRoute> {
        val viewModel: HomeViewModel = koinViewModel()
        val navigator: Navigator = koinInject()
        HomeScreen(viewModel = viewModel, navigator = navigator)
    }

    navigation<WeekRoute> {
        WeekScreen()
    }

    navigation<SettingRoute> {
        SettingScreen()
    }

    navigation<UploadRoute> {
        UploadScreen()
    }

    navigation<ScheduleRoute> { route ->
        val courseId = route.scheduleId
        val viewModel: ScheduleViewModel = koinViewModel(
            key = courseId.toString(),
            parameters = { parametersOf(courseId) }
        )
        val navigator: Navigator = koinInject()
        ScheduleScreen(viewModel = viewModel, navigator = navigator)
    }

}