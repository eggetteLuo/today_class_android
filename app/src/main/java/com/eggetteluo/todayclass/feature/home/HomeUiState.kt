package com.eggetteluo.todayclass.feature.home

import com.eggetteluo.todayclass.data.model.TodayCourseDetail

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val currentWeek: Int,
        val dayOfWeek: Int,
        val todayCourses: List<TodayCourseDetail>
    ) : HomeUiState()

    data class Error(val message: String) : HomeUiState()
    object NoActiveSemester : HomeUiState()
}
