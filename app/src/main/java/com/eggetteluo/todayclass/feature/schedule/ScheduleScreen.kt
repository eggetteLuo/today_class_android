package com.eggetteluo.todayclass.feature.schedule

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eggetteluo.todayclass.ui.root.LocalSnackbarHostState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val globalSnackbarHostState = LocalSnackbarHostState.current
    val context = LocalContext.current

    // 声明表单输入的受控状态
    var courseName by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    var classRoom by remember { mutableStateOf("") }
    var weekDay by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    var weeksDisplay by remember { mutableStateOf("") }
    var rawText by remember { mutableStateOf("") }

    // 闹钟相关状态
    var startTime by remember { mutableStateOf("") }
    var advanceMinutes by remember { mutableStateOf("30") }

    // 弹窗控制状态
    val showSaveDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }

    val isEditing = uiState is ScheduleUiState.Success
            && (uiState as ScheduleUiState.Success).scheduleDetails != null

    // 监听 ViewModel 传来的单次事件
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ScheduleUiEvent.SaveSuccess -> {
                    Log.d("ScheduleScreen", "Navigation back triggered by save/delete success")
                    onBackClick()
                }

                is ScheduleUiEvent.ShowError -> {
                    Log.e("ScheduleScreen", "ShowError event: ${event.message}")
                    globalSnackbarHostState.showSnackbar("发生错误: ${event.message}")
                }
            }
        }
    }

    // --- 弹窗组件 ---
    if (showSaveDialog.value) {
        AlertDialog(
            onDismissRequest = { showSaveDialog.value = false },
            title = { Text(text = "确认保存") },
            text = { Text(text = "是否保存对课程信息的修改？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSaveDialog.value = false
                        Log.d("ScheduleScreen", "Confirm save clicked")
                        viewModel.saveSchedule(
                            teacherName = teacherName,
                            classRoom = classRoom,
                            weekDayStr = weekDay,
                            sectionStr = section,
                            weeksDisplay = weeksDisplay
                        )
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog.value = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(text = "确认删除") },
            text = { Text(text = "是否确认删除该课程排课？此操作不可恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        Log.d("ScheduleScreen", "Confirm delete clicked")
                        viewModel.deleteSchedule()
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "编辑课程" else "新建课程",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            HorizontalFloatingToolbar(
                expanded = true,
                colors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { showSaveDialog.value = true },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "保存")
                    }
                }
            ) {
                if (isEditing) {
                    IconButton(onClick = { showDeleteDialog.value = true }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.Cancel, contentDescription = "取消")
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding(), bottom = 0.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            when (val state = uiState) {
                is ScheduleUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ScheduleUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("数据加载异常", color = MaterialTheme.colorScheme.error)
                    }
                }

                is ScheduleUiState.Success -> {
                    val details = state.scheduleDetails
                    val realStartTime = state.startTime // 从 ViewModel 中拿到的真实时间

                    // 同步数据库数据到 UI 状态
                    LaunchedEffect(details, realStartTime) {
                        details?.let {
                            courseName = it.course.name
                            courseCode = it.course.code
                            teacherName = it.course.teacherName
                            classRoom = it.schedule.classRoom
                            weekDay = it.schedule.weekDay.toString()
                            section = it.schedule.section.toString()
                            rawText = it.schedule.rawText
                            weeksDisplay = it.weeks.map { week -> week.weekNo }
                                .sorted().joinToString(", ")
                            startTime = realStartTime
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        FormCard(title = "课程基本信息") {
                            FormTextField(value = courseName, label = "课程名称", readOnly = true)
                            FormTextField(value = courseCode, label = "课程代码", readOnly = true)
                            FormTextField(
                                value = teacherName,
                                label = "任课教师",
                                onValueChange = { teacherName = it }
                            )
                        }

                        FormCard(title = "排课及上课地点") {
                            FormTextField(
                                value = classRoom,
                                label = "完整上课地点",
                                onValueChange = { classRoom = it })
                            FormTextField(
                                value = weekDay,
                                label = "星期（如: 1 表示周一）",
                                onValueChange = { weekDay = it },
                                keyboardType = KeyboardType.Number
                            )
                            FormTextField(
                                value = section,
                                label = "上课节次（第几节）",
                                onValueChange = { section = it },
                                keyboardType = KeyboardType.Number
                            )
                            FormTextField(
                                value = weeksDisplay,
                                label = "上课周次（逗号分隔）",
                                onValueChange = { weeksDisplay = it })
                        }

                        // 模块三：上课时间与提醒
                        if (startTime.isNotEmpty() && isEditing) {
                            FormCard(title = "上课时间与提醒") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "第 $section 节课",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = startTime,
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    OutlinedTextField(
                                        value = advanceMinutes,
                                        onValueChange = { newValue ->
                                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                advanceMinutes = newValue
                                            }
                                        },
                                        label = { Text("提前提醒(分钟)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )

                                    FilledTonalButton(
                                        onClick = {
                                            setSystemAlarm(
                                                context = context,
                                                timeString = startTime,
                                                courseName = courseName,
                                                advanceMinutesStr = advanceMinutes
                                            )
                                        },
                                        modifier = Modifier.padding(top = 8.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AlarmAdd,
                                            contentDescription = "添加闹钟",
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text("设为闹钟")
                                    }
                                }
                            }
                        }

                        if (rawText.isNotEmpty()) {
                            FormCard(title = "系统导入记录", isVariantStyle = true) {
                                FormTextField(
                                    value = rawText,
                                    label = "原始单元格内容",
                                    readOnly = true,
                                    minLines = 3
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

// ---------------- 提取的可复用组件 ----------------

@Composable
private fun FormCard(
    title: String,
    isVariantStyle: Boolean = false,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isVariantStyle) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isVariantStyle) 0.dp else 2.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        minLines = minLines,
        modifier = Modifier.fillMaxWidth()
    )
}

// ---------------- 闹钟设置工具函数 ----------------

/**
 * 解析真实时间字符串，计算提前时间，并调用系统闹钟 Intent
 * 兼容 API 30+ 软件可见性限制 (不再使用 resolveActivity)
 */
private fun setSystemAlarm(
    context: Context,
    timeString: String,
    courseName: String,
    advanceMinutesStr: String
) {
    try {
        val advanceMins = advanceMinutesStr.toIntOrNull() ?: 0

        val parts = timeString.split(":")
        if (parts.size != 2) throw IllegalArgumentException("Invalid time format")

        val originalHour = parts[0].toInt()
        val originalMinute = parts[1].toInt()

        var totalMinutes = originalHour * 60 + originalMinute - advanceMins

        if (totalMinutes < 0) {
            totalMinutes += 24 * 60
        }

        val alarmHour = (totalMinutes / 60) % 24
        val alarmMinute = totalMinutes % 60

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "上课提醒: $courseName")
            putExtra(AlarmClock.EXTRA_HOUR, alarmHour)
            putExtra(AlarmClock.EXTRA_MINUTES, alarmMinute)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false) // 保证弹出系统设置界面，更安全
        }

        // 直接采用捕获异常方案，规避小米等国产机器的 Package Visibility 拦截
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "未找到系统闹钟应用，请手动设置", Toast.LENGTH_SHORT).show()
        }

    } catch (e: Exception) {
        Log.e("AlarmError", "Failed to parse time or set alarm: ${e.message}")
        Toast.makeText(context, "时间解析错误，无法设置闹钟", Toast.LENGTH_SHORT).show()
    }
}
