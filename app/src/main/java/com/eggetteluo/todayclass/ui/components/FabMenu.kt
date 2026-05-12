package com.eggetteluo.todayclass.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FabMenu(
    onUploadClick: () -> Unit,
    onAddClick: () -> Unit,
    onCourseClick: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = {
                    expanded = it
                }
            ) {
                val icon = if (expanded) {
                    Icons.Default.Close
                } else {
                    Icons.Default.Add
                }
                Icon(
                    imageVector = icon,
                    contentDescription = "expanded",
                    modifier = Modifier.animateIcon(
                        checkedProgress = { if (expanded) 1f else 0f }
                    )
                )
            }
        }
    ) {
        FloatingActionButtonMenuItem(
            onClick = {
                scope.launch {
                    onUploadClick()
                    delay(120)
                    expanded = false
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.FileUpload,
                    contentDescription = "Import"
                )
            },
            text = {
                Text("导入外部课表")
            }
        )
        FloatingActionButtonMenuItem(
            onClick = {
                scope.launch {
                    onAddClick()
                    delay(120)
                    expanded = false
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            },
            text = {
                Text("添加课程")
            }
        )
        FloatingActionButtonMenuItem(
            onClick = {
                scope.launch {
                    onCourseClick()
                    delay(120)
                    expanded = false
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Edit"
                )
            },
            text = {
                Text("课程")
            }
        )
    }

}