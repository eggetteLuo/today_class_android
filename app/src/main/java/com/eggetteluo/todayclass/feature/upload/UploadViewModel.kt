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
                    semesterName = excelData[0][0]?.trim() ?: "未知学期"
                    Log.i("UploadViewModel", "Successful query semesterName: $semesterName")
                } else {
                    semesterName = "未知学期"
                }

                // 解析课程信息
                excelData.forEachIndexed { rowIndex, rowMap ->
                    if (rowIndex < 3) return@forEachIndexed

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

    fun saveToDatabase(courses: List<ParsedCourse>, semesterName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // 存储或查询学期信息，拿到 semesterId
            val exitingSemester = semesterInfoDao.getSemesterByName(semesterName)
            val semesterId = if (exitingSemester == null) {
                // 插入学期
                val newSemesterInfo = SemesterInfoEntity(
                    name = semesterName,
                    startData = 0,
                    endData = 0,
                    totalWeeks = 20,
                    currentWeekOverride = false,
                    remark = ""
                )
                semesterInfoDao.insertSemester(newSemesterInfo)
            } else {
                exitingSemester.id
            }

            for (course in courses) {
                // 存储或查询课程信息，拿到 courseId
                val existingCourse = courseDao.getCourseByCode(course.courseCode)
                val courseId = if (existingCourse == null) {
                    // 插入课程
                    val newCourse = CourseEntity(
                        code = course.courseCode,
                        name = course.courseName,
                        type = "",
                        remark = ""
                    )
                    courseDao.insertCourse(newCourse)
                } else {
                    existingCourse.id
                }

                // 计算时间规则
                val buildingType = CourseUtil.parseBuildingType(course.location) // 判断单双栋 ODD/EVEN
                val sectionNo = CourseUtil.sectionNameToInt(course.sectionName) // "第一节" -> 1

                // 查询 ruleId
                val timeRule = courseTimeRuleDao.getRuleByBuildingAndSection(
                    buildingType = buildingType,
                    sectionNo = sectionNo
                )
                val ruleId = timeRule?.id ?: 1L

                // 插入排课数据
                val newSchedule = CourseScheduleEntity(
                    courseId = courseId,
                    ruleId = ruleId,
                    semesterId = semesterId,
                    teacherName = course.teacher,
                    weekDay = course.dayOfWeek,
                    startSection = sectionNo,
                    endSection = sectionNo + 1,
                    classRoom = course.location,
                    rawText = course.cellText,
                    buildingType = buildingType,
                    campusName = "", buildingName = "", roomNo = ""
                )
                val scheduleId = courseScheduleDao.insertSchedule(newSchedule)

                // 插入上课周次数据
                val weekList = CourseUtil.parseWeeksString(course.weeks)
                val weekEntities = weekList.map { weekNo ->
                    CourseScheduleWeekEntity(
                        scheduleId = scheduleId,
                        weekNo = weekNo,
                        remark = ""
                    )
                }
                courseScheduleDao.insertScheduleWeeks(weekEntities)
            }
        }
    }

}