package com.eggetteluo.todayclass.di

import com.eggetteluo.todayclass.feature.home.HomeViewModel
import com.eggetteluo.todayclass.feature.setting.SettingViewModel
import com.eggetteluo.todayclass.feature.upload.UploadViewModel
import com.eggetteluo.todayclass.feature.week.WeekViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModelOf(::UploadViewModel)

    viewModelOf(::HomeViewModel)

    viewModelOf(::WeekViewModel)

    viewModelOf(::SettingViewModel)

}