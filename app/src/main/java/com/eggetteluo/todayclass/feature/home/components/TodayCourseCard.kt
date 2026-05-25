package com.eggetteluo.todayclass.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eggetteluo.todayclass.data.model.CourseStatus
import com.eggetteluo.todayclass.data.model.TodayCourseDetail
import com.eggetteluo.todayclass.util.CourseColorUtil
import com.eggetteluo.todayclass.util.DateUtil
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TodayCourseCard(course: TodayCourseDetail, onCardClick: () -> Unit) {
    // 获取当前系统是否为深色模式
    val isDarkTheme = isSystemInDarkTheme()

    // 维护当前时间的局部状态，用于驱动课程状态的变化
    var currentTime by remember {
        mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
    }

    // 每 10 秒刷新一次时间，确保上课/下课状态能够自动流转
    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000L)
            currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    // 使用 remember 缓存不变的计算结果，减少重组开销
    val startTime = remember(course.startTime) { course.startTime ?: "00:00" }
    val endTime = remember(course.endTime) { course.endTime ?: "23:59" }

    val status = DateUtil.getCourseStatus(startTime, endTime, currentTime)
    val isFinished = status == CourseStatus.FINISHED
    val isInProgress = status == CourseStatus.IN_PROGRESS

    // 统一从工具类获取哈希颜色，并依赖 courseName 缓存结果
    val (hashBgColor, hashTextColor) = remember(course.courseName, isDarkTheme) {
        CourseColorUtil.getCourseColorVariant(course.courseName, isDarkTheme)
    }

    // --- 动态边框计算 ---
    val cardBorderWidth by animateDpAsState(
        targetValue = if (isInProgress) 2.dp else 1.dp,
        label = "cardBorderWidth"
    )

    val cardBorderColor by animateColorAsState(
        targetValue = when (status) {
            CourseStatus.IN_PROGRESS -> hashTextColor // 上课中加深
            CourseStatus.FINISHED -> hashTextColor.copy(alpha = 0.15f) // 结束后变淡
            else -> hashTextColor.copy(alpha = 0.4f)
        },
        label = "cardBorderColor"
    )

    // --- 动态颜色计算 ---
    val cardContainerColor by animateColorAsState(
        targetValue = if (isFinished) hashBgColor.copy(alpha = 0.4f) else hashBgColor,
        label = "cardContainerColor"
    )

    val titleTextColor by animateColorAsState(
        targetValue = if (isFinished) hashTextColor.copy(alpha = 0.5f) else hashTextColor,
        label = "titleTextColor"
    )
    val iconAndSubTextColor by animateColorAsState(
        targetValue = if (isFinished) hashTextColor.copy(alpha = 0.4f) else hashTextColor.copy(alpha = 0.8f),
        label = "iconAndSubTextColor"
    )

    val sectionBgColor by animateColorAsState(
        targetValue = when {
            isInProgress -> hashTextColor
            isFinished -> hashBgColor.copy(alpha = 0.5f)
            else -> hashBgColor
        },
        label = "sectionBgColor"
    )
    val sectionTextColor by animateColorAsState(
        targetValue = when {
            isInProgress -> Color.White
            isFinished -> hashTextColor.copy(alpha = 0.6f)
            else -> hashTextColor
        },
        label = "sectionTextColor"
    )

    val timeTextColor by animateColorAsState(
        targetValue = if (isFinished) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface,
        label = "timeTextColor"
    )

    val timeLineColor by animateColorAsState(
        targetValue = if (isFinished) hashTextColor.copy(alpha = 0.15f) else hashTextColor.copy(
            alpha = 0.4f
        ),
        label = "timeLineColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // 提升无障碍体验：将整个卡片合并为一个语义节点供屏幕阅读器朗读
            .semantics {
                contentDescription =
                    "${course.courseName}, 第${course.section}节, 从 $startTime 到 $endTime"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- 左侧：时间与节次轴 ---
        Column(
            modifier = Modifier
                .width(64.dp)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = sectionBgColor,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "第${course.section}节",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = sectionTextColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = startTime,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = timeTextColor
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .width(2.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(timeLineColor)
            )
            Text(
                text = endTime,
                style = MaterialTheme.typography.bodyMedium,
                color = timeTextColor.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // --- 右侧：卡片内容主体 ---
        OutlinedCard(
            modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.large)
                .clickable { onCardClick() },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.outlinedCardColors(
                containerColor = cardContainerColor
            ),
            border = BorderStroke(
                width = cardBorderWidth,
                color = cardBorderColor
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = course.courseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = titleTextColor,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    StatusBadge(
                        status = status,
                        themeTextColor = hashTextColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 缓存字符串判断，避免在动画重组期间重复执行 ifBlank
                val roomText = remember(course.classRoom) {
                    course.classRoom.ifBlank { "未标注教室" }
                }
                val teacherText = remember(course.teacherName) {
                    course.teacherName.ifBlank { "暂无教师" }
                }

                InfoItem(
                    icon = Icons.Default.LocationOn,
                    text = roomText,
                    tint = iconAndSubTextColor
                )
                InfoItem(
                    icon = Icons.Default.Person,
                    text = teacherText,
                    tint = iconAndSubTextColor
                )
            }
        }
    }
}

/**
 * 状态徽章：吸收卡片的主题色，动态调整其表现并提供呼吸灯动画
 */
@Composable
private fun StatusBadge(status: CourseStatus, themeTextColor: Color) {
    val containerColor = when (status) {
        CourseStatus.IN_PROGRESS -> themeTextColor
        CourseStatus.UPCOMING -> themeTextColor.copy(alpha = 0.15f)
        CourseStatus.NOT_STARTED -> themeTextColor.copy(alpha = 0.08f)
        CourseStatus.FINISHED -> themeTextColor.copy(alpha = 0.08f)
    }
    val contentColor = when (status) {
        CourseStatus.IN_PROGRESS -> Color.White
        CourseStatus.UPCOMING -> themeTextColor
        CourseStatus.NOT_STARTED -> themeTextColor.copy(alpha = 0.8f)
        CourseStatus.FINISHED -> themeTextColor.copy(alpha = 0.6f)
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 只有在课程进行中时，才展示闪烁的红点动画
            if (status == CourseStatus.IN_PROGRESS) {
                val dotAlpha by rememberInfiniteTransition(label = "statusDotTransition").animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                    label = "dotAlpha",
                )
                Box(
                    Modifier
                        .size(6.dp)
                        .background(contentColor, RoundedCornerShape(3.dp))
                        .alpha(dotAlpha),
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = when (status) {
                    CourseStatus.IN_PROGRESS -> "正在进行"
                    CourseStatus.UPCOMING -> "即将开始"
                    CourseStatus.NOT_STARTED -> "待开始"
                    CourseStatus.FINISHED -> "已结束"
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                ),
                color = contentColor,
            )
        }
    }
}

/**
 * 基础图文信息行，用于展示教室、教师等辅助信息
 */
@Composable
private fun InfoItem(icon: ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null, // 图标仅作装饰，无障碍交由外层语义处理
            modifier = Modifier.size(16.dp),
            tint = tint,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}