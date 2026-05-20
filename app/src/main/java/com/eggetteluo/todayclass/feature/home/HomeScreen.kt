package com.eggetteluo.todayclass.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.CalendarToday
import androidx.compose.material.icons.twotone.ErrorOutline
import androidx.compose.material.icons.twotone.HotelClass
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eggetteluo.todayclass.feature.home.components.TodayCourseCard
import com.eggetteluo.todayclass.navigation.Navigator
import com.eggetteluo.todayclass.navigation.UploadRoute
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = koinViewModel()
    val navigator: Navigator = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LoadingIndicator(modifier = Modifier.size(64.dp))
                }

                is HomeUiState.NoActiveSemester -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.TwoTone.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "尚未导入课表",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "导入教务系统课表，开启高效学习生活",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                navigator.navigate(UploadRoute)
                            },
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
                        ) {
                            Text("去导入课表", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                is HomeUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.TwoTone.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "加载失败: ${state.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is HomeUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // --- 主体：课程列表 ---
                        if (state.todayCourses.isEmpty()) {
                            // 今天没课！展示开心的空状态
                            EmptyScheduleState()
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.todayCourses) { course ->
                                    // 课程卡片组件
                                    TodayCourseCard(course = course)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 今天没课的插画占位
@Composable
private fun EmptyScheduleState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.TwoTone.HotelClass,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "今天没有课哦",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "好好休息，或者去图书馆充充电吧！",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}