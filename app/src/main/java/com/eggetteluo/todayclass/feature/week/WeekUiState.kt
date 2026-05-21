package com.eggetteluo.todayclass.feature.week

import com.eggetteluo.todayclass.data.model.WeeklyCourseDetail

sealed class WeekUiState {
    object Loading : WeekUiState()
    data class Success(
        val currentWeek: Int,
        val weeklyCourses: List<WeeklyCourseDetail>
    ) : WeekUiState()

    data class Error(val message: String) : WeekUiState()
    object NoActiveSemester : WeekUiState()
}