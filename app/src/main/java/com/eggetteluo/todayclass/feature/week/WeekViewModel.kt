package com.eggetteluo.todayclass.feature.week

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eggetteluo.todayclass.data.local.dao.CourseScheduleDao
import com.eggetteluo.todayclass.data.local.dao.SemesterInfoDao
import com.eggetteluo.todayclass.util.DateUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class WeekViewModel(
    private val semesterDao: SemesterInfoDao,
    private val scheduleDao: CourseScheduleDao
) : ViewModel() {

    private val _selectedWeek = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<WeekUiState> = combine(
        semesterDao.getCurrentSemester(),
        _selectedWeek
    ) { semester, selected ->
        semester to selected
    }.flatMapLatest { (semester, selected) ->
        if (semester == null) {
            flowOf(WeekUiState.NoActiveSemester)
        } else {
            val (actualCurrentWeek, _) = DateUtil.getCurrentWeekAndDay(semester.startDate)

            // 如果用户选择了周数，就用用户选择的；否则使用实际的当前周
            val targetWeek = selected ?: actualCurrentWeek

            scheduleDao.getWeeklyCourses(
                semesterId = semester.id,
                currentWeek = targetWeek
            ).map { courses ->
                WeekUiState.Success(
                    currentWeek = actualCurrentWeek,
                    selectedWeek = targetWeek,
                    totalWeeks = semester.totalWeeks,
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

    // 用户选择特定周
    fun selectWeek(week: Int) {
        Log.d("WeekViewModel", "User explicitly selected week: $week")
        _selectedWeek.value = week
    }

    // 重置回到本周
    fun resetToCurrentWeek() {
        Log.d("WeekViewModel", "Resetting schedule view to actual current week")
        _selectedWeek.value = null
    }
}