package com.eggetteluo.todayclass.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eggetteluo.todayclass.data.local.dao.CourseDao
import com.eggetteluo.todayclass.data.local.dao.CourseScheduleDao
import com.eggetteluo.todayclass.data.local.dao.CourseTimeRuleDao
import com.eggetteluo.todayclass.data.local.dao.SemesterInfoDao
import com.eggetteluo.todayclass.data.local.entity.CourseEntity
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleEntity
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleWeekEntity
import com.eggetteluo.todayclass.data.local.entity.CourseTimeRuleEntity
import com.eggetteluo.todayclass.data.local.entity.SemesterInfoEntity

@Database(
    entities = [
        CourseEntity::class,
        CourseScheduleEntity::class,
        CourseTimeRuleEntity::class,
        SemesterInfoEntity::class,
        CourseScheduleWeekEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun semesterInfoDao(): SemesterInfoDao
    abstract fun courseScheduleDao(): CourseScheduleDao
    abstract fun courseTimeRuleDao(): CourseTimeRuleDao
}