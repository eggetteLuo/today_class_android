package com.eggetteluo.todayclass.util

import com.alibaba.excel.EasyExcel
import java.io.InputStream

object ExcelUtil {

    /**
     * 传入 InputStream 即可读取，安全转化为 String 类型的 Map
     */
    fun readExcelSync(inputStream: InputStream): List<Map<Int, String?>> {
        val rawData = EasyExcel.read(inputStream).sheet().doReadSync<Map<Int, Any?>>()
        return rawData.map { rowMap ->
            rowMap.mapValues { entry ->
                entry.value?.toString()
            }
        }
    }

}