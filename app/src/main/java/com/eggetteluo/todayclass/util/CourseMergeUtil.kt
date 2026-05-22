package com.eggetteluo.todayclass.util

import com.eggetteluo.todayclass.data.model.WeeklyCourseDetail

/**
 * 专用于网格渲染的包装类，记录了合并后的起始节次和持续长度
 */
data class MergedCourse(
    val originalCourse: WeeklyCourseDetail,
    val startSection: Int,
    val duration: Int
)

/**
 * 遍历一维列表，将同一天、相邻节次且 (名字、地点、教师) 完全相同的课程合并。
 * 前提条件：传入的 courses 必须已经按 weekDay 和 section 排序。
 */
fun mergeCourses(courses: List<WeeklyCourseDetail>): List<MergedCourse> {
    if (courses.isEmpty()) return emptyList()

    val result = mutableListOf<MergedCourse>()
    val groupedByDay = courses.groupBy { it.weekDay }

    for ((_, dailyCourses) in groupedByDay) {
        if (dailyCourses.isEmpty()) continue

        var currentStartCourse = dailyCourses[0]
        var currentDuration = 1

        for (i in 1 until dailyCourses.size) {
            val nextCourse = dailyCourses[i]

            val isSameCourse = nextCourse.courseName == currentStartCourse.courseName
            val isSameRoom = nextCourse.classRoom == currentStartCourse.classRoom
            val isSameTeacher = nextCourse.teacherName == currentStartCourse.teacherName
            val isContinuous = nextCourse.section == currentStartCourse.section + currentDuration

            if (isSameCourse && isSameRoom && isSameTeacher && isContinuous) {
                currentDuration++
            } else {
                result.add(
                    MergedCourse(
                        originalCourse = currentStartCourse,
                        startSection = currentStartCourse.section,
                        duration = currentDuration
                    )
                )
                currentStartCourse = nextCourse
                currentDuration = 1
            }
        }
        result.add(
            MergedCourse(
                originalCourse = currentStartCourse,
                startSection = currentStartCourse.section,
                duration = currentDuration
            )
        )
    }

    return result
}
