package com.eggetteluo.todayclass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleEntity
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleWeekEntity
import com.eggetteluo.todayclass.data.local.relation.ScheduleWithDetails
import com.eggetteluo.todayclass.data.model.TodayCourseDetail
import com.eggetteluo.todayclass.data.model.WeeklyCourseDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseScheduleDao {

    // 插入单个排课记录
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: CourseScheduleEntity): Long

    // 批量插入上课周次
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleWeeks(weeks: List<CourseScheduleWeekEntity>)

    // 获取某个学期的所有课程安排，并自动填充关联的课程、时间规则和周次
    @Transaction
    @Query("SELECT * FROM course_schedule WHERE semesterId = :semesterId")
    fun getSchedulesWithDetailsBySemester(semesterId: Long): Flow<List<ScheduleWithDetails>>

    // 获取某天（星期几）的所有排课记录
    @Transaction
    @Query("SELECT * FROM course_schedule WHERE semesterId = :semesterId AND weekDay = :weekDay")
    fun getSchedulesByDay(semesterId: Long, weekDay: Int): Flow<List<ScheduleWithDetails>>

    // 删除特定排课
    @Query("DELETE FROM course_schedule WHERE id = :scheduleId")
    suspend fun deleteScheduleById(scheduleId: Long)

    // 获取今日课表
    @Query(
        """
        SELECT 
            s.id AS scheduleId,
            c.name AS courseName,
            c.code AS courseCode,
            c.teacherName,
            s.classRoom,
            s.section,
            r.startTime,
            r.endTime
        FROM course_schedule AS s
        INNER JOIN course AS c ON s.courseId = c.id
        INNER JOIN course_schedule_week AS w ON s.id = w.scheduleId
        LEFT JOIN course_time_rule AS r ON s.ruleId = r.id
        WHERE s.semesterId = :semesterId 
            AND s.weekDay = :dayOfWeek 
            AND w.weekNo = :currentWeek
        ORDER BY s.section ASC
    """
    )
    fun getTodayCourses(
        semesterId: Long,
        currentWeek: Int,
        dayOfWeek: Int
    ): Flow<List<TodayCourseDetail>>

    // 获取指定周次的完整周课表
    @Query(
        """
        SELECT 
            s.id AS scheduleId,
            c.name AS courseName,
            c.code AS courseCode,
            c.teacherName,
            s.classRoom,
            s.weekDay,
            s.section,
            r.startTime,
            r.endTime
        FROM course_schedule AS s
        INNER JOIN course AS c ON s.courseId = c.id
        INNER JOIN course_schedule_week AS w ON s.id = w.scheduleId
        LEFT JOIN course_time_rule AS r ON s.ruleId = r.id
        WHERE s.semesterId = :semesterId 
            AND w.weekNo = :currentWeek
        ORDER BY s.weekDay ASC, s.section ASC
    """
    )
    fun getWeeklyCourses(
        semesterId: Long,
        currentWeek: Int
    ): Flow<List<WeeklyCourseDetail>>

    @Query("DELETE FROM course_schedule WHERE semesterId = :semesterId")
    fun deleteSchedulesBySemesterId(semesterId: Long)

    // 获取单条排课记录的完整详情
    @Transaction
    @Query("SELECT * FROM course_schedule WHERE id = :scheduleId")
    suspend fun getScheduleWithDetailsById(scheduleId: Long): ScheduleWithDetails?

}