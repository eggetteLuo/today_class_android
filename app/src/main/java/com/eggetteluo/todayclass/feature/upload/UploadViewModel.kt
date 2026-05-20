package com.eggetteluo.todayclass.feature.upload

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eggetteluo.todayclass.data.local.dao.CourseDao
import com.eggetteluo.todayclass.data.local.dao.CourseScheduleDao
import com.eggetteluo.todayclass.data.local.dao.CourseTimeRuleDao
import com.eggetteluo.todayclass.data.local.dao.SemesterInfoDao
import com.eggetteluo.todayclass.data.local.entity.CourseEntity
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleEntity
import com.eggetteluo.todayclass.data.local.entity.CourseScheduleWeekEntity
import com.eggetteluo.todayclass.data.local.entity.SemesterInfoEntity
import com.eggetteluo.todayclass.data.model.ParsedCourse
import com.eggetteluo.todayclass.util.CourseUtil
import com.eggetteluo.todayclass.util.DateUtil
import com.eggetteluo.todayclass.util.ExcelUtil
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

class UploadViewModel(
    private val courseDao: CourseDao,
    private val semesterInfoDao: SemesterInfoDao,
    private val courseScheduleDao: CourseScheduleDao,
    private val courseTimeRuleDao: CourseTimeRuleDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Idle)
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    fun importExcelFile(platformFile: PlatformFile) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UploadUiState.Loading

            try {
                Log.i("UploadViewModel", "Starting file processing: ${platformFile.name}")

                // 读取字节流转换为输入流
                val bytes = platformFile.readBytes()
                val inputStream = ByteArrayInputStream(bytes)

                // 读取 Excel 原始数据
                val excelData = ExcelUtil.readExcelSync(inputStream)
                val allParsedCourses = mutableListOf<ParsedCourse>()

                // 读取学期信息
                var semesterName: String
                if (excelData.isNotEmpty()) {
                    semesterName = excelData[1][0]?.trim() ?: "未知学期"
                    Log.i("UploadViewModel", "Successful query semesterName: $semesterName")
                } else {
                    semesterName = "未知学期"
                }

                // 解析课程信息
                excelData.forEachIndexed { rowIndex, rowMap ->
                    if (rowIndex < 3) return@forEachIndexed

                    Log.d("UploadViewModel", "index=$rowIndex map=$rowMap")

                    val sectionName = rowMap[0]?.trim() ?: "未知节次"

                    for (colIndex in 1..7) {
                        val cellText = rowMap[colIndex]
                        if (!cellText.isNullOrBlank()) {
                            val parsedCoursesInCell = CourseUtil.parseCellText(
                                cellText = cellText,
                                dayOfWeek = colIndex,
                                sectionName = sectionName
                            )
                            allParsedCourses.addAll(parsedCoursesInCell)
                        }
                    }
                }

                Log.i("UploadViewModel", "Successfully parsed ${allParsedCourses.size} courses.")

                _uiState.value = UploadUiState.Success(allParsedCourses, semesterName)
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Error processing file", e)
                _uiState.value = UploadUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun resetToIdle() {
        _uiState.value = UploadUiState.Idle
    }

    fun saveToDatabase(courses: List<ParsedCourse>, semesterName: String, currentWeek: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UploadUiState.Importing
            try {
                // 存储或查询学期信息，拿到 semesterId
                val existingSemester = semesterInfoDao.getSemesterByName(semesterName)
                val semesterId = if (existingSemester == null) {
                    // 插入学期
                    val (startTimestamp, endTimestamp) = DateUtil.calculateSemesterDates(
                        currentWeek = currentWeek,
                        totalWeeks = 20
                    )
                    val newSemesterInfo = SemesterInfoEntity(
                        name = semesterName,
                        startDate = startTimestamp,
                        endDate = endTimestamp,
                        totalWeeks = 20,
                        currentWeekOverride = null,
                        remark = ""
                    )
                    // 将其他学期改为非激活状态
                    semesterInfoDao.clearCurrentSemesterStatus()
                    // 插入新的学期，返回 semesterId
                    semesterInfoDao.insertSemester(newSemesterInfo)
                } else {
                    // 更新激活状态
                    semesterInfoDao.clearCurrentSemesterStatus()
                    semesterInfoDao.updateSemester(existingSemester.copy(isCurrent = true))
                    // 删除该学期的排课数据
                    courseScheduleDao.deleteSchedulesBySemesterId(existingSemester.id)
                    // 返回 semesterId
                    existingSemester.id
                }

                for (course in courses) {
                    // 处理基础课程信息
                    val existingCourse = courseDao.getCourseByCode(course.courseCode)
                    val courseId = if (existingCourse == null) {
                        // 插入课程
                        val newCourse = CourseEntity(
                            code = course.courseCode,
                            name = course.courseName,
                            teacherName = course.teacher,
                            type = "",
                            remark = ""
                        )
                        courseDao.insertCourse(newCourse)
                    } else {
                        existingCourse.id
                    }

                    // 解析公共参数
                    val buildingType = CourseUtil.parseBuildingType(course.location)
                    val currentSectionNo = CourseUtil.sectionNameToInt(course.sectionName)
                    val weekList = CourseUtil.parseWeeksString(course.weeks)

                    val timeRule = courseTimeRuleDao.getRuleByBuildingAndSection(
                        buildingType = buildingType,
                        sectionNo = currentSectionNo
                    )
                    val ruleId = timeRule?.id ?: 1L

                    // 插入排课数据
                    val schedule = CourseScheduleEntity(
                        courseId = courseId,
                        ruleId = ruleId,
                        semesterId = semesterId,
                        weekDay = course.dayOfWeek,
                        section = currentSectionNo,
                        classRoom = course.location,
                        rawText = course.cellText,
                        buildingType = buildingType
                    )
                    val scheduleId = courseScheduleDao.insertSchedule(schedule)

                    // 插入周次数据
                    val weekEntities = weekList.map { weekNo ->
                        CourseScheduleWeekEntity(scheduleId = scheduleId, weekNo = weekNo)
                    }
                    courseScheduleDao.insertScheduleWeeks(weekEntities)
                }

                _uiState.value = UploadUiState.ImportComplete
            } catch (e: Exception) {
                _uiState.value = UploadUiState.Error("入库失败: ${e.message}")
            }
        }
    }

}