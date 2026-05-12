package com.eggetteluo.todayclass.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,   // ID
    val code: String,   // 课程代码
    val name: String,   // 课程名称
    val type: String?,  // 课程类型，如必修课、选修课
    val remark: String? // 备注信息
)
