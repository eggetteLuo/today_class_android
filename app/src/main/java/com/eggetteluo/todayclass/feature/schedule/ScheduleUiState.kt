package com.eggetteluo.todayclass.feature.schedule

import com.eggetteluo.todayclass.data.local.relation.ScheduleWithDetails

sealed interface ScheduleUiState {
    data object Loading : ScheduleUiState
    data class Success(val scheduleDetails: ScheduleWithDetails?) : ScheduleUiState
    data object Error : ScheduleUiState
}

sealed interface ScheduleUiEvent {
    data object SaveSuccess : ScheduleUiEvent
    data class ShowError(val message: String) : ScheduleUiEvent
}