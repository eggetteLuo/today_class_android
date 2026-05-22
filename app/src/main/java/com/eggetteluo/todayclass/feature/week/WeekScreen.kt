package com.eggetteluo.todayclass.feature.week

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
                            onCourseClick = { }
                        )
                    }
                }
            }
        }
    }
}