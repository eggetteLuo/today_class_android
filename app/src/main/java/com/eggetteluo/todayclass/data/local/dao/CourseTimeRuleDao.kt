package com.eggetteluo.todayclass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eggetteluo.todayclass.data.local.entity.CourseTimeRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseTimeRuleDao {

    // 批量插入时间规则
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeRule(rules: List<CourseTimeRuleEntity>)

    // 获取所有时间规则
    @Query("SELECT * FROM course_time_rule")
    fun getAllTimeRules(): Flow<List<CourseTimeRuleEntity>>

    // 根据楼栋类型和节次查询时间规则
    @Query("SELECT * FROM course_time_rule WHERE buildingType = :buildingType AND sectionNo = :sectionNo")
    suspend fun getRuleByBuildingAndSection(buildingType: String, sectionNo: Int): CourseTimeRuleEntity?

}