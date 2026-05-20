package com.eggetteluo.todayclass.feature.home

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

class HomeViewModel(
    private val semesterDao: SemesterInfoDao,
    private val scheduleDao: CourseScheduleDao
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = semesterDao.getCurrentSemester()
        .flatMapLatest { semester ->
            if (semester == null) {
                flowOf(HomeUiState.NoActiveSemester)
            } else {
                val (currentWeek, dayOfWeek) = DateUtil.getCurrentWeekAndDay(semester.startDate)
                scheduleDao.getTodayCourses(
                    semesterId = semester.id,
                    currentWeek = currentWeek,
                    dayOfWeek = dayOfWeek
                ).map { courses ->
                    HomeUiState.Success(
                        currentWeek = currentWeek,
                        dayOfWeek = dayOfWeek,
                        todayCourses = courses
                    )
                }
            }
        }
        .catch { e ->
            Log.e("HomeViewModel", "Error loading schedule", e)
            emit(HomeUiState.Error(e.message ?: "加载失败"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

}