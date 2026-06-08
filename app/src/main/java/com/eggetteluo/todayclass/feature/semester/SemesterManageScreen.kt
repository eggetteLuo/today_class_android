package com.eggetteluo.todayclass.feature.semester

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eggetteluo.todayclass.data.local.entity.SemesterInfoEntity
import com.eggetteluo.todayclass.ui.root.LocalSnackbarHostState
import com.eggetteluo.todayclass.util.DateUtil
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun SemesterManageScreen(
    viewModel: SemesterManageViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    var editingSemester by remember { mutableStateOf<SemesterInfoEntity?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var pendingDeleteSemester by remember { mutableStateOf<SemesterInfoEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SemesterManageUiEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (showCreateDialog || editingSemester != null) {
        SemesterEditDialog(
            semester = editingSemester,
            onDismiss = {
                showCreateDialog = false
                editingSemester = null
            },
            onConfirm = { semesterId, name, currentWeek, totalWeeks ->
                viewModel.saveSemester(semesterId, name, currentWeek, totalWeeks)
                showCreateDialog = false
                editingSemester = null
            }
        )
    }

    pendingDeleteSemester?.let { semester ->
        AlertDialog(
            onDismissRequest = { pendingDeleteSemester = null },
            title = { Text("删除学期") },
            text = { Text("删除 ${semester.name} 会同时删除该学期所有排课。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDeleteSemester = null
                        viewModel.deleteSemester(semester.id)
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteSemester = null }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text("学期管理", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "新建学期")
            }
        }
    ) { innerPadding ->
        if (uiState.semesters.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无学期")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.semesters, key = { it.id }) { semester ->
                    SemesterRow(
                        semester = semester,
                        onSetCurrentClick = { viewModel.setCurrentSemester(semester.id) },
                        onEditClick = { editingSemester = semester },
                        onDeleteClick = { pendingDeleteSemester = semester }
                    )
                }
            }
        }
    }
}

@Composable
private fun SemesterRow(
    semester: SemesterInfoEntity,
    onSetCurrentClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val (currentWeek, _) = DateUtil.getCurrentWeekAndDay(semester.startDate)
    ListItem(
        headlineContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    semester.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (semester.isCurrent) {
                    AssistChip(
                        onClick = {},
                        label = { Text("当前") },
                        leadingIcon = {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        }
                    )
                }
            }
        },
        supportingContent = {
            Text(
                "${formatDate(semester.startDate)} - ${formatDate(semester.endDate)} · 第 $currentWeek/${semester.totalWeeks} 周"
            )
        },
        trailingContent = {
            Row {
                if (!semester.isCurrent) {
                    TextButton(onClick = onSetCurrentClick) {
                        Text("设为当前")
                    }
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Filled.Edit, contentDescription = "编辑")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SemesterEditDialog(
    semester: SemesterInfoEntity?,
    onDismiss: () -> Unit,
    onConfirm: (Long?, String, String, String) -> Unit
) {
    val initialWeek = semester?.let { DateUtil.getCurrentWeekAndDay(it.startDate).first } ?: 1
    var name by remember(semester) { mutableStateOf(semester?.name ?: "") }
    var currentWeek by remember(semester) { mutableStateOf(initialWeek.toString()) }
    var totalWeeks by remember(semester) { mutableStateOf((semester?.totalWeeks ?: 20).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (semester == null) "新建学期" else "编辑学期") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("学期名称") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = currentWeek,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() }) currentWeek = value
                    },
                    label = { Text("今天是第几周") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = totalWeeks,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() }) totalWeeks = value
                    },
                    label = { Text("总周数") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(semester?.id, name, currentWeek, totalWeeks)
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

private fun formatDate(timestamp: Long): String {
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
