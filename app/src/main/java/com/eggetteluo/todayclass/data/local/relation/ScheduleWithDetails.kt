package com.eggetteluo.todayclass.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.eggetteluo.todayclass.data.local.entity.CourseEntity
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleEntity
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleWeekEntity
import com.eggetteluo.todayclass.data.local.entity.CourseTimeRuleEntity

// 将一节课的完整信息组合在一起
data class ScheduleWithDetails(
    // 排课实体
    @Embedded val schedule: CourseScheduleEntity,
    // 关联课程信息
    @Relation(
        parentColumn = "courseId",
        entityColumn = "id"
    )
    val course: CourseEntity,
    // 关联时间规则
    @Relation(
        parentColumn = "ruleId",
        entityColumn = "id"
    )
    val timeRule: CourseTimeRuleEntity,
    // 关联上课周次
    @Relation(
        parentColumn = "id",
        entityColumn = "scheduleId"
    )
    val weeks: List<CourseScheduleWeekEntity>
)
