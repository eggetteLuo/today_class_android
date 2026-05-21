package com.eggetteluo.todayclass.feature.week

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eggetteluo.todayclass.data.local.dao.CourseScheduleDao
import com.eggetteluo.todayclass.data.local.dao.SemesterInfoDao
import com.eggetteluo.todayclass.util.DateUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class WeekViewModel(
    private val semesterDao: SemesterInfoDao,
    private val scheduleDao: CourseScheduleDao
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<WeekUiState> = semesterDao.getCurrentSemester()
        .flatMapLatest { semester ->
            if (semester == null) {
                flowOf(WeekUiState.NoActiveSemester)
            } else {
                val (currentWeek, _) = DateUtil.getCurrentWeekAndDay(semester.startDate)

                // 查询周课表
                scheduleDao.getWeeklyCourses(
                    semesterId = semester.id,
                    currentWeek = currentWeek
                ).map { courses ->
                    WeekUiState.Success(
                        currentWeek = currentWeek,
                        weeklyCourses = courses
                    )
                }
            }
        }
        .catch { e ->
            Log.e("WeekViewModel", "Error loading weekly schedule", e)
            emit(WeekUiState.Error(e.message ?: "加载周课表失败"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WeekUiState.Loading
        )

}