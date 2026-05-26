package com.eggetteluo.todayclass.feature.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eggetteluo.todayclass.data.local.dao.CourseScheduleDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val courseScheduleDao: CourseScheduleDao,
    private val scheduleId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        if (scheduleId == -1L) {
            _uiState.update { ScheduleUiState.Success(null) }
        }
        viewModelScope.launch {
            try {
                val details = courseScheduleDao.getScheduleWithDetailsById(scheduleId)
                if (details != null) {
                    _uiState.update { ScheduleUiState.Success(details) }
                } else {
                    _uiState.update { ScheduleUiState.Error }
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "DB Error", e)
                _uiState.update { ScheduleUiState.Error }
            }
        }
    }

}