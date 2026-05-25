package com.eggetteluo.todayclass.feature.week.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eggetteluo.todayclass.data.model.WeeklyCourseDetail
import com.eggetteluo.todayclass.ui.theme.LocalDarkTheme
import com.eggetteluo.todayclass.util.CourseColorUtil

/**
 * 周课表网格中的单个课程卡片组件
 *
 * @param course 课程详细信息
 * @param onClick 卡片点击回调
 */
@Composable
fun WeeklyCourseCard(
    course: WeeklyCourseDetail,
    onClick: () -> Unit
) {
    // 获取当前系统是否为深色模式
    val isDarkTheme = LocalDarkTheme.current

    // 使用 remember 缓存颜色计算结果，避免在页面滚动或重组时重复计算
    val (hashBgColor, hashTextColor) = remember(course.courseName, isDarkTheme) {
        CourseColorUtil.getCourseColorVariant(course.courseName, isDarkTheme)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .background(hashBgColor)
            .clickable(onClick = onClick)
            // 合并无障碍语义：让屏幕阅读器能够一次性完整朗读课程信息，而不是拆成多个零碎的文本
            .semantics {
                val roomText = course.classRoom.ifBlank { "未安排教室" }
                val teacherText = course.teacherName.ifBlank { "未安排教师" }
                contentDescription = "${course.courseName}, $roomText, $teacherText"
            }
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            // 居中对齐，让网格中的课程名称、教室、教师信息看起来更紧凑
            verticalArrangement = Arrangement.Center
        ) {
            // 课程名称 (加粗，作为主要视觉焦点)
            Text(
                text = course.courseName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                ),
                color = hashTextColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 4, // 限制最大行数，防止超长课程名撑爆网格
                overflow = TextOverflow.Ellipsis
            )

            // 教室信息 (带有 @ 前缀，略微降低透明度以弱化层级)
            if (course.classRoom.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "@${course.classRoom}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    ),
                    color = hashTextColor.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 教师信息
            if (course.teacherName.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = course.teacherName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    ),
                    color = hashTextColor.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}