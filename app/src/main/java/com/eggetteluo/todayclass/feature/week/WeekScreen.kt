package com.eggetteluo.todayclass.feature.week

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.CalendarToday
import androidx.compose.material.icons.twotone.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eggetteluo.todayclass.data.model.WeeklyCourseDetail
import com.eggetteluo.todayclass.navigation.Navigator
import com.eggetteluo.todayclass.navigation.UploadRoute
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// --- 全局布局常量 ---
private val SectionHeight = 64.dp
private val TimeAxisWidth = 36.dp
private const val TotalSections = 10

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeekScreen() {
    val viewModel: WeekViewModel = koinViewModel()
    val navigator: Navigator = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "周课表",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (uiState is WeekUiState.Success) {
                            val state = uiState as WeekUiState.Success
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "第${state.currentWeek}周",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = 0.dp
                ),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is WeekUiState.Loading -> {
                        LoadingIndicator(modifier = Modifier.size(64.dp))
                    }

                    is WeekUiState.NoActiveSemester -> {
                        NoSemesterView(navigator)
                    }

                    is WeekUiState.Error -> {
                        ErrorView(state.message)
                    }

                    is WeekUiState.Success -> {
                        WeeklyScheduleLayout(
                            courses = state.weeklyCourses,
                            onCourseClick = { /* TODO */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyScheduleLayout(
    courses: List<WeeklyCourseDetail>,
    onCourseClick: (WeeklyCourseDetail) -> Unit
) {
    // 💡 在渲染前，对原始数据进行连堂课合并计算
    // 使用 remember 确保只有在 courses 发生变化时才重新计算合并逻辑
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
            TimeAxisColumn(TotalSections)
            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // 将合并后的数据传给网格
            ScheduleGrid(
                mergedCourses = mergedCourses,
                totalSections = TotalSections,
                onCourseClick = onCourseClick
            )
        }
    }
}

// ==========================================
// 💡 核心：课程合并逻辑与数据结构
// ==========================================

/**
 * 专用于网格渲染的内部包装类，记录了合并后的起始节次和持续长度
 */
private data class MergedCourse(
    val originalCourse: WeeklyCourseDetail, // 保留第一节课的原始数据供点击使用
    val startSection: Int,
    val duration: Int
)

/**
 * 遍历一维列表，将同一天、相邻节次且 (名字、地点、教师) 完全相同的课程合并。
 * 前提条件：传入的 courses 必须已经按 weekDay 和 section 排序。
 */
private fun mergeCourses(courses: List<WeeklyCourseDetail>): List<MergedCourse> {
    if (courses.isEmpty()) return emptyList()

    val result = mutableListOf<MergedCourse>()

    // 按星期几分组，确保不同天的课绝对不会被误合在一起
    val groupedByDay = courses.groupBy { it.weekDay }

    for ((_, dailyCourses) in groupedByDay) {
        if (dailyCourses.isEmpty()) continue

        var currentStartCourse = dailyCourses[0]
        var currentDuration = 1

        for (i in 1 until dailyCourses.size) {
            val nextCourse = dailyCourses[i]

            // 💡 核心修复：增加对上课地点和老师的严格判断
            // 只有当 课程名、地点、老师 都一模一样，并且节次相连时，才算同一节大课
            val isSameCourse = nextCourse.courseName == currentStartCourse.courseName
            val isSameRoom = nextCourse.classRoom == currentStartCourse.classRoom
            val isSameTeacher = nextCourse.teacherName == currentStartCourse.teacherName
            val isContinuous = nextCourse.section == currentStartCourse.section + currentDuration

            if (isSameCourse && isSameRoom && isSameTeacher && isContinuous) {
                // 可以合并，持续时间 +1
                currentDuration++
            } else {
                // 不能合并，将之前攒好的课作为一个整体塞入结果集
                result.add(
                    MergedCourse(
                        originalCourse = currentStartCourse,
                        startSection = currentStartCourse.section,
                        duration = currentDuration
                    )
                )
                // 开启新的一轮统计
                currentStartCourse = nextCourse
                currentDuration = 1
            }
        }
        // 把最后一天/最后一波积攒的课加进去
        result.add(
            MergedCourse(
                originalCourse = currentStartCourse,
                startSection = currentStartCourse.section,
                duration = currentDuration
            )
        )
    }

    return result
}

// ==========================================
// 辅助子组件：星期表头、时间轴、网格底图
// ==========================================

@Composable
private fun DaysHeaderRow() {
    val days = listOf("一", "二", "三", "四", "五", "六", "日")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = TimeAxisWidth)
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
private fun TimeAxisColumn(totalSections: Int) {
    Column(
        modifier = Modifier
            .width(TimeAxisWidth)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        for (i in 1..totalSections) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SectionHeight),
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
    mergedCourses: List<MergedCourse>, // 💡 接收合并后的数据
    totalSections: Int,
    onCourseClick: (WeeklyCourseDetail) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val dayWidth = maxWidth / 7

        GridBackground(dayWidth, totalSections)

        // 💡 基于合并后的数据进行绝对定位
        mergedCourses.forEach { mergedCourse ->
            val course = mergedCourse.originalCourse

            val offsetX = dayWidth * (course.weekDay - 1)
            val offsetY = SectionHeight * (mergedCourse.startSection - 1)

            // 💡 核心：卡片高度 = 单节课高度 * 持续节数
            val cardHeight = SectionHeight * mergedCourse.duration

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .width(dayWidth)
                    .height(cardHeight)
                    .padding(1.dp) // 防粘连边距
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
private fun GridBackground(dayWidth: Dp, totalSections: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SectionHeight * totalSections)
    ) {
        for (i in 1..totalSections) {
            HorizontalDivider(
                modifier = Modifier.offset(y = SectionHeight * i),
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

@Composable
private fun WeeklyCourseCard(
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
            // 如果合并后卡片变高，可以稍微增加一点内边距让文字更舒展
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = course.courseName,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = textColor,
                fontWeight = FontWeight.Bold,
                // 防止合并两节长课时名字被截断，这里可以适当放宽行数
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "@${course.classRoom}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = textColor.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
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

@Composable
private fun NoSemesterView(navigator: Navigator) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.TwoTone.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("尚未导入课表", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "导入教务系统课表，开启高效学习生活",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navigator.navigate(UploadRoute) },
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Text("去导入课表", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.TwoTone.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "加载失败: $message", color = MaterialTheme.colorScheme.error)
    }
}