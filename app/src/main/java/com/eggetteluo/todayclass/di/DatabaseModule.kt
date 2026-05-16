package com.eggetteluo.todayclass.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eggetteluo.todayclass.data.local.AppDatabase
import com.eggetteluo.todayclass.data.local.DefaultTimeRules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        lateinit var database: AppDatabase
        database = Room.databaseBuilder(androidContext(), AppDatabase::class.java, "today_class_db")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val timeRuleDao = database.courseTimeRuleDao()
                        timeRuleDao.insertTimeRules(DefaultTimeRules.allRules)
                    }
                }
            })
            .build()
        database
    }

    single { get<AppDatabase>().courseDao() }

    single { get<AppDatabase>().semesterInfoDao() }

    single { get<AppDatabase>().courseScheduleDao() }

    single { get<AppDatabase>().courseTimeRuleDao() }

}