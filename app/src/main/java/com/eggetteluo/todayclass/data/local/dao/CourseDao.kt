package com.eggetteluo.todayclass.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eggetteluo.todayclass.data.local.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    // 插入课程信息
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    // 更新课程信息
    @Update
    suspend fun updateCourse(course: CourseEntity)

    // 删除课程信息
    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    // 获取所有课程列表，使用 Flow 监听数据库变化
    @Query("SELECT * FROM course")
    fun getAllCourses(): Flow<List<CourseEntity>>

    // 根据 ID 查询单门课程
    @Query("SELECT * FROM course WHERE id = :courseId")
    suspend fun getCourseById(courseId: Long): CourseEntity?

    // 根据课程代码查询单门课程
    @Query("SELECT * FROM course WHERE code = :courseCode")
    suspend fun getCourseByCode(courseCode: String): CourseEntity?

}