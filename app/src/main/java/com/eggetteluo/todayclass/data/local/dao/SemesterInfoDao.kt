package com.eggetteluo.todayclass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eggetteluo.todayclass.data.local.entity.SemesterInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SemesterInfoDao {

    // 插入学期
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: SemesterInfoEntity): Long

    // 更新学期
    @Update
    suspend fun updateSemester(semester: SemesterInfoEntity)

    // 获取当前处于激活状态的学期
    @Query("SELECT * FROM semester_info WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentSemester(): Flow<SemesterInfoEntity?>

    // 将所有学期的 isCurrent 设置为 false（通常用于切换当前学期前重置状态）
    @Query("UPDATE semester_info SET isCurrent = 0")
    suspend fun clearCurrentSemesterStatus()

    // 根据学期名称查找学期
    @Query("SELECT * FROM semester_info WHERE name = :semesterName")
    suspend fun getSemesterByName(semesterName: String): SemesterInfoEntity?

}