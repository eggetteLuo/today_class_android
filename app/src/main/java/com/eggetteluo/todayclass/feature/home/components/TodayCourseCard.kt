package com.eggetteluo.todayclass.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eggetteluo.todayclass.data.model.CourseStatus
import com.eggetteluo.todayclass.data.model.TodayCourseDetail
import com.eggetteluo.todayclass.util.DateUtil
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TodayCourseCard(course: TodayCourseDetail, onCardClick: () -> Unit) {
    var currentTime by remember {
        mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000L)
            currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    val startTime = course.startTime ?: "00:00"
    val endTime = course.endTime ?: "23:59"
    val status = DateUtil.getCourseStatus(startTime, endTime, currentTime)

    // 获取同款哈希颜色
    val (hashBgColor, hashTextColor) = getCourseColorVariant(course.courseName)

    val isFinished = status == CourseStatus.FINISHED
    val isInProgress = status == CourseStatus.IN_PROGRESS

    // --- 动态颜色与高度计算 ---
    val cardElevation by animateDpAsState(
        targetValue = when (status) {
            CourseStatus.IN_PROGRESS -> 8.dp
            CourseStatus.FINISHED -> 0.dp
            else -> 1.dp
        },
        label = "cardElevation"
    )

    // 卡片底色。已结束时保留主题色，降低透明度
    val cardContainerColor by animateColorAsState(
        targetValue = if (isFinished) hashBgColor.copy(alpha = 0.4f) else hashBgColor,
        label = "cardContainerColor"
    )

    // 核心修改 2：文字颜色。已结束时保留主题深色，降低透明度
    val titleTextColor by animateColorAsState(
        targetValue = if (isFinished) hashTextColor.copy(alpha = 0.5f) else hashTextColor,
        label = "titleTextColor"
    )
    val iconAndSubTextColor by animateColorAsState(
        targetValue = if (isFinished) hashTextColor.copy(alpha = 0.4f) else hashTextColor.copy(alpha = 0.8f),
        label = "iconAndSubTextColor"
    )

    // 左上角节次标签。已结束时也使用半透明主题色
    val sectionBgColor by animateColorAsState(
        targetValue = when {
            isInProgress -> hashTextColor // 上课时深色高亮
            isFinished -> hashBgColor.copy(alpha = 0.5f) // 结束时褪色
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

    // 左侧时间的文本颜色（结束时略微变淡）
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
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- 左侧：时间与节次 ---
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
                color = timeTextColor.copy(alpha = 0.7f) // 结束时间通常比开始时间稍淡
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // --- 右侧：卡片内容 ---
        androidx.compose.material3.Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onCardClick() },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = cardContainerColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = cardElevation,
                pressedElevation = if (isFinished) 0.dp else cardElevation,
                focusedElevation = if (isFinished) 0.dp else cardElevation,
                hoveredElevation = if (isFinished) 0.dp else cardElevation,
                draggedElevation = if (isFinished) 0.dp else cardElevation
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

                    // 💡 核心修改 4：将主题色传递给徽章组件
                    StatusBadge(
                        status = status,
                        themeBgColor = hashBgColor,
                        themeTextColor = hashTextColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                InfoItem(
                    Icons.Default.LocationOn,
                    course.classRoom.ifBlank { "未标注教室" },
                    iconAndSubTextColor
                )
                InfoItem(
                    Icons.Default.Person,
                    course.teacherName.ifBlank { "暂无教师" },
                    iconAndSubTextColor
                )
            }
        }
    }
}

// 💡 状态徽章：吸收卡片的主题色，动态调整其表现
@Composable
private fun StatusBadge(status: CourseStatus, themeBgColor: Color, themeTextColor: Color) {
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

@Composable
private fun InfoItem(icon: ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
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

@Composable
private fun getCourseColorVariant(courseName: String): Pair<Color, Color> {
    val hash = kotlin.math.abs(courseName.hashCode())
    val colorPalette = listOf(
        Color(0xFFE3F2FD) to Color(0xFF1565C0), // 浅蓝
        Color(0xFFF3E5F5) to Color(0xFF6A1B9A), // 浅紫
        Color(0xFFE8F5E9) to Color(0xFF2E7D32), // 浅绿
        Color(0xFFFFF3E0) to Color(0xFFEF6C00), // 浅橙
        Color(0xFFFFEBEE) to Color(0xFFC62828), // 浅红
        Color(0xFFE0F7FA) to Color(0xFF00838F)  // 浅青
    )
    return colorPalette[hash % colorPalette.size]
}