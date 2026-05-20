package com.eggetteluo.todayclass.feature.home.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TodayCourseCard(course: TodayCourseDetail) {
    // 1. 状态计算
    val currentTime = remember { LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) }
    val startTime = course.startTime ?: "00:00"
    val endTime = course.endTime ?: "23:59"
    // 假设你有转换逻辑，如果 TodayCourseDetail 的 section 能对应上 CourseStatus 逻辑
    val status = DateUtil.getCourseStatus(startTime, endTime, currentTime)

    // 2. 颜色与动态效果 (融合了你呼吸灯的设计)
    val courseColor = MaterialTheme.colorScheme.primary // 或者你可以根据 courseName 生成
    val containerAlpha = if (status == CourseStatus.FINISHED) 0.5f else 1f

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .width(64.dp)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = when (status) {
                    CourseStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                    CourseStatus.FINISHED -> MaterialTheme.colorScheme.outlineVariant
                    else -> MaterialTheme.colorScheme.primaryContainer
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "第${course.section}节",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
            Text(
                text = endTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // --- 右侧：高度定制化的信息卡片 (融合了你之前的样式) ---
        ElevatedCard(
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = course.courseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    StatusBadge(status = status, accentColor = courseColor)
                }

                InfoItem(
                    Icons.Default.LocationOn,
                    course.classRoom.ifBlank { "未标注教室" },
                    courseColor
                )
                InfoItem(
                    Icons.Default.Person,
                    course.teacherName.ifBlank { "暂无教师" },
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: CourseStatus, accentColor: Color) {
    val (text, color) = when (status) {
        CourseStatus.IN_PROGRESS -> "正在进行" to accentColor
        CourseStatus.UPCOMING -> "即将开始" to MaterialTheme.colorScheme.error
        CourseStatus.NOT_STARTED -> "待开始" to MaterialTheme.colorScheme.outline
        CourseStatus.FINISHED -> "已结束" to Color.Gray
    }
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (status == CourseStatus.IN_PROGRESS) {
                val dotAlpha by rememberInfiniteTransition(label = "statusDot").animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                    label = "dot",
                )
                Box(
                    Modifier
                        .size(6.dp)
                        .background(color, RoundedCornerShape(3.dp))
                        .alpha(dotAlpha),
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                ),
                color = color,
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
            modifier = Modifier.size(14.dp),
            tint = tint.copy(alpha = 0.7f),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = if (tint == Color.Gray) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = 0.8f
            ),
        )
    }
}