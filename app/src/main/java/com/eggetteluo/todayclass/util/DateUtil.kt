package com.eggetteluo.todayclass.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

object DateUtil {

    /**
     * 根据当前是第几周，自动推算学期的开始和结束时间戳
     * @param currentWeek 用户输入的当前周数 (例如 3)
     * @param totalWeeks 学期总周数 (默认 20 周)
     * @return Pair<Long, Long> (开始时间戳, 结束时间戳)
     */
    fun calculateSemesterDates(currentWeek: Int, totalWeeks: Int = 20): Pair<Long, Long> {
        // 获取今天的日期
        val today = LocalDate.now()

        // 找到本周的周一
        val thisMonday = today.with(DayOfWeek.MONDAY)

        // 逆推开学第一周的周一
        val firstWeekMonday = thisMonday.minusWeeks((currentWeek - 1).toLong())

        // 顺推学期结束周的星期日
        val lastWeekSunday = firstWeekMonday.plusWeeks(totalWeeks.toLong()).minusDays(1)

        // 转换为毫秒时间戳
        val zoneId = ZoneId.systemDefault()
        val startTimestamp = firstWeekMonday.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endTimestamp = lastWeekSunday.atStartOfDay(zoneId).toInstant().toEpochMilli()

        return Pair(startTimestamp, endTimestamp)
    }

}