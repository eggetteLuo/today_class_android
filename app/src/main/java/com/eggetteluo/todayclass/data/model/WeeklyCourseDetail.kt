package com.eggetteluo.todayclass.data.model

data class WeeklyCourseDetail(
    val scheduleId: Long,
    val courseName: String,
    val courseCode: String,
    val teacherName: String,
    val classRoom: String,
    val weekDay: Int, // 周课表必须知道星期几才能定位横坐标
    val section: Int, // 节次，用于定位纵坐标
    val startTime: String?,
    val endTime: String?
)
