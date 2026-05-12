package com.eggetteluo.todayclass.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_time_rule")
data class CourseTimeRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,            // ID
    val buildingType: String,    // 楼栋类型
    val sectionNo: Int,          // 节次编号
    val startTime: String,       // 课程开始时间
    val endTime: String,         // 课程结束时间
    val remark: String?          // 说明
)