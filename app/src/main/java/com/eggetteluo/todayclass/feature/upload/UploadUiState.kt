package com.eggetteluo.todayclass.feature.upload

import com.eggetteluo.todayclass.data.model.ParsedCourse

sealed class UploadUiState {
    object Idle : UploadUiState()
    object Loading : UploadUiState()
    data class Success(val courses: List<ParsedCourse>, val semesterName: String) : UploadUiState()
    data class Error(val message: String) : UploadUiState()
}
