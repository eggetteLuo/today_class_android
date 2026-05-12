package com.eggetteluo.todayclass.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.alibaba.excel.EasyExcel
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.event.AnalysisEventListener

/**
 * Excel 处理工具
 */
object ExcelUtil {

    /**
     * 遍历读取每一个单元格的内容
     * @param context Android 上下文，用于获取 ContentResolver
     * @param uri 文件的 Uri
     * @param onCellRead 每一行读取后的回调：(行索引, 这一行的数据Map)
     */
    fun readExcel(
        context: Context,
        uri: Uri,
        onCellRead: (Int, Map<Int, String>) -> Unit,
        onFinished: () -> Unit = {}
    ) {
        Log.i("ExcelUtil", "Opening stream for URI: $uri")

        try {
            context.contentResolver.openInputStream(uri).use { inputStream ->
                EasyExcel.read(inputStream, object : AnalysisEventListener<Map<Int, String>>() {
                    override fun invoke(
                        data: Map<Int, String>,
                        analysisContext: AnalysisContext
                    ) {
                        val rowIndex = analysisContext.readRowHolder().rowIndex
                        onCellRead(rowIndex, data)
                    }

                    override fun doAfterAllAnalysed(analysisContext: AnalysisContext) {
                        Log.i("ExcelUtil", "Read operation finished successfully.")
                        onFinished()
                    }
                })
            }
        } catch (e: Exception) {
            Log.e("ExcelUtil", "Error occurred while reading excel: ${e.message}")
        }
    }

}