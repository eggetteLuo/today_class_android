package com.eggetteluo.todayclass.util

import com.eggetteluo.todayclass.data.model.ParsedCourse

object CourseUtil {

    /**
     * 根据单元格原始数据内容，初始解析为对象数据
     * @param cellText 单元格文本
     * @param dayOfWeek 星期几 (来自列索引 colIndex)
     * @param sectionName 第几节课 (来自行首 rowMap[0])
     */
    fun parseCellText(
        cellText: String,
        dayOfWeek: Int,
        sectionName: String
    ): List<ParsedCourse> {
        val courses = mutableListOf<ParsedCourse>()

        // ==========================================
        // (.*?)\s*       -> Group 1: 匹配课程名称，忽略尾部空格
        // \((.*?)\)\s*   -> Group 2: 匹配括号里的课程代码
        // \((.*?)\)      -> Group 3: 匹配括号里的教师姓名
        // [\s\u00A0]*    -> 核心：跨越课程名和地点之间大量的换行符、半角空格和全角空格(\u00A0)
        // \((.*?)\)      -> Group 4: 匹配最后括号里的周次和地点组合
        // ==========================================
        val regex = """(.*?)\s*\((.*?)\)\s*\((.*?)\)[\s\u00A0]*\((.*?)\)""".toRegex()

        // 查找单元格中的所有课程块
        val matches = regex.findAll(cellText)

        for (match in matches) {
            val originalRawText = match.value.trim()

            val courseName = match.groupValues[1].trim()
            val courseCode = match.groupValues[2].trim()
            val teacher = match.groupValues[3].trim()

            // Group 4 拿到的是 "13-15 上茶苑教9栋401（软件工程实训室）"
            // 我们需要用空格把它劈开，前面是周次，后面是地点
            val weeksAndLocation = match.groupValues[4].trim()
            val parts = weeksAndLocation.split(Regex("\\s+"), limit = 2)

            val weeks = parts.getOrNull(0) ?: ""
            val location = parts.getOrNull(1) ?: ""

            courses.add(
                ParsedCourse(
                    dayOfWeek = dayOfWeek,
                    sectionName = sectionName,
                    courseName = courseName,
                    courseCode = courseCode,
                    teacher = teacher,
                    weeks = weeks,
                    location = location,
                    cellText = originalRawText
                )
            )
        }

        return courses
    }

}