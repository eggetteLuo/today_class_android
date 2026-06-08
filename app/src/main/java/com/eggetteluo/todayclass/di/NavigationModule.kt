package com.eggetteluo.todayclass.di

import com.eggetteluo.todayclass.feature.course.CourseManageScreen
import com.eggetteluo.todayclass.feature.course.CourseManageViewModel
import com.eggetteluo.todayclass.feature.home.HomeScreen
import com.eggetteluo.todayclass.feature.home.HomeViewModel
import com.eggetteluo.todayclass.feature.schedule.ScheduleScreen
import com.eggetteluo.todayclass.feature.schedule.ScheduleViewModel
import com.eggetteluo.todayclass.feature.semester.SemesterManageScreen
import com.eggetteluo.todayclass.feature.semester.SemesterManageViewModel
import com.eggetteluo.todayclass.feature.setting.SettingScreen
import com.eggetteluo.todayclass.feature.setting.SettingViewModel
import com.eggetteluo.todayclass.feature.upload.UploadScreen
import com.eggetteluo.todayclass.feature.upload.UploadViewModel
import com.eggetteluo.todayclass.feature.week.WeekScreen
import com.eggetteluo.todayclass.feature.week.WeekViewModel
import com.eggetteluo.todayclass.navigation.CourseManageRoute
import com.eggetteluo.todayclass.navigation.HomeRoute
import com.eggetteluo.todayclass.navigation.Navigator
import com.eggetteluo.todayclass.navigation.ScheduleRoute
import com.eggetteluo.todayclass.navigation.SemesterManageRoute
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
        HomeScreen(
            viewModel = viewModel,
            onUploadClick = { navigator.navigate(UploadRoute) },
            onAddCourseClick = { navigator.navigate(ScheduleRoute(-1L)) },
            onCourseManageClick = { navigator.navigate(CourseManageRoute) },
            onCourseClick = { scheduleId ->
                navigator.navigate(ScheduleRoute(scheduleId))
            }
        )
    }

    navigation<WeekRoute> {
        val viewModel: WeekViewModel = koinViewModel()
        val navigator: Navigator = koinInject()
        WeekScreen(
            viewModel = viewModel,
            onUploadClick = { navigator.navigate(UploadRoute) },
            onCourseClick = { scheduleId ->
                navigator.navigate(ScheduleRoute(scheduleId))
            }
        )
    }

    navigation<SettingRoute> {
        val viewModel: SettingViewModel = koinViewModel()
        val navigator: Navigator = koinInject()
        SettingScreen(
            viewModel = viewModel,
            onSemesterManageClick = { navigator.navigate(SemesterManageRoute) }
        )
    }

    navigation<UploadRoute> {
        val viewModel: UploadViewModel = koinViewModel()
        val navigator: Navigator = koinInject()
        UploadScreen(
            viewModel = viewModel,
            onBackClick = { navigator.back() }
        )
    }

    navigation<CourseManageRoute> {
        val viewModel: CourseManageViewModel = koinViewModel()
        val navigator: Navigator = koinInject()
        CourseManageScreen(
            viewModel = viewModel,
            onBackClick = { navigator.back() },
            onAddCourseClick = { navigator.navigate(ScheduleRoute(-1L)) }
        )
    }

    navigation<SemesterManageRoute> {
        val viewModel: SemesterManageViewModel = koinViewModel()
        val navigator: Navigator = koinInject()
        SemesterManageScreen(
            viewModel = viewModel,
            onBackClick = { navigator.back() }
        )
    }

    navigation<ScheduleRoute> { route ->
        val courseId = route.scheduleId
        val viewModel: ScheduleViewModel = koinViewModel(
            key = courseId.toString(),
            parameters = { parametersOf(courseId) }
        )
        val navigator: Navigator = koinInject()
        ScheduleScreen(
            viewModel = viewModel,
            onBackClick = { navigator.back() }
        )
    }

}
