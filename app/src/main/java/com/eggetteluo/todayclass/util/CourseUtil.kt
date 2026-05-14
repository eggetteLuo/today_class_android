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

    /**
     * 解析周次字符串
     */
    fun parseWeeksString(weeksStr: String): List<Int> {
        val weeks = mutableSetOf<Int>() // Set 自动去重
        val parts = weeksStr.split(",", "，")
        for (part in parts) {
            val trimmed = part.trim()
            when {
                trimmed.contains("单") -> {
                    val range = trimmed.replace("单", "").split("-")
                    if (range.size == 2) {
                        val start = range[0].toIntOrNull() ?: continue
                        val end = range[1].toIntOrNull() ?: continue
                        for (i in start..end step 2) weeks.add(i)
                    }
                }

                trimmed.contains("双") -> {
                    val range = trimmed.replace("双", "").split("-")
                    if (range.size == 2) {
                        val start = range[0].toIntOrNull() ?: continue
                        val end = range[1].toIntOrNull() ?: continue
                        val actualStart = if (start % 2 != 0) start + 1 else start
                        for (i in actualStart..end step 2) weeks.add(i)
                    }
                }

                trimmed.contains("-") -> {
                    val range = trimmed.split("-")
                    if (range.size == 2) {
                        val start = range[0].toIntOrNull() ?: continue
                        val end = range[1].toIntOrNull() ?: continue
                        for (i in start..end) weeks.add(i)
                    }
                }

                else -> trimmed.toIntOrNull()?.let { weeks.add(it) }
            }
        }
        return weeks.sorted()
    }

    /**
     * 解析节次名
     */
    fun sectionNameToInt(sectionName: String): Int {
        return when (sectionName) {
            "第一节" -> 1; "第二节" -> 2; "第三节" -> 3; "第四节" -> 4
            "第五节" -> 5; "第六节" -> 6; "第七节" -> 7; "第八节" -> 8
            "第九节" -> 9; "第十节" -> 10; "第十一节" -> 11; "第十二节" -> 12
            else -> 1 // 默认兜底
        }
    }

    /**
     * 根据教务系统的上课地点（如："上茶苑教9栋401"、"芭蕉苑教16栋203"、"体育馆"），
     * 自动判断是属于单数楼时间（ODD）还是双数楼时间（EVEN）
     */
    fun parseBuildingType(location: String): String {
        // 如果包含体育馆或田径场，直接算作单数楼作息
        if (location.contains("体育馆") || location.contains("田径场")) {
            return "ODD"
        }

        // 使用正则提取出楼栋数字。例如从 "上茶苑教9栋401" 中提取出数字 "9"
        // 匹配 "教X栋" 里面的数字 X
        val regex = """教(\d+)栋""".toRegex()
        val matchResult = regex.find(location)

        if (matchResult != null) {
            val buildingNum = matchResult.groupValues[1].toIntOrNull()
            if (buildingNum != null) {
                // 判断奇偶
                return if (buildingNum % 2 != 0) "ODD" else "EVEN"
            }
        }

        // 兜底策略：如果都匹配不到（比如只写了 "实训室"），默认使用单数楼作息
        return "ODD"
    }

}