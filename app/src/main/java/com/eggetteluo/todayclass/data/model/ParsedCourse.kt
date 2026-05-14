package com.eggetteluo.todayclass.data.model

data class ParsedCourse(
    val dayOfWeek: Int,     // 周几
    val sectionName: String,// 第几节课
    val courseName: String, // 课程名称
    val courseCode: String, // 课程代码
    val teacher: String,    // 教师名称
    val weeks: String,      // 周次信息
    val location: String,   // 上课地点
    val cellText: String    // 单元格原始数据
)
