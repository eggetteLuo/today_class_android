package com.eggetteluo.todayclass.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "course_schedule_week",
    foreignKeys = [
        ForeignKey(
            entity = CourseScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE // 级联删除：删除排课安排时，连带删除关联的具体周次数据
        )
    ],
    indices = [
        Index(value = ["scheduleId"])
    ]
)
data class CourseScheduleWeekEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,          // ID
    val scheduleId: Long,      // 上课安排编号 (外键)
    val weekNo: Int,           // 具体上课周次
    val remark: String?        // 说明
)