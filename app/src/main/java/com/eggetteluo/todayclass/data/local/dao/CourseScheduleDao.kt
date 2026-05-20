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
    // 注意：使用 @Relation 进行嵌套查询时，必须加上 @Transaction 注解保证数据一致性
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

    @Query("DELETE FROM course_schedule WHERE semesterId = :semesterId")
    fun deleteSchedulesBySemesterId(semesterId: Long)

}