package com.eggetteluo.todayclass.util

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

/**
 * 课程颜色分配工具类
 * 采用单例模式，保证调色板在内存中只存在一份，避免 Compose 频繁重组时产生多余的内存分配。
 */
object CourseColorUtil {

    /**
     * 课程颜色主题数据类，用于同时存储浅色和深色模式下的颜色配置
     */
    data class CourseThemeColor(
        val lightBg: Color,
        val lightText: Color,
        val darkBg: Color,
        val darkText: Color
    )

    // 预设的颜色板 (包含浅色和深色模式的映射)
    private val CourseColorPalette = listOf(
        CourseThemeColor(
            lightBg = Color(0xFFE3F2FD), lightText = Color(0xFF1565C0), // 浅蓝
            darkBg = Color(0xFF11293C), darkText = Color(0xFF90CAF9)    // 深色下的蓝
        ),
        CourseThemeColor(
            lightBg = Color(0xFFF3E5F5), lightText = Color(0xFF6A1B9A), // 浅紫
            darkBg = Color(0xFF281433), darkText = Color(0xFFCE93D8)    // 深色下的紫
        ),
        CourseThemeColor(
            lightBg = Color(0xFFE8F5E9), lightText = Color(0xFF2E7D32), // 浅绿
            darkBg = Color(0xFF143017), darkText = Color(0xFFA5D6A7)    // 深色下的绿
        ),
        CourseThemeColor(
            lightBg = Color(0xFFFFF3E0), lightText = Color(0xFFEF6C00), // 浅橙
            darkBg = Color(0xFF3B200B), darkText = Color(0xFFFFCC80)    // 深色下的橙
        ),
        CourseThemeColor(
            lightBg = Color(0xFFFFEBEE), lightText = Color(0xFFC62828), // 浅红
            darkBg = Color(0xFF3A1518), darkText = Color(0xFFEF9A9A)    // 深色下的红
        ),
        CourseThemeColor(
            lightBg = Color(0xFFE0F7FA), lightText = Color(0xFF00838F), // 浅青
            darkBg = Color(0xFF113236), darkText = Color(0xFF80DEEA)    // 深色下的青
        )
    )

    /**
     * 根据课程名称获取固定的主题色
     * @param courseName 课程名称
     * @param isDarkTheme 当前系统是否处于深色模式
     * @return Pair<Color, Color> 第一项为背景色，第二项为文字/边框色
     */
    fun getCourseColorVariant(courseName: String, isDarkTheme: Boolean): Pair<Color, Color> {
        val defaultColor = CourseColorPalette[0]
        if (courseName.isBlank()) {
            return if (isDarkTheme) defaultColor.darkBg to defaultColor.darkText
            else defaultColor.lightBg to defaultColor.lightText
        }

        val hash = abs(courseName.hashCode())
        val themeColor = CourseColorPalette[hash % CourseColorPalette.size]

        return if (isDarkTheme) themeColor.darkBg to themeColor.darkText
        else themeColor.lightBg to themeColor.lightText
    }
}