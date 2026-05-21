package com.eggetteluo.todayclass.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.ElevatedCard
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
    // 1. 实时时间状态管理
    var currentTime by remember {
        mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
    }

    // 开启一个独立于生命周期的协程，每 10 秒刷新一次时间
    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000L) // 间隔 10 秒
            currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    // 2. 状态计算
    val startTime = course.startTime ?: "00:00"
    val endTime = course.endTime ?: "23:59"
    val status = DateUtil.getCourseStatus(startTime, endTime, currentTime)

    // 3. 颜色与动态效果 (带平滑过渡动画)
    val courseColor = MaterialTheme.colorScheme.primary
    val isFinished = status == CourseStatus.FINISHED
    val isInProgress = status == CourseStatus.IN_PROGRESS

    // 全局透明度平滑过渡
    val containerAlpha by animateFloatAsState(
        targetValue = if (isFinished) 0.6f else 1f,
        label = "containerAlpha"
    )

    // 阴影高度平滑过渡：卡片会柔和地“浮起”或“落下”
    val cardElevation by animateDpAsState(
        targetValue = when (status) {
            CourseStatus.IN_PROGRESS -> 8.dp
            CourseStatus.FINISHED -> 0.dp
            else -> 1.dp
        },
        label = "cardElevation"
    )

    // 卡片底色平滑过渡
    val cardContainerColor by animateColorAsState(
        targetValue = when (status) {
            CourseStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer
            CourseStatus.FINISHED -> MaterialTheme.colorScheme.surfaceContainerLow
            else -> MaterialTheme.colorScheme.surface
        },
        label = "cardContainerColor"
    )

    // 文字颜色平滑过渡
    val titleTextColor by animateColorAsState(
        targetValue = if (isInProgress) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        label = "titleTextColor"
    )
    val iconAndSubTextColor by animateColorAsState(
        targetValue = if (isInProgress) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "iconAndSubTextColor"
    )

    // 左侧时间轴颜色联动平滑过渡
    val sectionBgColor by animateColorAsState(
        targetValue = if (isInProgress) courseColor else MaterialTheme.colorScheme.surfaceVariant,
        label = "sectionBgColor"
    )
    val sectionTextColor by animateColorAsState(
        targetValue = if (isInProgress) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "sectionTextColor"
    )
    val timeLineColor by animateColorAsState(
        targetValue = if (isInProgress) courseColor else MaterialTheme.colorScheme.outlineVariant,
        label = "timeLineColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(containerAlpha),
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
                fontWeight = FontWeight.Bold
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // --- 右侧：卡片内容 ---
        ElevatedCard(
            modifier = Modifier
                .weight(1f)
                .clickable { onCardClick() },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.elevatedCardColors(
                containerColor = cardContainerColor
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = cardElevation)
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
                    StatusBadge(status = status, accentColor = courseColor)
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

@Composable
private fun StatusBadge(status: CourseStatus, accentColor: Color) {
    val containerColor = when (status) {
        CourseStatus.IN_PROGRESS -> accentColor
        CourseStatus.UPCOMING -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (status) {
        CourseStatus.IN_PROGRESS -> MaterialTheme.colorScheme.onPrimary
        CourseStatus.UPCOMING -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
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
        )
    }
}