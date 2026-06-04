package com.eggetteluo.todayclass.feature.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eggetteluo.todayclass.data.local.dao.CourseScheduleDao
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
 * 课程排课编辑页面的 ViewModel
 * 负责处理课程信息的加载、保存（更新）以及删除逻辑，管理 UI 状态流与一次性事件流。
 */
class ScheduleViewModel(
    private val courseScheduleDao: CourseScheduleDao,
    private val scheduleId: Long
) : ViewModel() {

    // 持续性的 UI 状态，负责界面的数据渲染 (Loading, Success, Error)
    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    // 一次性的 UI 事件流，用于处理页面导航 (如返回上一页) 或弹窗提示，避免屏幕旋转时事件重放
    private val _uiEvent = MutableSharedFlow<ScheduleUiEvent>()
    val uiEvent: SharedFlow<ScheduleUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadSchedule()
    }

    /**
     * 加载排课数据
     * 根据传入的 scheduleId 判断是新建还是编辑：
     * - 若 scheduleId 为 -1L，则判定为新建课程，直接返回空的 Success 状态。
     * - 否则，从数据库中读取对应的课程及排课详情。
     */
    private fun loadSchedule() {
        if (scheduleId == -1L) {
            _uiState.update { ScheduleUiState.Success(null) }
            return
        }

        viewModelScope.launch {
            try {
                val details = courseScheduleDao.getScheduleWithDetailsById(scheduleId)
                if (details != null) {
                    _uiState.update { ScheduleUiState.Success(details) }
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
     * 保存/更新课程表单数据
     * * @param teacherName 任课教师名称
     * @param classRoom 完整上课地点
     * @param weekDayStr 星期（字符串，需转换为 Int）
     * @param sectionStr 节次（字符串，需转换为 Int）
     * @param weeksDisplay 上课周次（逗号分隔的字符串）
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
            // TODO: 待完善新建课程的插入逻辑（需补充 semesterId 等必要的外键关联数据）
            Log.w("ScheduleViewModel", "Create new schedule logic is pending")
            return
        }

        viewModelScope.launch {
            try {
                // 1. 更新课程维度的信息（任课教师）
                courseScheduleDao.updateCourseTeacher(details.course.id, teacherName)

                // 2. 更新排课维度的信息（上课地点、星期、节次），若格式转换失败则回退为原值
                val updatedSchedule = details.schedule.copy(
                    classRoom = classRoom,
                    weekDay = weekDayStr.toIntOrNull() ?: details.schedule.weekDay,
                    section = sectionStr.toIntOrNull() ?: details.schedule.section
                )
                courseScheduleDao.updateSchedule(updatedSchedule)

                // 3. 更新上课周次信息
                // 解析用户输入的字符串，提取有效数字并去重
                val parsedWeeks = weeksDisplay.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .mapNotNull { it.toIntOrNull() }
                    .distinct()

                val newWeekEntities = parsedWeeks.map { weekNo ->
                    CourseScheduleWeekEntity(scheduleId = details.schedule.id, weekNo = weekNo)
                }

                // 采用“先清空旧数据，再全量插入新数据”的策略，确保与用户输入完全一致
                courseScheduleDao.deleteWeeksByScheduleId(details.schedule.id)
                if (newWeekEntities.isNotEmpty()) {
                    courseScheduleDao.insertScheduleWeeks(newWeekEntities)
                }

                // 4. 全部更新完成，通知 UI 层执行保存成功的后续操作（如返回上一页）
                Log.d("ScheduleViewModel", "Schedule data updated successfully")
                _uiEvent.emit(ScheduleUiEvent.SaveSuccess)

            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Error occurred while saving schedule update", e)
                _uiEvent.emit(ScheduleUiEvent.ShowError("数据保存失败，请检查输入格式"))
            }
        }
    }

    /**
     * 删除当前排课记录
     * 提示：删除主表记录时，Room 会利用 ForeignKey.CASCADE 自动级联删除关联的周次数据
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
                // 执行删除操作
                courseScheduleDao.deleteScheduleById(details.schedule.id)

                Log.d("ScheduleViewModel", "Schedule deleted successfully")

                // 复用 SaveSuccess 事件，通知 UI 层操作成功并触发页面返回
                _uiEvent.emit(ScheduleUiEvent.SaveSuccess)

            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Failed to delete schedule", e)
                _uiEvent.emit(ScheduleUiEvent.ShowError("删除失败，请稍后重试"))
            }
        }
    }
}