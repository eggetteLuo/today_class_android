package com.eggetteluo.todayclass.feature.upload.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WeekSelectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    // 弹窗内部记录当前选择的周次（默认从第一周开始）
    var currentWeek by remember { mutableIntStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.TwoTone.DateRange,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text("设置当前教学进度")
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "请确认本周是开学后的第几周\n我们将据此为您自动推算开学时间",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 核心交互区：Stepper (减号 - 数字 - 加号)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 减号按钮
                    FilledTonalIconButton(
                        onClick = { if (currentWeek > 1) currentWeek-- },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Rounded.Remove, contentDescription = "减少一周")
                    }

                    // 中间的大数字显示
                    Text(
                        text = "第 $currentWeek 周",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.widthIn(min = 120.dp),
                        textAlign = TextAlign.Center
                    )

                    // 加号按钮
                    FilledTonalIconButton(
                        onClick = { if (currentWeek < 20) currentWeek++ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "增加一周")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(currentWeek) }) {
                Text("开始导入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}