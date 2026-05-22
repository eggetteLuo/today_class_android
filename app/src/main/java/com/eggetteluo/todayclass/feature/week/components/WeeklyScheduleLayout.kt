package com.eggetteluo.todayclass.feature.week.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eggetteluo.todayclass.data.model.WeekLayoutConstants
import com.eggetteluo.todayclass.data.model.WeeklyCourseDetail
import com.eggetteluo.todayclass.util.MergedCourse
import com.eggetteluo.todayclass.util.mergeCourses

@Composable
fun WeeklyScheduleLayout(
    courses: List<WeeklyCourseDetail>,
    onCourseClick: (WeeklyCourseDetail) -> Unit
) {
    val mergedCourses = remember(courses) {
        mergeCourses(courses)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DaysHeaderRow()
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            TimeAxisColumn()
            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            ScheduleGrid(
                mergedCourses = mergedCourses,
                onCourseClick = onCourseClick
            )
        }
    }
}

@Composable
private fun DaysHeaderRow() {
    val days = listOf("一", "二", "三", "四", "五", "六", "日")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = WeekLayoutConstants.TimeAxisWidth)
            .height(36.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TimeAxisColumn() {
    Column(
        modifier = Modifier
            .width(WeekLayoutConstants.TimeAxisWidth)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        for (i in 1..WeekLayoutConstants.TotalSections) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WeekLayoutConstants.SectionHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$i",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ScheduleGrid(
    mergedCourses: List<MergedCourse>,
    onCourseClick: (WeeklyCourseDetail) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val dayWidth = maxWidth / 7

        GridBackground(dayWidth)

        mergedCourses.forEach { mergedCourse ->
            val course = mergedCourse.originalCourse
            val offsetX = dayWidth * (course.weekDay - 1)
            val offsetY = WeekLayoutConstants.SectionHeight * (mergedCourse.startSection - 1)
            val cardHeight = WeekLayoutConstants.SectionHeight * mergedCourse.duration

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .width(dayWidth)
                    .height(cardHeight)
                    .padding(1.dp)
            ) {
                WeeklyCourseCard(
                    course = course,
                    onClick = { onCourseClick(course) }
                )
            }
        }
    }
}

@Composable
private fun GridBackground(dayWidth: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(WeekLayoutConstants.SectionHeight * WeekLayoutConstants.TotalSections)
    ) {
        for (i in 1..WeekLayoutConstants.TotalSections) {
            HorizontalDivider(
                modifier = Modifier.offset(y = WeekLayoutConstants.SectionHeight * i),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )
        }
        for (i in 1 until 7) {
            VerticalDivider(
                modifier = Modifier.offset(x = dayWidth * i),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )
        }
    }
}