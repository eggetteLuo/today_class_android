package com.eggetteluo.todayclass.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

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

    /**
     * 根据开学时间戳，计算今天是第几周、星期几
     * @return Pair<Int, Int> = Pair(当前周次, 星期几[1-7])
     */
    fun getCurrentWeekAndDay(startTimestamp: Long): Pair<Int, Int> {
        val today = LocalDate.now()
        val zoneId = ZoneId.systemDefault()

        // 如果没有开学时间，则返回第一周的今天
        if (startTimestamp == 0L) {
            return Pair(1, today.dayOfWeek.value)
        }

        // 将时间戳转为 LocalDate
        val startDate = java.time.Instant.ofEpochMilli(startTimestamp)
            .atZone(zoneId)
            .toLocalDate()

        // 计算今天和开学第一天差了多少天
        val daysBetween = ChronoUnit.DAYS.between(startDate, today)

        // 计算当前周次
        val currentWeek = if (daysBetween >= 0) {
            (daysBetween / 7).toInt() + 1
        } else {
            1
        }

        return Pair(currentWeek, today.dayOfWeek.value)
    }

}