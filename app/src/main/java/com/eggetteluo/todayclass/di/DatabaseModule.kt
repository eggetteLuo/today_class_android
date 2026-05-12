package com.eggetteluo.todayclass.di

import androidx.room.Room
import com.eggetteluo.todayclass.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "today_class_db")
            .build()
    }

    single { get<AppDatabase>().courseDao() }

    single { get<AppDatabase>().semesterInfoDao() }

    single { get<AppDatabase>().courseScheduleDao() }

    single { get<AppDatabase>().courseTimeRuleDao() }

}