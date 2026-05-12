package com.eggetteluo.todayclass.di

import com.eggetteluo.todayclass.feature.home.HomeScreen
import com.eggetteluo.todayclass.feature.setting.SettingScreen
import com.eggetteluo.todayclass.feature.upload.UploadScreen
import com.eggetteluo.todayclass.feature.week.WeekScreen
import com.eggetteluo.todayclass.navigation.HomeRoute
import com.eggetteluo.todayclass.navigation.Navigator
import com.eggetteluo.todayclass.navigation.SettingRoute
import com.eggetteluo.todayclass.navigation.UploadRoute
import com.eggetteluo.todayclass.navigation.WeekRoute
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {

    singleOf(::Navigator)

    navigation<HomeRoute> {
        HomeScreen()
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

}