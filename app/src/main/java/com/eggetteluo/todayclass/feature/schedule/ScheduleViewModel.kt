package com.eggetteluo.todayclass.feature.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eggetteluo.todayclass.data.local.dao.CourseScheduleDao
import com.eggetteluo.todayclass.data.local.dao.CourseTimeRuleDao
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleWeekEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 课程排课页面的 ViewModel
 * 负责管理排课详情的加载、修改保存和删除逻辑。
 */
class ScheduleViewModel(
    private val courseScheduleDao: CourseScheduleDao,
    private val courseTimeRuleDao: CourseTimeRuleDao,
    private val scheduleId: Long
) : ViewModel() {

    // 页面主状态，负责驱动 UI 的加载、成功展示或错误提示
    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    // 一次性事件流，用于触发页面导航 (如保存成功后返回) 或弹窗提示
    private val _uiEvent = MutableSharedFlow<ScheduleUiEvent>()
    val uiEvent: SharedFlow<ScheduleUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadSchedule()
    }

    /**
     * 根据传入的 scheduleId 从数据库加载排课详情及上课时间。
     * 若 id 为 -1L，则进入新建课程模式。
     */
    private fun loadSchedule() {
        if (scheduleId == -1L) {
            _uiState.update { ScheduleUiState.Success(null) }
            return
        }

        viewModelScope.launch {
            try {
                // 1. 获取课程及排课的基本详情
                val details = courseScheduleDao.getScheduleWithDetailsById(scheduleId)
                if (details != null) {
                    // 2. 根据关联的 ruleId 查询具体的时间规则（以获取准确的 startTime）
                    val timeRule = courseTimeRuleDao.getRuleById(details.schedule.ruleId)
                    val realStartTime = timeRule?.startTime ?: ""

                    // 3. 组装数据并更新到 UI 成功状态
                    _uiState.update { ScheduleUiState.Success(details, realStartTime) }
                } else {
                    _uiState.update { ScheduleUiState.Error }
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Failed to load schedule from database", e)
                _uiState.update { ScheduleUiState.Error }
            }
        }
    }

    /**
     * 保存用户在页面上对课程信息的修改。
     * 包括：任课教师、上课地点、星期、节次，以及上课周次列表。
     */
    fun saveSchedule(
        teacherName: String,
        classRoom: String,
        weekDayStr: String,
        sectionStr: String,
        weeksDisplay: String
    ) {
        val currentState = _uiState.value
        if (currentState !is ScheduleUiState.Success) return

        val details = currentState.scheduleDetails
        if (details == null) {
            // TODO: 新建课程逻辑尚未实现
            Log.w("ScheduleViewModel", "Create new schedule logic is pending")
            return
        }

        viewModelScope.launch {
            try {
                // 1. 更新 course 表中的任课教师
                courseScheduleDao.updateCourseTeacher(details.course.id, teacherName)

                // 2. 构造并更新 course_schedule 主表的信息，安全解析字符串
                val updatedSchedule = details.schedule.copy(
                    classRoom = classRoom,
                    weekDay = weekDayStr.toIntOrNull() ?: details.schedule.weekDay,
                    section = sectionStr.toIntOrNull() ?: details.schedule.section
                )
                courseScheduleDao.updateSchedule(updatedSchedule)

                // 3. 处理上课周次：将逗号分隔的字符串解析为去重的整数集合
                val parsedWeeks = weeksDisplay.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .mapNotNull { it.toIntOrNull() }
                    .distinct()

                val newWeekEntities = parsedWeeks.map { weekNo ->
                    CourseScheduleWeekEntity(scheduleId = details.schedule.id, weekNo = weekNo)
                }

                // 采用全量覆盖策略：先删除该排课旧的所有周次记录，再插入新的周次记录
                courseScheduleDao.deleteWeeksByScheduleId(details.schedule.id)
                if (newWeekEntities.isNotEmpty()) {
                    courseScheduleDao.insertScheduleWeeks(newWeekEntities)
                }

                Log.d("ScheduleViewModel", "Schedule data updated successfully")
                _uiEvent.emit(ScheduleUiEvent.SaveSuccess)

            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Error occurred while saving schedule update", e)
                _uiEvent.emit(ScheduleUiEvent.ShowError("数据保存失败，请检查输入格式"))
            }
        }
    }

    /**
     * 删除当前排课记录。
     * 依赖 Room 数据库外键的级联删除 (CASCADE) 特性，同步清理相关的周次数据。
     */
    fun deleteSchedule() {
        val currentState = _uiState.value
        if (currentState !is ScheduleUiState.Success) return

        val details = currentState.scheduleDetails
        if (details == null) {
            Log.w("ScheduleViewModel", "Cannot delete a non-existent schedule")
            return
        }

        viewModelScope.launch {
            try {
                courseScheduleDao.deleteScheduleById(details.schedule.id)
                Log.d("ScheduleViewModel", "Schedule deleted successfully")

                // 复用 SaveSuccess 事件，通知 UI 弹出页面
                _uiEvent.emit(ScheduleUiEvent.SaveSuccess)
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Failed to delete schedule", e)
                _uiEvent.emit(ScheduleUiEvent.ShowError("删除失败，请稍后重试"))
            }
        }
    }
}