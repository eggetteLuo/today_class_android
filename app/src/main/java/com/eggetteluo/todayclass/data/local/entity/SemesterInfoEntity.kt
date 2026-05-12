package com.eggetteluo.todayclass.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "semester_info")
data class SemesterInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,                  // ID
    val name: String,                  // 学期名称
    val startData: Long,               // 开始日期
    val endData: Long,                 // 结束日期
    val totalWeeks: Int,               // 学期总周数
    val weekStartDay: Int = 1,         // 每周开始日，1表示为周一，7表示为周日
    val isCurrent: Boolean = true,     // 是否为当前学期
    val currentWeekOverride: Boolean?, // 当前周手动修改值
    val remark: String?                // 说明
)
