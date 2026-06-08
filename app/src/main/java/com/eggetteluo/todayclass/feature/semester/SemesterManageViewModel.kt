package com.eggetteluo.todayclass.feature.semester

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eggetteluo.todayclass.data.local.dao.SemesterInfoDao
import com.eggetteluo.todayclass.data.local.entity.SemesterInfoEntity
import com.eggetteluo.todayclass.util.DateUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SemesterManageViewModel(
    private val semesterInfoDao: SemesterInfoDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SemesterManageUiState())
    val uiState: StateFlow<SemesterManageUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SemesterManageUiEvent>()
    val uiEvent: SharedFlow<SemesterManageUiEvent> = _uiEvent.asSharedFlow()

    init {
        observeSemesters()
    }

    private fun observeSemesters() {
        viewModelScope.launch {
            semesterInfoDao.getAllSemesters()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "学期加载失败"
                        )
                    }
                }
                .collect { semesters ->
                    _uiState.value = SemesterManageUiState(
                        semesters = semesters,
                        isLoading = false
                    )
                }
        }
    }

    fun saveSemester(
        semesterId: Long?,
        name: String,
        currentWeek: String,
        totalWeeks: String
    ) {
        viewModelScope.launch {
            val cleanName = name.trim()
            val currentWeekValue = currentWeek.toIntOrNull()
            val totalWeeksValue = totalWeeks.toIntOrNull()

            if (cleanName.isBlank()) {
                _uiEvent.emit(SemesterManageUiEvent.ShowMessage("请输入学期名称"))
                return@launch
            }
            if (currentWeekValue == null || currentWeekValue < 1) {
                _uiEvent.emit(SemesterManageUiEvent.ShowMessage("当前周必须大于 0"))
                return@launch
            }
            if (totalWeeksValue == null || totalWeeksValue !in 1..30) {
                _uiEvent.emit(SemesterManageUiEvent.ShowMessage("总周数必须在 1 到 30 之间"))
                return@launch
            }

            val (startDate, endDate) = DateUtil.calculateSemesterDates(
                currentWeek = currentWeekValue,
                totalWeeks = totalWeeksValue
            )

            if (semesterId == null) {
                semesterInfoDao.clearCurrentSemesterStatus()
                semesterInfoDao.insertSemester(
                    SemesterInfoEntity(
                        name = cleanName,
                        startDate = startDate,
                        endDate = endDate,
                        totalWeeks = totalWeeksValue,
                        currentWeekOverride = null,
                        remark = ""
                    )
                )
                _uiEvent.emit(SemesterManageUiEvent.ShowMessage("学期已创建并设为当前学期"))
            } else {
                val semester = semesterInfoDao.getSemesterById(semesterId)
                if (semester == null) {
                    _uiEvent.emit(SemesterManageUiEvent.ShowMessage("学期不存在"))
                    return@launch
                }
                semesterInfoDao.updateSemester(
                    semester.copy(
                        name = cleanName,
                        startDate = startDate,
                        endDate = endDate,
                        totalWeeks = totalWeeksValue
                    )
                )
                _uiEvent.emit(SemesterManageUiEvent.ShowMessage("学期已更新"))
            }
        }
    }

    fun setCurrentSemester(semesterId: Long) {
        viewModelScope.launch {
            val semester = semesterInfoDao.getSemesterById(semesterId)
            if (semester == null) {
                _uiEvent.emit(SemesterManageUiEvent.ShowMessage("学期不存在"))
                return@launch
            }
            semesterInfoDao.clearCurrentSemesterStatus()
            semesterInfoDao.updateSemester(semester.copy(isCurrent = true))
            _uiEvent.emit(SemesterManageUiEvent.ShowMessage("已切换当前学期"))
        }
    }

    fun deleteSemester(semesterId: Long) {
        viewModelScope.launch {
            val semesters = _uiState.value.semesters
            if (semesters.size <= 1) {
                _uiEvent.emit(SemesterManageUiEvent.ShowMessage("至少保留一个学期"))
                return@launch
            }
            val deletingCurrent = semesters.firstOrNull { it.id == semesterId }?.isCurrent == true
            semesterInfoDao.deleteSemesterById(semesterId)
            if (deletingCurrent) {
                val nextSemester = semesters.firstOrNull { it.id != semesterId }
                if (nextSemester != null) {
                    semesterInfoDao.clearCurrentSemesterStatus()
                    semesterInfoDao.updateSemester(nextSemester.copy(isCurrent = true))
                }
            }
            _uiEvent.emit(SemesterManageUiEvent.ShowMessage("学期已删除"))
        }
    }
}
