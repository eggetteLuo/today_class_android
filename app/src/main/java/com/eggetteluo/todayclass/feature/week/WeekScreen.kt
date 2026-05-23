package com.eggetteluo.todayclass.feature.week

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eggetteluo.todayclass.feature.week.components.ErrorView
import com.eggetteluo.todayclass.feature.week.components.NoSemesterView
import com.eggetteluo.todayclass.feature.week.components.WeeklyScheduleLayout
import com.eggetteluo.todayclass.navigation.Navigator
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeekScreen() {
    val viewModel: WeekViewModel = koinViewModel()
    val navigator: Navigator = koinInject()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp), topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = "周课表",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge
                        )

                        val state = uiState
                        if (state is WeekUiState.Success) {
                            Text(
                                text = "第 ${state.selectedWeek} 周" + if (state.selectedWeek == state.currentWeek) " (本周)" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }, actions = {
                    val state = uiState
                    if (state is WeekUiState.Success) {
                        // 上一周按钮
                        IconButton(
                            onClick = { viewModel.selectWeek(state.selectedWeek - 1) },
                            enabled = state.selectedWeek > 1
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Previous Week"
                            )
                        }

                        // 下一周按钮
                        IconButton(
                            onClick = { viewModel.selectWeek(state.selectedWeek + 1) },
                            enabled = state.selectedWeek < state.totalWeeks
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Next Week"
                            )
                        }

                        // 回到本周按钮
                        AnimatedVisibility(
                            visible = state.selectedWeek != state.currentWeek,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            TextButton(
                                onClick = { viewModel.resetToCurrentWeek() },
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = "Back to current week",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(end = 4.dp)
                                )
                                Text("回到本周")
                            }
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
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
                            onCourseClick = { }
                        )
                    }
                }
            }
        }
    }
}