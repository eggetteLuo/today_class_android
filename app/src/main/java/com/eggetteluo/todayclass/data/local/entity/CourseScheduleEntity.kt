package com.eggetteluo.todayclass.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "course_schedule",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE // 级联删除：删除课程时，连带删除该课程的排课
        ),
        ForeignKey(
            entity = CourseTimeRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["ruleId"],
            onDelete = ForeignKey.CASCADE // 级联删除：删除时间规则时，连带删除相关排课
        ),
        ForeignKey(
            entity = SemesterInfoEntity::class,
            parentColumns = ["id"],
            childColumns = ["semesterId"],
            onDelete = ForeignKey.CASCADE // 级联删除：删除学期时，连带删除该学期的所有排课
        )
    ],
    indices = [
        Index(value = ["courseId"]),
        Index(value = ["ruleId"]),
        Index(value = ["semesterId"])
    ]
)
data class CourseScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,               // ID
    val courseId: Long,             // 课程编号 (外键)
    val ruleId: Long,               // 上课时间规则编号 (外键)
    val semesterId: Long,           // 学期编号 (外键)
    val teacherName: String,        // 教师名称
    val weekDay: Int,               // 星期
    val startSection: Int,          // 开始节次
    val endSection: Int,            // 结束节次
    val campusName: String?,        // 区域名称
    val buildingName: String?,      // 楼栋名称
    val buildingType: String?,      // 楼栋类型
    val roomNo: String?,            // 教室号
    val classRoom: String,          // 完整上课地点
    val rawText: String             // Excel单元格原始文本
)