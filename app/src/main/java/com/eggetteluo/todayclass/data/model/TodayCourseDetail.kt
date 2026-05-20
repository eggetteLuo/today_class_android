package com.eggetteluo.todayclass.data.model

data class TodayCourseDetail(
    val scheduleId: Long,
    val courseName: String,
    val courseCode: String,
    val teacherName: String,
    val classRoom: String,
    val section: Int,
    val startTime: String?,
    val endTime: String?
)