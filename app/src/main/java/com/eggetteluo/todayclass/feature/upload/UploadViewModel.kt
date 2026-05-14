package com.eggetteluo.todayclass.feature.upload

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class UploadViewModel : ViewModel() {

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

                // 解析
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

                _uiState.value = UploadUiState.Success(allParsedCourses)
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Error processing file", e)
                _uiState.value = UploadUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun resetToIdle() {
        _uiState.value = UploadUiState.Idle
    }

}