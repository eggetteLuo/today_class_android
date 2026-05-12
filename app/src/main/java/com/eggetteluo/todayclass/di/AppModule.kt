package com.eggetteluo.todayclass.di

import org.koin.dsl.module

val appModule = module {

    includes(navigationModule, databaseModule)

}