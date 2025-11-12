package com.example.accountbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.example.accountbook.viewmodel.RecordViewModel
import java.util.Locale
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.accountbook.repository.DataState
import com.example.accountbook.model.Record
import com.example.accountbook.model.RecordType
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListScreen(
    onAddRecordClick: () -> Unit,
    onStatsClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: RecordViewModel,
    onEditRecord: (String) -> Unit
) {
    println("DEBUG: === RecordListScreen 开始执行 ===")

    val records = viewModel.records.collectAsState(initial = emptyList())

    val operationState by viewModel.operationState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var lastOperation by remember { mutableStateOf<String?>(null) }

    // 简单调试记录数量
    LaunchedEffect(records.value) {
        println("DEBUG: 记录列表更新，数量: ${records.value.size}")
        if (records.value.isNotEmpty()) {
            println("DEBUG: 第一条记录: ${records.value.first().category} - ${records.value.first().amount}")
        }
    }

//    val currentRecords by viewModel.records.collectAsStateWithLifecycle(initialValue = emptyList())

    val currentTotalIncome = records.value
        .filter { it.type == RecordType.INCOME }
        .sumOf { it.amount }

    val currentTotalExpenses = records.value
        .filter { it.type == RecordType.EXPENSE }
        .sumOf { it.amount }

    val currentBalance = currentTotalIncome - currentTotalExpenses

    println("DEBUG: 统计计算 - 收入: $currentTotalIncome, 支出: $currentTotalExpenses, 余额: $currentBalance")


    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is DataState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Short
                )
                lastOperation = null
            }
            is DataState.Success<*> -> {
                if (lastOperation != null) {
                    snackbarHostState.showSnackbar(
                        message = when (lastOperation) {
                            "add" -> "记录添加成功"
                            "delete" -> "记录删除成功"
                            "edit" -> "记录更新成功" // 新增：编辑成功提示
                            else -> "操作成功"
                        },
                        duration = SnackbarDuration.Short
                    )
                    lastOperation = null
                }
            }
            else -> {}
        }
    }
    // 处理删除操作的函数
    fun handleDeleteRecord(record: Record) {
        println("DEBUG: 删除记录: ${record.category} - ${record.amount}")
        lastOperation = "delete"
        viewModel.deleteRecord(record)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("记账本") },
                actions = {
                    IconButton(
                        onClick = onStatsClick,
                        enabled = operationState !is DataState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BarChart,
                            contentDescription = "统计"
                        )
                    }
                    IconButton(
                        onClick = onLogout,
                        enabled = operationState !is DataState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "登出"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            val isLoading = operationState is DataState.Loading
            FloatingActionButton(
                onClick = if (isLoading) { {} } else {
                    {
                        lastOperation = "add"
                        onAddRecordClick()
                    }
                },
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = "添加记录")
                }
            }
        },
        bottomBar = {  // 新增：使用 bottomBar 确保统计信息始终显示
            if (records.value.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "总收入: ¥${String.format(Locale.getDefault(), "%.2f", currentTotalIncome)} | " +
                                    "总支出: ¥${String.format(Locale.getDefault(), "%.2f", currentTotalExpenses)} | " +
                                    "余额: ¥${String.format(Locale.getDefault(), "%.2f", currentBalance)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 新增：长按编辑提示
            if (records.value.isNotEmpty()) {
                Text(
                    text = "💡 提示：长按记录可进行编辑",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (operationState is DataState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )
            }
            if (records.value.isEmpty()) {
                EmptyRecordState(onAddRecordClick = onAddRecordClick)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(records.value) { record ->
                        RecordItem(
                            record = record,
                            onEditRecord = { recordId ->
                                println("DEBUG: 准备跳转到编辑页面，记录ID: $recordId") // 添加调试日志
                                onEditRecord(recordId) // 这个应该调用外部的导航回调
                            },    // 编辑回调
                            onDeleteRecord = { recordToDelete ->
                                handleDeleteRecord(recordToDelete)  // 确保这个回调正确传递
                            },
                            modifier = Modifier.padding(vertical = 4.dp),
                            isDeleting = operationState is DataState.Loading
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyRecordState(onAddRecordClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ReceiptLong,
            contentDescription = "空状态",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "还没有记账记录",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "开始记录你的第一笔收支，\n掌握财务状况从今天开始",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )


        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAddRecordClick,
            modifier = Modifier.width(200.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "开始记账",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("开始记账")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "点击右下角按钮也可添加记录",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}