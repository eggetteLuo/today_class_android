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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eggetteluo.todayclass.data.model.WeeklyCourseDetail

@Composable
fun WeeklyCourseCard(
    course: WeeklyCourseDetail,
    onClick: () -> Unit
) {
    val (bgColor, textColor) = getCourseColorVariant(course.courseName)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = course.courseName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                ),
                color = textColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            if (course.classRoom.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "@${course.classRoom}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    ),
                    color = textColor.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (course.teacherName.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = course.teacherName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    ),
                    color = textColor.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun getCourseColorVariant(courseName: String): Pair<Color, Color> {
    val hash = kotlin.math.abs(courseName.hashCode())
    val colorPalette = listOf(
        Color(0xFFE3F2FD) to Color(0xFF1565C0),
        Color(0xFFF3E5F5) to Color(0xFF6A1B9A),
        Color(0xFFE8F5E9) to Color(0xFF2E7D32),
        Color(0xFFFFF3E0) to Color(0xFFEF6C00),
        Color(0xFFFFEBEE) to Color(0xFFC62828),
        Color(0xFFE0F7FA) to Color(0xFF00838F)
    )
    return colorPalette[hash % colorPalette.size]
}