package com.eggetteluo.todayclass.util

import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

object ExcelUtil {
    fun readExcelSync(inputStream: InputStream): List<Map<Int, String?>> {
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)
        val gridData = mutableMapOf<Pair<Int, Int>, String>()

        // 1. 处理合并单元格（广播填充）
        for (i in 0 until sheet.numMergedRegions) {
            val region = sheet.getMergedRegion(i)
            val firstCell = sheet.getRow(region.firstRow)?.getCell(region.firstColumn)
            val cellValue = firstCell?.toString()?.trim() ?: ""
            if (cellValue.isNotEmpty()) {
                for (r in region.firstRow..region.lastRow) {
                    for (c in region.firstColumn..region.lastColumn) {
                        gridData[Pair(r, c)] = cellValue
                    }
                }
            }
        }

        // 2. 填充非合并单元格 (遍历所有行)
        for (r in 0..sheet.lastRowNum) {
            val row = sheet.getRow(r) ?: continue
            for (c in 0..7) {
                if (!gridData.containsKey(Pair(r, c))) {
                    val value = row.getCell(c)?.toString()?.trim() ?: ""
                    if (value.isNotEmpty()) gridData[Pair(r, c)] = value
                }
            }
        }

        // 3. 转换回 List
        val result = mutableListOf<Map<Int, String?>>()
        for (r in 0..sheet.lastRowNum) {
            val rowMap = mutableMapOf<Int, String?>()
            for (c in 0..7) {
                rowMap[c] = gridData[Pair(r, c)]
            }
            result.add(rowMap)
        }
        return result
    }
}
