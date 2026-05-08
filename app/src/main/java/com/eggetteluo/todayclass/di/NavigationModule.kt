package com.eggetteluo.todayclass.di

import com.eggetteluo.todayclass.navigation.MainRoute
import com.eggetteluo.todayclass.navigation.Navigator
import com.eggetteluo.todayclass.ui.app.MainScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {

    singleOf(::Navigator)

    navigation<MainRoute> {
        MainScreen()
    }

}