package com.eggetteluo.todayclass.feature.course

import com.eggetteluo.todayclass.data.local.entity.CourseEntity

data class CourseManageUiState(
    val courses: List<CourseEntity> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed interface CourseManageUiEvent {
    data class ShowMessage(val message: String) : CourseManageUiEvent
}
