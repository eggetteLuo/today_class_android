package com.eggetteluo.todayclass.feature.upload

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eggetteluo.todayclass.util.ExcelUtil
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

@Composable
fun UploadScreen() {
    val scope = rememberCoroutineScope()

    val launcher = rememberFilePickerLauncher(
        type = PickerType.File(extensions = listOf("xlsx")),
        mode = PickerMode.Single,
        title = "选择课表 Excel"
    ) { platformFile ->
        if (platformFile != null) {
            scope.launch(Dispatchers.IO) {
                Log.i("UploadScreen", "File selected via FileKit: ${platformFile.name}")

                try {
                    // 读取文件的字节流
                    val bytes = platformFile.readBytes()

                    // 将 ByteArray 转成 EasyExcel 需要的 InputStream
                    val inputStream = ByteArrayInputStream(bytes)

                    // 读取 Excel 数据
                    val excelData = ExcelUtil.readExcelSync(inputStream)

                    Log.i("UploadScreen", "Successfully read ${excelData.size} rows!")

                    // 打印看看
                    excelData.forEachIndexed { index, row ->
                        Log.d("UploadScreen", "Row $index: $row")
                    }

                } catch (e: Exception) {
                    Log.e("UploadScreen", "Error processing file", e)
                }
            }
        } else {
            Log.w("UploadScreen", "User canceled the picker.")
        }
    }

    // 页面显示即触发
    LaunchedEffect(Unit) {
        launcher.launch()
    }

    // UI
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("正在通过 FileKit 选择文件...")
    }
}