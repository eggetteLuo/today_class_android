package com.eggetteluo.todayclass.feature.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eggetteluo.todayclass.data.local.dao.CourseDao
import com.eggetteluo.todayclass.data.local.dao.CourseScheduleDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CourseManageViewModel(
    private val courseDao: CourseDao,
    private val scheduleDao: CourseScheduleDao
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val _uiState = MutableStateFlow(CourseManageUiState())
    val uiState: StateFlow<CourseManageUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CourseManageUiEvent>()
    val uiEvent: SharedFlow<CourseManageUiEvent> = _uiEvent.asSharedFlow()

    init {
        observeCourses()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCourses() {
        viewModelScope.launch {
            query.flatMapLatest { keyword ->
                val trimmed = keyword.trim()
                if (trimmed.isEmpty()) {
                    courseDao.getAllCourses()
                } else {
                    courseDao.searchCourses(trimmed)
                }.map { courses ->
                    CourseManageUiState(
                        courses = courses,
                        query = keyword,
                        isLoading = false
                    )
                }
            }.catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "课程加载失败"
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateQuery(value: String) {
        query.value = value
        _uiState.update { it.copy(query = value) }
    }

    fun deleteCourse(courseId: Long) {
        viewModelScope.launch {
            val scheduleCount = scheduleDao.countSchedulesByCourseId(courseId)
            courseDao.deleteCourseById(courseId)
            val message = if (scheduleCount > 0) {
                "课程及 $scheduleCount 条排课已删除"
            } else {
                "课程已删除"
            }
            _uiEvent.emit(CourseManageUiEvent.ShowMessage(message))
        }
    }
}
