package com.eggetteluo.todayclass.feature.semester

import com.eggetteluo.todayclass.data.local.entity.SemesterInfoEntity

data class SemesterManageUiState(
    val semesters: List<SemesterInfoEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed interface SemesterManageUiEvent {
    data class ShowMessage(val message: String) : SemesterManageUiEvent
}
