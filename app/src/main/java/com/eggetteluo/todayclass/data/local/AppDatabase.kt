package com.eggetteluo.todayclass.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eggetteluo.todayclass.data.local.dao.CourseDao
import com.eggetteluo.todayclass.data.local.dao.CourseScheduleDao
import com.eggetteluo.todayclass.data.local.dao.CourseTimeRuleDao
import com.eggetteluo.todayclass.data.local.dao.SemesterInfoDao
import com.eggetteluo.todayclass.data.local.entity.CourseEntity
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleEntity
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleWeekEntity
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleWeekNoteEntity
import com.eggetteluo.todayclass.data.local.entity.CourseTimeRuleEntity
import com.eggetteluo.todayclass.data.local.entity.SemesterInfoEntity

@Database(
    entities = [
        CourseEntity::class,
        CourseScheduleEntity::class,
        CourseTimeRuleEntity::class,
        SemesterInfoEntity::class,
        CourseScheduleWeekEntity::class,
        CourseScheduleWeekNoteEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun semesterInfoDao(): SemesterInfoDao
    abstract fun courseScheduleDao(): CourseScheduleDao
    abstract fun courseTimeRuleDao(): CourseTimeRuleDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `course_schedule_week_note` (
                        `scheduleId` INTEGER NOT NULL,
                        `weekNo` INTEGER NOT NULL,
                        `note` TEXT NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`scheduleId`, `weekNo`),
                        FOREIGN KEY(`scheduleId`) REFERENCES `course_schedule`(`id`)
                            ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_course_schedule_week_note_scheduleId`
                    ON `course_schedule_week_note` (`scheduleId`)
                    """.trimIndent()
                )
            }
        }
    }
}
