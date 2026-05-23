package com.eggetteluo.todayclass.feature.week

import com.eggetteluo.todayclass.data.model.WeeklyCourseDetail

sealed interface WeekUiState {
    data object Loading : WeekUiState
    data object NoActiveSemester : WeekUiState
    data class Error(val message: String) : WeekUiState
    data class Success(
        val currentWeek: Int,
        val selectedWeek: Int,
        val totalWeeks: Int,
        val weeklyCourses: List<WeeklyCourseDetail>
    ) : WeekUiState
}